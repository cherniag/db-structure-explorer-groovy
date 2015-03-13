package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.User;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * User: gch Date: 12/19/13
 */
public class MatchUtils {

    public static Matcher<User> getUserIdAndUserNameMatcher(final User user) {
        return new BaseMatcher<User>() {
            @Override
            public boolean matches(Object o) {
                if (!(o instanceof User)) {
                    return false;
                }
                User incomeUser = (User) o;
                if (user == incomeUser) {
                    return true;
                }
                return user.getId() == incomeUser.getId() &&
                       ((user.getUserName() != null && user.getUserName().equals(incomeUser.getUserName())) || (user.getUserName() == null && incomeUser.getUserName() == null)

                       );
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Users have different id and(or) userName");
            }
        };
    }
}
