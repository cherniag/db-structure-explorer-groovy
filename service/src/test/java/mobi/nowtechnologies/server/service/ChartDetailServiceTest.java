package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.assembler.ChartDetailsAsm;
import mobi.nowtechnologies.server.persistence.domain.Artist;
import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.Label;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.MediaFile;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.repository.ChartDetailRepository;
import mobi.nowtechnologies.server.persistence.repository.MediaRepository;
import mobi.nowtechnologies.server.service.exception.ServiceCheckedException;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.dto.admin.ChartItemDto;
import mobi.nowtechnologies.server.shared.enums.ChgPosition;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
@RunWith(PowerMockRunner.class)
public class ChartDetailServiceTest {

    private static ChartDetailService fixtureChartDetailService;
    private static ChartDetailRepository mockChartDetailRepository;
    private static MediaService mockMediaService;
    private List<ChartDetail> originalChartDetails;
    private List<Media> medias;
    private MediaRepository mediaRepository;

    @Test
    public void testChartDetailService_1() throws Exception {
        ChartDetailService result = new ChartDetailService();
        assertNotNull(result);
    }

    private List<ChartDetail> getChartDetails(final long publishTimeMillis) {
        if (originalChartDetails == null) {
            originalChartDetails = new LinkedList<ChartDetail>();

            List<Media> medias = getMedias();

            int i = 0;
            for (Media media : medias) {
                i++;
                final Chart chart = getChartInstance(i);

                ChartDetail originalChartDetail = getChartDetailInstance(publishTimeMillis, i, media, chart);

                originalChartDetails.add(originalChartDetail);
            }
            originalChartDetails = Collections.unmodifiableList(originalChartDetails);
        }
        return originalChartDetails;
    }

    private Chart getChartInstance(int i) {
        final Chart chart = new Chart();
        chart.setI(i);
        return chart;
    }

    private ChartDetail getChartDetailInstance(final long publishTimeMillis, int i, Media media, final Chart chart) {
        ChartDetail originalChartDetail = new ChartDetail();
        originalChartDetail.setChannel("channel" + i);
        originalChartDetail.setChart(chart);
        originalChartDetail.setChgPosition(ChgPosition.DOWN);
        originalChartDetail.setI(i);
        originalChartDetail.setInfo("info" + i);
        originalChartDetail.setMedia(media);
        originalChartDetail.setPosition((byte) i);
        originalChartDetail.setPrevPosition((byte) 0);
        originalChartDetail.setPublishTimeMillis(publishTimeMillis);
        originalChartDetail.setVersionAsPrimitive(i);
        return originalChartDetail;
    }

    private List<Media> getMedias() {
        if (medias == null) {
            medias = new LinkedList<Media>();

            for (int i = 0; i < 10; i++) {
                Media media = getMediaInstance(i);
                medias.add(media);
                when(mediaRepository.findOne(media.getI())).thenReturn(media);
                when(mediaRepository.save(media)).thenReturn(media);
            }
            medias = Collections.unmodifiableList(medias);
        }
        return medias;
    }

    private Media getMediaInstance(int i) {
        MediaFile mediaFile = new MediaFile();
        mediaFile.setI(1);
        mediaFile.setFilename("Some filename");

        MediaFile audioFile = new MediaFile();
        audioFile.setI(2);
        audioFile.setFilename("Another filename");

        Artist artist = new Artist();
        artist.setI(1);
        artist.setName("Some artist name");

        Media media = new Media();
        media.setArtist(artist);
        media.setImageFileSmall(mediaFile);
        media.setLabel(new Label().withName("label name"));

        media.setAudioFile(audioFile);

        media.setI(i);
        return media;
    }

