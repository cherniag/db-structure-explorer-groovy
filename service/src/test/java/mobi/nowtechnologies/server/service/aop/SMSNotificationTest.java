package mobi.nowtechnologies.server.service.aop;

import mobi.nowtechnologies.server.security.NowTechTokenBasedRememberMeServices;
import mobi.nowtechnologies.server.service.payment.http.MigHttpService;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import antlr.Utils;

@RunWith( PowerMockRunner.class )
@PrepareForTest(Utils.class)
public class SMSNotificationTest {
	
	@Mock
	private MigHttpService mockMigService;
	
	@Mock
	private NowTechTokenBasedRememberMeServices mockRememberMeServices;
	
	@Mock
	private CommunityResourceBundleMessageSource mockMessageSource;

	private SMSNotification fixture;

	@Before
	public void setUp()
		throws Exception {
		
		fixture = new SMSNotification();
	}
}