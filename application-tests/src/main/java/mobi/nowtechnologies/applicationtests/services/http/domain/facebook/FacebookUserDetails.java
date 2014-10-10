package mobi.nowtechnologies.applicationtests.services.http.domain.facebook;

import mobi.nowtechnologies.applicationtests.services.http.domain.common.UserDetails;

/**
 * Created by kots on 9/12/2014.
 */
public class FacebookUserDetails extends UserDetails {
    private String facebookId;

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    @Override
    public String toString() {
        return "FacebookUserDetails{" +
                "facebookId='" + facebookId + '\'' +
                '}';
    }
}