    @Test
    public void testFindChartDetailTreeAndUpdateDrm_Success() throws Exception {

        Integer chartId = 1;
        Long nearestLatestPublishTimeMillis = new Date().getTime();
        int userId = 1;

        User user = new User();
        user.setId(userId);
        final UserGroup userGroup = new UserGroup().withId(1);
        user.setUserGroup(userGroup);

        List<ChartDetail> originalChartDetails = getChartDetails(nearestLatestPublishTimeMillis);

        Mockito.when(mockChartDetailRepository.findNearestLatestPublishDate(Mockito.anyLong(), Mockito.eq(chartId))).thenReturn(nearestLatestPublishTimeMillis);
        Mockito.when(mockChartDetailRepository.findChartDetailTreeForDrmUpdateByChartAndPublishTimeMillis(chartId, nearestLatestPublishTimeMillis)).thenReturn(originalChartDetails);

        List<ChartDetail> actualChartDetails = fixtureChartDetailService.findChartDetailTree(chartId, new Date(), true);

        assertNotNull(actualChartDetails);
        assertEquals(originalChartDetails, actualChartDetails);

        actualChartDetails = new LinkedList<ChartDetail>(actualChartDetails);
        Collections.sort(actualChartDetails, new ChartDetailComparator());

        for (int i = 0; i < actualChartDetails.size(); i++) {
            ChartDetail actualChartDetail = actualChartDetails.get(i);
            Media actualMedia = actualChartDetail.getMedia();
            assertNotNull(actualMedia);
            assertEquals(originalChartDetails.get(i).getMedia(), actualMedia);
        }
    }

    /**
     * Run the List<ChartDetail> findChartDetailTreeAndUpdateDrm(User,byte) method test.
     *
     * @throws Exception
     */
    @Test
    public void testFindChartDetailTreeAndUpdateDrm_NotLocked_Success() throws Exception {

        Integer chartId = 1;
        Long nearestLatestPublishTimeMillis = new Date().getTime();
        int userId = 1;

        User user = new User();
        user.setId(userId);
        final UserGroup userGroup = new UserGroup().withId(1);
        user.setUserGroup(userGroup);

        List<ChartDetail> originalChartDetails = getChartDetails(nearestLatestPublishTimeMillis);

        Mockito.when(mockChartDetailRepository.findNearestLatestPublishDate(Mockito.anyLong(), Mockito.eq(chartId))).thenReturn(nearestLatestPublishTimeMillis);
        Mockito.when(mockChartDetailRepository.findNotLockedChartDetailTreeForDrmUpdateByChartAndPublishTimeMillis(chartId, nearestLatestPublishTimeMillis)).thenReturn(originalChartDetails);

        List<ChartDetail> actualChartDetails = fixtureChartDetailService.findChartDetailTree(chartId, new Date(), false);

        assertNotNull(actualChartDetails);
        assertEquals(originalChartDetails, actualChartDetails);

        actualChartDetails = new LinkedList<ChartDetail>(actualChartDetails);
        Collections.sort(actualChartDetails, new ChartDetailComparator());

        for (int i = 0; i < actualChartDetails.size(); i++) {
            ChartDetail actualChartDetail = actualChartDetails.get(i);
            Media actualMedia = actualChartDetail.getMedia();
            assertNotNull(actualMedia);
            assertEquals(originalChartDetails.get(i).getMedia(), actualMedia);
        }

        verify(mockChartDetailRepository, times(1)).findNotLockedChartDetailTreeForDrmUpdateByChartAndPublishTimeMillis(chartId, nearestLatestPublishTimeMillis);
    }

