package mobi.nowtechnologies.server.service;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.rules.ExpectedException.none;

/**
 * Created by oar on 2/25/14.
 */
public class CloudFileImagesServiceTest {

    @Rule
    public ExpectedException thrown = none();

    private CloudFileImagesService cloudFileImagesService = new CloudFileImagesService();

    @Test
    public void testFindByEmptyPrefix() {
        thrown.expect(IllegalArgumentException.class);
        cloudFileImagesService.findByPrefix(null);
    }

    @Test
    public void testFindByEmptyPrefixWithEmptyString() {
        thrown.expect(IllegalArgumentException.class);
        cloudFileImagesService.findByPrefix("  ");
    }


}
