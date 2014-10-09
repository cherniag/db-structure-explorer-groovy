package mobi.nowtechnologies.applicationtests.services.http.domain.google_plus;

import mobi.nowtechnologies.applicationtests.services.http.domain.common.UserDetails;

/**
 * Created by kots on 9/12/2014.
 */
public class GooglePlusUserDetails extends UserDetails {
    private String googlePlusId;

    public String getGooglePlusId() {
        return googlePlusId;
    }

    public void setGooglePlusId(String googlePlusId) {
        this.googlePlusId = googlePlusId;
    }

    @Override
    public String toString() {
        return "GooglePlusUserDetails{" +
                "googlePlusId='" + googlePlusId + '\'' +
                '}';
    }
}
