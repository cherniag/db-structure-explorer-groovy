/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.service.itunes;

import mobi.nowtechnologies.server.service.exception.ServiceException;
/**
 * Created by zam on 4/16/2015.
 */
public final class ITunesClientException extends ServiceException {

    public ITunesClientException(String message) {
        super(message);
    }

    public ITunesClientException(String message, Throwable e) {
        super(message, e);
    }
}
