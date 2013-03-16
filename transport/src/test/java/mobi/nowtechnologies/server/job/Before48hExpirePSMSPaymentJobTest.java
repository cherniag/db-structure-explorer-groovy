package mobi.nowtechnologies.server.job;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Locale;

import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.O2ClientService;
import mobi.nowtechnologies.server.service.payment.http.MigHttpService;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSourceImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.domain.Pageable;

@RunWith(PowerMockRunner.class)
public class Before48hExpirePSMSPaymentJobTest {

	@Mock
	private UserRepository mockUserRepository;
	
	@Mock
	private CommunityResourceBundleMessageSourceImpl mockMessageSource;
	
	@Mock
	private O2ClientService mockO2ClientService;
	
	@Mock
	private MigHttpService mockMigHttpService;
	
	private Before48hPSMSPaymentJob fixture;
	
	@Test
	public void testExecute_Successful()
		throws Exception {
		Community community = CommunityFactory.createCommunity();
		community.setRewriteUrlParameter("o2");
		
		UserGroup userGroup = new UserGroup();
		userGroup.setCommunity(community);
		
		User user = UserFactory.createUser();
		user.setUserGroup(userGroup);
		String msg = "Test warning message";
		String msgCode = "job.before48.psms.consumer";

		when(mockUserRepository.findBefore48hExpireUsers(anyInt(), any(Pageable.class))).thenReturn(Collections.singletonList(user));
		when(mockMessageSource.getMessage(eq("o2"), eq(msgCode), eq((Object[])null), eq((Locale)null))).thenReturn(msg);
		when(mockO2ClientService.sendFreeSms(eq(user.getMobile()), eq(msg))).thenReturn(true);
		when(mockMigHttpService.makeFreeSMSRequest(eq(user.getMobile()), eq(msg))).thenReturn(null);
		
		fixture.execute();

		verify(mockMessageSource, times(1)).getMessage(eq("o2"), eq(msgCode), eq((Object[])null), eq((Locale)null));
		verify(mockO2ClientService, times(0)).sendFreeSms(eq(user.getMobile()), eq(msg));
		verify(mockMigHttpService, times(1)).makeFreeSMSRequest(eq(user.getMobile()), eq(msg));
		verify(mockUserRepository, times(1)).findBefore48hExpireUsers(anyInt(), any(Pageable.class));
	}

	@Before
	public void setUp()
		throws Exception {
		fixture = new Before48hPSMSPaymentJob();
		fixture.setO2ClientService(mockO2ClientService);
		fixture.setMessageSource(mockMessageSource);
		fixture.setUserRepository(mockUserRepository);
		fixture.setMigHttpService(mockMigHttpService);
	}
}