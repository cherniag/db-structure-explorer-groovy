package mobi.nowtechnologies.server.service.streamzine;

import mobi.nowtechnologies.server.service.exception.ServiceException;

import java.util.List;

public class StreamzineNotAvailable extends ServiceException {

    public StreamzineNotAvailable(String community, List<String> available) {
        super(createMessage(community, available));
    }

    private static String createMessage(String community, List<String> available) {
        return "Not available for " + community + ", allowed for: " + available;
    }
}
