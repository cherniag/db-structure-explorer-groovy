package mobi.nowtechnologies.applicationtests.services.http.domain.facebook;

/**
 * @author kots
 * @since 8/20/2014.
 */
class DataWrapper {
    private User user;
    private Error errorMessage;

    public Error getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(Error errorMessage) {
        this.errorMessage = errorMessage;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
