package mobi.nowtechnologies.server.editor;

import mobi.nowtechnologies.server.persistence.domain.streamzine.badge.Resolution;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang.math.NumberUtils;

import org.springframework.beans.ConversionNotSupportedException;

public class ResolutionParameterEditor extends PropertyEditorSupport {

    private static final String x = "x";
    private static final String X = x.toUpperCase();
    private static final String pattern = "[" + x + X + "]";

    @Override
    public String getAsText() {
        Resolution value = (Resolution) getValue();
        return value.getWidth() + x + value.getHeight();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        String[] split = text.split(pattern);
        if (split.length != 2) {
            throw new ConversionNotSupportedException(text, Resolution.class, null);
        }
        int w = NumberUtils.toInt(split[0], 0);
        int h = NumberUtils.toInt(split[1], 0);
        if (w == 0 || h == 0) {
            throw new ConversionNotSupportedException(text, Resolution.class, null);
        }
        setValue(new Resolution(w, h));
    }
}
