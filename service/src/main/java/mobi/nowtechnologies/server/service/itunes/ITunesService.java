package mobi.nowtechnologies.server.service.itunes;

import mobi.nowtechnologies.server.shared.service.BasicResponse;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public interface ITunesService {
	
	BasicResponse processInAppSubscription(int userId, String transactionReceipt);

}
