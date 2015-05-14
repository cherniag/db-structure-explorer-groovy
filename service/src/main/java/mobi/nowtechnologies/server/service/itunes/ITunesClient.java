/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.service.itunes;

import mobi.nowtechnologies.server.service.itunes.impl.ITunesResult;
/**
 * Created by zam on 1/26/2015.
 */
public interface ITunesClient {

    ITunesResult verifyReceipt(ITunesConnectionConfig connectionConfig, String appStoreReceipt) throws ITunesConnectionException, ITunesResponseFormatException;

}
