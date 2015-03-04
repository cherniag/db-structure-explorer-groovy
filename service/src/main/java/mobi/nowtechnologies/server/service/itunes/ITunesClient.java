/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.service.itunes;

/**
 * Created by zam on 1/26/2015.
 */
public interface ITunesClient {

    ITunesResult verifyReceipt(ITunesConnectionConfig connectionConfig, String appStoreReceipt);
}
