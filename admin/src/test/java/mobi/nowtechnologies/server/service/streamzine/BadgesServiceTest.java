package mobi.nowtechnologies.server.service.streamzine;

import mobi.nowtechnologies.server.persistence.domain.streamzine.FilenameAlias;
import mobi.nowtechnologies.server.persistence.repository.FilenameAliasRepository;
import mobi.nowtechnologies.server.service.CloudFileImagesService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import static org.mockito.Mockito.*;

public class BadgesServiceTest {
    @Mock
    private FilenameAliasRepository filenameAliasRepository;
    @Mock
    private CloudFileImagesService cloudFileImagesService;
    @InjectMocks
    private BadgesService badgesService;
    @Captor
    private ArgumentCaptor<FilenameAlias> filenameAlias;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testUpdate() throws Exception {
        final long id = 1;
        final String newName = "newName";

        FilenameAlias alias = mock(FilenameAlias.class);

        when(filenameAliasRepository.findOne(id)).thenReturn(alias);

        badgesService.update(id, newName);

        verify(alias).setAlias(newName);
    }
}
