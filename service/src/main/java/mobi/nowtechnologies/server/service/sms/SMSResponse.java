package mobi.nowtechnologies.server.service.sms;

/**
 * Created with IntelliJ IDEA. User: Alexsandr_Kolpakov Date: 9/27/13 Time: 1:38 PM To change this template use File | Settings | File Templates.
 */
public interface SMSResponse {
    boolean isSuccessful();
    String getDescriptionError();
}
