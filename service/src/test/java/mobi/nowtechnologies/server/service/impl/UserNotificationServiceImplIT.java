package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.device.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.service.UserNotificationService;
import mobi.nowtechnologies.server.service.sms.SMPPServiceImpl;
import mobi.nowtechnologies.server.service.sms.SMSResponse;
import mobi.nowtechnologies.server.service.vodafone.impl.VFNZSMSGatewayServiceImpl;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.support.http.PostsSaverPostService;
import mobi.nowtechnologies.server.transport.service.TimestampExtFileNameFilter;
import static mobi.nowtechnologies.server.support.http.PostsSaverPostService.Monitor;

import javax.annotation.Resource;

import java.io.File;
import java.util.Date;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.sentaca.spring.smpp.mt.MTMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

/**
 * User: gch Date: 12/19/13
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/shared.xml", "/META-INF/service-test.xml", "classpath:post-service-test.xml", "/META-INF/dao-test.xml"})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
//TODO: Rebuild test not to use mocks.
public class UserNotificationServiceImplIT {

    @Resource
    private UserNotificationService userNotificationService;

    @Resource(name = "vf_nz.service.SmsProviderSpy")
    private VFNZSMSGatewayServiceImpl smsGatewayService;

    @Resource
    private PostsSaverPostService postsSaverPostService;

    @Value("${sms.temporaryFolder}")
    private File smsTemporaryFolder;
    private SMPPServiceImpl smppService;

    @Before
    public void setUp() throws Exception {
        reset(smsGatewayService);
        smppService = mock(SMPPServiceImpl.class);
        smsGatewayService.setSmppService(smppService);
    }

    @Test
    public void checkSendChargeNotificationReminderShouldBeSent() throws Exception {
        when(smppService.sendMessage(any(MTMessage.class))).thenReturn(getSmsResponse());
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.getUserGroup().getCommunity().setRewriteUrlParameter("vf_nz");
        user.setProvider(ProviderType.VF);
        user.setMobile("+64789654123");
        userNotificationService.sendChargeNotificationReminder(user);
        verify(smsGatewayService).send(eq("+64789654123"), eq("You are charged for 28 days continuously"), eq("4003"));
    }

    @Test
    public void checkSendChargeNotificationReminderShouldBeSentWithDefaultSMSText() throws Exception {
        final long time = new Date().getTime();
        Monitor monitor = postsSaverPostService.getMonitor();
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.getUserGroup().getCommunity().setRewriteUrlParameter("o2");
        user.setProvider(ProviderType.O2);
        user.setMobile("+44789654123");
        userNotificationService.sendChargeNotificationReminder(user);
        monitor.waitToComplete(5000);

        File smsFile = getLastSmsFile(time);
        List<String> smsText = Files.readLines(smsFile, Charsets.UTF_8);
        assertTrue(smsText.contains("Parameter: BODY..Value: You are charged for 28 days continuously default text"));
    }

    @Test
    public void checkSendChargeNotificationReminderShouldNotBeSentForRejectedDevice() throws Exception {
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
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
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
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
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.getUserGroup().getCommunity().setRewriteUrlParameter("a_not_available_community");
        user.setProvider(ProviderType.VF);
        user.setMobile("+64789654123");
        userNotificationService.sendChargeNotificationReminder(user);
        verify(smsGatewayService, never()).send(anyString(), anyString(), anyString());
    }

    private File getLastSmsFile(long time) {
        File[] list = smsTemporaryFolder.listFiles(new TimestampExtFileNameFilter(time));

        Assert.assertEquals(1, list.length);

        return list[0];
    }

    private SMSResponse getSmsResponse() {
        return new SMSResponse() {
            @Override
            public boolean isSuccessful() {
                return true;
            }

            @Override
            public String getDescriptionError() {
                return null;
            }
        };
    }

}
