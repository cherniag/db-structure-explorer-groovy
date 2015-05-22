package mobi.nowtechnologies.server.service.payment.response;

import mobi.nowtechnologies.server.support.http.BasicResponse;

import javax.servlet.http.HttpServletResponse;

/**
 * Author: Gennadii Cherniaiev Date: 4/18/2014
 */
public class InternalErrorResponse extends PaymentSystemResponse {

    private InternalErrorResponse(BasicResponse response, String errorDescription) {
        super(response, false);
        this.descriptionError = errorDescription;
        this.isSuccessful = false;
    }

    public static InternalErrorResponse createErrorResponse(String errorDescription) {
        BasicResponse basicResponse = new BasicResponse();
        basicResponse.setStatusCode(HttpServletResponse.SC_OK);
        basicResponse.setMessage("Internal error while executing");
        return new InternalErrorResponse(basicResponse, errorDescription);
    }
}
