package mobi.nowtechnologies.server.service.payment.response;


import mobi.nowtechnologies.server.support.http.BasicResponse;

/**
 * @author Titov Mykhaylo (titov)
 */
public class MigResponseFactory {

    public static MigResponse createFailMigResponse() {
        return MigResponse.failMigResponse("");
    }

    public static MigResponse createSuccessfulMigResponse() {
        return MigResponse.successfulMigResponse();
    }


    public static MigResponse createMigResponse2() {
        return new MigResponse(new BasicResponse());
    }
}