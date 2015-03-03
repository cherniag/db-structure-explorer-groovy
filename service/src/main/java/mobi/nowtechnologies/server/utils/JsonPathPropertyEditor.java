package mobi.nowtechnologies.server.utils;

import java.beans.PropertyEditorSupport;

import com.jayway.jsonpath.JsonPath;

public class JsonPathPropertyEditor extends PropertyEditorSupport {

    @Override
    public String getAsText() {
        throw new UnsupportedOperationException("It is considered to be used in setters only");
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(tryToCreate(text));
    }

    private JsonPath tryToCreate(String text) {
        if (text == null) {
            return null;
        }

        return JsonPath.compile(text);
    }
}
