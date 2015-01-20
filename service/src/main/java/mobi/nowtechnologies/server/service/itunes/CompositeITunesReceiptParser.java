package mobi.nowtechnologies.server.service.itunes;

import mobi.nowtechnologies.server.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

public class CompositeITunesReceiptParser implements ITunesReceiptParser {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private List<ITunesReceiptParser> parsers = new ArrayList<ITunesReceiptParser>();

    @Override
    public ITunesParseResult parse(String response) throws ITunesReceiptParseException {
        for (ITunesReceiptParser parser : parsers) {
            logger.debug("Try to parse iTunes response [{}] with parser [{}]", response, parser);

            try {
                ITunesParseResult parseResult = parser.parse(response);

                logger.debug("Parsed result is {}", parseResult);

                return parseResult;
            } catch (ITunesReceiptParseException e) {
                logger.debug("Failed to parse with " + parser, e);
            }
        }

        throw new ServiceException("Couldn't parse response: " + response + " with parsers: " + parsers);
    }
}
