package mobi.nowtechnologies.server.job;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.shared.log.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

public class UpdateO2UserBatchTask implements Callable<Object> {
    private transient static final Logger LOG = LoggerFactory.getLogger(UpdateO2UserBatchTask.class);

    private transient UserRepository userRepository;
    private transient UpdateO2UserTask task;
    private transient List<Integer> usersId;

    public UpdateO2UserBatchTask(List<Integer> usersId) {
        this.usersId = usersId;
        userRepository = (UserRepository) SpringContext.getBean("userRepository");
        task = (UpdateO2UserTask) SpringContext.getBean("job.UpdateO2UserTask");
    }

    @Override
    public Object call() throws Exception {
        LOG.info("Update O2 batch [{}]", usersId.size());
    	if (isNotEmpty(usersId)){
            updateBatchOfUsers();
        }
        return null;
    }

    private void updateBatchOfUsers() {
    	long beforeExecutionTimeNano = System.nanoTime();
    	List<User> users = fetchUsersForUpdate();
    	long executionDurationMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - beforeExecutionTimeNano);
    	LOG.info("read [{}] users in [{}] ms", users.size(), executionDurationMillis);
    	
    	beforeExecutionTimeNano = System.nanoTime();
    	for (User u : users){
            task.handleUserUpdate(u);
        }
    	executionDurationMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - beforeExecutionTimeNano);
        LOG.info("[{}] users updated in [{}] ms", users.size(), executionDurationMillis);
    }

    public List<User> fetchUsersForUpdate() {
        List<User> result = new ArrayList<User>(usersId.size());
        for (Integer id : usersId) {
            result.add(userRepository.findOne(id));
        }
        return result;
    }
}
