package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.dao.CommunityDao;
import mobi.nowtechnologies.server.persistence.dao.EntityDao;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PayPalPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.service.EntityService;
import mobi.nowtechnologies.server.service.PaymentDetailsService;
import mobi.nowtechnologies.server.service.payment.http.PayPalHttpService;
import mobi.nowtechnologies.server.service.payment.impl.PayPalPaymentServiceImpl;
import mobi.nowtechnologies.server.service.payment.request.PayPalRequest;
import mobi.nowtechnologies.server.shared.service.BasicResponse;
import mobi.nowtechnologies.server.shared.service.PostService;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml", "/META-INF/service-test.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class PayPalPaymentServiceIT {

    private PayPalPaymentServiceImpl payPalPaymentServiceImpl;
    private BasicResponse successfulResponse;
    private ArgumentCaptor<List> listArgumentCaptor = ArgumentCaptor.forClass(List.class);
    private PostService postService = mock(PostService.class);

    @Resource(name = "paymentDetailsRepository")
    private PaymentDetailsRepository paymentDetailsRepository;

	@Resource(name = "persistence.EntityDao")
	private EntityDao entityDao;

	@Resource(name = "service.PaymentDetailsService")
	private PaymentDetailsService paymentDetailsService;

    @Resource(name = "request.payPalRequest")
    private PayPalRequest payPalRequest;

    @Before
	public void before() {
        initPayPalPaymentService();
        initMocks();
    }

    private void initMocks() {
        successfulResponse = PaymentTestUtils.createBasicResponse(HttpServletResponse.SC_OK,
                "TOKEN=EC%2d5YJ748178G052312W&TIMESTAMP=2011%2d12%2d23T19%3a40%3a07Z&CORRELATIONID=80d5883fa4b48&ACK=Success&VERSION=80%2e0&BUILD=2271164");
        when(postService.sendHttpPost(anyString(),anyListOf(NameValuePair.class),anyString()))
                .thenReturn(successfulResponse);
    }

    private void initPayPalPaymentService() {
        EntityService entityService = new EntityService();
        entityService.setEntityDao(entityDao);
        PayPalHttpService httpService = new PayPalHttpService();
        httpService.setApiUrl("https://api-3t.sandbox.paypal.com/nvp");
        httpService.setRequest(payPalRequest);
        httpService.setPostService(postService);
        payPalPaymentServiceImpl = new PayPalPaymentServiceImpl();
        payPalPaymentServiceImpl.setExpireMillis(3L);
        payPalPaymentServiceImpl.setRedirectURL("redirectUrl");
        payPalPaymentServiceImpl.setRetriesOnError(3);
        payPalPaymentServiceImpl.setHttpService(httpService);
        payPalPaymentServiceImpl.setEntityService(entityService);
        payPalPaymentServiceImpl.setPaymentDetailsService(paymentDetailsService);
        payPalPaymentServiceImpl.setPaymentDetailsRepository(paymentDetailsRepository);
    }

    @Test
	public void testThatPaymentDetailsForVFNZAreCommitted() {
        User user = createAndSaveUser();
			
		Community community = CommunityDao.getCommunity("vf_nz");
		PaymentPolicy paymentPolicy = PaymentTestUtils.createPaymentPolicy();
		paymentPolicy.setCommunity(community);
		entityDao.saveEntity(paymentPolicy);
			
		Long id = payPalPaymentServiceImpl.commitPaymentDetails("token", user, paymentPolicy, true).getI();
        assertNotNull(id);
        PayPalPaymentDetails paymentDetails = (PayPalPaymentDetails)paymentDetailsRepository.findOne(id);
        assertNotNull(paymentDetails);
        assertTrue(paymentDetails.isActivated());
        assertEquals(user.getId(), paymentDetails.getOwner().getId());
        assertEquals(user.getCurrentPaymentDetails().getI(), paymentDetails.getI());
		assertEquals(1, user.getPaymentDetailsList().size());
	}

    @Test
    public void testThatBillingRequestForVFNZAreSentWhenCommittingPaymentDetails() {
        User user = createAndSaveUser();
        Community community = CommunityDao.getCommunity("vf_nz");
        PaymentPolicy paymentPolicy = PaymentTestUtils.createPaymentPolicy();
        paymentPolicy.setCommunity(community);
        entityDao.saveEntity(paymentPolicy);
        payPalPaymentServiceImpl.commitPaymentDetails("token", user, paymentPolicy, true);
        ArgumentCaptor<List> listArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(postService).sendHttpPost(anyString(), listArgumentCaptor.capture(), anyString());
        List actualPayPalRequestParameters = listArgumentCaptor.getValue();
        assertTrue(actualPayPalRequestParameters.contains(new BasicNameValuePair("USER", "vfnz_user_name")));
        assertTrue(actualPayPalRequestParameters.contains(new BasicNameValuePair("PWD", "vfnz_password")));
        assertTrue(actualPayPalRequestParameters.contains(new BasicNameValuePair("VERSION", "80.0")));
        assertTrue(actualPayPalRequestParameters.contains(new BasicNameValuePair("BUTTONSOURCE", "vfnzSource")));
        assertTrue(actualPayPalRequestParameters.contains(new BasicNameValuePair("SIGNATURE", "vfnz_signature")));
        assertTrue(actualPayPalRequestParameters.contains(new BasicNameValuePair("TOKEN","token")));
        assertTrue(actualPayPalRequestParameters.contains(new BasicNameValuePair("METHOD","CreateBillingAgreement")));
    }

    private User createAndSaveUser() {
        User user = new User();
        user.setUserName("good@user.com");
        entityDao.saveEntity(user);
        return user;
    }

    @Test
    public void testThatBillingRequestForO2AreSentWhenCommittingPaymentDetails() {
        User user = createAndSaveUser();
        Community community = CommunityDao.getCommunity("o2");
        PaymentPolicy paymentPolicy = PaymentTestUtils.createPaymentPolicy();
        paymentPolicy.setCommunity(community);
        entityDao.saveEntity(paymentPolicy);
        payPalPaymentServiceImpl.commitPaymentDetails("token", user, paymentPolicy, true);
        verify(postService).sendHttpPost(anyString(), listArgumentCaptor.capture(), anyString());
        List actualPayPalRequestParameters = listArgumentCaptor.getValue();
        assertTrue(actualPayPalRequestParameters.contains(new BasicNameValuePair("USER", "o2_user_name")));
        assertTrue(actualPayPalRequestParameters.contains(new BasicNameValuePair("PWD","o2_password")));
        assertTrue(actualPayPalRequestParameters.contains(new BasicNameValuePair("VERSION","90.0")));
        assertTrue(actualPayPalRequestParameters.contains(new BasicNameValuePair("BUTTONSOURCE","o2Source")));
        assertTrue(actualPayPalRequestParameters.contains(new BasicNameValuePair("SIGNATURE","o2_signature")));
    }

    @Test
    public void testThatBillingRequestForOtherCommunityAreSentWhenCommittingPaymentDetails() {
        User user = createAndSaveUser();
        Community community = CommunityDao.getCommunity("Metal Hammer");
        PaymentPolicy paymentPolicy = PaymentTestUtils.createPaymentPolicy();
        paymentPolicy.setCommunity(community);
        entityDao.saveEntity(paymentPolicy);
        payPalPaymentServiceImpl.commitPaymentDetails("token", user, paymentPolicy, true);
        ArgumentCaptor<List> listArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(postService).sendHttpPost(anyString(), listArgumentCaptor.capture(), anyString());
        List actualPayPalRequestParameters = listArgumentCaptor.getValue();
        assertTrue(actualPayPalRequestParameters.contains(new BasicNameValuePair("USER", "cn_1313656118_biz_api1.chartsnow.mobi")));
        assertTrue(actualPayPalRequestParameters.contains(new BasicNameValuePair("PWD","1313656158")));
        assertTrue(actualPayPalRequestParameters.contains(new BasicNameValuePair("SIGNATURE","AoTXEljMZhDQEXJFn1kQJo2C6CbIAPP7uCi4Y-85yG98nlcq-IJBt9jQ")));
    }



}