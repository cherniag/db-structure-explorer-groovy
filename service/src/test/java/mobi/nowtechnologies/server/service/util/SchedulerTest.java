package mobi.nowtechnologies.server.service.util;

import mobi.nowtechnologies.server.persistence.domain.TaskFactory;
import mobi.nowtechnologies.server.persistence.domain.task.SendChargeNotificationTask;
import mobi.nowtechnologies.server.persistence.domain.task.Task;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.*;

/**
 * User: gch
 * Date: 12/18/13
 */
@RunWith(MockitoJUnitRunner.class)
public class SchedulerTest {
    public static final String SEND_CHARGE_NOTIFICATION_TASK_SCHEDULE = "sendchargenotificationtask.schedule.period.in.millis";
    public static final String COMMUNITY_WITH_EXISTING_PROPERTY = "vf_nz";
    public static final String COMMUNITY_WITH_WRONG_PROPERTY = "o2";
    public static final String COMMUNITY_WITH_NULL_PROPERTY = "mts";
    @Mock
    private CommunityResourceBundleMessageSource messageSource;

    @InjectMocks
    private Scheduler scheduler;

    @Before
    public void setUp() throws Exception {
        when(messageSource.getMessage(eq(COMMUNITY_WITH_EXISTING_PROPERTY), eq(SEND_CHARGE_NOTIFICATION_TASK_SCHEDULE), any(Object[].class), any(Locale.class))).thenReturn("1000");
        when(messageSource.getMessage(isNull(String.class), eq(SEND_CHARGE_NOTIFICATION_TASK_SCHEDULE), any(Object[].class), any(Locale.class))).thenReturn("5000");
        when(messageSource.getMessage(eq(COMMUNITY_WITH_WRONG_PROPERTY), eq(SEND_CHARGE_NOTIFICATION_TASK_SCHEDULE), any(Object[].class), any(Locale.class))).thenReturn("it's not a number");
        when(messageSource.getMessage(eq(COMMUNITY_WITH_NULL_PROPERTY), eq(SEND_CHARGE_NOTIFICATION_TASK_SCHEDULE), any(Object[].class), any(Locale.class))).thenReturn(null);
    }

    @Test
    public void checkScheduleTaskForExistingCommunity(){
        Task task = new SendChargeNotificationTask();
        long creationTimestamp = System.currentTimeMillis();
        task.setCreationTimestamp(creationTimestamp);
        scheduler.scheduleTask(COMMUNITY_WITH_EXISTING_PROPERTY,task);
        assertThat(task.getExecutionTimestamp(), is(creationTimestamp + 1000L));
        verify(messageSource, times(1)).getMessage(eq(COMMUNITY_WITH_EXISTING_PROPERTY), eq(SEND_CHARGE_NOTIFICATION_TASK_SCHEDULE), any(Object[].class), any(Locale.class));
    }

    @Test
    public void checkScheduleTaskForNullCommunityAndDefaultSchedulePropertyShouldBeUsed(){
        Task task = new SendChargeNotificationTask();
        long creationTimestamp = System.currentTimeMillis();
        task.setCreationTimestamp(creationTimestamp);
        scheduler.scheduleTask(null,task);
        assertThat(task.getExecutionTimestamp(), is(creationTimestamp + 5000L));
        verify(messageSource, times(1)).getMessage(isNull(String.class), eq(SEND_CHARGE_NOTIFICATION_TASK_SCHEDULE), any(Object[].class), any(Locale.class));
    }

    @Test(expected = ServiceException.class)
    public void checkScheduleTaskForWrongScheduleProperty(){
        Task task = new SendChargeNotificationTask();
        scheduler.scheduleTask(COMMUNITY_WITH_WRONG_PROPERTY,task);
        verify(messageSource, times(1)).getMessage(eq(COMMUNITY_WITH_WRONG_PROPERTY), eq(SEND_CHARGE_NOTIFICATION_TASK_SCHEDULE), any(Object[].class), any(Locale.class));
    }

    @Test(expected = ServiceException.class)
    public void checkScheduleTaskForNullScheduleProperty(){
        Task task = new SendChargeNotificationTask();
        scheduler.scheduleTask(COMMUNITY_WITH_NULL_PROPERTY,task);
        verify(messageSource, times(1)).getMessage(eq(COMMUNITY_WITH_NULL_PROPERTY), eq(SEND_CHARGE_NOTIFICATION_TASK_SCHEDULE), any(Object[].class), any(Locale.class));
    }

    @Test
    public void checkScheduleTaskForExistingCommunityWithoutCreationTimeStamp(){
        Task task = new SendChargeNotificationTask();
        scheduler.scheduleTask(COMMUNITY_WITH_EXISTING_PROPERTY,task);
        assertThat(task.getExecutionTimestamp(), not(is(0L)));
        assertThat(task.getExecutionTimestamp(), greaterThan(System.currentTimeMillis()));
        verify(messageSource, times(1)).getMessage(eq(COMMUNITY_WITH_EXISTING_PROPERTY), eq(SEND_CHARGE_NOTIFICATION_TASK_SCHEDULE), any(Object[].class), any(Locale.class));
    }

    @Test
    public void testReScheduleTask() throws Exception {
        Task task = TaskFactory.createSendChargeNotificationTask();
        long now = System.currentTimeMillis();
        task.setCreationTimestamp(now - 5000L);
        task.setExecutionTimestamp(now);
        scheduler.reScheduleTask(COMMUNITY_WITH_EXISTING_PROPERTY, task);
        assertThat(task.getExecutionTimestamp(), is(now + 1000L));
        verify(messageSource, times(1)).getMessage(eq(COMMUNITY_WITH_EXISTING_PROPERTY), eq(SEND_CHARGE_NOTIFICATION_TASK_SCHEDULE), any(Object[].class), any(Locale.class));
    }

    @Test(expected = ServiceException.class)
    public void checkReScheduleTaskForWrongScheduleProperty(){
        Task task = TaskFactory.createSendChargeNotificationTask();
        scheduler.scheduleTask(COMMUNITY_WITH_WRONG_PROPERTY,task);
        verify(messageSource, times(1)).getMessage(eq(COMMUNITY_WITH_WRONG_PROPERTY), eq(SEND_CHARGE_NOTIFICATION_TASK_SCHEDULE), any(Object[].class), any(Locale.class));
    }
}