    /**
     * Run the List<ChartDetail> findChartDetailTreeAndUpdateDrm(User,byte) method test.
     *
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testFindChartDetailTreeAndUpdateDrm_DrmsIsEmpty_Success() throws Exception {

        Integer chartId = 1;
        Long nearestLatestPublishTimeMillis = new Date().getTime();
        int userId = 1;

        User user = new User();
        user.setId(userId);
        final UserGroup userGroup = new UserGroup().withId(1);
        user.setUserGroup(userGroup);

        List<ChartDetail> originalChartDetails = getChartDetails(0L);

        Mockito.when(mockChartDetailRepository.findNearestLatestPublishDate(Mockito.anyLong(), Mockito.eq(chartId))).thenReturn(nearestLatestPublishTimeMillis);
        Mockito.when(mockChartDetailRepository.findChartDetailTreeForDrmUpdateByChartAndPublishTimeMillis(chartId, nearestLatestPublishTimeMillis)).thenReturn(originalChartDetails);

        List<ChartDetail> actualChartDetails = fixtureChartDetailService.findChartDetailTree(chartId, new Date(), true);

        assertNotNull(actualChartDetails);
        assertEquals(originalChartDetails, actualChartDetails);

        actualChartDetails = new LinkedList<ChartDetail>(actualChartDetails);
        Collections.sort(actualChartDetails, new ChartDetailComparator());

        for (int i = 0; i < actualChartDetails.size(); i++) {
            ChartDetail actualChartDetail = actualChartDetails.get(i);
            Media actualMedia = actualChartDetail.getMedia();
            assertNotNull(actualMedia);
            assertEquals(originalChartDetails.get(i).getMedia(), actualMedia);
        }
    }

    /**
     * Run the List<ChartDetail> findChartDetailTreeAndUpdateDrm(User,byte) method test.
     *
     * @throws Exception
     */
    @Test
    public void testFindChartDetailTreeAndUpdateDrm_NearestLatestPublishTimeMillisIsNull_Success() throws Exception {

        Integer chartId = 1;
        Long nearestLatestPublishTimeMillis = null;
        int userId = 1;

        User user = new User();
        user.setId(userId);
        final UserGroup userGroup = new UserGroup().withId(1);
        user.setUserGroup(userGroup);

        List<ChartDetail> originalChartDetails = getChartDetails(0L);

        Mockito.when(mockChartDetailRepository.findNearestLatestPublishDate(Mockito.anyLong(), Mockito.eq(chartId))).thenReturn(nearestLatestPublishTimeMillis);
        Mockito.when(mockChartDetailRepository.findChartDetailTreeForDrmUpdateByChartAndPublishTimeMillis(chartId, nearestLatestPublishTimeMillis)).thenReturn(originalChartDetails);

        List<ChartDetail> actualChartDetails = fixtureChartDetailService.findChartDetailTree(chartId, new Date(), true);

        assertNotNull(actualChartDetails);
        assertTrue(actualChartDetails.isEmpty());
    }

    /**
     * Run the List<ChartDetail> findActualChartItems(Byte,Date) method test.
     *
     * @throws Exception
     */
    @Test
    public void testGetActualChartItems_Success() throws Exception {
        Integer chartId = new Integer(1);
        Date selectedPublishDate = new Date();
        Long nearestLatestPublishTimeMillis = new Date().getTime();

        List<ChartDetail> originalChartDetails = getChartDetails(0L);

        Mockito.when(mockChartDetailRepository.findNearestLatestPublishDate(Mockito.anyLong(), Mockito.eq(chartId))).thenReturn(nearestLatestPublishTimeMillis);
        Mockito.when(mockChartDetailRepository.findActualChartItems(Mockito.eq(chartId), Mockito.eq(nearestLatestPublishTimeMillis))).thenReturn(originalChartDetails);

        List<ChartDetail> actualChartDetails = fixtureChartDetailService.getActualChartItems(chartId, selectedPublishDate);

        assertNotNull(actualChartDetails);
        assertEquals(originalChartDetails, actualChartDetails);

    }

    /**
     * Run the List<ChartDetail> findActualChartItems(Byte,Date) method test.
     *
     * @throws Exception
     */
    @Test
    public void testGetActualChartItems_NearestLatestPublishTimeMillisIsNull_Success() throws Exception {
        Integer chartId = new Integer(1);
        Date selectedPublishDate = new Date();
        Long nearestLatestPublishTimeMillis = null;

        Mockito.when(mockChartDetailRepository.findNearestLatestPublishDate(Mockito.anyLong(), Mockito.eq(chartId))).thenReturn(nearestLatestPublishTimeMillis);

        List<ChartDetail> actualChartDetails = fixtureChartDetailService.getActualChartItems(chartId, selectedPublishDate);

        assertNotNull(actualChartDetails);
        assertEquals(0, actualChartDetails.size());
    }

