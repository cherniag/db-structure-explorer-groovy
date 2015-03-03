package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.streamzine.Dimensions;
import mobi.nowtechnologies.server.persistence.domain.streamzine.FilenameAlias;

import javax.annotation.Resource;

import org.junit.*;
import static org.junit.Assert.*;

public class FilenameAliasRepositoryIT extends AbstractRepositoryIT {

    @Resource
    FilenameAliasRepository filenameAliasRepository;

    @Test
    public void save() {
        FilenameAlias saved = filenameAliasRepository.save(new FilenameAlias("filename", "alias", new Dimensions(50, 50)));

        assertTrue(saved.getId() > 0);
    }
}