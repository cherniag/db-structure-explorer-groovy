package mobi.nowtechnologies.server.service.payment.impl;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.service.PaymentDetailsService;
import mobi.nowtechnologies.server.service.PaymentPolicyService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.payment.PaymentSystemService;
import mobi.nowtechnologies.server.service.sms.SMSMessageProcessorContainer;
import mobi.nowtechnologies.server.service.vodafone.impl.VFNZSMSGatewayServiceImpl;
import mobi.nowtechnologies.server.shared.Utils;
import org.jsmpp.bean.DeliverSm;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static mobi.nowtechnologies.server.persistence.domain.Community.VF_NZ_COMMUNITY_REWRITE_URL;
import static mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus.ERROR;
import static org.jsmpp.bean.SMSCDeliveryReceipt.SUCCESS_FAILURE;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/shared.xml", "/META-INF/dao-test.xml", "/META-INF/service-test.xml"})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class VFPaymentServiceImplIT {

    //private PowerMockRule powerMockRule = new PowerMockRule();

    @Autowired
    private VFPaymentServiceImpl paymentService;

    @Autowired
    private SMSMessageProcessorContainer processorContainer;

    @Resource(name = "service.PendingPaymentService")
    private PendingPaymentServiceImpl pendingPaymentService;

    @Resource(name = "service.PaymentDetailsService")
    private PaymentDetailsService paymentDetailsService;

    @Resource(name = "vf_nz.service.UserService")
    private UserService userService;

    @Resource(name = "service.PaymentPolicyService")
    private PaymentPolicyService paymentPolicyService;

    @Resource(name = "service.map.paymentSystems")
    private Map<String, PaymentSystemService> paymentSystems;

    private VFPaymentServiceImpl paymentServiceTarget;
    private VFNZSMSGatewayServiceImpl gatewayServiceTarget;

    @Before
    public void setUp() throws Exception {
        paymentServiceTarget = (VFPaymentServiceImpl) ((Advised) paymentService).getTargetSource().getTarget();
        gatewayServiceTarget = paymentServiceTarget.gatewayService;
        paymentServiceTarget.setGatewayService(spy(gatewayServiceTarget));
    }

    @After
    public void tireDown(){
        paymentServiceTarget.setGatewayService(gatewayServiceTarget);
    }

    @Test
    public void testStartVFPayment_SuccessfulResponse_Successful() throws Exception {
        String userName = "+642102247311";
        String community = "vf_nz";
        Integer paymentPolicyId = 231;

        User user = userService.findByNameAndCommunity(userName, community);
        PaymentPolicy paymentPolicy = paymentPolicyService.getPaymentPolicy(paymentPolicyId);
        paymentService.commitPaymentDetails(user, paymentPolicy);

        List<PendingPayment> createPendingPayments = pendingPaymentService.createPendingPayments();
        for (PendingPayment pendingPayment : createPendingPayments) {
            PaymentSystemService paymentSystemService = paymentSystems.get(pendingPayment.getPaymentSystem());
            if (paymentSystemService == paymentService) {
                paymentSystemService.startPayment(pendingPayment);
            }
        }

        DeliverSm deliverSm = new DeliverSm();
        deliverSm.setSmscDeliveryReceipt();
        deliverSm.setDestAddress("3313");
        deliverSm.setSourceAddr("642102247311");
        deliverSm.setShortMessage(buildMessage("108768587", "000", "000", "1310020119", "1310020119", "DELIVRD", "000", "It is test").getBytes());
        processorContainer.processStatusReportMessage(deliverSm);

        Mockito.verify(paymentServiceTarget.gatewayService, times(1)).send("+642102247311", "Your payment to vf_nz Tracks was successful. You were charged: 5 GBP", "3313", SUCCESS_FAILURE, 600000);

        int nextSubPayment = Utils.getEpochSeconds() + 4 * Utils.WEEK_SECONDS;
        user = userService.findByNameAndCommunity(userName, community);
        assertTrue(Math.abs(nextSubPayment - user.getNextSubPayment()) < 4);

        List<PendingPayment> pendingPayments = pendingPaymentService.getPendingPayments(user.getId());
        assertEquals(0, pendingPayments.size());
    }

    @Test
    public void testStartVFPayment_ErrorResponse_Successful() throws Exception {
        String userName = "+642102247311";
        String community = VF_NZ_COMMUNITY_REWRITE_URL;
        Integer paymentPolicyId = 231;

        User user = userService.findByNameAndCommunity(userName, community);
        Integer oldNextSubPayment = user.getNextSubPayment();
        PaymentPolicy paymentPolicy = paymentPolicyService.getPaymentPolicy(paymentPolicyId);
        paymentService.commitPaymentDetails(user, paymentPolicy);

        List<PendingPayment> createPendingPayments = pendingPaymentService.createPendingPayments();
        for (PendingPayment pendingPayment : createPendingPayments) {
            PaymentSystemService paymentSystemService = paymentSystems.get(pendingPayment.getPaymentSystem());
            if (paymentSystemService == paymentService) {
                pendingPayment.getPaymentDetails().incrementRetries();
                pendingPayment.getPaymentDetails().incrementRetries();
                pendingPayment.getPaymentDetails().incrementRetries();
                paymentDetailsService.update(pendingPayment.getPaymentDetails());
                paymentSystemService.startPayment(pendingPayment);
            }
        }

        DeliverSm deliverSm = new DeliverSm();
        deliverSm.setSmscDeliveryReceipt();
        deliverSm.setDestAddress("3313");
        deliverSm.setSourceAddr("642102247311");
        deliverSm.setShortMessage(buildMessage("108768587", "000", "000", "1310020119", "1310020119", "UNDELIV", "001", "It is test").getBytes());
        processorContainer.processStatusReportMessage(deliverSm);

        Mockito.verify(paymentServiceTarget.gatewayService, times(1)).send("+642102247311", "Your payment to vf_nz Tracks was successful. You were charged: 5 GBP", "3313", SUCCESS_FAILURE, 600000);
        user = userService.findByNameAndCommunity(userName, community);
        assertEquals(oldNextSubPayment.intValue(), user.getNextSubPayment());
        assertEquals("001", user.getCurrentPaymentDetails().getErrorCode());
        assertEquals("UNDELIV", user.getCurrentPaymentDetails().getDescriptionError());
        assertEquals(ERROR, user.getCurrentPaymentDetails().getLastPaymentStatus());

        List<PendingPayment> pendingPayments = pendingPaymentService.getPendingPayments(user.getId());
        assertEquals(0, pendingPayments.size());
    }

    protected String buildMessage(String msgId, String sub, String dlvrd, String submitDate, String doneDate, String stat, String err, String text) {
        return "id:" + msgId + " sub:" + sub + " dlvrd:" + dlvrd + " submit date:" + submitDate + " done date:" + doneDate + " stat:" + stat + " err:" + err + " Text:" + text;
    }
}