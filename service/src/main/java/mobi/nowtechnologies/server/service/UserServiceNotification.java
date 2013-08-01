package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.User;

/**
 * 
 * Methods of this class are intercepted by AOP and used to send sms messages. The methods
 * are not in UserService because AOP can't intercept calls in the same object
 *
 */
public class UserServiceNotification {

	public void sendSmsFor4GDowngradeForFreeTrial(User user) {
    	// this method call is intercepted with AOP and a message will be sent to the user
    }

    public void sendSmsFor4GDowngradeForSubscribed(User user) {
    	// this method call is intercepted with AOP and a message will be sent to the user
    }
	
}
