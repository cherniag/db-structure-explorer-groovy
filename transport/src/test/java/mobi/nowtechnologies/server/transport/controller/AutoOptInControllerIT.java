package mobi.nowtechnologies.server.transport.controller;

import com.google.gson.*;
import mobi.nowtechnologies.server.mock.MockWebApplication;
import mobi.nowtechnologies.server.mock.MockWebApplicationContextLoader;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.shared.dto.OAuthProvider;
import mobi.nowtechnologies.server.shared.enums.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletException;
import java.io.IOException;

import static mobi.nowtechnologies.server.shared.dto.OAuthProvider.*;
import static mobi.nowtechnologies.server.shared.enums.Contract.*;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.*;
import static mobi.nowtechnologies.server.shared.enums.SegmentType.*;
import static mobi.nowtechnologies.server.shared.enums.Tariff.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * User: Titov Mykhaylo (titov)
 * 05.09.13 15:44
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:transport-servlet-test.xml",
        "classpath:META-INF/service-test.xml",
        "classpath:META-INF/soap.xml",
        "classpath:META-INF/dao-test.xml",
        "classpath:META-INF/soap.xml",
        "classpath:META-INF/shared.xml" }, loader = MockWebApplicationContextLoader.class)
@MockWebApplication(name = "transport.EntityController", webapp = "classpath:.")
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class AutoOptInControllerIT {

    @Autowired
    private DispatcherServlet dispatcherServlet;
    private Gson gson;
    private JsonParser jsonParser;

    @Before
    public void setUp(){
        gson = new Gson();
        jsonParser = new JsonParser();
    }

    @Test
    public void shouldAutoOptIn() throws ServletException, IOException {
        //given
        String userName = "+447111111114";
        String appVersion = "4.2";
        String apiVersion = "4.2";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String deviceUid = "b88106713409e92622461a876abcd74b";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);
        String otac = "otac";

        String url = "/h/" + communityUrl + "/" + apiVersion + "/AUTO_OPT_IN";

        MockHttpServletRequest httpServletRequestMock = new MockHttpServletRequest("POST", url);
        httpServletRequestMock.addHeader("Content-Type", "application/x-www-form-urlencoded");
        httpServletRequestMock.addHeader("Accept", "application/json");
        httpServletRequestMock.setPathInfo(url);

        httpServletRequestMock.addParameter("APP_VERSION", appVersion);
        httpServletRequestMock.addParameter("USER_NAME", userName);
        httpServletRequestMock.addParameter("USER_TOKEN", userToken);
        httpServletRequestMock.addParameter("TIMESTAMP", timestamp);
        httpServletRequestMock.addParameter("DEVICE_UID", deviceUid);
        httpServletRequestMock.addParameter("OTAC_TOKEN", otac);

        MockHttpServletResponse httpServletResponseMock = new MockHttpServletResponse();

        //when
        dispatcherServlet.service(httpServletRequestMock, httpServletResponseMock);

        //then
        assertEquals(HttpStatus.OK.value(), httpServletResponseMock.getStatus());

        final JsonArray asJsonArrayResponseObjectMember = parseSuccessfulResponse(httpServletResponseMock.getContentAsString());
        assertEquals(1, asJsonArrayResponseObjectMember.size());

        AccountCheckDTO accountCheckDTO = gson.fromJson(asJsonArrayResponseObjectMember.get(0), AccountCheckDTO.class);

        assertEquals(null, accountCheckDTO.displayName);
        assertEquals("SUBSCRIBED", accountCheckDTO.status);
        assertEquals(userName, accountCheckDTO.userName);
        assertEquals("IOS", accountCheckDTO.deviceUID);
        assertEquals(storedToken, accountCheckDTO.userToken);
        assertEquals("IOS", accountCheckDTO.deviceType);
        assertNotNull(accountCheckDTO.rememberMeToken);
        assertEquals("O2_PSMS", accountCheckDTO.paymentType);
        assertEquals("+447111111111", accountCheckDTO.phoneNumber);
        assertEquals(0, accountCheckDTO.subBalance);
        assertEquals(null, accountCheckDTO.paymentStatus);
        assertEquals(new Integer(1), accountCheckDTO.operator);
        assertEquals(true, accountCheckDTO.paymentEnabled);
        assertEquals("PLAYS", accountCheckDTO.drmType);
        assertEquals(100, accountCheckDTO.drmValue);
        assertEquals(PaymentDetailsStatus.NONE, accountCheckDTO.lastPaymentStatus);
        assertEquals(false, accountCheckDTO.promotedDevice);
        assertEquals(true, accountCheckDTO.freeTrial);
        assertEquals(1321452650, accountCheckDTO.chartTimestamp);
        assertEquals(21, accountCheckDTO.chartItems);
        assertEquals(1317300123, accountCheckDTO.newsTimestamp);
        assertEquals(10, accountCheckDTO.newsItems);
        assertEquals(null, accountCheckDTO.promotionLabel);
        assertEquals(false, accountCheckDTO.fullyRegistred);
        assertEquals(2, accountCheckDTO.promotedWeeks);
        assertEquals(NONE, accountCheckDTO.oAuthProvider);
        assertEquals(false, accountCheckDTO.hasPotentialPromoCodePromotion);
        assertEquals(false, accountCheckDTO.hasOffers);
        assertEquals(null, accountCheckDTO.activation);
        assertEquals("com.musicqubed.o2.autorenew.test", accountCheckDTO.appStoreProductId);
        assertEquals(O2, accountCheckDTO.provider);
        assertEquals(PAYM, accountCheckDTO.contract);
        assertEquals(CONSUMER, accountCheckDTO.segment);
        assertEquals(_3G, accountCheckDTO.tariff);
        assertEquals(0, accountCheckDTO.graceCreditSeconds);
        assertEquals(true, accountCheckDTO.canGetVideo);
        assertEquals(false, accountCheckDTO.canPlayVideo);
        assertEquals(false, accountCheckDTO.hasAllDetails);
        assertEquals(true, accountCheckDTO.showFreeTrial);
        assertEquals(false, accountCheckDTO.canActivateVideoTrial);
        assertEquals(false, accountCheckDTO.eligibleForVideo);
        assertEquals(null, accountCheckDTO.lastSubscribedPaymentSystem);
        assertEquals(null, accountCheckDTO.subscriptionChanged);
        assertEquals(true, accountCheckDTO.subjectToAutoOptIn);

    }

    private JsonArray parseSuccessfulResponse(final String contentAsString) {
        JsonElement jsonElement = jsonParser.parse(contentAsString);

        final JsonObject asJsonObject = jsonElement.getAsJsonObject();
        final JsonObject asJsonObjectResponse = asJsonObject.get("class mobi.nowtechnologies.server.persistence.domain.Response").getAsJsonObject();
        final JsonElement jsonElementResponseObjectMember = asJsonObjectResponse.get("object");
        final JsonArray asJsonArrayResponseObjectMember = jsonElementResponseObjectMember.getAsJsonArray();
        return asJsonArrayResponseObjectMember;
    }
}
