package mobi.nowtechnologies.server.job;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Locale;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.enums.SegmentType;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.O2ClientService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.enums.Contract;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSourceImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class Before48hExpirePSMSPaymentJobTest {

	@Mock
	private UserRepository mockUserRepository;
	
	@Mock
	private CommunityResourceBundleMessageSourceImpl mockMessageSource;
	
	@Mock
	private O2ClientService mockO2ClientService;
	
	private Before48hExpirePSMSPaymentJob fixture;
	
	@Test
	public void testExecute_Successful()
		throws Exception {
		String availableCommunities = "o2";
		String availableProviders = "o2";
		SegmentType availableSegments = SegmentType.CONSUMER;
		Contract availableContracts = Contract.PAYG;
		
		User user = UserFactory.createUser();
		String msg = "Test warning message";
		String msgCode = "job.before48.psms.consumer";

		when(mockUserRepository.findBefore48hExpireUsers(anyInt(), eq(availableProviders), eq(availableSegments), eq(availableContracts))).thenReturn(Collections.singletonList(user));
		when(mockMessageSource.getMessage(eq("o2"), eq(msgCode), eq((Object[])null), eq((Locale)null))).thenReturn(msg);
		when(mockO2ClientService.sendFreeSms(eq(user.getMobile()), eq(msg))).thenReturn(true);
		
		fixture.execute();

		verify(mockMessageSource, times(1)).getMessage(eq("o2"), eq(msgCode), eq((Object[])null), eq((Locale)null));
		verify(mockO2ClientService, times(1)).sendFreeSms(eq(user.getMobile()), eq(msg));
		verify(mockUserRepository, times(1)).findBefore48hExpireUsers(anyInt(), eq(availableProviders), eq(availableSegments), eq(availableContracts));
	}

	@Before
	public void setUp()
		throws Exception {
		fixture = new Before48hExpirePSMSPaymentJob();
		fixture.setO2ClientService(mockO2ClientService);
		fixture.setMessageSource(mockMessageSource);
		fixture.setUserRepository(mockUserRepository);
	}
}