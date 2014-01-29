package mobi.nowtechnologies.server.service.exception;

/**
 * User: Alexsandr_Kolpakov
 * Date: 11/11/13
 * Time: 11:28 AM
 */
public class CanNotDeactivatePaymentDetailsException extends ServiceException{

    public CanNotDeactivatePaymentDetailsException() {
        super("Can not deactivate payments details in pending status.");
    }
}
