/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.service.itunes.impl;

import mobi.nowtechnologies.server.service.itunes.ITunesClient;
import mobi.nowtechnologies.server.service.itunes.ITunesClientException;
import mobi.nowtechnologies.server.service.itunes.ITunesConnectionConfig;
import mobi.nowtechnologies.server.service.itunes.ITunesResponseParser;
import mobi.nowtechnologies.server.service.itunes.ITunesResponseParserException;
import mobi.nowtechnologies.server.service.itunes.ITunesResult;
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
    public ITunesResult verifyReceipt(ITunesConnectionConfig config, String appStoreReceipt) throws ITunesClientException {
        String url = config.getUrl();

        ITunesInAppSubscriptionRequestDto requestDto = new ITunesInAppSubscriptionRequestDto(appStoreReceipt, config.getPassword());
        String body = gson.toJson(requestDto);
        logger.info("Trying to validate in-app subscription using url [{}] with following params [{}]", url, body);
        try {
            BasicResponse basicResponse = postService.sendHttpPost(url, body);
            if (basicResponse.getStatusCode() != httpOkCode) {
                String message = String.format("The request of in-app subscription validation returned unexpected basicResponse: %s", basicResponse);
                logger.warn(message);
                throw new ITunesClientException(message);
            }
            return iTunesResponseParser.parseVerifyReceipt(basicResponse.getMessage());
        } catch (ITunesResponseParserException e) {
            // already logged
            throw new ITunesClientException("Unexpected parser exception" + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            String message = String.format("Failed to process iTunes response. Unexpected exception: %s", e.getMessage());
            logger.error(message, e);
            throw new ITunesClientException(message, e);
        }
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
