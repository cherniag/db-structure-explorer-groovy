package mobi.nowtechnologies.server.service.exception;

import mobi.nowtechnologies.common.util.ServerMessage;

/**
 * @author Maksym Chernolevskyi (maksym)
 */
public class UserCredentialsException extends ServiceException {
    public UserCredentialsException(ServerMessage serverMessage) {
        super(serverMessage);
    }

}
