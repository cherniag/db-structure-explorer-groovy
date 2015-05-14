/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.service.itunes.impl;

import mobi.nowtechnologies.server.service.itunes.ITunesConnectionException;
import mobi.nowtechnologies.server.service.itunes.ITunesClient;
import mobi.nowtechnologies.server.service.itunes.ITunesConnectionConfig;
import mobi.nowtechnologies.server.service.itunes.ITunesResponseFormatException;
import mobi.nowtechnologies.server.service.itunes.ITunesResponseParser;
import mobi.nowtechnologies.server.shared.dto.ITunesInAppSubscriptionRequestDto;
import mobi.nowtechnologies.server.support.http.BasicResponse;
import mobi.nowtechnologies.server.support.http.PostService;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ITunesClientImpl implements ITunesClient {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Gson gson = new Gson();

    private PostService postService;
    private ITunesResponseParser iTunesResponseParser;

    private int httpOkCode;

    @Override
    public ITunesResult verifyReceipt(ITunesConnectionConfig config, String appStoreReceipt) throws ITunesConnectionException, ITunesResponseFormatException {
        String url = config.getUrl();

        ITunesInAppSubscriptionRequestDto requestDto = new ITunesInAppSubscriptionRequestDto(appStoreReceipt, config.getPassword());
        String body = gson.toJson(requestDto);

        logger.info("Trying to validate in-app subscription using url [{}] with following params [{}]", url, body);

        BasicResponse basicResponse = null;
        try {
            basicResponse = postService.sendHttpPost(url, body);
        } catch (Exception e) {
            throw new ITunesConnectionException(e.getCause());
        }
        if (basicResponse.getStatusCode() != httpOkCode) {
            throw new ITunesConnectionException("ITunes response is unsuccessful, status=" + basicResponse.getStatusCode());
        }

        return iTunesResponseParser.parseVerifyReceipt(basicResponse.getMessage());
    }


    public void setHttpOkCode(int httpOkCode) {
        this.httpOkCode = httpOkCode;
    }

    public void setPostService(PostService postService) {
        this.postService = postService;
    }

    public void setiTunesResponseParser(ITunesResponseParser iTunesResponseParser) {
        this.iTunesResponseParser = iTunesResponseParser;
    }
}
