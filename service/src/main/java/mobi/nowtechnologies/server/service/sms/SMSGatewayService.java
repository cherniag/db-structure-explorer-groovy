package mobi.nowtechnologies.server.service.sms;

/**
 * User: Alexsandr_Kolpakov
 * Date: 9/27/13
 * Time: 1:12 PM
 */
public interface SMSGatewayService<T extends SMSResponse> {
     T send(String numbers, String message, String title);
}
