package mobi.nowtechnologies.server.service.itunes;

/**
 * Author: Gennadii Cherniaiev Date: 1/6/2015
 */
public interface ITunesResponseParser {

    ITunesResult parseVerifyReceipt(String payload) throws ITunesResponseParserException;
}
