package mobi.nowtechnologies.server.service;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.repository.DrmRepository;
import mobi.nowtechnologies.server.service.exception.ServiceException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * The class <code>DrmServiceTest</code> contains tests for the class <code>{@link DrmService}</code>.
 *
 * @generatedBy CodePro at 10/26/12 10:17 AM
 * @author Alexander Kolpakov (akolpakov)
 * @version $Revision: 1.0 $
 */
@RunWith(PowerMockRunner.class)
public class DrmServiceTest {	
	@Mock
	private ChartDetailService chartDetailService;
	
	private DrmService fixture;
	
	@Mock
	private DrmRepository drmRepository;
	
	/**
	 * Run the Object[] processBuyTrackCommand(User,String,String) method test for not licensed track.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/26/12 10:17 AM
	 */
	@Test
	public void testProcessBuyTrackCommand_NotLicensedTrack_Failure() throws Exception {
		User user = new User();
		user.setId(1);
		String isrc = "some_isrc";
		String communityName = "some_community";
		
		when(chartDetailService.isTrackCanBeBoughtAccordingToLicense(any(String.class))).thenReturn(false);

		try{
			fixture.processBuyTrackCommand(user, isrc, communityName);
		}catch (ServiceException e) {
			if(e.getErrorCodeForMessageLocalization().equals("buyTrack.command.error.attemptToBuyBonusTrack"))
				return;
		}
		
		fail("Expect throw a ServiceException with code = buyTrack.command.error.attemptToBuyBonusTrack");
	}
	
	/**
	 * Run the Drm findDrmByUserAndMedia(User user, Media media, DrmPolicy drmPolicy) method test with not null search result by media and user.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/23/12 9:46 AM
	 */
	@Test
	public void testFindDrmByUserAndMedia_WithNotNullSearch_Successful() throws Exception {
		User user = new User();
		Media media = new Media();
		user.setId(1);
		media.setI(1);
		
		Drm drm = anyDrm();
		
		when(drmRepository.findByUserAndMedia(anyInt(),  anyInt())).thenReturn(drm);
		
		Drm result = fixture.findDrmByUserAndMedia(user, media, null, true);

		assertEquals(drm, result);
	}
	
	/**
	 * Run the Drm findDrmByUserAndMedia(User user, Media media, DrmPolicy drmPolicy) method test with not null drms of user.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/23/12 9:46 AM
	 */
	@Test
	public void testFindDrmByUserAndMedia_WithNotNullUserDrms_Successful() throws Exception {
		User user = new User();
		Media media = new Media();
		Drm drm = new Drm();
		user.setId(1);
		media.setI(1);
		drm.setMedia(media);
		drm.setUser(user);
		user.setDrms(Collections.singletonList(drm));		
		
		Drm result = fixture.findDrmByUserAndMedia(user, media, null, true);

		assertEquals(drm, result);
		
		verify(drmRepository, times(0)).findByUserAndMedia(anyInt(),  anyInt());
	}
	
	/**
	 * Run the Drm findDrmByUserAndMedia(User user, Media media, DrmPolicy drmPolicy) method test with null search result by media and user and not null drmPolicy.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/23/12 9:46 AM
	 */
	@Test
	public void testFindDrmByUserAndMedia_WithNullSearchByNotNullDrmPolicy_Successful() throws Exception {
		DrmType drmType = new DrmType();
		DrmPolicy drmPolicy = new DrmPolicy();
		User user = new User();
		Media media = new Media();
		user.setId(1);
		media.setI(1);
		drmPolicy.setDrmType(drmType);
		drmPolicy.setDrmValue((byte)30);
		
		when(drmRepository.findByUserAndMedia(anyInt(),  anyInt())).thenReturn(null);
		when(drmRepository.save(any(Drm.class))).thenAnswer(new Answer<Drm>() {
			@Override
			public Drm answer(InvocationOnMock invocation) throws Throwable {
				Drm drm = (Drm) invocation.getArguments()[0];
				drm.setI((int)Math.random()*100);
				return drm;
			}
		});
		
		Drm result = fixture.findDrmByUserAndMedia(user, media, drmPolicy, true);

		assertNotNull(result);
		assertNotNull(result.getI());
		assertEquals(drmPolicy.getDrmType(), result.getDrmType());
		assertEquals(drmPolicy.getDrmValue(), result.getDrmValue());
		assertEquals(user, result.getUser());
		assertEquals(media, result.getMedia());
	}
	
