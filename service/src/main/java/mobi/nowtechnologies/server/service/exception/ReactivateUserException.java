package mobi.nowtechnologies.server.service.exception;

/**
 * Created by oar on 4/30/2014.
 */
public class ReactivateUserException extends ServiceException {

    public ReactivateUserException() {
        super("601", "Reactivation required");
    }


}
