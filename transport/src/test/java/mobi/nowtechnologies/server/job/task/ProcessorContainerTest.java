package mobi.nowtechnologies.server.job.task;

import mobi.nowtechnologies.server.persistence.domain.task.SendChargeNotificationTask;
import mobi.nowtechnologies.server.persistence.domain.task.SendPaymentErrorNotificationTask;
import mobi.nowtechnologies.server.persistence.domain.task.UserTask;

import java.util.ArrayList;
import java.util.List;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * User: gch Date: 12/16/13
 */
@RunWith(MockitoJUnitRunner.class)
public class ProcessorContainerTest {

    @InjectMocks
    private ProcessorContainer processorContainer;
    @Mock
    private SendChargeNotificationTaskProcessor sendChargeNotificationProcessor;

    @Before
    public void setUp() throws Exception {
        List<TaskProcessor> processors = new ArrayList<>();
        processors.add(sendChargeNotificationProcessor);
        processorContainer.setProcessors(processors);
    }

    @Test
    public void checkSendChargeNotificationTaskProcessing(){
        when(sendChargeNotificationProcessor.supports(any(SendChargeNotificationTask.class))).thenReturn(true);
        SendChargeNotificationTask task = mock(SendChargeNotificationTask.class);

        processorContainer.process(task);

        Mockito.verify(sendChargeNotificationProcessor).process(task);
    }

    @Test
    public void checkSendPaymentErrorNotificationTaskProcessing(){
        when(sendChargeNotificationProcessor.supports(any(SendChargeNotificationTask.class))).thenReturn(false);
        UserTask task = mock(SendPaymentErrorNotificationTask.class);

        processorContainer.process(task);

        Mockito.verify(sendChargeNotificationProcessor, never()).process(task);
    }

}
