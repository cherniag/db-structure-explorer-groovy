package mobi.nowtechnologies.server.error;

public class ThrottlingException extends RuntimeException {

    private static final long serialVersionUID = -1400156279926467553L;

    private String username;
    private String community;

    public ThrottlingException(String username, String community) {
        this.username = username;
        this.community = community;
    }

    @Override
    public String toString() {
        return "[REJECTED] user: " + this.username + " community: " + this.community;
    }
}