package mobi.nowtechnologies.server.service.payment.impl;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.PaymentDetailsService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.payment.http.MigHttpService;
import mobi.nowtechnologies.server.service.payment.request.MigRequest;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSourceImpl;
import mobi.nowtechnologies.server.shared.service.PostService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


public class MigPaymentServiceTest {
	
	PaymentDetailsService service;
	
	//@Before
	public void before() {
		service = new PaymentDetailsService();
			UserService userService = Mockito.mock(UserService.class);
			Mockito.when(userService.findById(Mockito.anyInt())).thenReturn(new User());
			Mockito.when(userService.updateUser(Mockito.any(User.class))).thenReturn(new User());
		service.setUserService(userService);
		
		CommunityResourceBundleMessageSourceImpl messageSource = new CommunityResourceBundleMessageSourceImpl();
			messageSource.setBasenames(new String[] { "classpath:services_test" });
			messageSource.setDefaultEncoding("utf8");
			messageSource.setCacheSeconds(180);
			messageSource.setUseCodeAsDefaultMessage(true);
		
		service.setMessageSource(messageSource);
		
		MigHttpService httpService = new MigHttpService();
			httpService.setPostService(new PostService());
			httpService.setRequest(new MigRequest());
			httpService.setFreeSMSURL("http://91.216.137.155:8105/mig/mig-chartsnow/test.asp");
			httpService.setPremiumSMSURL("http://91.216.137.155:8105/mig/mig-chartsnow/test.asp");
			
		MigPaymentServiceImpl migPaymentService = new MigPaymentServiceImpl();
			migPaymentService.setHttpService(httpService);
		
		service.setMigPaymentService(migPaymentService);
	}
	
	//@Test
	public void sendPin_Successful() {
		service.resendPin(1, "+380991536036", "occ");
	}
}