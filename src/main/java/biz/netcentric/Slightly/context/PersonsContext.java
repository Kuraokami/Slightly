package biz.netcentric.Slightly.context;

import org.mozilla.javascript.NativeJavaObject;

/**
 * This class contains a specific Context for a Person that can be rendered
 * The Context uses a Javascript Context to hold the variable values.
 */
public class PersonsContext extends SlightlyContext {

    public PersonsContext() {
        super();
    }

    public Object findParameter(String parameterName) {
        Object result;
        final Object element = super.getCx().evaluateString(getScope(), parameterName, "element", 1, null);
        if (element instanceof Boolean) {
            result = element;
        } else {
            result = ((NativeJavaObject) element).unwrap();
        }
        return result;
    }
}
