package mobi.nowtechnologies.applicationtests.services.http.domain.common;

import mobi.nowtechnologies.applicationtests.services.http.common.Error;

/**
 * Created by kots on 9/11/2014.
 */
public class DataWrapper {
    private mobi.nowtechnologies.applicationtests.services.http.common.Error errorMessage;
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
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
