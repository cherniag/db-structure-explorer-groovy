package mobi.nowtechnologies.applicationtests.services.http.facebook;

import mobi.nowtechnologies.applicationtests.services.http.common.Error;
import mobi.nowtechnologies.applicationtests.services.http.domain.common.Response;
import mobi.nowtechnologies.applicationtests.services.http.domain.common.User;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author kots
 * @since 8/20/2014.
 */
@XmlRootElement(name = "response")
public class FacebookResponse {

    //json hack
    private Response response;
    private User user;
    private Error errorMessage;

    public Error getErrorMessage() {
        return errorMessage != null ?
               errorMessage :
               response.getData().get(0).getErrorMessage();
    }

    public void setErrorMessage(Error errorMessage) {
        this.errorMessage = errorMessage;
    }

    public User getUser() {
        boolean xmlCase = user != null;
        return xmlCase ?
               user :
               response.getData().get(0).getUser();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "FacebookResponse{" +
               "response=" + response +
               ", user=" + user +
               ", errorMessage=" + errorMessage +
               '}';
    }
}