    /**
     * Run the List<ChartDetail> findActualChartItems(Byte,Date) method test.
     *
     * @throws Exception
     */
    @Test(expected = ServiceException.class)
    public void testGetActualChartItems_ChartIdIsNull_Failure() throws Exception {
        Integer chartId = null;
        Date selectedPublishDate = new Date();

        fixtureChartDetailService.getActualChartItems(chartId, selectedPublishDate);
    }

    /**
     * Run the List<ChartDetail> findActualChartItems(Byte,Date) method test.
     *
     * @throws Exception
     */
    @Test(expected = ServiceException.class)
    public void testGetActualChartItems_SelectedPublishDateIsNull_Failure() throws Exception {
        Integer chartId = new Integer(1);
        Date selectedPublishDate = null;

        fixtureChartDetailService.getActualChartItems(chartId, selectedPublishDate);
    }

    /**
     * Run the List<String> findAllChannels() method test.
     *
     * @throws Exception
     */
    @Test
    public void testGetAllChannels_Success() throws Exception {

        List<String> allChannels = Arrays.asList("c1", "a2");

        Mockito.when(mockChartDetailRepository.findAllChannels()).thenReturn(allChannels);

        List<String> actualAllChannels = fixtureChartDetailService.getAllChannels();

        assertNotNull(actualAllChannels);
        assertEquals(allChannels, actualAllChannels);
    }

    private void assertChartDetailEquals(ChartDetail originalChartDetail, ChartDetail actualChartDetail) {
        assertEquals(originalChartDetail.getChannel(), actualChartDetail.getChannel());
        assertEquals(originalChartDetail.getChartId(), actualChartDetail.getChartId());
        assertEquals(originalChartDetail.getChgPosition(), actualChartDetail.getChgPosition());
        assertEquals(originalChartDetail.getInfo(), actualChartDetail.getInfo());
        assertEquals(originalChartDetail.getMediaId(), actualChartDetail.getMediaId());
        assertEquals(originalChartDetail.getPosition(), actualChartDetail.getPosition());
        assertEquals(originalChartDetail.getPrevPosition(), actualChartDetail.getPrevPosition());
        assertEquals(originalChartDetail.getPublishTimeMillis(), actualChartDetail.getPublishTimeMillis());
    }

    /**
     * Run the List<ChartDetail> findChartItemsByDate(Byte,Date) method test.
     *
     * @throws Exception
     */
    @Test
    public void testGetChartItemsByDate_Success() throws Exception {
        Integer chartId = new Integer(1);
        Date selectedPublishDate = new Date();

        final long selectedPublishTimeMillis = selectedPublishDate.getTime();
        List<ChartDetail> originalChartDetails = getChartDetails(selectedPublishTimeMillis);

        Mockito.when(mockChartDetailRepository.findChartItemsByDate(Mockito.eq(chartId), Mockito.eq(selectedPublishTimeMillis))).thenReturn(originalChartDetails);

        List<ChartDetail> actualChartDetails = fixtureChartDetailService.getChartItemsByDate(chartId, selectedPublishDate, true);

        assertNotNull(actualChartDetails);
        assertChartDetailsEquals(originalChartDetails, actualChartDetails);
    }

    private void assertChartDetailsEquals(List<ChartDetail> originalChartDetails, List<ChartDetail> actualChartDetails) {
        assertEquals(originalChartDetails.size(), actualChartDetails.size());

        actualChartDetails = new LinkedList<ChartDetail>(actualChartDetails);
        Collections.sort(actualChartDetails, new ChartDetailComparator());

        for (int i = 0; i < originalChartDetails.size(); i++) {
            assertChartDetailEquals(originalChartDetails.get(i), actualChartDetails.get(i));
        }

    }

