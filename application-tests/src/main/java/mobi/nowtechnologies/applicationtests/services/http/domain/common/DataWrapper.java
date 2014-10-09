package mobi.nowtechnologies.applicationtests.services.http.domain.common;

/**
 * Created by kots on 9/11/2014.
 */
public class DataWrapper {
    private mobi.nowtechnologies.applicationtests.services.http.domain.common.Error errorMessage;
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public mobi.nowtechnologies.applicationtests.services.http.domain.common.Error getErrorMessage() {
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
