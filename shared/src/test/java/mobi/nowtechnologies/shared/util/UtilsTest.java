package mobi.nowtechnologies.shared.util;

import mobi.nowtechnologies.server.shared.Utils;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.*;
import static org.junit.Assert.*;

public class UtilsTest {

    @Test
    public void getRandomPassword() {
        Pattern pattern = Pattern.compile("\\d{6}");


        String randomPassword = Utils.getRandomString(6);

        Matcher matcher = pattern.matcher(randomPassword);

        Assert.assertEquals(6, randomPassword.length());
        Assert.assertTrue(matcher.matches());
    }

    @Test
    public void testGetRandomUUID() throws Exception {
        Pattern pattern = Pattern.compile("\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}");
        String randomUUID = Utils.getRandomUUID();
        assertFalse(randomUUID.isEmpty());
        Matcher matcher = pattern.matcher(randomUUID);
        assertTrue("Expected uuid is " + randomUUID, matcher.matches());
    }

    @Test
    public void testGetRandomUUIDAreDifferent() throws Exception {
        Set<String> UUIDs = new HashSet<String>();
        for (int i = 0; i < 10; i++) {
            UUIDs.add(Utils.getRandomUUID());
        }
        assertEquals(10, UUIDs.size());
    }
}