    @Test(expected = Exception.class)
    public void testGetChartItemsByDate_ChartIdIsNull_Failure() throws Exception {
        Integer chartId = null;
        Date selectedPublishDate = new Date();

        fixtureChartDetailService.getChartItemsByDate(chartId, selectedPublishDate, true);
    }

    @Test(expected = Exception.class)
    public void testGetChartItemsByDate_SelectedPublishDateIsNull_Failure() throws Exception {
        Integer chartId = new Integer(1);
        Date selectedPublishDate = null;

        fixtureChartDetailService.getChartItemsByDate(chartId, selectedPublishDate, true);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSaveChartItems_Successful() throws Exception {
        Date selectedPublishDate = new Date();
        final long selectedPublishTimeMillis = selectedPublishDate.getTime();
        List<ChartDetail> originalChartDetails = getChartDetails(selectedPublishTimeMillis);
        List<ChartItemDto> chartItemDtos = ChartDetailsAsm.toChartItemDtos(originalChartDetails);

        doNothing().when(mockChartDetailRepository).delete(anyInt());
        when(mockChartDetailRepository.save(anyList())).thenReturn(Collections.<ChartDetail>emptyList());

        List<ChartDetail> actualChartDetails = fixtureChartDetailService.saveChartItems(chartItemDtos);

        assertNotNull(actualChartDetails);
        assertChartDetailsEquals(originalChartDetails, actualChartDetails);

        verify(mockChartDetailRepository, times(chartItemDtos.size())).delete(anyInt());
        verify(mockChartDetailRepository, times(1)).save(anyList());
    }

    /**
     * Run the List<ChartDetail> findChartItemsByDate(List<ChartDetailDto>) method test on delete exception.
     *
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @Test(expected = Exception.class)
    public void testSaveChartItems_OnDeleteException() throws Exception {
        Date selectedPublishDate = new Date();

        final long selectedPublishTimeMillis = selectedPublishDate.getTime();
        List<ChartDetail> originalChartDetails = getChartDetails(selectedPublishTimeMillis);
        List<ChartItemDto> chartItemDtos = ChartDetailsAsm.toChartItemDtos(originalChartDetails);

        doThrow(new Exception()).when(mockChartDetailRepository).deleteInBatch(anyList());
        doNothing().when(mockChartDetailRepository).save(anyList());

        fixtureChartDetailService.saveChartItems(chartItemDtos);
    }

    /**
     * Run the List<ChartDetail> findChartItemsByDate(List<ChartDetailDto>) method test on insert exception.
     *
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @Test(expected = Exception.class)
    public void testSaveChartItems_OnInsertException() throws Exception {
        Date selectedPublishDate = new Date();

        final long selectedPublishTimeMillis = selectedPublishDate.getTime();
        List<ChartDetail> originalChartDetails = getChartDetails(selectedPublishTimeMillis);
        List<ChartItemDto> chartItemDtos = ChartDetailsAsm.toChartItemDtos(originalChartDetails);

        doNothing().when(mockChartDetailRepository).deleteInBatch(anyList());
        doThrow(new Exception()).when(mockChartDetailRepository).save(anyList());

        fixtureChartDetailService.saveChartItems(chartItemDtos);
    }

    @Test
    public void testDeleteChartItems_Success() {

        Integer chartId = 1;
        long selectedPublishDateTime = 0;

        List<ChartDetail> originalChartDetails = getChartDetails(selectedPublishDateTime);

        Mockito.when(mockChartDetailRepository.findAllActualChartDetails(chartId, selectedPublishDateTime)).thenReturn(originalChartDetails);

        Mockito.doNothing().when(mockChartDetailRepository).delete(originalChartDetails);

        boolean actualSuccess = fixtureChartDetailService.deleteChartItems(chartId, selectedPublishDateTime);

        assertTrue(actualSuccess);
    }

    @Test(expected = RuntimeException.class)
    public void testDeleteChartItems_delete_Failure() {

        Integer chartId = 1;
        long selectedPublishDateTime = 0;

        List<ChartDetail> originalChartDetails = getChartDetails(selectedPublishDateTime);

        Mockito.when(mockChartDetailRepository.findAllActualChartDetails(chartId, selectedPublishDateTime)).thenReturn(originalChartDetails);
        Mockito.doThrow(new RuntimeException()).when(mockChartDetailRepository).delete(originalChartDetails);

        fixtureChartDetailService.deleteChartItems(chartId, selectedPublishDateTime);
    }

    @Test(expected = RuntimeException.class)
    public void testDeleteChartItems_getActualChartDetails_Failure() {

        Integer chartId = 1;
        long selectedPublishDateTime = 0;

        Mockito.doThrow(new RuntimeException()).when(mockChartDetailRepository).findAllActualChartDetails(chartId, selectedPublishDateTime);

        fixtureChartDetailService.deleteChartItems(chartId, selectedPublishDateTime);
    }

    @Test
    public void testUpdateChartItems_Success() throws ServiceCheckedException {

        Integer chartId = 1;
        long selectedPublishDateTime = 0;
        long newPublishDateTime = 0;

        int expectedUpdatedRowCount = 5;

        Mockito.when(mockChartDetailRepository.countChartDetail(chartId, selectedPublishDateTime)).thenReturn(0L);
        Mockito.when(mockChartDetailRepository.updateChartItems(newPublishDateTime, selectedPublishDateTime, chartId)).thenReturn(expectedUpdatedRowCount);

        int actualUpdatedRowCount = fixtureChartDetailService.updateChartItems(chartId, selectedPublishDateTime, newPublishDateTime);

        assertEquals(expectedUpdatedRowCount, actualUpdatedRowCount);

        Mockito.verify(mockChartDetailRepository).countChartDetail(chartId, selectedPublishDateTime);
        Mockito.verify(mockChartDetailRepository).updateChartItems(newPublishDateTime, selectedPublishDateTime, chartId);

    }

    @Test(expected = ServiceCheckedException.class)
    public void testUpdateChartItems_newPublishTimeAlreadyScheduled_Failure() throws ServiceCheckedException {

        Integer chartId = 1;
        long selectedPublishDateTime = 0;
        long newPublishDateTime = 0;

        int expectedUpdatedRowCount = 5;

        Mockito.when(mockChartDetailRepository.countChartDetail(chartId, selectedPublishDateTime)).thenReturn(1L);

        int actualUpdatedRowCount = fixtureChartDetailService.updateChartItems(chartId, selectedPublishDateTime, newPublishDateTime);

        assertEquals(expectedUpdatedRowCount, actualUpdatedRowCount);

        Mockito.verify(mockChartDetailRepository).countChartDetail(chartId, selectedPublishDateTime);
        Mockito.verify(mockChartDetailRepository, times(0)).updateChartItems(newPublishDateTime, selectedPublishDateTime, chartId);

    }

    @Test(expected = ServiceCheckedException.class)
    public void testUpdateChartItems_UpdatedRowCountIs0_Failure() throws ServiceCheckedException {

        Integer chartId = 1;
        long selectedPublishDateTime = 0;
        long newPublishDateTime = 0;

        int expectedUpdatedRowCount = 0;

        Mockito.when(mockChartDetailRepository.countChartDetail(chartId, selectedPublishDateTime)).thenReturn(0L);
        Mockito.when(mockChartDetailRepository.updateChartItems(newPublishDateTime, selectedPublishDateTime, chartId)).thenReturn(expectedUpdatedRowCount);

        int actualUpdatedRowCount = fixtureChartDetailService.updateChartItems(chartId, selectedPublishDateTime, newPublishDateTime);

        assertEquals(expectedUpdatedRowCount, actualUpdatedRowCount);

        Mockito.verify(mockChartDetailRepository).countChartDetail(chartId, selectedPublishDateTime);
        Mockito.verify(mockChartDetailRepository).updateChartItems(newPublishDateTime, selectedPublishDateTime, chartId);

    }

    @Test
    public void testgetLockedChartItemISRCs_ExistLastNearestItems_Success() throws ServiceCheckedException {

        Integer chartId = 1;
        Date selectedPublishDateTime = new Date();
        Date nearestPublishDateTime = new Date();
        List<Media> ids = Collections.singletonList(new Media());

        Mockito.when(mockChartDetailRepository.findNearestLatestPublishDate(eq(selectedPublishDateTime.getTime()), eq(chartId))).thenReturn(nearestPublishDateTime.getTime());
        Mockito.when(mockChartDetailRepository.findLockedChartItemByDate(eq(chartId), eq(nearestPublishDateTime.getTime()))).thenReturn(ids);

        List<Media> result = fixtureChartDetailService.getLockedChartItemISRCs(chartId, selectedPublishDateTime);

        assertNotNull(result);
        assertEquals(ids.size(), result.size());

        Mockito.verify(mockChartDetailRepository).findNearestLatestPublishDate(eq(selectedPublishDateTime.getTime()), eq(chartId));
        Mockito.verify(mockChartDetailRepository).findLockedChartItemByDate(eq(chartId), eq(nearestPublishDateTime.getTime()));

    }

    @Test
    public void testgetLockedChartItemISRCs_NotExistLastNearestItems_Success() throws ServiceCheckedException {

        Integer chartId = 1;
        Date selectedPublishDateTime = new Date();
        Date nearestPublishDateTime = new Date();
        List<Media> ids = Collections.singletonList(new Media());

        Mockito.when(mockChartDetailRepository.findNearestLatestPublishDate(eq(selectedPublishDateTime.getTime()), eq(chartId))).thenReturn(null);
        Mockito.when(mockChartDetailRepository.findLockedChartItemByDate(eq(chartId), eq(nearestPublishDateTime.getTime()))).thenReturn(ids);

        List<Media> result = fixtureChartDetailService.getLockedChartItemISRCs(chartId, selectedPublishDateTime);

        assertNotNull(result);
        assertEquals(0, result.size());

        Mockito.verify(mockChartDetailRepository).findNearestLatestPublishDate(eq(selectedPublishDateTime.getTime()), eq(chartId));
        Mockito.verify(mockChartDetailRepository, times(0)).findLockedChartItemByDate(eq(chartId), eq(nearestPublishDateTime.getTime()));

    }

    @Test(expected = ServiceException.class)
    public void testgetLockedChartItemISRCs_NullSelectedDate_Failure() throws ServiceCheckedException {

        Integer chartId = 1;
        Date selectedPublishDateTime = null;
        Date nearestPublishDateTime = new Date();
        List<Media> mediaList = Collections.singletonList(new Media());

        Mockito.when(mockChartDetailRepository.findLockedChartItemByDate(eq(chartId), eq(nearestPublishDateTime.getTime()))).thenReturn(mediaList);

        fixtureChartDetailService.getLockedChartItemISRCs(chartId, selectedPublishDateTime);
    }

    @Test(expected = ServiceException.class)
    public void testgetLockedChartItemISRCs_NullChartId_Failure() throws ServiceCheckedException {

        Integer chartId = null;
        Date selectedPublishDateTime = new Date();

        fixtureChartDetailService.getLockedChartItemISRCs(chartId, selectedPublishDateTime);
    }

    /**
     * Perform pre-test initialization.
     *
     * @throws Exception if the initialization fails for some reason
     */
    @Before
    public void setUp() throws Exception {
        fixtureChartDetailService = new ChartDetailService();
        mockChartDetailRepository = PowerMockito.mock(ChartDetailRepository.class);
        fixtureChartDetailService.setChartDetailRepository(mockChartDetailRepository);
        mediaRepository = PowerMockito.mock(MediaRepository.class);
        mockMediaService = PowerMockito.mock(MediaService.class);

        fixtureChartDetailService.setMediaRepository(mediaRepository);
    }

    private final class ChartDetailComparator implements Comparator<ChartDetail> {

        @Override
        public int compare(ChartDetail o1, ChartDetail o2) {
            return o1.getPosition() - o2.getPosition();
        }
    }
}