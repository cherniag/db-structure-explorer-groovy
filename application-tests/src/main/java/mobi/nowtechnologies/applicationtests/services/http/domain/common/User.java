package mobi.nowtechnologies.applicationtests.services.http.domain.common;

import mobi.nowtechnologies.applicationtests.services.http.common.UserInResponse;

/**
 * @author kots
 * @since 8/20/2014.
 */
public class User extends UserInResponse {
    private UserDetails userDetails;

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }
}
