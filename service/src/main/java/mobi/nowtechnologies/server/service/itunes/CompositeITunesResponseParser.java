package mobi.nowtechnologies.server.service.itunes;

import mobi.nowtechnologies.server.service.itunes.impl.ITunesResult;

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
    public ITunesResult parseVerifyReceipt(String response) throws ITunesResponseFormatException {
        for (ITunesResponseParser parser : parsers) {
            logger.debug("Try to parse iTunes response [{}] with parser [{}]", response, parser);

            try {
                ITunesResult parseResult = parser.parseVerifyReceipt(response);

                logger.debug("Parsed result is {}", parseResult);

                return parseResult;
            } catch (ITunesResponseFormatException e) {
                logger.debug("Failed to parse with " + parser, e);
            }
        }

        throw new ITunesResponseFormatException("Couldn't parse response: " + response + " with parsers: " + parsers);
    }
}
