package mobi.nowtechnologies.server.job.task;

import mobi.nowtechnologies.server.persistence.domain.NZSubscriberInfo;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.task.SendChargeNotificationTask;
import mobi.nowtechnologies.server.persistence.domain.task.SendPaymentErrorNotificationTask;
import mobi.nowtechnologies.server.persistence.repository.NZSubscriberInfoRepository;
import mobi.nowtechnologies.server.service.TaskService;
import mobi.nowtechnologies.server.service.UserNotificationService;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
        SendPaymentErrorNotificationTask task = new SendPaymentErrorNotificationTask();

        boolean supports = simpleUserNotificationTaskProcessor.supports(task);

        assertTrue(supports);
    }

    @Test
    public void testNotSupports() throws Exception {
        simpleUserNotificationTaskProcessor.setSupportedTaskType(SendPaymentErrorNotificationTask.TASK_TYPE);
        SendChargeNotificationTask task = new SendChargeNotificationTask();

        boolean notSupports = simpleUserNotificationTaskProcessor.supports(task);

        assertFalse(notSupports);
    }
}