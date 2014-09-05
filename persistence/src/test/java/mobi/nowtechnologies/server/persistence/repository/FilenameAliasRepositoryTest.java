package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.streamzine.FilenameAlias;
import org.junit.Test;

import javax.annotation.Resource;

import static org.junit.Assert.assertTrue;

public class FilenameAliasRepositoryTest extends AbstractRepositoryIT {
    @Resource
    FilenameAliasRepository filenameAliasRepository;

    @Test
    public void save() {
        FilenameAlias saved = filenameAliasRepository.save(new FilenameAlias("filename", "alias", 50, 50));

        assertTrue(saved.getId() > 0);
    }
}