package mobi.nowtechnologies.server.admin.util;

import java.beans.PropertyEditorSupport;

// @author Titov Mykhaylo (titov) on 17.12.2014.
public class EnumEditor extends PropertyEditorSupport {

    private static final String NONE = "none";

    private Class<? extends Enum> clazz;

    public EnumEditor(Class<? extends Enum> clazz) {
        this.clazz = clazz;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (!NONE.equals(text)) {
            setValue(Enum.valueOf(clazz, text));
        }
    }
}
