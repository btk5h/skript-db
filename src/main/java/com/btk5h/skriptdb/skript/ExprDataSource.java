package com.btk5h.skriptdb.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores the connection information for a data source. This should be saved to a variable in a
 * `script load` event or manually through an effect command.
 * <p>
 * The url format for your database may vary! The example provided uses a MySQL database.
 *
 * @name Data Source
 * @index -1
 * @pattern [the] data(base|[ ]source) [(of|at)] %string% [with [a] [max[imum]] [connection] life[ ]time of %timespan%]"
 * @return datasource
 * @example set {sql} to the database "mysql://localhost:3306/mydatabase?user=admin&password=12345&useSSL=false"
 * @since 0.1.0
 */
public class ExprDataSource extends SimpleExpression<HikariDataSource> {
    private static final Map<String, HikariDataSource> connectionCache = new HashMap<>();

    static {
        Skript.registerExpression(
            ExprDataSource.class, HikariDataSource.class, ExpressionType.COMBINED,
            "[the] data(base|[ ]source) [(of|at)] %string% [with [a] [max[imum]] [connection] life[ ]time of %-timespan%]"
        );
    }

    private Expression<String> url;
    private Expression<Timespan> maxLifetime;

    @Override
    protected HikariDataSource[] get(Event e) {
        String jdbcUrl = url.getSingle(e);
        if (jdbcUrl == null) {
            return null;
        }

        if (!jdbcUrl.startsWith("jdbc:")) {
            jdbcUrl = "jdbc:" + jdbcUrl;
        }

        if (connectionCache.containsKey(jdbcUrl)) {
            return new HikariDataSource[]{connectionCache.get(jdbcUrl)};
        }

        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(jdbcUrl);

        if (maxLifetime != null) {
            Timespan l = maxLifetime.getSingle(e);

            if (l != null) {
                ds.setMaxLifetime(l.getMilliSeconds());
            }
        }

        connectionCache.put(jdbcUrl, ds);

        return new HikariDataSource[]{ds};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @NotNull
    @Override
    public Class<? extends HikariDataSource> getReturnType() {
        return HikariDataSource.class;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "datasource " + url.toString(e, debug);
    }

    //@Override
    //    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed,
    //                        SkriptParser.ParseResult parseResult) {
    //        url = exprs[0];
    //        maxLifetime = exprs[1];
    //        return true;
    //    }
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        return false;
    }
}
