package mobi.nowtechnologies.server.editor;

import mobi.nowtechnologies.server.persistence.domain.streamzine.badge.Resolution;
import org.junit.Test;
import org.springframework.beans.ConversionNotSupportedException;

import static org.junit.Assert.assertEquals;

public class ResolutionParameterEditorTest {
    ResolutionParameterEditor editor = new ResolutionParameterEditor();

    @Test
    public void testSetAsTextSuccessWithLowerCaseDelim() throws Exception {
        editor.setAsText("1x1");
        Resolution r = (Resolution) editor.getValue();

        assertEquals(1, r.getWidth());
        assertEquals(1, r.getHeight());
    }

    @Test
    public void testSetAsTextSuccessWithUpperCaseDelim() throws Exception {
        editor.setAsText("1X1");
        Resolution r = (Resolution) editor.getValue();

        assertEquals(1, r.getWidth());
        assertEquals(1, r.getHeight());
    }

    @Test(expected = ConversionNotSupportedException.class)
    public void testSetAsTextNotSuccessfullBadDelim() throws Exception {
        editor.setAsText("1a1");
    }

    @Test(expected = ConversionNotSupportedException.class)
    public void testSetAsTextNotSuccessfullDelimOneDigit() throws Exception {
        editor.setAsText("1x");
    }

    @Test(expected = ConversionNotSupportedException.class)
    public void testSetAsTextNotSuccessfullDelimOneDigitAndNot1() throws Exception {
        editor.setAsText("1xA");
    }

    @Test(expected = ConversionNotSupportedException.class)
    public void testSetAsTextNotSuccessfullDelimOneDigitAndNot2() throws Exception {
        editor.setAsText("Ax1");
    }

    @Test(expected = ConversionNotSupportedException.class)
    public void testSetAsTextNotSuccessfullWhenIsEmpty() throws Exception {
        editor.setAsText("");
    }
}