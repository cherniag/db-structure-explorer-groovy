package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.service.sms.SMPPServiceImpl;
import mobi.nowtechnologies.server.service.vodafone.impl.VFNZSMSGatewayServiceImpl;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * User: gch
 * Date: 12/19/13
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/service-test.xml", "/META-INF/dao-test.xml", "/META-INF/shared.xml"})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class UserNotificationServiceImplIT extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private UserNotificationServiceImpl userNotificationService;

    @Autowired
    @Qualifier("vf_nz.service.SmsProviderSpy")
    private VFNZSMSGatewayServiceImpl smsGatewayService;

    @Before
    public void setUp() throws Exception {
        reset(smsGatewayService);
        SMPPServiceImpl smppService = mock(SMPPServiceImpl.class);
        smsGatewayService.setSmppService(smppService);
    }

    @Test
    public void checkSendChargeNotificationReminderShouldBeSent() throws Exception {
        User user = UserFactory.createUser();
        user.getUserGroup().getCommunity().setRewriteUrlParameter("vf_nz");
        user.setProvider(ProviderType.VF);
        user.setMobile("+64789654123");
        userNotificationService.sendChargeNotificationReminder(user);
        verify(smsGatewayService).send(eq("+64789654123"), eq("You are charged for 28 days continuously"), eq("4003"));
    }

    @Test
    public void checkSendChargeNotificationReminderShouldBeSentWithDefaultSMSText() throws Exception {
        User user = UserFactory.createUser();
        user.getUserGroup().getCommunity().setRewriteUrlParameter("o2");
        user.setProvider(ProviderType.O2);
        user.setMobile("+44789654123");
        userNotificationService.sendChargeNotificationReminder(user);
        verify(smsGatewayService).send(eq("+44789654123"), eq("You are charged for 28 days continuously default text"), eq("4003"));
    }

    @Test
    public void checkSendChargeNotificationReminderShouldNotBeSentForRejectedDevice() throws Exception {
        User user = UserFactory.createUser();
        user.getUserGroup().getCommunity().setRewriteUrlParameter("vf_nz");
        user.setProvider(ProviderType.NON_VF);
        user.setMobile("+64789654123");
        DeviceType deviceType = new DeviceType();
        deviceType.setName(DeviceType.WINDOWS_PHONE);
        user.setDeviceType(deviceType);
        userNotificationService.sendChargeNotificationReminder(user);
        verify(smsGatewayService, never()).send(anyString(), anyString(), anyString());
    }

    @Test
    public void checkSendChargeNotificationReminderShouldNotBeSentForRejectedDevice2() throws Exception {
        User user = UserFactory.createUser();
        user.getUserGroup().getCommunity().setRewriteUrlParameter("vf_nz");
        user.setProvider(ProviderType.NON_VF);
        user.setMobile("+64789654123");
        DeviceType deviceType = new DeviceType();
        deviceType.setName(DeviceType.J2ME);
        user.setDeviceType(deviceType);
        userNotificationService.sendChargeNotificationReminder(user);
        verify(smsGatewayService, never()).send(anyString(), anyString(), anyString());
    }

    @Test
    public void checkSendChargeNotificationReminderShouldNotBeSentForNonAvailableCommunity() throws Exception {
        User user = UserFactory.createUser();
        user.getUserGroup().getCommunity().setRewriteUrlParameter("a_not_available_community");
        user.setProvider(ProviderType.VF);
        user.setMobile("+64789654123");
        userNotificationService.sendChargeNotificationReminder(user);
        verify(smsGatewayService, never()).send(anyString(), anyString(), anyString());
    }

}