	@Test
	public void testFindDrmByUserAndMedia_WithNotCreateDrmIfNotExists_Successful() throws Exception {
		DrmType drmType = new DrmType();
		DrmPolicy drmPolicy = new DrmPolicy();
		User user = new User();
		Media media = new Media();
		user.setId(1);
		media.setI(1);
		drmPolicy.setDrmType(drmType);
		drmPolicy.setDrmValue((byte)30);
		
		when(drmRepository.findByUserAndMedia(anyInt(),  anyInt())).thenReturn(null);
		when(drmRepository.save(any(Drm.class))).thenAnswer(new Answer<Drm>() {
			@Override
			public Drm answer(InvocationOnMock invocation) throws Throwable {
				Drm drm = (Drm) invocation.getArguments()[0];
				drm.setI((int)Math.random()*100);
				return drm;
			}
		});
		
		Drm result = fixture.findDrmByUserAndMedia(user, media, drmPolicy, false);
		
		assertNotNull(result);
		assertNotNull(result.getI());
		assertEquals(drmPolicy.getDrmType(), result.getDrmType());
		assertEquals(drmPolicy.getDrmValue(), result.getDrmValue());
		assertEquals(user, result.getUser());
		assertEquals(media, result.getMedia());
		
		verify(drmRepository, times(0)).save(any(Drm.class));
	}
	
	/**
	 * Run the Drm findDrmByUserAndMedia(User user, Media media, DrmPolicy drmPolicy) method test with null search result by media and user and null drmPolicy.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/23/12 9:46 AM
	 */
	@Test
	public void testFindDrmByUserAndMedia_WithNullSearchByNullDrmPolicy_Successful() throws Exception {
		User user = new User();
		Media media = new Media();
		user.setId(1);
		media.setI(1);
		
		when(drmRepository.findByUserAndMedia(anyInt(),  anyInt())).thenReturn(null);
		
		Drm result = fixture.findDrmByUserAndMedia(user, media, null, true);

		assertNull(result);
	}
	
	/**
	 * Run the Drm findDrmByUserAndMedia(User user, Media media, DrmPolicy drmPolicy) method test by null media.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/23/12 9:46 AM
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFindDrmByUserAndMedia_ByNullMedia_Failure() throws Exception {
		User user = new User();
		user.setId(1);
		
		fixture.findDrmByUserAndMedia(user, null, null, true);
	}
	
	/**
	 * Run the Drm findDrmByUserAndMedia(User user, Media media, DrmPolicy drmPolicy) method test by null user.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/23/12 9:46 AM
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFindDrmByUserAndMedia_ByNullUser_Failure() throws Exception {
		Media media = new Media();
		media.setI(1);
		
		fixture.findDrmByUserAndMedia(null, media, null, true);
	}
	
	public static Drm anyDrm() {
		User user = new User();
		user.setId((int)(Math.random()*100));
		Media media = new Media();
		media.setI((int)(Math.random()*100));
		
		Drm drm = new Drm();
		drm.setI((int)(Math.random()*100));
		drm.setUser(user);
		drm.setMedia(media);
		
		return drm;
	}

	/**
	 * Perform pre-test initialization.
	 *
	 * @throws Exception
	 *         if the initialization fails for some reason
	 *
	 * @generatedBy CodePro at 10/26/12 10:17 AM
	 */
	@Before
	public void setUp()
		throws Exception {
		
		fixture = new DrmService();
		fixture.setChartDetailService(chartDetailService);
		fixture.setDrmRepository(drmRepository);
	}

	/**
	 * Perform post-test clean-up.
	 *
	 * @throws Exception
	 *         if the clean-up fails for some reason
	 *
	 * @generatedBy CodePro at 10/26/12 10:17 AM
	 */
	@After
	public void tearDown()
		throws Exception {
	}
}