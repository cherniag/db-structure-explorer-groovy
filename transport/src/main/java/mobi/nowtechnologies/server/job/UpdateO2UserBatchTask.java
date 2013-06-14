package mobi.nowtechnologies.server.job;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

public class UpdateO2UserBatchTask implements Callable {
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
        if (isNotEmpty(usersId))
            updateBatchOfUsers();
        return null;
    }

    private void updateBatchOfUsers() {
        List<User> users = fetchUsersForUpdate();
        for (User u : users)
            task.handleUserUpdate(u);
    }

    public List<User> fetchUsersForUpdate() {
        List<User> result = new ArrayList<User>(usersId.size());
        for (Integer id : usersId) {
            result.add(userRepository.findOne(id));
        }
        return result;
    }
}
