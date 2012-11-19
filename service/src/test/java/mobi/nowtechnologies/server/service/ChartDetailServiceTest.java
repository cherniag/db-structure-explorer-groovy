package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.assembler.ChartDetailsAsm;
import mobi.nowtechnologies.server.persistence.dao.ChartDetailDao;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.repository.ChartDetailRepository;
import mobi.nowtechnologies.server.service.exception.ServiceCheckedException;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.dto.admin.ChartItemDto;
import mobi.nowtechnologies.server.shared.dto.admin.ChartItemPositionDto;
import mobi.nowtechnologies.server.shared.dto.admin.MediaDto;
import mobi.nowtechnologies.server.shared.enums.ChgPosition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.*;

/**
 * The class <code>ChartDetailServiceTest</code> contains tests for the class
 * <code>{@link ChartDetailService}</code>.
 * 
 * @generatedBy CodePro at 07.08.12 17:19
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
@RunWith(PowerMockRunner.class)
public class ChartDetailServiceTest {
	private static ChartDetailService fixtureChartDetailService;
	private static ChartDetailRepository mockChartDetailRepository;
	private static DrmService mockDrmService;
	private List<ChartDetail> originalChartDetails;
	private List<Media> medias;
	private List<Drm> drms;
	private static EntityService mockEntityService;
	private static ChartDetailDao mockChartDetailDao;
	private static MediaService mockMediaService;

	private final class ChartDetailComparator implements Comparator<ChartDetail> {
		@Override
		public int compare(ChartDetail o1, ChartDetail o2) {
			return o1.getPosition() - o2.getPosition();
		}
	}

	/**
	 * Run the ChartDetailService() constructor test.
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test
	public void testChartDetailService_1() throws Exception {
		ChartDetailService result = new ChartDetailService();
		assertNotNull(result);
	}

	/**
	 * Run the List<ChartDetail>
	 * cloneChartItemsForSelectedPublishDateIfOnesDoesNotExist(Date,Byte) method
	 * test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test
	public void testCloneChartItemsForSelectedPublishDateIfOnesDoesNotExist_ThoseOfSelectedPublishDateDoesNotExistAndPreviousOnesExists_Success()
			throws Exception {
		Date choosedPublishDate = new Date();
		Byte chartId = new Byte((byte) 1);
		final long choosedPublishTimeMillis = choosedPublishDate.getTime();
		final long nearestLatestPublishDate = choosedPublishTimeMillis - 10000L;
		boolean minorUpdate=false;

		List<ChartDetail> originalChartDetails = getChartDetails(nearestLatestPublishDate);

		Mockito.when(mockChartDetailRepository.getCount(chartId, choosedPublishTimeMillis)).thenReturn(0L);
		Mockito.when(mockChartDetailRepository.findNearestLatestPublishDate(choosedPublishTimeMillis, chartId)).thenReturn(nearestLatestPublishDate);
		Mockito.when(mockChartDetailRepository.findByChartAndPublishTimeMillis(chartId, nearestLatestPublishDate)).thenReturn(originalChartDetails);
		Mockito.when(mockChartDetailRepository.save(Mockito.anyCollectionOf(ChartDetail.class))).thenAnswer(new Answer<List<ChartDetail>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<ChartDetail> answer(InvocationOnMock invocation) throws Throwable {
				List<ChartDetail> clonedChartDetails = (List<ChartDetail>) invocation.getArguments()[0];
				int i = 10;
				for (ChartDetail clonedChartDetail : clonedChartDetails) {
					clonedChartDetail.setI(i++);
				}
				return clonedChartDetails;
			}
		});

		List<ChartDetail> clonedChartDetails = fixtureChartDetailService.cloneChartItemsForSelectedPublishDateIfOnesDoesNotExist(choosedPublishDate, chartId, minorUpdate);

		assertNotNull(clonedChartDetails);
		assertEquals(originalChartDetails.size(), clonedChartDetails.size());

		clonedChartDetails = new LinkedList<ChartDetail>(clonedChartDetails);
		Collections.sort(clonedChartDetails, new ChartDetailComparator());

		for (int j = 0; j < clonedChartDetails.size(); j++) {
			ChartDetail clonedChartDetail = clonedChartDetails.get(j);
			ChartDetail originalChartDetail = originalChartDetails.get(j);

			assertEquals(originalChartDetail.getChannel(), clonedChartDetail.getChannel());
			assertEquals(originalChartDetail.getChart(), clonedChartDetail.getChart());
			assertEquals(ChgPosition.UNCHANGED, clonedChartDetail.getChgPosition());
			assertEquals(originalChartDetail.getInfo(), clonedChartDetail.getInfo());
			assertEquals(originalChartDetail.getMedia(), clonedChartDetail.getMedia());
			assertEquals(originalChartDetail.getPosition(), clonedChartDetail.getPosition());
			assertEquals(originalChartDetail.getPosition(), clonedChartDetail.getPrevPosition());
			assertEquals(choosedPublishTimeMillis, clonedChartDetail.getPublishTimeMillis());
		}

	}
	
	/**
	 * Run the List<ChartDetail>
	 * cloneChartItemsForSelectedPublishDateIfOnesDoesNotExist(Date,Byte) method
	 * test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test
	public void testCloneChartItemsForSelectedPublishDateIfOnesDoesNotExist_ThoseOfSelectedPublishDateDoesNotExistAndPreviousOnesExists_MinorUpdate_Success()
			throws Exception {
		Date choosedPublishDate = new Date();
		Byte chartId = new Byte((byte) 1);
		final long choosedPublishTimeMillis = choosedPublishDate.getTime();
		final long nearestLatestPublishDate = choosedPublishTimeMillis - 10000L;
		boolean minorUpdate=true;

		List<ChartDetail> originalChartDetails = getChartDetails(nearestLatestPublishDate);

		Mockito.when(mockChartDetailRepository.getCount(chartId, choosedPublishTimeMillis)).thenReturn(0L);
		Mockito.when(mockChartDetailRepository.findNearestLatestPublishDate(choosedPublishTimeMillis, chartId)).thenReturn(nearestLatestPublishDate);
		Mockito.when(mockChartDetailRepository.findByChartAndPublishTimeMillis(chartId, nearestLatestPublishDate)).thenReturn(originalChartDetails);
		Mockito.when(mockChartDetailRepository.save(Mockito.anyCollectionOf(ChartDetail.class))).thenAnswer(new Answer<List<ChartDetail>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<ChartDetail> answer(InvocationOnMock invocation) throws Throwable {
				List<ChartDetail> clonedChartDetails = (List<ChartDetail>) invocation.getArguments()[0];
				int i = 10;
				for (ChartDetail clonedChartDetail : clonedChartDetails) {
					clonedChartDetail.setI(i++);
				}
				return clonedChartDetails;
			}
		});

		List<ChartDetail> clonedChartDetails = fixtureChartDetailService.cloneChartItemsForSelectedPublishDateIfOnesDoesNotExist(choosedPublishDate, chartId, minorUpdate);

		assertNotNull(clonedChartDetails);
		assertEquals(originalChartDetails.size(), clonedChartDetails.size());

		clonedChartDetails = new LinkedList<ChartDetail>(clonedChartDetails);
		Collections.sort(clonedChartDetails, new ChartDetailComparator());

		for (int j = 0; j < clonedChartDetails.size(); j++) {
			ChartDetail clonedChartDetail = clonedChartDetails.get(j);
			ChartDetail originalChartDetail = originalChartDetails.get(j);

			assertEquals(originalChartDetail.getChannel(), clonedChartDetail.getChannel());
			assertEquals(originalChartDetail.getChart(), clonedChartDetail.getChart());
			assertEquals(originalChartDetail.getChgPosition(), clonedChartDetail.getChgPosition());
			assertEquals(originalChartDetail.getInfo(), clonedChartDetail.getInfo());
			assertEquals(originalChartDetail.getMedia(), clonedChartDetail.getMedia());
			assertEquals(originalChartDetail.getPosition(), clonedChartDetail.getPosition());
			assertEquals(originalChartDetail.getPrevPosition(), clonedChartDetail.getPrevPosition());
			assertEquals(choosedPublishTimeMillis, clonedChartDetail.getPublishTimeMillis());
		}

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
		chart.setI((byte) i);
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
		originalChartDetail.setVersion(i);
		return originalChartDetail;
	}

	private List<Media> getMedias() {
		if (medias == null) {
			medias = new LinkedList<Media>();

			for (int i = 0; i < 10; i++) {
				Media media = getMediaInstance(i);
				medias.add(media);
			}
			medias = Collections.unmodifiableList(medias);
		}
		return medias;
	}

	private Media getMediaInstance(int i) {
		MediaFile mediaFile = new MediaFile();
		mediaFile.setI(1);
		mediaFile.setFilename("Some filename");
		
		Artist artist = new Artist();
		artist.setI(1);
		artist.setName("Some artist name");
		
		Media media = new Media();
		media.setArtist(artist);
		media.setImageFileSmall(mediaFile);
		
		media.setI(i);
		return media;
	}

	private List<Drm> getDrms() {
		if (drms == null) {
			List<Media> medias = getMedias();
			drms = new LinkedList<Drm>();
			for (int i = 0; i < 5; i++) {
				Drm drm = new Drm();
				drm.setMedia(medias.get(i));
				drms.add(drm);
			}
			drms = Collections.unmodifiableList(drms);
		}
		return drms;
	}

	/**
	 * Run the List<ChartDetail>
	 * cloneChartItemsForSelectedPublishDateIfOnesDoesNotExist(Date,Byte) method
	 * test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test
	public void testCloneChartItemsForSelectedPublishDateIfOnesDoesNotExist_OnesForSelectedPublishDateAlreadyExists_Success() throws Exception {
		Date choosedPublishDate = new Date();
		Byte chartId = new Byte((byte) 1);
		boolean minorUpdate=false;
		final long choosedPublishTimeMillis = choosedPublishDate.getTime();

		Mockito.when(mockChartDetailRepository.getCount(chartId, choosedPublishTimeMillis)).thenReturn(1L);

		List<ChartDetail> clonedChartDetails = fixtureChartDetailService.cloneChartItemsForSelectedPublishDateIfOnesDoesNotExist(choosedPublishDate, chartId, minorUpdate);

		assertNotNull(clonedChartDetails);
		assertEquals(0, clonedChartDetails.size());
	}

	/**
	 * Run the List<ChartDetail>
	 * cloneChartItemsForSelectedPublishDateIfOnesDoesNotExist(Date,Byte) method
	 * test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test
	public void testCloneChartItemsForSelectedPublishDateIfOnesDoesNotExist_OnesForPrevoiusPublishDateDoesNotExist_Success() throws Exception {
		Date choosedPublishDate = new Date();
		Byte chartId = new Byte((byte) 1);
		final long choosedPublishTimeMillis = choosedPublishDate.getTime();
		final Long nearestLatestPublishDate = null;
		boolean minorUpdate=false;

		Mockito.when(mockChartDetailRepository.getCount(chartId, choosedPublishTimeMillis)).thenReturn(0L);
		Mockito.when(mockChartDetailRepository.findNearestLatestPublishDate(choosedPublishTimeMillis, chartId)).thenReturn(nearestLatestPublishDate);

		List<ChartDetail> clonedChartDetails = fixtureChartDetailService.cloneChartItemsForSelectedPublishDateIfOnesDoesNotExist(choosedPublishDate, chartId, minorUpdate);

		assertNotNull(clonedChartDetails);
		assertEquals(0, clonedChartDetails.size());
	}

	/**
	 * Run the List<ChartDetail>
	 * cloneChartItemsForSelectedPublishDateIfOnesDoesNotExist(Date,Byte) method
	 * test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test(expected = ServiceException.class)
	public void testCloneChartItemsForSelectedPublishDateIfOnesDoesNotExist_ChoosedPublishDateIsNull_Failure() throws Exception {
		Date choosedPublishDate = null;
		Byte chartId = new Byte((byte) 1);
		boolean minorUpdate=false;

		fixtureChartDetailService.cloneChartItemsForSelectedPublishDateIfOnesDoesNotExist(choosedPublishDate, chartId, minorUpdate);
	}

	/**
	 * Run the List<ChartDetail>
	 * cloneChartItemsForSelectedPublishDateIfOnesDoesNotExist(Date,Byte) method
	 * test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test(expected = ServiceException.class)
	public void testCloneChartItemsForSelectedPublishDateIfOnesDoesNotExist_ChartIdIsNull_Failure() throws Exception {
		Date choosedPublishDate = new Date();
		Byte chartId = null;
		boolean minorUpdate=false;

		fixtureChartDetailService.cloneChartItemsForSelectedPublishDateIfOnesDoesNotExist(choosedPublishDate, chartId, minorUpdate);
	}

	/**
	 * Run the boolean delete(Integer) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test
	public void testDelete_Success() throws Exception {
		Integer chartItemId = new Integer(1);

		boolean result = fixtureChartDetailService.delete(chartItemId);

		assertEquals(true, result);
	}

	/**
	 * Run the boolean delete(Integer) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test(expected = ServiceException.class)
	public void testDelete_ChartItemIdIsNull_Failure() throws Exception {
		Integer chartItemId = null;

		fixtureChartDetailService.delete(chartItemId);
	}

	/**
	 * Run the List<ChartDetail> findChartDetailTreeAndUpdateDrm(User,byte)
	 * method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test(expected = mobi.nowtechnologies.server.service.exception.ServiceException.class)
	public void testFindChartDetailTreeAndUpdateDrm_UserIsNull_Failure() throws Exception {
		User user = null;
		byte chartId = (byte) 1;

		fixtureChartDetailService.findChartDetailTreeAndUpdateDrm(user, chartId);
	}

	/**
	 * Run the List<ChartDetail> findChartDetailTreeAndUpdateDrm(User,byte)
	 * method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test(expected = mobi.nowtechnologies.server.service.exception.ServiceException.class)
	public void testFindChartDetailTreeAndUpdateDrm_DrmPolicyIsNull_Failure() throws Exception {

		User user = new User();
		user.setUserGroup(new UserGroup());
		byte chartId = (byte) 1;

		fixtureChartDetailService.findChartDetailTreeAndUpdateDrm(user, chartId);
	}

	/**
	 * Run the List<ChartDetail> findChartDetailTreeAndUpdateDrm(User,byte)
	 * method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test(expected = mobi.nowtechnologies.server.service.exception.ServiceException.class)
	public void testFindChartDetailTreeAndUpdateDrm_DrmTypeIsNull_Failure() throws Exception {

		User user = new User();
		DrmPolicy drmPolicy = new DrmPolicy();
		final UserGroup userGroup = new UserGroup();
		userGroup.setDrmPolicy(drmPolicy);
		user.setUserGroup(userGroup);
		byte chartId = (byte) 1;

		fixtureChartDetailService.findChartDetailTreeAndUpdateDrm(user, chartId);
	}

	/**
	 * Run the List<ChartDetail> findChartDetailTreeAndUpdateDrm(User,byte)
	 * method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test
	public void testFindChartDetailTreeAndUpdateDrm_Success() throws Exception {

		byte chartId = (byte) 1;
		Long nearestLatestPublishTimeMillis = new Date().getTime();
		int userId = 1;

		User user = new User();
		user.setId(userId);
		DrmType drmType = new DrmType();
		DrmPolicy drmPolicy = new DrmPolicy();
		drmPolicy.setDrmType(drmType);
		final UserGroup userGroup = new UserGroup();
		userGroup.setDrmPolicy(drmPolicy);
		user.setUserGroup(userGroup);

		List<ChartDetail> originalChartDetails = getChartDetails(nearestLatestPublishTimeMillis);
		List<Drm> drms = getDrms();

		Mockito.when(mockDrmService.findDrmAndDrmTypeTree(userId)).thenReturn(drms);
		Mockito.when(mockChartDetailRepository.findNearestLatestPublishDate(Mockito.anyLong(), Mockito.eq(chartId))).thenReturn(nearestLatestPublishTimeMillis);
		Mockito.when(mockChartDetailRepository.findChartDetailTreeForDrmUpdateByChartAndPublishTimeMillis(chartId, nearestLatestPublishTimeMillis)).thenReturn(
				originalChartDetails);

		List<ChartDetail> actualChartDetails = fixtureChartDetailService.findChartDetailTreeAndUpdateDrm(user, chartId);

		assertNotNull(actualChartDetails);
		assertEquals(originalChartDetails, actualChartDetails);

		actualChartDetails = new LinkedList<ChartDetail>(actualChartDetails);
		Collections.sort(actualChartDetails, new ChartDetailComparator());

		for (int i = 0; i < actualChartDetails.size(); i++) {
			ChartDetail actualChartDetail = actualChartDetails.get(i);
			Media actualMedia = actualChartDetail.getMedia();
			assertNotNull(actualMedia);
			assertEquals(originalChartDetails.get(i).getMedia(), actualMedia);
			List<Drm> actualDrms = actualMedia.getDrms();
			assertNotNull(actualDrms);
			assertEquals(1, actualDrms.size());
		}
	}

	/**
	 * Run the List<ChartDetail> findChartDetailTreeAndUpdateDrm(User,byte)
	 * method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFindChartDetailTreeAndUpdateDrm_DrmsIsEmpty_Success() throws Exception {

		byte chartId = (byte) 1;
		Long nearestLatestPublishTimeMillis = new Date().getTime();
		int userId = 1;

		User user = new User();
		user.setId(userId);
		DrmType drmType = new DrmType();
		DrmPolicy drmPolicy = new DrmPolicy();
		drmPolicy.setDrmType(drmType);
		final UserGroup userGroup = new UserGroup();
		userGroup.setDrmPolicy(drmPolicy);
		user.setUserGroup(userGroup);

		List<ChartDetail> originalChartDetails = getChartDetails(0L);
		List<Drm> drms = Collections.EMPTY_LIST;

		Mockito.when(mockDrmService.findDrmAndDrmTypeTree(userId)).thenReturn(drms);
		Mockito.when(mockChartDetailRepository.findNearestLatestPublishDate(Mockito.anyLong(), Mockito.eq(chartId))).thenReturn(nearestLatestPublishTimeMillis);
		Mockito.when(mockChartDetailRepository.findChartDetailTreeForDrmUpdateByChartAndPublishTimeMillis(chartId, nearestLatestPublishTimeMillis)).thenReturn(
				originalChartDetails);

		List<ChartDetail> actualChartDetails = fixtureChartDetailService.findChartDetailTreeAndUpdateDrm(user, chartId);

		assertNotNull(actualChartDetails);
		assertEquals(originalChartDetails, actualChartDetails);

		actualChartDetails = new LinkedList<ChartDetail>(actualChartDetails);
		Collections.sort(actualChartDetails, new ChartDetailComparator());

		for (int i = 0; i < actualChartDetails.size(); i++) {
			ChartDetail actualChartDetail = actualChartDetails.get(i);
			Media actualMedia = actualChartDetail.getMedia();
			assertNotNull(actualMedia);
			assertEquals(originalChartDetails.get(i).getMedia(), actualMedia);
			List<Drm> actualDrms = actualMedia.getDrms();
			assertNotNull(actualDrms);
			assertEquals(1, actualDrms.size());
		}
	}

	/**
	 * Run the List<ChartDetail> findChartDetailTreeAndUpdateDrm(User,byte)
	 * method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test
	public void testFindChartDetailTreeAndUpdateDrm_NearestLatestPublishTimeMillisIsNull_Success() throws Exception {

		byte chartId = (byte) 1;
		Long nearestLatestPublishTimeMillis = null;
		int userId = 1;

		User user = new User();
		user.setId(userId);
		DrmType drmType = new DrmType();
		DrmPolicy drmPolicy = new DrmPolicy();
		drmPolicy.setDrmType(drmType);
		final UserGroup userGroup = new UserGroup();
		userGroup.setDrmPolicy(drmPolicy);
		user.setUserGroup(userGroup);

		List<ChartDetail> originalChartDetails = getChartDetails(0L);
		List<Drm> drms = getDrms();

		Mockito.when(mockDrmService.findDrmAndDrmTypeTree(userId)).thenReturn(drms);
		Mockito.when(mockChartDetailRepository.findNearestLatestPublishDate(Mockito.anyLong(), Mockito.eq(chartId))).thenReturn(nearestLatestPublishTimeMillis);
		Mockito.when(mockChartDetailRepository.findChartDetailTreeForDrmUpdateByChartAndPublishTimeMillis(chartId, nearestLatestPublishTimeMillis)).thenReturn(
				originalChartDetails);

		List<ChartDetail> actualChartDetails = fixtureChartDetailService.findChartDetailTreeAndUpdateDrm(user, chartId);

		assertNotNull(actualChartDetails);
		assertTrue(actualChartDetails.isEmpty());
	}

	/**
	 * Run the List<ChartDetail> getActualChartItems(Byte,Date) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test
	public void testGetActualChartItems_Success() throws Exception {
		Byte chartId = new Byte((byte) 1);
		Date selectedPublishDate = new Date();
		Long nearestLatestPublishTimeMillis = new Date().getTime();

		List<ChartDetail> originalChartDetails = getChartDetails(0L);

		Mockito.when(mockChartDetailRepository.findNearestLatestPublishDate(Mockito.anyLong(), Mockito.eq(chartId))).thenReturn(nearestLatestPublishTimeMillis);
		Mockito.when(mockChartDetailRepository.getActualChartDetails(Mockito.eq(chartId), Mockito.eq(nearestLatestPublishTimeMillis))).thenReturn(
				originalChartDetails);

		List<ChartDetail> actualChartDetails = fixtureChartDetailService.getActualChartItems(chartId, selectedPublishDate);

		assertNotNull(actualChartDetails);
		assertEquals(originalChartDetails, actualChartDetails);

	}

	/**
	 * Run the List<ChartDetail> getActualChartItems(Byte,Date) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test
	public void testGetActualChartItems_NearestLatestPublishTimeMillisIsNull_Success() throws Exception {
		Byte chartId = new Byte((byte) 1);
		Date selectedPublishDate = new Date();
		Long nearestLatestPublishTimeMillis = null;

		Mockito.when(mockChartDetailRepository.findNearestLatestPublishDate(Mockito.anyLong(), Mockito.eq(chartId))).thenReturn(nearestLatestPublishTimeMillis);

		List<ChartDetail> actualChartDetails = fixtureChartDetailService.getActualChartItems(chartId, selectedPublishDate);

		assertNotNull(actualChartDetails);
		assertEquals(0, actualChartDetails.size());
	}

	/**
	 * Run the List<ChartDetail> getActualChartItems(Byte,Date) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test(expected = ServiceException.class)
	public void testGetActualChartItems_ChartIdIsNull_Failure() throws Exception {
		Byte chartId = null;
		Date selectedPublishDate = new Date();

		fixtureChartDetailService.getActualChartItems(chartId, selectedPublishDate);
	}

	/**
	 * Run the List<ChartDetail> getActualChartItems(Byte,Date) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test(expected = ServiceException.class)
	public void testGetActualChartItems_SelectedPublishDateIsNull_Failure() throws Exception {
		Byte chartId = new Byte((byte) 1);
		Date selectedPublishDate = null;

		fixtureChartDetailService.getActualChartItems(chartId, selectedPublishDate);
	}

	/**
	 * Run the List<String> getAllChannels() method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test
	public void testGetAllChannels_Success() throws Exception {

		List<String> allChannels = Arrays.asList("c1", "a2");

		Mockito.when(mockChartDetailRepository.getAllChannels()).thenReturn(allChannels);

		List<String> actualAllChannels = fixtureChartDetailService.getAllChannels();

		assertNotNull(actualAllChannels);
		assertEquals(allChannels, actualAllChannels);
	}

	/**
	 * Run the List<Long> getAllPublishTimeMillis(Byte) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test
	public void testGetAllPublishTimeMillis_Success() throws Exception {
		Byte chartId = new Byte((byte) 1);

		List<Long> allPublishTimeMillis = Arrays.asList(1L, 666L, 999L);

		Mockito.when(mockChartDetailRepository.getAllPublishTimeMillis(Mockito.eq(chartId))).thenReturn(allPublishTimeMillis);

		List<Long> actualAllPublishTimeMillis = fixtureChartDetailService.getAllPublishTimeMillis(chartId);

		assertNotNull(actualAllPublishTimeMillis);
		assertEquals(allPublishTimeMillis, actualAllPublishTimeMillis);
	}

	/**
	 * Run the List<Long> getAllPublishTimeMillis(Byte) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test(expected = ServiceException.class)
	public void testGetAllPublishTimeMillis_ChartIdIsNull_Failure() throws Exception {
		Byte chartId = null;

		fixtureChartDetailService.getAllPublishTimeMillis(chartId);
	}

	/**
	 * Run the ChartDetail getChartItemById(Integer) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test
	public void testGetChartItemById_Success() throws Exception {
		Integer chartItemId = new Integer(1);

		final Chart chart = getChartInstance(1);
		final Media media = getMediaInstance(1);

		ChartDetail originalChartDetail = getChartDetailInstance(98L, 1, media, chart);

		Mockito.when(mockChartDetailRepository.findById(Mockito.eq(chartItemId))).thenReturn(originalChartDetail);

		ChartDetail actualChartDetail = fixtureChartDetailService.getChartItemById(chartItemId);

		assertNotNull(actualChartDetail);
		assertEquals(originalChartDetail, actualChartDetail);

		assertChartDetailEquals(originalChartDetail, actualChartDetail);
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
	 * Run the ChartDetail getChartItemById(Integer) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test(expected = ServiceException.class)
	public void testGetChartItemById_ChartItemIdIsNull_Failure() throws Exception {
		Integer chartItemId = null;

		fixtureChartDetailService.getChartItemById(chartItemId);
	}

	/**
	 * Run the List<ChartDetail> getChartItemsByDate(Byte,Date) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test
	public void testGetChartItemsByDate_Success() throws Exception {
		Byte chartId = new Byte((byte) 1);
		Date selectedPublishDate = new Date();

		final long selectedPublishTimeMillis = selectedPublishDate.getTime();
		List<ChartDetail> originalChartDetails = getChartDetails(selectedPublishTimeMillis);

		Mockito.when(mockChartDetailRepository.getChartItemsByDate(Mockito.eq(chartId), Mockito.eq(selectedPublishTimeMillis)))
				.thenReturn(originalChartDetails);

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

	/**
	 * Run the List<ChartDetail> getChartItemsByDate(Byte,Date) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test(expected = ServiceException.class)
	public void testGetChartItemsByDate_ChartIdIsNull_Failure() throws Exception {
		Byte chartId = null;
		Date selectedPublishDate = new Date();

		fixtureChartDetailService.getChartItemsByDate(chartId, selectedPublishDate, true);
	}

	/**
	 * Run the List<ChartDetail> getChartItemsByDate(Byte,Date) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test(expected = ServiceException.class)
	public void testGetChartItemsByDate_SelectedPublishDateIsNull_Failure() throws Exception {
		Byte chartId = new Byte((byte) 1);
		Date selectedPublishDate = null;

		fixtureChartDetailService.getChartItemsByDate(chartId, selectedPublishDate, true);
	}
	
	/**
	 * Run the List<ChartDetail> getChartItemsByDate(List<ChartDetailDto>) method test successfully.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
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
	 * Run the List<ChartDetail> getChartItemsByDate(List<ChartDetailDto>) method test on delete exception.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
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
	 * Run the List<ChartDetail> getChartItemsByDate(List<ChartDetailDto>) method test on insert exception.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
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

	/**
	 * Run the ChgPosition getChgPosition(byte,byte) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test
	public void testGetChgPosition_PositionAndPrevPositionAreEqualAndBonus_Sucess() throws Exception {
		byte position = (byte) 1;
		byte prevPosition = (byte) 1;
		boolean isBonus = true;

		ChgPosition result = fixtureChartDetailService.getChgPosition(position, prevPosition,isBonus);

		assertNotNull(result);
		assertEquals(ChgPosition.NONE, result);
	}

	/**
	 * Run the ChgPosition getChgPosition(byte,byte) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test
	public void testGetChgPosition_PositionMoreThanPrevPositionAndBonus_Success() throws Exception {

		byte position = (byte) 10;
		byte prevPosition = (byte) 1;
		boolean isBonus = true;

		ChgPosition result = fixtureChartDetailService.getChgPosition(position, prevPosition,isBonus);

		assertNotNull(result);
		assertEquals(ChgPosition.NONE, result);
	}

	/**
	 * Run the ChgPosition getChgPosition(byte,byte) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test
	public void testGetChgPosition_PrevPositionMoreThanPositionAndBonus_Success() throws Exception {
		byte position = (byte) 1;
		byte prevPosition = (byte) 10;
		boolean isBonus = true;

		ChgPosition result = fixtureChartDetailService.getChgPosition(position, prevPosition,isBonus);

		assertNotNull(result);
		assertEquals(ChgPosition.NONE, result);
	}
	
	/**
	 * Run the ChgPosition getChgPosition(byte,byte) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test
	public void testGetChgPosition_PrevPositionIsZeroAndBonus_Success() throws Exception {
		byte position = (byte) 1;
		byte prevPosition = (byte) 0;
		boolean isBonus = true;

		ChgPosition result = fixtureChartDetailService.getChgPosition(position, prevPosition,isBonus);

		assertNotNull(result);
		assertEquals(ChgPosition.NONE, result);
	}
	
	/**
	 * Run the ChgPosition getChgPosition(byte,byte) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test
	public void testGetChgPosition_PositionAndPrevPositionAreEqualAndNotABonus_Success() throws Exception {
		byte position = (byte) 1;
		byte prevPosition = (byte) 1;
		boolean isBonus = false;

		ChgPosition result = fixtureChartDetailService.getChgPosition(position, prevPosition, isBonus);

		assertNotNull(result);
		assertEquals(ChgPosition.UNCHANGED, result);
	}

	/**
	 * Run the ChgPosition getChgPosition(byte,byte) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test
	public void testGetChgPosition_PositionMoreThanPrevPositionAndNotABonus_Success() throws Exception {

		byte position = (byte) 10;
		byte prevPosition = (byte) 1;
		boolean isBonus = false;

		ChgPosition result = fixtureChartDetailService.getChgPosition(position, prevPosition, isBonus);

		assertNotNull(result);
		assertEquals(ChgPosition.DOWN, result);
	}

	/**
	 * Run the ChgPosition getChgPosition(byte,byte) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test
	public void testGetChgPosition_PrevPositionMoreThanPositionAndNotABonus_Success() throws Exception {
		byte position = (byte) 1;
		byte prevPosition = (byte) 10;
		boolean isBonus = false;

		ChgPosition result = fixtureChartDetailService.getChgPosition(position, prevPosition, isBonus);

		assertNotNull(result);
		assertEquals(ChgPosition.UP, result);
	}
	
	/**
	 * Run the ChgPosition getChgPosition(byte,byte) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test
	public void testGetChgPosition_PrevPositionIsZeroAndNotABonus_Success() throws Exception {
		byte position = (byte) 1;
		byte prevPosition = (byte) 0;
		boolean isBonus = false;

		ChgPosition result = fixtureChartDetailService.getChgPosition(position, prevPosition,isBonus);

		assertNotNull(result);
		assertEquals(ChgPosition.UNCHANGED, result);
	}
	
	/**
	 * Run the ChgPosition getChgPosition(byte,byte) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testGetChgPosition_PositionIsLessThanZero_Failure() throws Exception {
		byte position = (byte) -1;
		byte prevPosition = (byte) 0;
		boolean isBonus = true;

		fixtureChartDetailService.getChgPosition(position, prevPosition,isBonus);

	}
	
	/**
	 * Run the ChgPosition getChgPosition(byte,byte) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testGetChgPosition_PrevPositionIsLessThanZero_Failure() throws Exception {
		byte position = (byte) 1;
		byte prevPosition = (byte) -1;
		boolean isBonus = true;

		fixtureChartDetailService.getChgPosition(position, prevPosition,isBonus);

	}

	/**
	 * Run the boolean isTrackCanBeBoughtAccordingToLicense(String) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test
	public void testIsTrackCanBeBoughtAccordingToLicense_Success() throws Exception {
		String isrc = "";

		boolean isTrackCanBeBoughtAccordingToLicense = true;
		Mockito.when(mockChartDetailDao.isTrackCanBeBoughtAccordingToLicense(isrc)).thenReturn(isTrackCanBeBoughtAccordingToLicense);
		boolean result = fixtureChartDetailService.isTrackCanBeBoughtAccordingToLicense(isrc);

		assertTrue(result);
	}

	/**
	 * Run the boolean isTrackCanBeBoughtAccordingToLicense(String) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test(expected = ServiceException.class)
	public void testIsTrackCanBeBoughtAccordingToLicense_IsrcIsNull_Failure() throws Exception {
		String isrc = null;

		fixtureChartDetailService.isTrackCanBeBoughtAccordingToLicense(isrc);
	}

	/**
	 * Run the ChartDetail saveChartItem(ChartItemDto,Chart) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test
	public void testNotBonusSaveChartItem_Success() throws Exception {
		final int mediaId = 1;
		byte chartId = (byte) mediaId;
		final Date choosedPublishDate = new Date();
		Byte position = (byte) 3;
		final Integer chartDetailId = 55;

		long choosedPublishTimeMillis = choosedPublishDate.getTime();

		ChartItemDto chartItemDto = new ChartItemDto();
		final MediaDto mediaDto = new MediaDto();
		mediaDto.setId(mediaId);
		chartItemDto.setMediaDto(mediaDto);
		chartItemDto.setPublishTime(choosedPublishDate);
		Chart chart = new Chart();
		chart.setI(chartId);
		chartItemDto.setChartId(chartId);
		
		Media originalMedia = getMediaInstance(mediaId);

		Mockito.when(mockChartDetailRepository.findMaxPosition(Mockito.eq(chart), Mockito.eq(choosedPublishTimeMillis))).thenReturn(position);
		Mockito.when(mockMediaService.findById(Mockito.eq(mediaId))).thenReturn(originalMedia);
		Mockito.when(mockChartDetailRepository.save(Mockito.any(ChartDetail.class))).thenAnswer(new Answer<ChartDetail>() {

			@Override
			public ChartDetail answer(InvocationOnMock invocation) throws Throwable {
				ChartDetail chartDetail = (ChartDetail) invocation.getArguments()[0];
				chartDetail.setI(chartDetailId);
				return chartDetail;
			}
		});

		ChartDetail actualChartDetail = fixtureChartDetailService.saveChartItem(chartItemDto, chart);

		assertNotNull(actualChartDetail);
		assertChartItemDtoWithChartDetail((byte) (position+1), chartDetailId, chartItemDto, actualChartDetail, ChgPosition.UNCHANGED, (byte) 0);
	}
	
	
	/**
	 * Run the ChartDetail saveChartItem(ChartItemDto,Chart) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test(expected=ServiceCheckedException.class)
	public void testSaveChartItem_DataIntegrityViolationException_Failure() throws Exception {
		final int mediaId = 1;
		byte chartId = (byte) mediaId;
		final Date choosedPublishDate = new Date();
		Byte position = (byte) 3;

		long choosedPublishTimeMillis = choosedPublishDate.getTime();

		ChartItemDto chartItemDto = new ChartItemDto();
		final MediaDto mediaDto = new MediaDto();
		mediaDto.setId(mediaId);
		chartItemDto.setMediaDto(mediaDto);
		chartItemDto.setPublishTime(choosedPublishDate);
		Chart chart = new Chart();
		chart.setI(chartId);
		chartItemDto.setChartId(chartId);
		
		Media originalMedia = getMediaInstance(mediaId);

		Mockito.when(mockChartDetailRepository.findMaxPosition(Mockito.eq(chart), Mockito.eq(choosedPublishTimeMillis))).thenReturn(position);
		Mockito.when(mockMediaService.findById(Mockito.eq(mediaId))).thenReturn(originalMedia);
		Mockito.when(mockChartDetailRepository.save(Mockito.any(ChartDetail.class))).thenThrow(new DataIntegrityViolationException(null));

		fixtureChartDetailService.saveChartItem(chartItemDto, chart);
	}

	private void assertChartItemDtoWithChartDetail(byte expectedPosition, final Integer chartDetailId, ChartItemDto chartItemDto, ChartDetail actualChartDetail, final ChgPosition chgPosition, byte expectedPrevPosition) {
		assertEquals(chartItemDto.getChannel(), actualChartDetail.getChannel());
		assertEquals(chartItemDto.getChartId(), actualChartDetail.getChartId());
		assertEquals(chgPosition, actualChartDetail.getChgPosition());
		assertEquals(chartItemDto.getInfo(), actualChartDetail.getInfo());
		assertEquals(chartItemDto.getMediaDto().getId().intValue(), actualChartDetail.getMediaId());
		assertEquals(expectedPosition, actualChartDetail.getPosition());
		assertEquals(expectedPrevPosition, actualChartDetail.getPrevPosition());
		assertEquals(chartItemDto.getPublishTime().getTime(), actualChartDetail.getPublishTimeMillis());
		assertEquals(chartDetailId, actualChartDetail.getI());
	}
	
	/**
	 * Run the ChartDetail saveChartItem(ChartItemDto,Chart) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test
	public void testSaveNotBonusChartItem_PositionIsNull_Success() throws Exception {
		final int mediaId = 1;
		byte chartId = (byte) mediaId;
		final Date choosedPublishDate = new Date();
		Byte position = null;
		final Integer chartDetailId = 55;

		long choosedPublishTimeMillis = choosedPublishDate.getTime();

		ChartItemDto chartItemDto = new ChartItemDto();
		final MediaDto mediaDto = new MediaDto();
		mediaDto.setId(mediaId);
		chartItemDto.setMediaDto(mediaDto);
		chartItemDto.setPublishTime(choosedPublishDate);
		Chart chart = new Chart();
		chart.setI(chartId);
		chartItemDto.setChartId(chartId);
		
		Media originalMedia = getMediaInstance(mediaId);

		Mockito.when(mockChartDetailRepository.findMaxPosition(Mockito.eq(chart), Mockito.eq(choosedPublishTimeMillis))).thenReturn(position);
		Mockito.when(mockMediaService.findById(Mockito.eq(mediaId))).thenReturn(originalMedia);
		Mockito.when(mockChartDetailRepository.save(Mockito.any(ChartDetail.class))).thenAnswer(new Answer<ChartDetail>() {

			@Override
			public ChartDetail answer(InvocationOnMock invocation) throws Throwable {
				ChartDetail chartDetail = (ChartDetail) invocation.getArguments()[0];
				chartDetail.setI(chartDetailId);
				return chartDetail;
			}
		});

		ChartDetail actualChartDetail = fixtureChartDetailService.saveChartItem(chartItemDto, chart);

		assertNotNull(actualChartDetail);
		
		assertChartItemDtoWithChartDetail((byte)1, chartDetailId, chartItemDto, actualChartDetail, ChgPosition.UNCHANGED, (byte) 0);
	}


	/**
	 * Run the ChartDetail saveChartItem(ChartItemDto,Chart) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test(expected = ServiceException.class)
	public void testSaveChartItem_ChartItemDtoIsNull_Failure() throws Exception {

		ChartItemDto chartItemDto = null;
		Chart chart = new Chart();

		fixtureChartDetailService.saveChartItem(chartItemDto, chart);
	}

	/**
	 * Run the ChartDetail saveChartItem(ChartItemDto,Chart) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test(expected = ServiceException.class)
	public void testSaveChartItem_ChartIsNull_Failure() throws Exception {

		ChartItemDto chartItemDto = new ChartItemDto();
		Chart chart = null;

		fixtureChartDetailService.saveChartItem(chartItemDto, chart);
	}

	/**
	 * Run the ChartDetail updateChartItem(ChartItemDto,Chart) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test
	public void testUpdateChartItem_Success() throws Exception {
		final int mediaId = 1;
		byte chartId = (byte) mediaId;
		final Date choosedPublishDate = new Date();
		Byte position = (byte) 3;
		byte prevPosition =(byte)2;
		final Integer chartDetailId = 55;

		long choosedPublishTimeMillis = choosedPublishDate.getTime();

		final ChgPosition chgPosition = ChgPosition.DOWN;
		ChartItemDto chartItemDto = new ChartItemDto();
		final MediaDto mediaDto = new MediaDto();
		mediaDto.setId(mediaId);
		chartItemDto.setMediaDto(mediaDto);
		chartItemDto.setPublishTime(choosedPublishDate);
		Chart chart = new Chart();
		chart.setI(chartId);
		chartItemDto.setChartId(chartId);
		chartItemDto.setPosition(position);
		chartItemDto.setPrevPosition(prevPosition);
		chartItemDto.setId(chartDetailId);
		chartItemDto.setChgPosition(chgPosition);
		
		Media originalMedia = getMediaInstance(mediaId);
		
		ChartDetail origChartDetail = getChartDetailInstance(choosedPublishTimeMillis, chartDetailId, originalMedia, chart);

		Mockito.when(mockChartDetailRepository.findOne(Mockito.eq(chartDetailId))).thenReturn(origChartDetail);
		Mockito.when(mockChartDetailRepository.save(Mockito.any(ChartDetail.class))).thenAnswer(new Answer<ChartDetail>() {

			@Override
			public ChartDetail answer(InvocationOnMock invocation) throws Throwable {
				ChartDetail chartDetail = (ChartDetail) invocation.getArguments()[0];
				return chartDetail;
			}
		});

		ChartDetail actualChartDetail = fixtureChartDetailService.updateChartItem(chartItemDto, chart);

		assertNotNull(actualChartDetail);
		assertChartItemDtoWithChartDetail(position, chartDetailId, chartItemDto, actualChartDetail, chgPosition, prevPosition);
	}
	
	/**
	 * Run the ChartDetail updateChartItem(ChartItemDto,Chart) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test(expected=ServiceException.class)
	public void testUpdateChartItem_ChartItemDtoIsNull_Failure() throws Exception {
		ChartItemDto chartItemDto = null;
		Chart chart = new Chart();

		fixtureChartDetailService.updateChartItem(chartItemDto, chart);
	}
	
	/**
	 * Run the ChartDetail updateChartItem(ChartItemDto,Chart) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test(expected=ServiceException.class)
	public void testUpdateChartItem_ChartIsNull_Failure() throws Exception {
		ChartItemDto chartItemDto = new ChartItemDto();
		Chart chart = null;

		fixtureChartDetailService.updateChartItem(chartItemDto, chart);
	}

	/**
	 * Run the List<ChartDetail> updateChartItemsPositions(ChartItemPositionDto)
	 * method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test
	public void testUpdateChartItemsPositions_Success() throws Exception {
		List<ChartDetail> originalChartDetails = getChartDetails(0L);

		Map<Integer, Byte> idPositionMap = new HashMap<Integer, Byte>();

		byte j = (byte) 80;
		for (ChartDetail originalChartDetail : originalChartDetails) {
			idPositionMap.put(originalChartDetail.getI(), j++);
		}

		ChartItemPositionDto chartItemPositionDto = new ChartItemPositionDto();
		chartItemPositionDto.setPositionMap(idPositionMap);

		Mockito.when(mockChartDetailRepository.getByIds(idPositionMap.keySet())).thenReturn(originalChartDetails);
		
		List<ChartDetail> actualChartDetails = fixtureChartDetailService.updateChartItemsPositions(chartItemPositionDto);

		assertNotNull(actualChartDetails);
		assertEquals(originalChartDetails.size(), actualChartDetails.size());

		actualChartDetails = new LinkedList<ChartDetail>(actualChartDetails);
		Collections.sort(actualChartDetails, new ChartDetailComparator());

		for (int i = 0; i < originalChartDetails.size(); i++) {
			ChartDetail originalChartDetail = originalChartDetails.get(i);
			ChartDetail actualChartDetail = actualChartDetails.get(i);
			
			assertEquals(originalChartDetail.getChannel(), actualChartDetail.getChannel());
			assertEquals(originalChartDetail.getChart(), actualChartDetail.getChart());
			assertEquals(ChgPosition.NONE, actualChartDetail.getChgPosition());
			assertEquals(originalChartDetail.getInfo(), actualChartDetail.getInfo());
			assertEquals(originalChartDetail.getMedia(), actualChartDetail.getMedia());
			assertEquals(idPositionMap.get(originalChartDetail.getI()).byteValue(), actualChartDetail.getPosition());
			assertEquals(originalChartDetail.getPrevPosition(), actualChartDetail.getPrevPosition());
			assertEquals(originalChartDetail.getPublishTimeMillis(), actualChartDetail.getPublishTimeMillis());
		}
	}

	/**
	 * Run the List<ChartDetail> updateChartItemsPositions(ChartItemPositionDto)
	 * method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Test(expected=ServiceException.class)
	public void testUpdateChartItemsPositions_ChartItemPositionDtoIsNull_Failure() throws Exception {
		ChartItemPositionDto chartItemPositionDto = null;

		fixtureChartDetailService.updateChartItemsPositions(chartItemPositionDto);
	}

	/**
	 * Run the List<ChartDetail> updateChartItemsPositions(ChartItemPositionDto)
	 * method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testUpdateChartItemsPositions_IdPositionMapIsEmpty_Success() throws Exception {
		Map<Integer, Byte> idPositionMap = Collections.EMPTY_MAP;

		ChartItemPositionDto chartItemPositionDto = new ChartItemPositionDto();
		chartItemPositionDto.setPositionMap(idPositionMap);

		Mockito.when(mockChartDetailRepository.getByIds(idPositionMap.keySet())).thenReturn(Collections.EMPTY_LIST);
		
		List<ChartDetail> actualChartDetails = fixtureChartDetailService.updateChartItemsPositions(chartItemPositionDto);

		assertNotNull(actualChartDetails);
		assertTrue(actualChartDetails.isEmpty());
	}
	
	@Test
	public void testDeleteChartItems_Success() {
		
		Byte chartId =1;
		long selectedPublishDateTime=0;
		
		List<ChartDetail> originalChartDetails = getChartDetails(selectedPublishDateTime);
		
		Mockito.when(mockChartDetailRepository.getActualChartDetails(chartId, selectedPublishDateTime)).thenReturn(originalChartDetails);
		
		Mockito.doNothing().when(mockChartDetailRepository).delete(originalChartDetails);
						
		boolean actualSuccess = fixtureChartDetailService.deleteChartItems(chartId, selectedPublishDateTime);
		
		assertTrue(actualSuccess);
	}
	
	@Test(expected=RuntimeException.class)
	public void testDeleteChartItems_delete_Failure() {
		
		Byte chartId = 1;
		long selectedPublishDateTime=0;
		
		List<ChartDetail> originalChartDetails = getChartDetails(selectedPublishDateTime);
		
		Mockito.when(mockChartDetailRepository.getActualChartDetails(chartId, selectedPublishDateTime)).thenReturn(originalChartDetails);
		Mockito.doThrow(new RuntimeException()).when(mockChartDetailRepository).delete(originalChartDetails);
						
		fixtureChartDetailService.deleteChartItems(chartId, selectedPublishDateTime);
	}
	
	@Test(expected=RuntimeException.class)
	public void testDeleteChartItems_getActualChartDetails_Failure() {
		
		Byte chartId = 1;
		long selectedPublishDateTime=0;
		
		Mockito.doThrow(new RuntimeException()).when(mockChartDetailRepository).getActualChartDetails(chartId, selectedPublishDateTime);
						
		fixtureChartDetailService.deleteChartItems(chartId, selectedPublishDateTime);
	}
	
	@Test
	public void testUpdateChartItems_Success() throws ServiceCheckedException{
		
		Byte chartId = 1;
		long selectedPublishDateTime=0;
		long newPublishDateTime=0;
		
		int expectedUpdatedRowCount=5;
		
		Mockito.when(mockChartDetailRepository.getCount(chartId, selectedPublishDateTime)).thenReturn(0L);
		Mockito.when(mockChartDetailRepository.updateChartItems(newPublishDateTime, selectedPublishDateTime, chartId)).thenReturn(expectedUpdatedRowCount);
	
		int actualUpdatedRowCount = fixtureChartDetailService.updateChartItems(chartId, selectedPublishDateTime, newPublishDateTime);
		
		assertEquals(expectedUpdatedRowCount, actualUpdatedRowCount);
		
		Mockito.verify(mockChartDetailRepository).getCount(chartId, selectedPublishDateTime);
		Mockito.verify(mockChartDetailRepository).updateChartItems(newPublishDateTime, selectedPublishDateTime, chartId);
		
	}
	
	@Test(expected=ServiceCheckedException.class)
	public void testUpdateChartItems_newPublishTimeAlreadyScheduled_Failure() throws ServiceCheckedException {
		
		Byte chartId = 1;
		long selectedPublishDateTime=0;
		long newPublishDateTime=0;
		
		int expectedUpdatedRowCount=5;
		
		Mockito.when(mockChartDetailRepository.getCount(chartId, selectedPublishDateTime)).thenReturn(1L);
	
		int actualUpdatedRowCount = fixtureChartDetailService.updateChartItems(chartId, selectedPublishDateTime, newPublishDateTime);
		
		assertEquals(expectedUpdatedRowCount, actualUpdatedRowCount);
		
		Mockito.verify(mockChartDetailRepository).getCount(chartId, selectedPublishDateTime);
		Mockito.verify(mockChartDetailRepository, times(0)).updateChartItems(newPublishDateTime, selectedPublishDateTime, chartId);
		
	}
	
	@Test(expected=ServiceCheckedException.class)
	public void testUpdateChartItems_UpdatedRowCountIs0_Failure() throws ServiceCheckedException {
		
		Byte chartId = 1;
		long selectedPublishDateTime=0;
		long newPublishDateTime=0;
		
		int expectedUpdatedRowCount=0;
		
		Mockito.when(mockChartDetailRepository.getCount(chartId, selectedPublishDateTime)).thenReturn(0L);
		Mockito.when(mockChartDetailRepository.updateChartItems(newPublishDateTime, selectedPublishDateTime, chartId)).thenReturn(expectedUpdatedRowCount);
	
		int actualUpdatedRowCount = fixtureChartDetailService.updateChartItems(chartId, selectedPublishDateTime, newPublishDateTime);
		
		assertEquals(expectedUpdatedRowCount, actualUpdatedRowCount);
		
		Mockito.verify(mockChartDetailRepository).getCount(chartId, selectedPublishDateTime);
		Mockito.verify(mockChartDetailRepository).updateChartItems(newPublishDateTime, selectedPublishDateTime, chartId);
		
	}

	/**
	 * Perform pre-test initialization.
	 * 
	 * @throws Exception
	 *             if the initialization fails for some reason
	 * 
	 * @generatedBy CodePro at 07.08.12 17:19
	 */
	@Before
	public void setUp() throws Exception {
		fixtureChartDetailService = new ChartDetailService();
		fixtureChartDetailService.setMediaLogTypeService(new MediaLogTypeService());
		mockDrmService = PowerMockito.mock(DrmService.class);
		fixtureChartDetailService.setDrmService(mockDrmService);
		mockChartDetailRepository = PowerMockito.mock(ChartDetailRepository.class);
		fixtureChartDetailService.setChartDetailRepository(mockChartDetailRepository);
		mockEntityService = PowerMockito.mock(EntityService.class);
		fixtureChartDetailService.setEntityService(mockEntityService);
		mockChartDetailDao = PowerMockito.mock(ChartDetailDao.class);
		fixtureChartDetailService.setChartDetailDao(mockChartDetailDao);
		mockMediaService = PowerMockito.mock(MediaService.class);
		fixtureChartDetailService.setMediaService(mockMediaService);
	}
}