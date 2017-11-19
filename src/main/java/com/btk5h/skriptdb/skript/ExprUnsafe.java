package com.btk5h.skriptdb.skript;

import org.bukkit.event.Event;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

@Name("Unsafe Expression")
@Description("Opts out of automatic SQL injection protection for a specific expression in a statement.")
@Examples({
		"execute \"select %unsafe {columns variable}% from %{table variable}%\" in {sql} and store the result in {output::*}",
		"execute unsafe {fully dynamic query} in {sql}"
})
@Since("0.1.0")
public class ExprUnsafe extends SimpleExpression<String> {
  static {
    Skript.registerExpression(ExprUnsafe.class, String.class, ExpressionType.COMBINED,
        "unsafe %string%");
  }

  private Expression<String> str;

  @Override
  protected String[] get(Event e) {
    return str.getArray(e);
  }

  @Override
  public boolean isSingle() {
    return true;
  }

  @Override
  public Class<? extends String> getReturnType() {
    return String.class;
  }

  @Override
  public String toString(Event e, boolean debug) {
    return "unsafe " + str.toString(e, debug);
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed,
                      SkriptParser.ParseResult parseResult) {
    str = (Expression<String>) exprs[0];
    return true;
  }
}
