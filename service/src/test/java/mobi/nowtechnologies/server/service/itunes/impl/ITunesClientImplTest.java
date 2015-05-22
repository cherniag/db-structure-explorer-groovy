/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.service.itunes.impl;

import mobi.nowtechnologies.server.service.itunes.ITunesConnectionConfig;
import mobi.nowtechnologies.server.service.itunes.ITunesResponseParser;
import mobi.nowtechnologies.server.service.itunes.ITunesResult;
import mobi.nowtechnologies.server.support.http.BasicResponse;
import mobi.nowtechnologies.server.support.http.PostService;

import org.junit.*;
import org.mockito.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ITunesClientImplTest {

    final static int HTTP_OK = 200;
    final static String JSON_BODY = "{\"receipt-data\":\"appStoreReceipt\",\"password\":\"password\"}";

    @Mock
    PostService postService;
    @Mock
    ITunesResponseParser iTunesResponseParser;
    @Mock
    ITunesConnectionConfig config;

    @InjectMocks
    ITunesClientImpl iTunesClient;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        iTunesClient.setHttpOkCode(HTTP_OK);

        when(config.getUrl()).thenReturn("url");
        when(config.getPassword()).thenReturn("password");
    }

    @Test
    public void testVerifyReceipt_ResposeNoOK() throws Exception {
        final String appStoreReceipt = "appStoreReceipt";

        final BasicResponse basicResponse = mock(BasicResponse.class);
        when(basicResponse.getStatusCode()).thenReturn(2 * HTTP_OK);

        when(postService.sendHttpPost(config.getUrl(), JSON_BODY)).thenReturn(basicResponse);

        assertNull(iTunesClient.verifyReceipt(config, appStoreReceipt));

        verify(postService, times(1)).sendHttpPost(config.getUrl(), JSON_BODY);
        verify(config, times(3)).getUrl();
        verify(config, times(1)).getPassword();
        verify(basicResponse, times(1)).getStatusCode();

        verifyNotWantedInteractions(basicResponse);
    }

    @Test
    public void testVerifyReceipt_OK() throws Exception {
        final String appStoreReceipt = "appStoreReceipt";

        final BasicResponse basicResponse = mock(BasicResponse.class);
        when(basicResponse.getStatusCode()).thenReturn(HTTP_OK);
        when(basicResponse.getMessage()).thenReturn("messageFromITunes");

        when(postService.sendHttpPost(config.getUrl(), JSON_BODY)).thenReturn(basicResponse);

        final ITunesResult result = mock(ITunesResult.class);
        when(iTunesResponseParser.parseVerifyReceipt(basicResponse.getMessage())).thenReturn(result);

        assertSame(result, iTunesClient.verifyReceipt(config, appStoreReceipt));

        verify(postService, times(1)).sendHttpPost(config.getUrl(), JSON_BODY);
        verify(config, times(3)).getUrl();
        verify(config, times(1)).getPassword();
        verify(iTunesResponseParser, times(1)).parseVerifyReceipt(basicResponse.getMessage());
        verify(basicResponse, times(1)).getStatusCode();
        verify(basicResponse, times(3)).getMessage();

        verifyNotWantedInteractions(iTunesResponseParser, basicResponse);
    }

    void verifyNotWantedInteractions(Object... mocks) {
        verifyNoMoreInteractions(postService, config);
        verifyNoMoreInteractions(mocks);
    }
}