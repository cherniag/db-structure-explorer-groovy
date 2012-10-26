package mobi.nowtechnologies.server.service;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.exception.ServiceException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
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