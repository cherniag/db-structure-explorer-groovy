package mobi.nowtechnologies.applicationtests.services.repeat;


import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;

public class UserRepeatable implements Repeatable<User> {

    private UserRepository userRepository;
    private User originalUser;
    private User found;

    public UserRepeatable(UserRepository userRepository, User originalUser) {
        this.userRepository = userRepository;
        this.originalUser = originalUser;
    }

    @Override
    public boolean again() {
        return found.getNextSubPayment() == originalUser.getNextSubPayment();
    }

    @Override
    public User result() {
        found = userRepository.findOne(originalUser.getId());

        return found;
    }
}
