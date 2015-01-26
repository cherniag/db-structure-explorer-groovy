package mobi.nowtechnologies.common.util;

import org.junit.Test;

import java.text.DateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DateTimeUtilsTest {

    @Test
    public void testGetDateFromInt_1() {
        Date result = DateTimeUtils.getDateFromInt(1);

        assertNotNull(result);
        assertEquals(DateFormat.getInstance().format(new Date(1000L)), DateFormat.getInstance().format(result));
        assertEquals(1000L, result.getTime());
    }

    @Test
    public void shouldReturnMillisInSeconds() {
        //given
        Integer seconds = Integer.MAX_VALUE;
        //when
        long millis = DateTimeUtils.secondsToMillis(seconds);
        //then
        assertEquals(millis, seconds * 1000L);
    }

}