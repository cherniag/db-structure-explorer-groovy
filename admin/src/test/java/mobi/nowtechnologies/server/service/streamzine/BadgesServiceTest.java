package mobi.nowtechnologies.server.service.streamzine;

import mobi.nowtechnologies.server.persistence.domain.streamzine.FilenameAlias;
import mobi.nowtechnologies.server.persistence.repository.FilenameAliasRepository;
import mobi.nowtechnologies.server.service.CloudFileImagesService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.Assert.assertTrue;
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
    public void testFindAllBadges() throws Exception {
        badgesService.findAllBadges();
        verify(filenameAliasRepository).findAllByDomain(FilenameAlias.Domain.HEY_LIST_BADGES);
    }

    @Test
    public void testUpdate() throws Exception {
        String oldName = "oldName";
        String newName = "newName";
        FilenameAlias alias = mock(FilenameAlias.class);

        when(filenameAliasRepository.findByAlias(oldName)).thenReturn(alias);

        badgesService.update(oldName, newName);

        verify(alias).setAlias(newName);
        verify(filenameAliasRepository).saveAndFlush(alias);
    }

    @Test
    public void testDelete() throws Exception {
        String existingName = "existingName";
        FilenameAlias alias = mock(FilenameAlias.class);

        when(filenameAliasRepository.findByAlias(existingName)).thenReturn(alias);

        badgesService.delete(existingName);

        verify(filenameAliasRepository).findByAlias(existingName);
        verify(filenameAliasRepository).delete(alias);
        verifyNoMoreInteractions(cloudFileImagesService);
    }

    @Test
    public void testUpload() throws Exception {
        final String value = "value";
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(value);

        badgesService.upload(file);

        verify(filenameAliasRepository).save(filenameAlias.capture());
        verify(cloudFileImagesService).uploadImageWithGivenName(eq(file), anyString());
        assertTrue(filenameAlias.getValue().getFileName().contains(value + "_"));
    }
}
