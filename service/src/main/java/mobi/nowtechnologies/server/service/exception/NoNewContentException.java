package mobi.nowtechnologies.server.service.exception;

/**
 * Created by Oleg Artomov on 10/3/2014.
 */
public class NoNewContentException extends RuntimeException {

    private final Long dateOfLastUpdateInMillis;

    private final Long checkForTimeInMillis;

    public NoNewContentException(Long dateOfLastUpdateInMillis, Long checkForTimeInMillis) {
        this.dateOfLastUpdateInMillis = dateOfLastUpdateInMillis;
        this.checkForTimeInMillis = checkForTimeInMillis;
    }

    public Long getDateOfLastUpdateInMillis() {
        return dateOfLastUpdateInMillis;
    }

    public Long getCheckForTimeInMillis() {
        return checkForTimeInMillis;
    }

}
