package mobi.nowtechnologies.server.apptests.facebook;

import java.util.Locale;

import org.springframework.social.facebook.api.FacebookProfile;

class FailureFacebookProfile extends FacebookProfile {

    public FailureFacebookProfile(String id, String username, String name, String firstName, String lastName, String gender) {
        super(id, username, name, firstName, lastName, gender, Locale.getDefault());
    }
}
