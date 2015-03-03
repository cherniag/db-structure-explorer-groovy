package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.BadgeMappingFactory;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.CommunityFactory;
import mobi.nowtechnologies.server.persistence.domain.ResolutionFactory;
import mobi.nowtechnologies.server.persistence.domain.streamzine.FilenameAlias;
import mobi.nowtechnologies.server.persistence.domain.streamzine.badge.BadgeMapping;
import mobi.nowtechnologies.server.persistence.domain.streamzine.badge.Resolution;
import mobi.nowtechnologies.server.persistence.repository.BadgeMappingRepository;
import mobi.nowtechnologies.server.persistence.repository.FilenameAliasRepository;
import mobi.nowtechnologies.server.persistence.repository.ResolutionRepository;
import mobi.nowtechnologies.server.service.file.image.ImageService;
import mobi.nowtechnologies.server.service.streamzine.BadgesService;
import mobi.nowtechnologies.server.service.streamzine.CloudFileImagesService;

import java.util.Arrays;
import java.util.List;

import org.junit.*;
import org.junit.rules.*;
import org.mockito.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BadgesServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Mock
    private FilenameAliasRepository filenameAliasRepository;
    @Mock
    private CloudFileImagesService cloudFileImagesService;
    @Mock
    private ResolutionRepository resolutionRepository;
    @Mock
    private BadgeMappingRepository badgeMappingRepository;
    @Mock
    private CloudFileService cloudFileService;
    @Mock
    private ImageService imageService;
    @InjectMocks
    private BadgesService badgesService;
    @Captor
    private ArgumentCaptor<FilenameAlias> filenameAlias;
    @Captor
    private ArgumentCaptor<Iterable<? extends BadgeMapping>> entities;

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

    //
    // Badges:
    //
    @Test
    public void testGetBadgeFileNameNoResolution() throws Exception {
        // given
        Resolution resolution = ResolutionFactory.create("deviceType", 1, 1);
        Community community = CommunityFactory.createCommunityMock(1, "hl_uk");

        // when
        final long badgeId = 1L;
        List<BadgeMapping> mappings = Arrays.asList(BadgeMappingFactory.general("fileName"));

        // No resolution:
        when(resolutionRepository.find(anyString(), anyInt(), anyInt())).thenReturn(null);
        when(badgeMappingRepository.findByCommunityAndFilenameId(community, badgeId)).thenReturn(mappings);

        // then
        assertEquals("fileName", badgesService.getBadgeFileName(badgeId, community, resolution));
        verify(badgeMappingRepository).findByCommunityAndFilenameId(community, badgeId);
    }

    @Test
    public void testGetBadgeFileNameForResolutionAndBadgeIsAvailableAndImageIsResized() throws Exception {
        // given
        Resolution resolution = ResolutionFactory.create("deviceType", 1, 1);
        Community community = CommunityFactory.createCommunityMock(1, "hl_uk");

        // when
        final long badgeId = 1L;
        List<BadgeMapping> mappings = Arrays.asList(BadgeMappingFactory.specific("general", "specific"), BadgeMappingFactory.general("general"));

        when(resolutionRepository.find(anyString(), anyInt(), anyInt())).thenReturn(resolution);
        when(badgeMappingRepository.findByCommunityResolutionAndFilenameId(community, resolution, badgeId)).thenReturn(mappings);

        // then
        assertEquals("specific", badgesService.getBadgeFileName(badgeId, community, resolution));
        verify(badgeMappingRepository).findByCommunityResolutionAndFilenameId(community, resolution, badgeId);
    }

    @Test
    public void testGetBadgeFileNameForResolutionAndBadgeIsAvailableButImageIsNotResized() throws Exception {
        // given
        Resolution resolution = ResolutionFactory.create("deviceType", 1, 1);
        Community community = CommunityFactory.createCommunityMock(1, "hl_uk");

        // when
        final long badgeId = 1L;
        List<BadgeMapping> mappings = Arrays.asList(BadgeMappingFactory.specific("general", null), BadgeMappingFactory.general("general"));

        when(resolutionRepository.find(anyString(), anyInt(), anyInt())).thenReturn(resolution);
        when(badgeMappingRepository.findByCommunityResolutionAndFilenameId(community, resolution, badgeId)).thenReturn(mappings);

        // then
        assertEquals("general", badgesService.getBadgeFileName(badgeId, community, resolution));
        verify(badgeMappingRepository).findByCommunityResolutionAndFilenameId(community, resolution, badgeId);
    }

    @Test
    public void testGetBadgeFileNameForResolutionAndBadgeIsAvailableButOnlyGeneral() throws Exception {
        // given
        Resolution resolution = ResolutionFactory.create("deviceType", 1, 1);
        Community community = CommunityFactory.createCommunityMock(1, "hl_uk");

        // when
        final long badgeId = 1L;
        List<BadgeMapping> mappings = Arrays.asList(
            // no specific
            BadgeMappingFactory.general("general"));

        when(resolutionRepository.find(anyString(), anyInt(), anyInt())).thenReturn(resolution);
        when(badgeMappingRepository.findByCommunityResolutionAndFilenameId(community, resolution, badgeId)).thenReturn(mappings);

        // then
        assertEquals("general", badgesService.getBadgeFileName(badgeId, community, resolution));
        verify(badgeMappingRepository).findByCommunityResolutionAndFilenameId(community, resolution, badgeId);
    }


    @Test
    public void testGetBadgeFileNameForResolutionAndBadgeNotFoundById() throws Exception {
        // given
        Resolution resolution = ResolutionFactory.create("deviceType", 1, 1);
        Community community = CommunityFactory.createCommunityMock(1, "hl_uk");

        // when
        final long badgeId = 1L;
        List<BadgeMapping> mappings = Arrays.asList(
            // not found: empty list
        );

        when(resolutionRepository.find(anyString(), anyInt(), anyInt())).thenReturn(resolution);
        when(badgeMappingRepository.findByCommunityResolutionAndFilenameId(community, resolution, badgeId)).thenReturn(mappings);

        // then
        thrown.expect(IllegalArgumentException.class);
        assertEquals("specific", badgesService.getBadgeFileName(badgeId, community, resolution));
    }


    //
    // Cloud delete
    //
    @Test
    public void testDeleteCloudFileByAliasForNotFound() throws Exception {
        // given
        final long id = 1L;

        FilenameAlias alias = mock(FilenameAlias.class);
        when(alias.getFileName()).thenReturn("fileName");
        when(alias.getId()).thenReturn(id);

        // when
        when(filenameAliasRepository.findOne(id)).thenReturn(null);

        badgesService.deleteCloudFileByAlias(alias);

        // then
        verify(filenameAliasRepository).findOne(id);
        verifyNoMoreInteractions(cloudFileService, filenameAliasRepository);
    }

    @Test
    public void testDeleteCloudFileByAlias() throws Exception {
        // given
        final long id = 1L;

        FilenameAlias alias = mock(FilenameAlias.class);
        when(alias.getFileName()).thenReturn("fileName");
        when(alias.getId()).thenReturn(id);

        // when
        when(filenameAliasRepository.findOne(id)).thenReturn(alias);

        badgesService.deleteCloudFileByAlias(alias);

        // then
        verify(filenameAliasRepository).findOne(id);
        verify(filenameAliasRepository).delete(alias);
        verify(cloudFileService).deleteFile("fileName");

    }

    //
    // Resolution
    //
    @Test
    public void testRemoveResolution() throws Exception {
        // given
        long id = 1L;
        List<BadgeMapping> mappings = Arrays.asList(BadgeMappingFactory.specific("general", "specific"));

        Resolution resolution = ResolutionFactory.create("deviceType", 1, 1);

        // when
        when(resolutionRepository.findOne(resolution.getId())).thenReturn(resolution);
        when(badgeMappingRepository.findByResolution(resolution)).thenReturn(mappings);

        List<FilenameAlias> result = badgesService.removeResolution(id);

        // then
        assertEquals(1, result.size());
        assertEquals("specific", result.get(0).getFileName());
        verify(badgeMappingRepository).findByResolution(resolution);
        verify(badgeMappingRepository).deleteByResolution(resolution);
        verify(resolutionRepository).delete(resolution);
    }

    @Test
    public void testCreateResolution() throws Exception {
        // given
        List<BadgeMapping> mappings = Arrays.asList(BadgeMappingFactory.specific("general", "specific"));

        Community community = CommunityFactory.createCommunityMock(1, "hl_uk");
        Resolution resolution = ResolutionFactory.create("deviceType", 1, 1);

        // when
        when(badgeMappingRepository.findAllDefault()).thenReturn(mappings);
        badgesService.createResolution(resolution);

        // then
        verify(resolutionRepository).saveAndFlush(resolution);
        verify(badgeMappingRepository).findAllDefault();
        verify(badgeMappingRepository).save(entities.capture());

        assertEquals("general", entities.getValue().iterator().next().getOriginalFilenameAlias().getFileName());
    }

}
