package mobi.nowtechnologies.server.service.payment.response;

import mobi.nowtechnologies.server.shared.service.BasicResponse;

import org.springframework.util.StringUtils;

public abstract class PaymentSystemResponse {

    protected boolean isSuccessful;
    protected boolean isFuture;
    protected String descriptionError;
    protected int httpStatus;
    protected String message;
    protected String errorCode;

    public PaymentSystemResponse(BasicResponse response, boolean isFuture) {
        this(isFuture);

        if (!isFuture) {
            httpStatus = response.getStatusCode();
            if (StringUtils.hasLength(response.getMessage()) && response.getMessage().length() > 255) {
                message = response.getMessage().substring(0, 254);
            }
            else {
                message = response.getMessage();
            }
            descriptionError = "";
        }
    }

    public PaymentSystemResponse() {
    }

    public PaymentSystemResponse(boolean isFuture) {
        this.isFuture = isFuture;
    }

    public boolean isFuture() {
        return isFuture;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public String getDescriptionError() {
        return descriptionError;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return "PaymentSystemResponse{" +
               "isSuccessful=" + isSuccessful +
               ", isFuture=" + isFuture +
               ", descriptionError='" + descriptionError + '\'' +
               ", httpStatus=" + httpStatus +
               ", message='" + message + '\'' +
               ", errorCode='" + errorCode + '\'' +
               "} ";
    }
}