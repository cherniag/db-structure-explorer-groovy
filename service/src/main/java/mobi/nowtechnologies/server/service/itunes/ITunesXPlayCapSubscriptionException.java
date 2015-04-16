/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.service.itunes;

import mobi.nowtechnologies.common.util.ServerMessage;
import mobi.nowtechnologies.server.service.exception.ServiceException;
/**
 * Created by zam on 4/16/2015.
 */
public class ITunesXPlayCapSubscriptionException extends ServiceException {

    public ITunesXPlayCapSubscriptionException(ServerMessage message) {
        super(message);
    }
}
