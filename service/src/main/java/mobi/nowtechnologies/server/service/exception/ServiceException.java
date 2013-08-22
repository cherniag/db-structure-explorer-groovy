/**
 * 
 */
package mobi.nowtechnologies.server.service.exception;

import mobi.nowtechnologies.common.util.ServerMessage;

import java.util.Map;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class ServiceException extends RuntimeException {
	private static final String DEFAULT_ERROR_CODE = "error.external";
	private static final long serialVersionUID = 1L;
	
	private ServerMessage serverMessage;
    private Integer errorCode;
	private String localizedMessage;
	protected String errorCodeForMessageLocalization = DEFAULT_ERROR_CODE;

	public ServiceException(String message) {
		super(message);
	}
	
	public ServiceException(String message, Throwable e) {
		super(message, e);
	}
	
	public ServiceException(ServerMessage serverMessage) {
		this.serverMessage = serverMessage;
	}
	
	public ServiceException(String code, String defaultMessage) {
		this.errorCodeForMessageLocalization = code;
		this.localizedMessage = defaultMessage;
	}

	public ServiceException(String code, String defaultMessage, String message) {
		super(message);
		
		this.errorCodeForMessageLocalization = code;
		this.localizedMessage = defaultMessage;
	}
	
	public ServerMessage getServerMessage() {
		return serverMessage;
	}
	
	public void setLocalizedMessage(String localizedMessage){
		if (localizedMessage == null)
			throw new NullPointerException(
					"The parameter localizedMessage is null");
		
		this.localizedMessage = localizedMessage;
	}
	
	@Override
	public String getLocalizedMessage() {
		return localizedMessage;
	}

	public String getErrorCodeForMessageLocalization() {
		return errorCodeForMessageLocalization;
	}

	public static ServiceException getInstance(String errorCodeForMessageLocalization) {
		ServiceException serviceException = new ServiceException(errorCodeForMessageLocalization, "");
		return serviceException;
	}
	
	public Integer getErrorCode() {
		return errorCode;
	}
	
	public String getDefaultMessage() {
		return localizedMessage;
	}
	
	@Override
	public String toString() {
		return "ServiceException [message=" +getMessage()+ ", localizedMessage=" + localizedMessage + ", errorCodeForMessageLocalization=" + errorCodeForMessageLocalization + ", serverMessage="
				+ serverMessage + "]";
	}

    public ServiceException addErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public enum Error{
        NOT_ELIGIBLE(5001);

        private int code;

        Error(int code){
            this.code = code;
        }
        public int getCode(){
            return code;
        }
        @Override
        public String toString(){
            return new Integer(code).toString();
        }
    }
}
