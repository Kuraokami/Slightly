package biz.netcentric.Slightly.context;

import org.mozilla.javascript.*;

import javax.script.ScriptContext;

/**
 * This class is the core of all Contexts.
 *
 * Though this class can be used directly, it is recommended to use a child class of it to simplify the development.
 */
public class SlightlyContext {
    Scriptable scope;
    private Context cx;
    public SlightlyContext() {
        cx = Context.enter();
        scope = new ImporterTopLevel(cx);
    }

    public void addVariableToContext(String variableKey, Object variable){
        scope.put(variableKey, scope, variable);
    }

    public Context getCx() {
        return cx;
    }

    public Scriptable getScope() {
        return scope;
    }
}
