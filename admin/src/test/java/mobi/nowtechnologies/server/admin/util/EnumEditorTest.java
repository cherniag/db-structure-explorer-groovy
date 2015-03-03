package mobi.nowtechnologies.server.admin.util;

import org.junit.*;
import static org.junit.Assert.*;

public class EnumEditorTest {

    EnumEditor enumEditor;

    @Before
    public void setUp() {
        enumEditor = new EnumEditor(TestEnum.class);
    }

    @Test
    public void shouldSetNullWhenTextIsNone() {
        //given
        String text = "none";

        //when
        enumEditor.setAsText(text);

        //then
        assertNull(enumEditor.getValue());
    }

    @Test
    public void shouldSetAsText() {
        //given
        String text = TestEnum.test1.name();

        //when
        enumEditor.setAsText(text);

        //then
        assertSame(enumEditor.getValue(), TestEnum.test1);
    }

    private static enum TestEnum {
        test1
    }
}