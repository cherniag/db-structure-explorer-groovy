package mobi.nowtechnologies.server.service.data;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.UserService;

import java.util.List;

/**
 * User: Alexsandr_Kolpakov Date: 10/2/13 Time: 12:32 PM
 */
public abstract class BasicUserDetailsUpdater<T extends SubscriberData> implements UserDetailsUpdater<T> {

    private UserService userService;

    private UserRepository userRepository;

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void process(T data) {
        List<User> list = userRepository.findByMobile(data.getPhoneNumber());

        for (User user : list) {
            userService.populateSubscriberData(user, data);
        }
    }
}
