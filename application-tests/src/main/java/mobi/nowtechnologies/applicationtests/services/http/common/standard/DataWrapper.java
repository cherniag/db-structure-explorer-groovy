package mobi.nowtechnologies.applicationtests.services.http.common.standard;

import mobi.nowtechnologies.applicationtests.services.http.common.Error;
import mobi.nowtechnologies.applicationtests.services.http.common.UserInResponse;

/**
 * Created by kots on 9/11/2014.
 */
public class DataWrapper {
    private Error errorMessage;
    private UserInResponse user;

    public UserInResponse getUser() {
        return user;
    }

    public void setUser(UserInResponse user) {
        this.user = user;
    }

    public Error getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(Error errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "DataWrapper{" +
                "errorMessage=" + errorMessage +
                ", user=" + user +
                '}';
    }
}
