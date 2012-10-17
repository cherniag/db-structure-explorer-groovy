package mobi.nowtechnologies.server.service.exception;

import mobi.nowtechnologies.common.util.ServerMessage;

/**
 * UserCredentialsException
 * 
 * @author Maksym Chernolevskyi (maksym)
 */
public class UserCredentialsException extends ServiceException {
	private static final long serialVersionUID = 1L;

	public UserCredentialsException(String message) {
		super(message);
	}

	public UserCredentialsException(String message, Throwable e) {
		super(message, e);
	}
	
	public UserCredentialsException(ServerMessage serverMessage) {
		super(serverMessage);
	}
	
}
