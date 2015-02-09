/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.service.itunes.payment;

import mobi.nowtechnologies.server.persistence.domain.User;

/**
 * Created by zam on 1/16/2015.
 */
public interface ITunesPaymentDetails {
    User getUser();

    String getOriginalTransactionId();

    String getAppStoreReceipt();

    String getProductId();

    Long getPurchaseTime();

    Long getExpireTime();
}
