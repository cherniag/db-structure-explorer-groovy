package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.shared.service.PostService.Response;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public interface ITunesService {
	
	Response processInAppSubscription(int userId, String transactionReceipt);

}
