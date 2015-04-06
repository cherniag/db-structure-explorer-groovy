package mobi.nowtechnologies.applicationtests.services.http.googleplus;

import mobi.nowtechnologies.applicationtests.services.http.common.Error;
import mobi.nowtechnologies.applicationtests.services.http.domain.common.Response;
import mobi.nowtechnologies.applicationtests.services.http.domain.common.User;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author kots
 * @since 9/11/2014.
 */
@XmlRootElement(name = "response")
public class GooglePlusResponse {

    private Error errorMessage;
    private Response response;
    private User user;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
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

    public Error getErrorMessage() {
        return errorMessage != null ?
               errorMessage :
               response.getData().get(0).getErrorMessage();
    }

    public void setErrorMessage(Error errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "GooglePlusResponse{" +
               "errorMessage=" + errorMessage +
               ", response=" + response +
               ", user=" + user +
               '}';
    }
}
