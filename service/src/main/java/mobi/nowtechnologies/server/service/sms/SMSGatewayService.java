package mobi.nowtechnologies.server.service.sms;

/**
 * Created with IntelliJ IDEA.
 * User: Alexsandr_Kolpakov
 * Date: 9/27/13
 * Time: 1:12 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SMSGatewayService<T extends SMSResponse> {
     T send(String numbers, String message, String title);
}
