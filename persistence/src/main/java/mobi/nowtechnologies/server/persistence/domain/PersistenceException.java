package mobi.nowtechnologies.server.persistence.domain;


/**
 * PersistenceException
 *
 * @author Maksym Chernolevskyi (maksym)
 */
public class PersistenceException extends RuntimeException {

    private static final long serialVersionUID = -6190452820362375521L;

    public PersistenceException() { }

    public PersistenceException(String message) {
        super(message);
    }

    public PersistenceException(Throwable e) {
        super(e);
    }

    public PersistenceException(String message, Throwable e) {
        super(message, e);
    }

}
