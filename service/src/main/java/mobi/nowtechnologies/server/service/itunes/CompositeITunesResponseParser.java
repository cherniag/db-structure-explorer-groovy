package mobi.nowtechnologies.server.service.itunes;

import mobi.nowtechnologies.server.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

public class CompositeITunesResponseParser implements ITunesResponseParser {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private List<ITunesResponseParser> parsers = new ArrayList<ITunesResponseParser>();

    @Override
    public ITunesResult parseVerifyReceipt(String response) throws ITunesResponseParserException {
        for (ITunesResponseParser parser : parsers) {
            logger.debug("Try to parse iTunes response [{}] with parser [{}]", response, parser);

            try {
                ITunesResult parseResult = parser.parseVerifyReceipt(response);

                logger.debug("Parsed result is {}", parseResult);

                return parseResult;
            } catch (ITunesResponseParserException e) {
                logger.debug("Failed to parse with " + parser, e);
            }
        }

        throw new ServiceException("Couldn't parse response: " + response + " with parsers: " + parsers);
    }
}