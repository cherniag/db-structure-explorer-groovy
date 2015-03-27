package mobi.nowtechnologies.server.job;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.task.SendChargeNotificationTask;
import mobi.nowtechnologies.server.persistence.domain.task.Task;
import mobi.nowtechnologies.server.persistence.repository.TaskRepository;
import mobi.nowtechnologies.server.persistence.repository.UserGroupRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.sms.SMPPServiceImpl;
import mobi.nowtechnologies.server.service.sms.SMSResponse;
import mobi.nowtechnologies.server.service.vodafone.impl.VFNZSMSGatewayServiceImpl;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.ProviderType;

import javax.annotation.Resource;

import java.util.Date;

import com.sentaca.spring.smpp.mt.MTMessage;

import org.springframework.transaction.annotation.Transactional;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * User: gch Date: 12/20/13
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextHierarchy({@ContextConfiguration(locations = {"classpath:transport-root-test.xml"}), @ContextConfiguration(locations = {"classpath:transport-servlet-test.xml"})})
@WebAppConfiguration
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class BusinessTaskJobTestIT {

    @Resource
    private BusinessTaskJob sendChargeNotificationJob;

    @Resource
    private TaskRepository<Task> taskRepository;

    @Resource
    private UserRepository userRepository;

    @Resource(name = "vf_nz.service.SmsProviderSpy")
    private VFNZSMSGatewayServiceImpl smsGatewayService;

    @Resource
    private UserGroupRepository userGroupRepository;

    private SMPPServiceImpl smppService;
    private User user;

    @Before
    public void setUp() throws Exception {
        smppService = mock(SMPPServiceImpl.class);
        SMSResponse smsResponse = mock(SMSResponse.class);
        when(smsResponse.isSuccessful()).thenReturn(true);
        when(smppService.sendMessage(any(MTMessage.class))).thenReturn(smsResponse);
        smsGatewayService.setSmppService(smppService);
        taskRepository.deleteAll();
    }

    @Test
    public void checkSendChargeNotificationTaskExecution() throws Exception {
        long now = System.currentTimeMillis();
        UserGroup userGroup = userGroupRepository.findOne(8);
        user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setId(0);
        user.setUserName("+64598720352");
        user.setUserGroup(userGroup);
        user.setProvider(ProviderType.VF);
        SendChargeNotificationTask sendChargeNotificationTask = new SendChargeNotificationTask(new Date(), UserFactory.createUser(ActivationStatus.ACTIVATED));
        sendChargeNotificationTask.setUser(userRepository.save(user));

        ReflectionTestUtils.setField(sendChargeNotificationTask, "executionTimestamp", now);
        ReflectionTestUtils.setField(sendChargeNotificationTask, "creationTimestamp", now - 2000L);

        sendChargeNotificationTask = (SendChargeNotificationTask) taskRepository.save(sendChargeNotificationTask);
        sendChargeNotificationJob.execute();
        Task saved = (Task) taskRepository.findOne(sendChargeNotificationTask.getId());
        assertThat(saved.getExecutionTimestamp(), is(now + 2000L));
        verify(smppService).sendMessage(argThat(getMTMMessageMatcherMatcher("You are charged for 28 days continuously", user.getMobile())));
    }

    @Test
    public void checkSendChargeNotificationTaskInFutureShouldNotBeSend() throws Exception {
        long now = System.currentTimeMillis();
        UserGroup userGroup = userGroupRepository.findOne(8);
        userGroup.setId(8);
        user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setId(0);
        user.setUserName("+6459336695");
        user.setUserGroup(userGroup);
        user.setProvider(ProviderType.VF);
        SendChargeNotificationTask sendChargeNotificationTask = new SendChargeNotificationTask(new Date(), UserFactory.createUser(ActivationStatus.ACTIVATED));
        sendChargeNotificationTask.setUser(userRepository.save(user));

        ReflectionTestUtils.setField(sendChargeNotificationTask, "executionTimestamp", now + 3000L);
        ReflectionTestUtils.setField(sendChargeNotificationTask, "creationTimestamp", now - 2000L);

        sendChargeNotificationTask = (SendChargeNotificationTask) taskRepository.save(sendChargeNotificationTask);
        sendChargeNotificationJob.execute();
        Task saved = (Task) taskRepository.findOne(sendChargeNotificationTask.getId());
        assertThat(saved.getExecutionTimestamp(), is(now + 3000L));
        verify(smppService, never()).sendMessage(argThat(getMTMMessageMatcherMatcher("You are charged for 28 days continuously", user.getMobile())));
    }

    private BaseMatcher<MTMessage> getMTMMessageMatcherMatcher(final String text, final String phone) {
        return new BaseMatcher<MTMessage>() {
            @Override
            public boolean matches(Object o) {
                if (!(o instanceof MTMessage)) {
                    return false;
                }
                MTMessage input = (MTMessage) o;
                return text.equals(input.getContent()) && phone.equals(input.getDestinationAddress());
            }

            @Override
            public void describeTo(Description description) {}
        };
    }
}
