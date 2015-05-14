package mobi.nowtechnologies.server.service.itunes;

import mobi.nowtechnologies.server.service.itunes.impl.ITunesResult;
/**
 * Author: Gennadii Cherniaiev Date: 1/6/2015
 */
public interface ITunesResponseParser {

    ITunesResult parseVerifyReceipt(String payload) throws ITunesResponseFormatException;
}
