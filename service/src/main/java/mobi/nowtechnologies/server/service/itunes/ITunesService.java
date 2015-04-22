/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.service.itunes;

import mobi.nowtechnologies.server.persistence.domain.User;

/**
 * Created by zam on 1/15/2015.
 */
public interface ITunesService {

    void processInAppSubscription(User user, String transactionReceipt, boolean createITunesPaymentDetails) throws Exception;
}
