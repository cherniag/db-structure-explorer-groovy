package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Drm;

import javax.annotation.Resource;

import org.junit.*;

import static junit.framework.Assert.assertNotNull;


/**
 * @author Alexander Kolpakov (akolpakov)
 */

public class DrmRepositoryIT extends AbstractRepositoryIT {

    @Resource
    private DrmRepository drmRepository;

    @Test
    public void testFindByUserAndMedia() {
        Drm drm = drmRepository.findByUserAndMedia(1, 49);

        assertNotNull(drm);
    }
}