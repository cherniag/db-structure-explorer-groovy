/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.service.itunes;

import mobi.nowtechnologies.server.persistence.domain.User;

import java.util.Map;

/**
 * Created by zam on 1/15/2015.
 */
public interface ITunesService {

    void processInAppSubscription(User user, String transactionReceipt);

    Map<String, ?> processXPlayCapSubscription(User user, String transactionReceipt) throws ITunesXPlayCapSubscriptionException;
}
