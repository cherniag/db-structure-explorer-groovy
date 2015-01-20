package mobi.nowtechnologies.server.utils;

import com.jayway.jsonpath.JsonPath;

import java.beans.PropertyEditorSupport;

public class JsonPathPropertyEditor extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(tryToCreate(text));
    }

    @Override
    public String getAsText() {
        throw new UnsupportedOperationException("It is considered to be used in setters only");
    }

    private JsonPath tryToCreate(String text) {
        if(text == null) {
            return null;
        }

        return JsonPath.compile(text);
    }
}
