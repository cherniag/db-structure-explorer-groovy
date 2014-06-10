package mobi.nowtechnologies.server.job;

import com.sentaca.spring.smpp.mt.MTMessage;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.domain.enums.TaskStatus;
import mobi.nowtechnologies.server.persistence.domain.task.SendChargeNotificationTask;
import mobi.nowtechnologies.server.persistence.domain.task.Task;
import mobi.nowtechnologies.server.persistence.repository.TaskRepository;
import mobi.nowtechnologies.server.persistence.repository.UserGroupRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.sms.SMPPServiceImpl;
import mobi.nowtechnologies.server.service.vodafone.impl.VFNZSMSGatewayServiceImpl;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

/**
 * User: gch
 * Date: 12/20/13
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextHierarchy({
        @ContextConfiguration(locations = {
                "classpath:transport-root-test.xml", "classpath:jobs-test.xml"}),
        @ContextConfiguration(locations = {
                "classpath:transport-servlet-test.xml"})})
@WebAppConfiguration
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class BusinessTaskJobTestIT {
    @Autowired
    private BusinessTaskJob businessTaskJob;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    @Qualifier("vf_nz.service.SmsProviderSpy")
    private VFNZSMSGatewayServiceImpl smsGatewayService;

    private SMPPServiceImpl smppService;

    @Resource
    private UserGroupRepository userGroupRepository;

    @Before
    public void setUp() throws Exception {
        smppService = mock(SMPPServiceImpl.class);
        when(smppService.sendMessage(any(MTMessage.class))).thenReturn(true);
        smsGatewayService.setSmppService(smppService);
        taskRepository.deleteAll();
    }

    @Test
    public void checkSendChargeNotificationTaskExecution() throws Exception {
        long now = System.currentTimeMillis();
        UserGroup userGroup = userGroupRepository.findOne(8);
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setId(0);
        user.setUserName("+64598720352");
        user.setUserGroup(userGroup);
        user.setProvider(ProviderType.VF);
        SendChargeNotificationTask sendChargeNotificationTask = TaskFactory.createSendChargeNotificationTask();
        sendChargeNotificationTask.setTaskStatus(TaskStatus.ACTIVE);
        sendChargeNotificationTask.setUser(userRepository.save(user));
        sendChargeNotificationTask.setCreationTimestamp(now - 2000L);
        sendChargeNotificationTask.setExecutionTimestamp(now);
        sendChargeNotificationTask = (SendChargeNotificationTask) taskRepository.save(sendChargeNotificationTask);
        businessTaskJob.execute();
        TimeUnit.SECONDS.sleep(3);
        Task saved = (Task) taskRepository.findOne(sendChargeNotificationTask.getId());
        assertThat(saved.getExecutionTimestamp(), is(now + 2000L));
        verify(smppService).sendMessage(argThat(getMTMMessageMatcherMatcher("You are charged for 28 days continuously", user.getMobile())));
    }

    @Test
    public void checkSendChargeNotificationTaskInFutureShouldNotBeSend() throws Exception {
        long now = System.currentTimeMillis();
        UserGroup userGroup = UserGroupFactory.createUserGroup();
        userGroup.setId(8);
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setId(0);
        user.setUserName("+6459336695");
        user.setUserGroup(userGroup);
        user.setProvider(ProviderType.VF);
        SendChargeNotificationTask sendChargeNotificationTask = TaskFactory.createSendChargeNotificationTask();
        sendChargeNotificationTask.setTaskStatus(TaskStatus.ACTIVE);
        sendChargeNotificationTask.setUser(userRepository.save(user));
        sendChargeNotificationTask.setCreationTimestamp(now - 2000L);
        sendChargeNotificationTask.setExecutionTimestamp(now + 3000L);
        sendChargeNotificationTask = (SendChargeNotificationTask) taskRepository.save(sendChargeNotificationTask);
        businessTaskJob.execute();
        TimeUnit.SECONDS.sleep(3);
        Task saved = (Task) taskRepository.findOne(sendChargeNotificationTask.getId());
        assertThat(saved.getExecutionTimestamp(), is(now + 3000L));
        verify(smppService, never()).sendMessage(argThat(getMTMMessageMatcherMatcher("You are charged for 28 days continuously", user.getMobile())));
    }

    private BaseMatcher<MTMessage> getMTMMessageMatcherMatcher(final String text, final String phone){
        return new BaseMatcher<MTMessage>() {
            @Override
            public boolean matches(Object o) {
                if (!(o instanceof MTMessage)){
                    return false;
                }
                MTMessage input = (MTMessage)o;
                return text.equals(input.getContent()) && phone.equals(input.getDestinationAddress());
            }
            @Override
            public void describeTo(Description description) {}
        } ;
    }
}
