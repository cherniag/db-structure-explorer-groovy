package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PayPalPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.PaymentDetailsService;
import mobi.nowtechnologies.server.service.payment.http.PayPalHttpService;
import mobi.nowtechnologies.server.service.payment.impl.PayPalPaymentServiceImpl;
import mobi.nowtechnologies.server.service.payment.request.PayPalRequest;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.support.http.BasicResponse;
import mobi.nowtechnologies.server.support.http.PostService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/shared.xml", "/META-INF/service-test.xml", "/META-INF/dao-test.xml"})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class PayPalPaymentServiceIT {

    private static final String REDIRECT_URL = "http://redirect";
    private static final String API_URL = "https://api-3t.sandbox.paypal.com/nvp";
    private PayPalPaymentServiceImpl payPalPaymentServiceImpl;
    private BasicResponse successfulResponse;
    private ArgumentCaptor<List> payPalRequestParamsArgumentCaptor = ArgumentCaptor.forClass(List.class);
    private PostService postService = mock(PostService.class);
    private ApplicationEventPublisher applicationEventPublisher = mock(ApplicationEventPublisher.class);

    @Resource(name = "paymentDetailsRepository")
    private PaymentDetailsRepository paymentDetailsRepository;

    @Resource(name = "service.PaymentDetailsService")
    private PaymentDetailsService paymentDetailsService;

    @Resource(name = "request.payPalRequest")
    private PayPalRequest payPalRequest;

    @Resource(name = "communityRepository")
    private CommunityRepository communityRepository;

    @Resource
    PaymentPolicyRepository paymentPolicyRepository;

    @Resource
    UserRepository userRepository;

    @Before
    public void before() {
        initPayPalPaymentService();
        initMocks();
    }

    private void initMocks() {
        successfulResponse = PaymentTestUtils.createBasicResponse(HttpServletResponse.SC_OK,
                                                                  "TOKEN=EC%2d5YJ748178G052312W&TIMESTAMP=2011%2d12%2d23T19%3a40%3a07Z&CORRELATIONID=80d5883fa4b48&ACK=Success&VERSION=80%2e0&BUILD" +
                                                                  "=2271164&BILLINGAGREEMENTID=QWW45E98RM54S");
        when(postService.sendHttpPost(anyString(), anyListOf(NameValuePair.class), anyString())).thenReturn(successfulResponse);
    }

    private void initPayPalPaymentService() {
        PayPalHttpService httpService = new PayPalHttpService();
        httpService.setApiUrl(API_URL);
        httpService.setRequest(payPalRequest);
        httpService.setPostService(postService);
        payPalPaymentServiceImpl = new PayPalPaymentServiceImpl();
        payPalPaymentServiceImpl.setExpireMillis(3L);
        payPalPaymentServiceImpl.setRedirectURL(REDIRECT_URL);
        payPalPaymentServiceImpl.setRetriesOnError(3);
        payPalPaymentServiceImpl.setHttpService(httpService);
        payPalPaymentServiceImpl.setPaymentDetailsService(paymentDetailsService);
        payPalPaymentServiceImpl.setPaymentDetailsRepository(paymentDetailsRepository);
        payPalPaymentServiceImpl.setApplicationEventPublisher(applicationEventPublisher);
    }

    @Test
    public void testThatPaymentDetailsForVFNZAreCommitted() {
        User user = createAndSaveUser();
        PaymentPolicy paymentPolicy = createAndSavePaymentPolicy("vf_nz");
        Long id = payPalPaymentServiceImpl.commitPaymentDetails("token", user, paymentPolicy, true).getI();
        assertNotNull(id);
        PayPalPaymentDetails paymentDetails = (PayPalPaymentDetails) paymentDetailsRepository.findOne(id);
        assertNotNull(paymentDetails);
        assertTrue(paymentDetails.isActivated());
        assertEquals(user.getId(), paymentDetails.getOwner().getId());
        assertEquals(user.getCurrentPaymentDetails().getI(), paymentDetails.getI());
        assertEquals(1, paymentDetailsService.getPaymentDetails(user).size());
    }

    @Test
    public void testThatBillingRequestForVFNZAreSentWhenCommittingPaymentDetails() {
        User user = createAndSaveUser();
        PaymentPolicy paymentPolicy = createAndSavePaymentPolicy("vf_nz");
        payPalPaymentServiceImpl.commitPaymentDetails("token", user, paymentPolicy, true);
        ArgumentCaptor<List> listArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(postService).sendHttpPost(eq(API_URL), listArgumentCaptor.capture(), anyString());
        List actualPayPalRequestParameters = listArgumentCaptor.getValue();
        assertEquals(7, actualPayPalRequestParameters.size());
        assertTrue(actualPayPalRequestParameters.containsAll(Arrays.asList(new BasicNameValuePair("USER", "vfnz_user_name"),
                                                                           new BasicNameValuePair("PWD", "vfnz_password"),
                                                                           new BasicNameValuePair("VERSION", "80.0"),
                                                                           new BasicNameValuePair("BUTTONSOURCE", "vfnzSource"),
                                                                           new BasicNameValuePair("SIGNATURE", "vfnz_signature"),
                                                                           new BasicNameValuePair("TOKEN", "token"),
                                                                           new BasicNameValuePair("METHOD", "CreateBillingAgreement"))));
    }

    private User createAndSaveUser() {
        User user = new User();
        user.setUserName("good@user.com");
        user.setActivationStatus(ActivationStatus.ACTIVATED);
        userRepository.save(user);
        return user;
    }

    @Test
    public void testThatBillingRequestForO2AreSentWhenCommittingPaymentDetails() {
        User user = createAndSaveUser();
        PaymentPolicy paymentPolicy = createAndSavePaymentPolicy("o2");
        payPalPaymentServiceImpl.commitPaymentDetails("token", user, paymentPolicy, true);
        verify(postService).sendHttpPost(eq(API_URL), payPalRequestParamsArgumentCaptor.capture(), anyString());
        List actualPayPalRequestParameters = payPalRequestParamsArgumentCaptor.getValue();
        assertEquals(7, actualPayPalRequestParameters.size());
        assertTrue(actualPayPalRequestParameters.containsAll(Arrays.asList(new BasicNameValuePair("USER", "o2_user_name"),
                                                                           new BasicNameValuePair("PWD", "o2_password"),
                                                                           new BasicNameValuePair("VERSION", "90.0"),
                                                                           new BasicNameValuePair("BUTTONSOURCE", "o2Source"),
                                                                           new BasicNameValuePair("SIGNATURE", "o2_signature"))));
    }

    @Test
    public void testThatBillingRequestForOtherCommunityAreSentWhenCommittingPaymentDetails() {
        User user = createAndSaveUser();
        PaymentPolicy paymentPolicy = createAndSavePaymentPolicy("Metal Hammer");
        payPalPaymentServiceImpl.commitPaymentDetails("token", user, paymentPolicy, true);
        ArgumentCaptor<List> listArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(postService).sendHttpPost(eq(API_URL), listArgumentCaptor.capture(), anyString());
        List actualPayPalRequestParameters = listArgumentCaptor.getValue();
        assertTrue(actualPayPalRequestParameters.containsAll(Arrays.asList(new BasicNameValuePair("USER", "cn_1313656118_biz_api1.chartsnow.mobi"),
                                                                           new BasicNameValuePair("PWD", "1313656158"),
                                                                           new BasicNameValuePair("SIGNATURE", "AoTXEljMZhDQEXJFn1kQJo2C6CbIAPP7uCi4Y-85yG98nlcq-IJBt9jQ"))));
    }

    @Test
    public void testThatTokenRequestForVFNZAreSentWhenCreatingPaymentDetails() {
        User user = createAndSaveUser();
        PaymentPolicy paymentPolicy = createAndSavePaymentPolicy("vf_nz");
        PayPalPaymentDetails paymentDetails = payPalPaymentServiceImpl.createPaymentDetails("Some billing description", "http://success", "http://fail", user, paymentPolicy);
        assertNotNull(paymentDetails);
        ArgumentCaptor<List> listArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(postService).sendHttpPost(eq(API_URL), listArgumentCaptor.capture(), anyString());
        List actualPayPalRequestParameters = listArgumentCaptor.getValue();
        assertEquals(12, actualPayPalRequestParameters.size());
        assertTrue(actualPayPalRequestParameters.containsAll(Arrays.asList(new BasicNameValuePair("RETURNURL", "http://success"),
                                                                           new BasicNameValuePair("CANCELURL", "http://fail"),
                                                                           new BasicNameValuePair("L_BILLINGAGREEMENTDESCRIPTION0", "Some billing description"),
                                                                           new BasicNameValuePair("CURRENCYCODE", "GBP"),
                                                                           new BasicNameValuePair("METHOD", "SetExpressCheckout"),
                                                                           new BasicNameValuePair("L_BILLINGTYPE0", "vfnz_MerchantInitiatedBillingSingleAgreement"),
                                                                           new BasicNameValuePair("PAYMENTACTION", "Authorization"))));
        assertEquals(paymentDetails.getBillingAgreementTxId(), REDIRECT_URL + "?cmd=_express-checkout&useraction=commit&token=EC-5YJ748178G052312W");
    }

    private PaymentPolicy createAndSavePaymentPolicy(String communityName) {
        Community community = communityRepository.findByName(communityName);
        PaymentPolicy paymentPolicy = PaymentTestUtils.createPaymentPolicy();
        paymentPolicy.setCommunity(community);
        paymentPolicyRepository.save(paymentPolicy);
        return paymentPolicy;
    }


}