package mobi.nowtechnologies.server.job.task;

import mobi.nowtechnologies.server.persistence.domain.NZSubscriberInfo;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.task.SendChargeNotificationTask;
import mobi.nowtechnologies.server.persistence.domain.task.SendPaymentErrorNotificationTask;
import mobi.nowtechnologies.server.persistence.repository.NZSubscriberInfoRepository;
import mobi.nowtechnologies.server.service.TaskService;
import mobi.nowtechnologies.server.service.UserNotificationService;
import mobi.nowtechnologies.server.service.exception.ServiceException;

import java.util.Date;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SimpleUserNotificationTaskProcessorTest {
    @Mock
    private NZSubscriberInfoRepository nzSubscriberInfoRepository;
    @Mock
    private UserNotificationService userNotificationService;
    @Mock
    private TaskService taskService;
    @InjectMocks
    private SimpleUserNotificationTaskProcessor simpleUserNotificationTaskProcessor;
    @Mock
    private User user;
    @Mock
    private NZSubscriberInfo nzSubscriberInfo;

    private String messageKey = "sms.message.key";
    private int userId = 100;
    private String msisdn = "+641234567777";

    @Before
    public void setUp() throws Exception {
        simpleUserNotificationTaskProcessor.setMessageKey(messageKey);
        when(nzSubscriberInfo.getMsisdn()).thenReturn(msisdn);
        when(user.getId()).thenReturn(userId);
    }

    @Test
    public void processSuccess() throws Exception {
        SendPaymentErrorNotificationTask task = mock(SendPaymentErrorNotificationTask.class);
        when(task.getUser()).thenReturn(user);
        when(nzSubscriberInfoRepository.findSubscriberInfoByUserId(userId)).thenReturn(nzSubscriberInfo);

        simpleUserNotificationTaskProcessor.process(task);

        verify(userNotificationService).sendSMSByKey(user, msisdn, messageKey);
        verify(taskService).removeTask(task);
    }

    @Test
    public void processIfNoNZSubscriberInfo() throws Exception {
        SendPaymentErrorNotificationTask task = mock(SendPaymentErrorNotificationTask.class);
        when(task.getUser()).thenReturn(user);
        when(nzSubscriberInfoRepository.findSubscriberInfoByUserId(userId)).thenReturn(null);

        simpleUserNotificationTaskProcessor.process(task);

        verify(userNotificationService, never()).sendSMSByKey(eq(user), anyString(), eq(messageKey));
        verify(taskService).removeTask(task);
    }

    @Test
    public void processIfExceptionOnSMSSend() throws Exception {
        SendPaymentErrorNotificationTask task = mock(SendPaymentErrorNotificationTask.class);
        when(task.getUser()).thenReturn(user);
        when(nzSubscriberInfoRepository.findSubscriberInfoByUserId(userId)).thenReturn(nzSubscriberInfo);
        when(userNotificationService.sendSMSByKey(user, msisdn, messageKey)).thenThrow(new ServiceException(""));

        simpleUserNotificationTaskProcessor.process(task);

        verify(userNotificationService).sendSMSByKey(user, msisdn, messageKey);
        verify(taskService).removeTask(task);
    }

    @Test
    public void testSupports() throws Exception {
        simpleUserNotificationTaskProcessor.setSupportedTaskType(SendPaymentErrorNotificationTask.TASK_TYPE);
        SendPaymentErrorNotificationTask task = new SendPaymentErrorNotificationTask(new Date(), user);

        boolean supports = simpleUserNotificationTaskProcessor.supports(task);

        assertTrue(supports);
    }

    @Test
    public void testNotSupports() throws Exception {
        simpleUserNotificationTaskProcessor.setSupportedTaskType(SendPaymentErrorNotificationTask.TASK_TYPE);
        SendChargeNotificationTask task = new SendChargeNotificationTask(new Date(), user);

        boolean notSupports = simpleUserNotificationTaskProcessor.supports(task);

        assertFalse(notSupports);
    }
}