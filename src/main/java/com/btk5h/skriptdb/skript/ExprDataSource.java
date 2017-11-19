package com.btk5h.skriptdb.skript;

import com.zaxxer.hikari.HikariDataSource;

import org.bukkit.event.Event;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprDataSource extends SimpleExpression<HikariDataSource> {
  static {
    Skript.registerExpression(ExprDataSource.class, HikariDataSource.class,
        ExpressionType.COMBINED, "[the] data(base|[ ]source) [(of|at)] %string%");
  }

  private Expression<String> url;

  @Override
  protected HikariDataSource[] get(Event e) {
    String jdbcUrl = url.getSingle(e);
    if (jdbcUrl == null) {
      return null;
    }

    if (!jdbcUrl.startsWith("jdbc:")) {
      jdbcUrl = "jdbc:" + jdbcUrl;
    }

    HikariDataSource ds = new HikariDataSource();
    ds.setJdbcUrl(jdbcUrl);

    return new HikariDataSource[] {ds};
  }

  @Override
  public boolean isSingle() {
    return true;
  }

  @Override
  public Class<? extends HikariDataSource> getReturnType() {
    return HikariDataSource.class;
  }

  @Override
  public String toString(Event e, boolean debug) {
    return "datasource " + url.toString(e, debug);
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed,
                      SkriptParser.ParseResult parseResult) {
    url = (Expression<String>) exprs[0];
    return true;
  }
}
