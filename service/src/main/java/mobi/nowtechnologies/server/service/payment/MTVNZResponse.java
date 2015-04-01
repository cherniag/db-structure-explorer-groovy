package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.service.payment.response.PaymentSystemResponse;


/**
 * Author: Gennadii Cherniaiev
 * Date: 2/27/2015
 */
public class MTVNZResponse extends PaymentSystemResponse {
    private static final int HTTP_OK = 200;
    private static final int HTTP_SERVICE_UNAVAILABLE = 503;

    private String phoneNumber;


    public static MTVNZResponse successfulResponse(String phoneNumber){
        MTVNZResponse mtvnzResponse = new MTVNZResponse();
        mtvnzResponse.phoneNumber = phoneNumber;
        mtvnzResponse.httpStatus = HTTP_OK;
        mtvnzResponse.isSuccessful = true;
        return mtvnzResponse;
    }

    public static MTVNZResponse errorResponse(String phoneNumber, String errorCode, String descriptionError){
        MTVNZResponse mtvnzResponse = new MTVNZResponse();
        mtvnzResponse.phoneNumber = phoneNumber;
        mtvnzResponse.errorCode = errorCode;
        mtvnzResponse.descriptionError = descriptionError;
        mtvnzResponse.httpStatus = HTTP_OK;
        mtvnzResponse.isSuccessful = false;
        return mtvnzResponse;
    }

    public static MTVNZResponse serviceUnavailableResponse(String phoneNumber, String descriptionError){
        MTVNZResponse mtvnzResponse = new MTVNZResponse();
        mtvnzResponse.phoneNumber = phoneNumber;
        mtvnzResponse.descriptionError = descriptionError;
        mtvnzResponse.httpStatus = HTTP_SERVICE_UNAVAILABLE;
        mtvnzResponse.isSuccessful = false;
        return mtvnzResponse;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public String toString() {
        return "MTVNZResponse{" +
               "phoneNumber='" + phoneNumber + '\'' +
               ", isSuccessful=" + isSuccessful +
               ", isFuture=" + isFuture +
               ", descriptionError='" + descriptionError + '\'' +
               ", httpStatus=" + httpStatus +
               ", message='" + message + '\'' +
               ", errorCode='" + errorCode + '\'' +
               '}';
    }
}
