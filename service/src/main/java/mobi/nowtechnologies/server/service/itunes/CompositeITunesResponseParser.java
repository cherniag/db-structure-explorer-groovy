package mobi.nowtechnologies.server.service.itunes;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompositeITunesResponseParser implements ITunesResponseParser {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private List<ITunesResponseParser> parsers = new ArrayList<>();

    @Override
    public ITunesResult parseVerifyReceipt(String response) throws ITunesResponseParserException {
        ITunesResponseParserException e2Throw = null;
        for (ITunesResponseParser parser : parsers) {
            logger.debug("Try to parse iTunes response [{}] with parser [{}]", response, parser);

            try {
                ITunesResult parseResult = parser.parseVerifyReceipt(response);

                logger.debug("Parsed result is {}", parseResult);

                return parseResult;
            } catch (ITunesResponseParserException e) {
                logger.debug("Failed to parse with " + parser, e);
                e2Throw = e;
            }
        }
        logger.error("None of available parsers were able to parse iTunes response");
        throw e2Throw;
    }
}
