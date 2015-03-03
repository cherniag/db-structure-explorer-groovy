package mobi.nowtechnologies.server.job.task;

import mobi.nowtechnologies.server.persistence.domain.TaskFactory;
import mobi.nowtechnologies.server.persistence.domain.task.SendChargeNotificationTask;
import mobi.nowtechnologies.server.persistence.domain.task.Task;
import mobi.nowtechnologies.server.shared.Processor;

import java.util.HashMap;
import java.util.Map;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import static org.mockito.Matchers.*;

/**
 * User: gch Date: 12/16/13
 */
@RunWith(MockitoJUnitRunner.class)
public class ProcessorContainerTest {

    @InjectMocks
    private ProcessorContainer processorContainer;
    @Mock
    private Processor<Task> sendChargeNotificationProcessor;

    @Before
    public void setUp() throws Exception {
        Map<String, Processor<Task>> processorMap = new HashMap<String, Processor<Task>>();
        processorMap.put("SendChargeNotificationTask", sendChargeNotificationProcessor);
        processorContainer.setProcessorMap(processorMap);
    }

    @Test
    public void checkSendChargeNotificationTaskProcessing() {
        Task task = TaskFactory.createSendChargeNotificationTask();
        processorContainer.process(task);
        Mockito.verify(sendChargeNotificationProcessor).process(any(SendChargeNotificationTask.class));
    }


}
