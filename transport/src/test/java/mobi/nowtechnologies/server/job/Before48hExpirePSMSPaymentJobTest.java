package mobi.nowtechnologies.server.job;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.service.UserService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.domain.Pageable;

@RunWith(PowerMockRunner.class)
public class Before48hExpirePSMSPaymentJobTest {

	@Mock
	private UserService mockUserService;
		
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

		when(mockUserService.findBefore48hExpireUsers(anyInt(), any(Pageable.class))).thenReturn(Collections.singletonList(user));
		
		fixture.executeInternal(null);

		verify(mockUserService, times(1)).findBefore48hExpireUsers(anyInt(), any(Pageable.class));
	}

	@Before
	public void setUp()
		throws Exception {
		fixture = new Before48hPSMSPaymentJob();
		fixture.setUserService(mockUserService);
	}
}