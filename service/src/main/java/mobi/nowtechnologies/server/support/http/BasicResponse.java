package mobi.nowtechnologies.server.support.http;

/**
 * Created with IntelliJ IDEA. User: Alexsandr_Kolpakov Date: 9/27/13 Time: 1:27 PM To change this template use File | Settings | File Templates.
 */
public class BasicResponse {

    private String message;
    private int statusCode;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("BasicResponse [message=")
                                  .append(getMessage() != null ? getMessage().replaceAll("\r\n", ", ") : null)
                                  .append(", statusCode=")
                                  .append(getStatusCode())
                                  .append("]")
                                  .toString();
    }
}
