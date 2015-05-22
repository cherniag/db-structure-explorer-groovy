package mobi.nowtechnologies.server.trackrepo;

import mobi.nowtechnologies.server.shared.dto.PageListDto;
import mobi.nowtechnologies.server.trackrepo.TrackRepositoryHttpClientImpl.HttpClientFactory;
import mobi.nowtechnologies.server.trackrepo.dto.IngestWizardDataDto;
import mobi.nowtechnologies.server.trackrepo.dto.ResourceFileDto;
import mobi.nowtechnologies.server.trackrepo.dto.TrackDto;
import mobi.nowtechnologies.server.trackrepo.enums.AudioResolution;
import mobi.nowtechnologies.server.trackrepo.enums.FileType;
import mobi.nowtechnologies.server.trackrepo.enums.TrackStatus;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gson.reflect.TypeToken;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.protocol.HTTP;
import org.bouncycastle.util.encoders.Base64;
import static org.apache.http.HttpVersion.HTTP_1_1;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.invocation.*;
import org.mockito.stubbing.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Titov Mykhaylo (titov)
 * @author Mayboroda Dmytro
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(TrackRepositoryHttpClientImpl.HttpClientFactory.class)
public class TrackRepositoryHttpClientTest {

    private TrackRepositoryHttpClientImpl client;
    private String clientUsername = "admin";
    private String clientPassword = "admin";

    @Before
    public void before() {
        client = new TrackRepositoryHttpClientImpl();
        client.setUsername(clientUsername);
        client.setPassword(clientPassword);
        client.init();
    }

    @Test
    public void testGetDrops_Successful() throws Exception {

        HttpClient httpClient = mock(HttpClient.class);
        final HttpResponse successfulSearchResponse = new BasicHttpResponse(new BasicStatusLine(HTTP_1_1, 200, "OK")) {
            @Override
            public HttpEntity getEntity() {
                try {
                    return new StringEntity("{\"suid\":\"1374224031997\",\"drops\":[{\"name\":\"manifest.00000000000002472000.txt\",\"date\":1374219880000,\"tracks\":null,\"selected\":false}," +
                                            "{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/warner_cdu/new_release/20111011_0926_13\"," +
                                            "\"date\":1374219880000," +
                                            "\"tracks\":null,\"selected\":false}," +
                                            "{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/warner_cdu/new_release_3" +
                                            ".4/20130103060213909\",\"date\":1374219880000,\"tracks\":null,\"selected\":false}," +
                                            "{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/fuga_cdu/5051823094641_20130606112518431\"," +
                                            "\"date\":1374219880000," +
                                            "\"tracks\":null,\"selected\":false},{\"name\":\"3000007191631\",\"date\":1374219880000,\"tracks\":null,\"selected\":false}," +
                                            "{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/ioda_cdu/chartsnow_20121016_70734/477929\"," +
                                            "\"date\":1374219880000," +
                                            "\"tracks\":null,\"selected\":false}," +
                                            "{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/ci_cdu/PIAS/20130321103906984\"," +
                                            "\"date\":1374219880000,\"tracks\":null,\"selected\":false}," +
                                            "{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/manual/020313/020313.csv\",\"date\":1374219880000," +
                                            "\"tracks\":null," +
                                            "\"selected\":false},{\"name\":\"fake.xml\",\"date\":1374224024000,\"tracks\":null,\"selected\":false}]}");
                } catch (UnsupportedEncodingException e) {
                    return null;
                }
            }
        };
        when(httpClient.execute(any(HttpUriRequest.class))).thenAnswer(new Answer<HttpResponse>() {
            @Override
            public HttpResponse answer(InvocationOnMock invocationOnMock) throws Throwable {
                HttpGet request = (HttpGet) invocationOnMock.getArguments()[0];

                assertEquals("application/json", request.getLastHeader(HTTP.CONTENT_TYPE).getValue());

                return successfulSearchResponse;
            }
        });
        TrackRepositoryHttpClientImpl spy = spy(client);
        when(spy.getHttpClient()).thenReturn(httpClient);

        IngestWizardDataDto result = spy.getDrops();

        assertEquals("1374224031997", result.getSuid());
        assertEquals(9, result.getDrops().size());
        verify(spy, times(1)).getSecuredHeaders();
    }

    @Test
    public void testGetDrops_Fail() throws Exception {
        HttpClient httpClient = getFailedHttpClient();
        TrackRepositoryHttpClientImpl spy = spy(client);
        when(spy.getHttpClient()).thenReturn(httpClient);

        IngestWizardDataDto result = spy.getDrops();

        assertEquals(0, result.getDrops().size());
        assertEquals(null, result.getSuid());
        verify(spy, times(1)).getSecuredHeaders();
    }

    @Test
    public void testSelectDrops_Successful() throws Exception {
        TrackRepositoryHttpClientImpl spy = spy(client);

        final String inputJson = "{\"suid\":\"1374224031997\",\"drops\":[{\"name\":\"manifest.00000000000002472000.txt\",\"date\":1374219880000,\"tracks\":null,\"selected\":false}," +
                                 "{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/warner_cdu/new_release/20111011_0926_13\"," +
                                 "\"date\":1374219880000,\"tracks\":null," +
                                 "\"selected\":false},{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/warner_cdu/new_release_3" +
                                 ".4/20130103060213909\"," +
                                 "\"date\":1374219880000,\"tracks\":null,\"selected\":false}," +
                                 "{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/fuga_cdu" +
                                 "/5051823094641_20130606112518431\",\"date\":1374219880000,\"tracks\":null,\"selected\":false},{\"name\":\"3000007191631\",\"date\":1374219880000,\"tracks\":null," +
                                 "\"selected\":false}," +
                                 "{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/ioda_cdu/chartsnow_20121016_70734/477929\"," +
                                 "\"date\":1374219880000,\"tracks\":null," +
                                 "\"selected\":false},{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/ci_cdu/PIAS/20130321103906984\"," +
                                 "\"date\":1374219880000," +
                                 "\"tracks\":null,\"selected\":false},{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/manual/020313/020313.csv\"," +
                                 "\"date\":1374219880000,\"tracks\":null,\"selected\":false},{\"name\":\"fake.xml\",\"date\":1374224024000,\"tracks\":null,\"selected\":false}]}";
        Type type = new TypeToken<IngestWizardDataDto>() {}.getType();
        IngestWizardDataDto data = spy.gsonMillis.fromJson(new StringReader(inputJson), type);
        final String outputJson =
            "{\"suid\":\"1374224031997\",\"drops\":[{\"name\":\"manifest.00000000000002472000.txt\",\"date\":1374219880000,\"tracks\":[{\"productCode\":\"828768551424\",\"title\":\"Mr. Tambourine " +
            "Man\",\"artist\":\"The Byrds\",\"isrc\":\"USSM16500019\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}],\"selected\":true}," +
            "{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/warner_cdu/new_release/20111011_0926_13\",\"date\":1374219880000," +
            "\"tracks\":[{\"productCode\":\"A10302B0001242753E\",\"title\":\"Talking To The Moon\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001985\",\"exists\":false,\"type\":\"INSERT\"," +
            "\"selected\":true},{\"productCode\":\"A10302B0001239465G\",\"title\":\"Our First Time\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001884\",\"exists\":false,\"type\":\"INSERT\"," +
            "\"selected\":true},{\"productCode\":\"A10302B00000004D0H\",\"title\":\"The Blower's Daughter\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100003\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001239462M\",\"title\":\"Runaway Baby\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001885\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001177364N\",\"title\":\"Just The Way You Are\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001269\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004D2D\",\"title\":\"Older Chests\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100005\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001239286M\",\"title\":\"Grenade\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001883\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001239463K\",\"title\":\"Marry You\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001887\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004D57\",\"title\":\"Cold Water\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100008\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001138697X\",\"title\":\"Somewhere In Brooklyn\",\"artist\":\"Bruno Mars\",\"isrc\":\"USEE11000166\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001239468A\",\"title\":\"The Lazy Song\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001886\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004D1F\",\"title\":\"Cannonball\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100004\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004C85\",\"title\":\"Delicate\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0000001\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004CZN\",\"title\":\"Volcano\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100002\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001230215J\",\"title\":\"Liquor Store Blues\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001881\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001138699T\",\"title\":\"The Other Side\",\"artist\":\"Bruno Mars\",\"isrc\":\"USEE11000167\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001138698V\",\"title\":\"Count On Me\",\"artist\":\"Bruno Mars\",\"isrc\":\"USEE11000168\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0000078780Z\",\"title\":\"Eskimo\",\"artist\":\"Damien Rice\",\"isrc\":\"GBFTG0300054\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001239466E\",\"title\":\"Talking To The Moon\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001777\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004D65\",\"title\":\"I Remember\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100009\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004D49\",\"title\":\"Cheers Darlin'\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100007\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004D3B\",\"title\":\"Amie\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100006\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true}],\"selected\":true},{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/warner_cdu/new_release_3" +
            ".4/20130103060213909\",\"date\":1374219880000,\"tracks\":[{\"productCode\":\"A10302B00008091667\",\"title\":\"Falling Down\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900017\"," +
            "\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00008091667\",\"title\":\"Showbiz\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900008\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00008091667\",\"title\":\"Escape\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900009\",\"exists\":false,\"type\":\"INSERT\"," +
            "\"selected\":true},{\"productCode\":\"A10302B00008091667\",\"title\":\"Unintended\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900015\",\"exists\":false,\"type\":\"INSERT\"," +
            "\"selected\":true},{\"productCode\":\"A10302B00008091667\",\"title\":\"Hate This and I'll Love You\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900013\",\"exists\":false,\"type\":\"INSERT\"," +
            "\"selected\":true},{\"productCode\":\"A10302B00008091667\",\"title\":\"Sunburn\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900012\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Uno\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900001\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Cave\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900007\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Fillip\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900010\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Sober\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900016\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Muscle Museum\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900014\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Spiral Static\",\"artist\":\"Muse\",\"isrc\":\"GBCVT0000001\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Overdue\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900011\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}],\"selected\":true}," +
            "{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/fuga_cdu/5051823094641_20130606112518431\",\"date\":1374219880000," +
            "\"tracks\":[{\"productCode\":\"5051823094641\",\"title\":\"Higher\",\"artist\":\"The Golden Boy\",\"isrc\":\"GBCEN1300409\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"5051823094641\",\"title\":\"Out of My Mind\",\"artist\":\"The Golden Boy\",\"isrc\":\"GBCEN1300411\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"5051823094641\",\"title\":\"Gonna Do\",\"artist\":\"The Golden Boy\",\"isrc\":\"GBCEN1300410\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}]," +
            "\"selected\":true},{\"name\":\"3000007191631\",\"date\":1374219880000,\"tracks\":[{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"GBSXS1100209\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"ROCRP1002941\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"GBSXS1100265\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"GBSXS1100266\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"GBSXS1100210\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"GBSXS1100212\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"GBSXS1100211\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"ROCRP1002948\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}],\"selected\":true}," +
            "{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/ioda_cdu/chartsnow_20121016_70734/477929\",\"date\":1374219880000," +
            "\"tracks\":[{\"productCode\":\"5506206\",\"title\":\"Cedar Hives\",\"artist\":\"Agar Agar\",\"isrc\":\"US5881200011\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"5506205\",\"title\":\"Quarry Diving\",\"artist\":\"Agar Agar\",\"isrc\":\"US5881200012\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"5506203\",\"title\":\"Argaarg\",\"artist\":\"Agar Agar\",\"isrc\":\"US5881200014\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"5506204\",\"title\":\"You brought me to the country!\",\"artist\":\"Agar Agar\",\"isrc\":\"US5881200013\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"5506207\",\"title\":\"Trees Sweet Heat\",\"artist\":\"Agar Agar\",\"isrc\":\"US5881200010\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}]," +
            "\"selected\":true},{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/ci_cdu/PIAS/20130321103906984\",\"date\":1374219880000," +
            "\"tracks\":[],\"selected\":true},{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/manual/020313/020313.csv\",\"date\":1374219880000," +
            "\"tracks\":[],\"selected\":true},{\"name\":\"fake.xml\",\"date\":1374224024000,\"tracks\":[],\"selected\":true}]}";

        HttpClient httpClient = mock(HttpClient.class);
        final HttpResponse successfulSearchResponse = new BasicHttpResponse(new BasicStatusLine(HTTP_1_1, 200, "OK")) {
            @Override
            public HttpEntity getEntity() {
                try {
                    return new StringEntity(outputJson);
                } catch (UnsupportedEncodingException e) {
                    return null;
                }
            }
        };
        when(httpClient.execute(any(HttpUriRequest.class))).thenAnswer(new Answer<HttpResponse>() {
            @Override
            public HttpResponse answer(InvocationOnMock invocationOnMock) throws Throwable {
                HttpPost request = (HttpPost) invocationOnMock.getArguments()[0];

                assertEquals("application/json", request.getLastHeader(HTTP.CONTENT_TYPE).getValue());

                return successfulSearchResponse;
            }
        });
        when(spy.getHttpClient()).thenReturn(httpClient);

        IngestWizardDataDto result = spy.selectDrops(data);

        assertEquals(data.getSuid(), result.getSuid());
        assertEquals(9, result.getDrops().size());
        assertNotNull(result.getDrops().get(0).getTracks());
        verify(spy, times(1)).getSecuredHeaders();
    }

    @Test
    public void testSelectDrops_Fail() throws Exception {
        HttpClient httpClient = getFailedHttpClient();
        TrackRepositoryHttpClientImpl spy = spy(client);
        when(spy.getHttpClient()).thenReturn(httpClient);

        final String inputJson = "{\"suid\":\"1374224031997\",\"drops\":[{\"name\":\"manifest.00000000000002472000.txt\",\"date\":1374219880000,\"tracks\":null,\"selected\":false}," +
                                 "{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/warner_cdu/new_release/20111011_0926_13\"," +
                                 "\"date\":1374219880000,\"tracks\":null," +
                                 "\"selected\":false},{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/warner_cdu/new_release_3" +
                                 ".4/20130103060213909\"," +
                                 "\"date\":1374219880000,\"tracks\":null,\"selected\":false}," +
                                 "{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/fuga_cdu" +
                                 "/5051823094641_20130606112518431\",\"date\":1374219880000,\"tracks\":null,\"selected\":false},{\"name\":\"3000007191631\",\"date\":1374219880000,\"tracks\":null," +
                                 "\"selected\":false}," +
                                 "{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/ioda_cdu/chartsnow_20121016_70734/477929\"," +
                                 "\"date\":1374219880000,\"tracks\":null," +
                                 "\"selected\":false},{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/ci_cdu/PIAS/20130321103906984\"," +
                                 "\"date\":1374219880000," +
                                 "\"tracks\":null,\"selected\":false},{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/manual/020313/020313.csv\"," +
                                 "\"date\":1374219880000,\"tracks\":null,\"selected\":false},{\"name\":\"fake.xml\",\"date\":1374224024000,\"tracks\":null,\"selected\":false}]}";
        Type type = new TypeToken<IngestWizardDataDto>() {}.getType();
        IngestWizardDataDto data = spy.gsonMillis.fromJson(new StringReader(inputJson), type);

        IngestWizardDataDto result = spy.selectDrops(data);

        assertEquals(0, result.getDrops().size());
        assertEquals(null, result.getSuid());
        verify(spy, times(1)).getSecuredHeaders();
    }

    @Test
    public void testSelectTrackDrops_Successful() throws Exception {
        TrackRepositoryHttpClientImpl spy = spy(client);

        final String inputJson =
            "{\"suid\":\"1374224031997\",\"drops\":[{\"name\":\"manifest.00000000000002472000.txt\",\"date\":1374219880000,\"tracks\":[{\"productCode\":\"828768551424\",\"title\":\"Mr. Tambourine " +
            "Man\",\"artist\":\"The Byrds\",\"isrc\":\"USSM16500019\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}],\"selected\":true}," +
            "{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/warner_cdu/new_release/20111011_0926_13\",\"date\":1374219880000," +
            "\"tracks\":[{\"productCode\":\"A10302B0001242753E\",\"title\":\"Talking To The Moon\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001985\",\"exists\":false,\"type\":\"INSERT\"," +
            "\"selected\":true},{\"productCode\":\"A10302B0001239465G\",\"title\":\"Our First Time\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001884\",\"exists\":false,\"type\":\"INSERT\"," +
            "\"selected\":true},{\"productCode\":\"A10302B00000004D0H\",\"title\":\"The Blower's Daughter\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100003\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001239462M\",\"title\":\"Runaway Baby\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001885\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001177364N\",\"title\":\"Just The Way You Are\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001269\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004D2D\",\"title\":\"Older Chests\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100005\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001239286M\",\"title\":\"Grenade\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001883\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001239463K\",\"title\":\"Marry You\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001887\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004D57\",\"title\":\"Cold Water\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100008\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001138697X\",\"title\":\"Somewhere In Brooklyn\",\"artist\":\"Bruno Mars\",\"isrc\":\"USEE11000166\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001239468A\",\"title\":\"The Lazy Song\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001886\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004D1F\",\"title\":\"Cannonball\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100004\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004C85\",\"title\":\"Delicate\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0000001\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004CZN\",\"title\":\"Volcano\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100002\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001230215J\",\"title\":\"Liquor Store Blues\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001881\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001138699T\",\"title\":\"The Other Side\",\"artist\":\"Bruno Mars\",\"isrc\":\"USEE11000167\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001138698V\",\"title\":\"Count On Me\",\"artist\":\"Bruno Mars\",\"isrc\":\"USEE11000168\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0000078780Z\",\"title\":\"Eskimo\",\"artist\":\"Damien Rice\",\"isrc\":\"GBFTG0300054\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001239466E\",\"title\":\"Talking To The Moon\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001777\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004D65\",\"title\":\"I Remember\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100009\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004D49\",\"title\":\"Cheers Darlin'\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100007\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004D3B\",\"title\":\"Amie\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100006\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true}],\"selected\":true},{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/warner_cdu/new_release_3" +
            ".4/20130103060213909\",\"date\":1374219880000,\"tracks\":[{\"productCode\":\"A10302B00008091667\",\"title\":\"Falling Down\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900017\"," +
            "\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00008091667\",\"title\":\"Showbiz\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900008\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00008091667\",\"title\":\"Escape\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900009\",\"exists\":false,\"type\":\"INSERT\"," +
            "\"selected\":true},{\"productCode\":\"A10302B00008091667\",\"title\":\"Unintended\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900015\",\"exists\":false,\"type\":\"INSERT\"," +
            "\"selected\":true},{\"productCode\":\"A10302B00008091667\",\"title\":\"Hate This and I'll Love You\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900013\",\"exists\":false,\"type\":\"INSERT\"," +
            "\"selected\":true},{\"productCode\":\"A10302B00008091667\",\"title\":\"Sunburn\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900012\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Uno\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900001\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Cave\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900007\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Fillip\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900010\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Sober\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900016\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Muscle Museum\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900014\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Spiral Static\",\"artist\":\"Muse\",\"isrc\":\"GBCVT0000001\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Overdue\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900011\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}],\"selected\":true}," +
            "{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/fuga_cdu/5051823094641_20130606112518431\",\"date\":1374219880000," +
            "\"tracks\":[{\"productCode\":\"5051823094641\",\"title\":\"Higher\",\"artist\":\"The Golden Boy\",\"isrc\":\"GBCEN1300409\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"5051823094641\",\"title\":\"Out of My Mind\",\"artist\":\"The Golden Boy\",\"isrc\":\"GBCEN1300411\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"5051823094641\",\"title\":\"Gonna Do\",\"artist\":\"The Golden Boy\",\"isrc\":\"GBCEN1300410\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}]," +
            "\"selected\":true},{\"name\":\"3000007191631\",\"date\":1374219880000,\"tracks\":[{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"GBSXS1100209\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"ROCRP1002941\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"GBSXS1100265\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"GBSXS1100266\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"GBSXS1100210\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"GBSXS1100212\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"GBSXS1100211\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"ROCRP1002948\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}],\"selected\":true}," +
            "{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/ioda_cdu/chartsnow_20121016_70734/477929\",\"date\":1374219880000," +
            "\"tracks\":[{\"productCode\":\"5506206\",\"title\":\"Cedar Hives\",\"artist\":\"Agar Agar\",\"isrc\":\"US5881200011\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"5506205\",\"title\":\"Quarry Diving\",\"artist\":\"Agar Agar\",\"isrc\":\"US5881200012\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"5506203\",\"title\":\"Argaarg\",\"artist\":\"Agar Agar\",\"isrc\":\"US5881200014\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"5506204\",\"title\":\"You brought me to the country!\",\"artist\":\"Agar Agar\",\"isrc\":\"US5881200013\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"5506207\",\"title\":\"Trees Sweet Heat\",\"artist\":\"Agar Agar\",\"isrc\":\"US5881200010\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}]," +
            "\"selected\":true},{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/ci_cdu/PIAS/20130321103906984\",\"date\":1374219880000," +
            "\"tracks\":[],\"selected\":true},{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/manual/020313/020313.csv\",\"date\":1374219880000," +
            "\"tracks\":[],\"selected\":true},{\"name\":\"fake.xml\",\"date\":1374224024000,\"tracks\":[],\"selected\":true}]}";
        Type type = new TypeToken<IngestWizardDataDto>() {}.getType();
        IngestWizardDataDto data = spy.gsonMillis.fromJson(new StringReader(inputJson), type);
        final String outputJson =
            "{\"suid\":\"1374224031997\",\"drops\":[{\"name\":\"manifest.00000000000002472000.txt\",\"date\":1374219880000,\"tracks\":[{\"productCode\":\"828768551424\",\"title\":\"Mr. Tambourine " +
            "Man\",\"artist\":\"The Byrds\",\"isrc\":\"USSM16500019\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}],\"selected\":true}," +
            "{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/warner_cdu/new_release/20111011_0926_13\",\"date\":1374219880000," +
            "\"tracks\":[{\"productCode\":\"A10302B0001242753E\",\"title\":\"Talking To The Moon\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001985\",\"exists\":false,\"type\":\"INSERT\"," +
            "\"selected\":true},{\"productCode\":\"A10302B0001239465G\",\"title\":\"Our First Time\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001884\",\"exists\":false,\"type\":\"INSERT\"," +
            "\"selected\":true},{\"productCode\":\"A10302B00000004D0H\",\"title\":\"The Blower's Daughter\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100003\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001239462M\",\"title\":\"Runaway Baby\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001885\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001177364N\",\"title\":\"Just The Way You Are\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001269\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004D2D\",\"title\":\"Older Chests\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100005\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001239286M\",\"title\":\"Grenade\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001883\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001239463K\",\"title\":\"Marry You\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001887\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004D57\",\"title\":\"Cold Water\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100008\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001138697X\",\"title\":\"Somewhere In Brooklyn\",\"artist\":\"Bruno Mars\",\"isrc\":\"USEE11000166\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001239468A\",\"title\":\"The Lazy Song\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001886\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004D1F\",\"title\":\"Cannonball\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100004\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004C85\",\"title\":\"Delicate\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0000001\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004CZN\",\"title\":\"Volcano\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100002\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001230215J\",\"title\":\"Liquor Store Blues\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001881\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001138699T\",\"title\":\"The Other Side\",\"artist\":\"Bruno Mars\",\"isrc\":\"USEE11000167\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001138698V\",\"title\":\"Count On Me\",\"artist\":\"Bruno Mars\",\"isrc\":\"USEE11000168\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0000078780Z\",\"title\":\"Eskimo\",\"artist\":\"Damien Rice\",\"isrc\":\"GBFTG0300054\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001239466E\",\"title\":\"Talking To The Moon\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001777\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004D65\",\"title\":\"I Remember\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100009\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004D49\",\"title\":\"Cheers Darlin'\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100007\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004D3B\",\"title\":\"Amie\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100006\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true}],\"selected\":true},{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/warner_cdu/new_release_3" +
            ".4/20130103060213909\",\"date\":1374219880000,\"tracks\":[{\"productCode\":\"A10302B00008091667\",\"title\":\"Falling Down\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900017\"," +
            "\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00008091667\",\"title\":\"Showbiz\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900008\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00008091667\",\"title\":\"Escape\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900009\",\"exists\":false,\"type\":\"INSERT\"," +
            "\"selected\":true},{\"productCode\":\"A10302B00008091667\",\"title\":\"Unintended\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900015\",\"exists\":false,\"type\":\"INSERT\"," +
            "\"selected\":true},{\"productCode\":\"A10302B00008091667\",\"title\":\"Hate This and I'll Love You\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900013\",\"exists\":false,\"type\":\"INSERT\"," +
            "\"selected\":true},{\"productCode\":\"A10302B00008091667\",\"title\":\"Sunburn\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900012\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Uno\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900001\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Cave\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900007\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Fillip\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900010\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Sober\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900016\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Muscle Museum\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900014\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Spiral Static\",\"artist\":\"Muse\",\"isrc\":\"GBCVT0000001\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Overdue\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900011\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}],\"selected\":true}," +
            "{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/fuga_cdu/5051823094641_20130606112518431\",\"date\":1374219880000," +
            "\"tracks\":[{\"productCode\":\"5051823094641\",\"title\":\"Higher\",\"artist\":\"The Golden Boy\",\"isrc\":\"GBCEN1300409\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"5051823094641\",\"title\":\"Out of My Mind\",\"artist\":\"The Golden Boy\",\"isrc\":\"GBCEN1300411\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"5051823094641\",\"title\":\"Gonna Do\",\"artist\":\"The Golden Boy\",\"isrc\":\"GBCEN1300410\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}]," +
            "\"selected\":true},{\"name\":\"3000007191631\",\"date\":1374219880000,\"tracks\":[{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"GBSXS1100209\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"ROCRP1002941\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"GBSXS1100265\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"GBSXS1100266\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"GBSXS1100210\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"GBSXS1100212\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"GBSXS1100211\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"ROCRP1002948\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}],\"selected\":true}," +
            "{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/ioda_cdu/chartsnow_20121016_70734/477929\",\"date\":1374219880000," +
            "\"tracks\":[{\"productCode\":\"5506206\",\"title\":\"Cedar Hives\",\"artist\":\"Agar Agar\",\"isrc\":\"US5881200011\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"5506205\",\"title\":\"Quarry Diving\",\"artist\":\"Agar Agar\",\"isrc\":\"US5881200012\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"5506203\",\"title\":\"Argaarg\",\"artist\":\"Agar Agar\",\"isrc\":\"US5881200014\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"5506204\",\"title\":\"You brought me to the country!\",\"artist\":\"Agar Agar\",\"isrc\":\"US5881200013\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"5506207\",\"title\":\"Trees Sweet Heat\",\"artist\":\"Agar Agar\",\"isrc\":\"US5881200010\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}]," +
            "\"selected\":true},{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/ci_cdu/PIAS/20130321103906984\",\"date\":1374219880000," +
            "\"tracks\":[],\"selected\":true},{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/manual/020313/020313.csv\",\"date\":1374219880000," +
            "\"tracks\":[],\"selected\":true},{\"name\":\"fake.xml\",\"date\":1374224024000,\"tracks\":[],\"selected\":true}]}";

        HttpClient httpClient = mock(HttpClient.class);
        final HttpResponse successfulSearchResponse = new BasicHttpResponse(new BasicStatusLine(HTTP_1_1, 200, "OK")) {
            @Override
            public HttpEntity getEntity() {
                try {
                    return new StringEntity(outputJson);
                } catch (UnsupportedEncodingException e) {
                    return null;
                }
            }
        };
        when(httpClient.execute(any(HttpUriRequest.class))).thenAnswer(new Answer<HttpResponse>() {
            @Override
            public HttpResponse answer(InvocationOnMock invocationOnMock) throws Throwable {
                HttpPost request = (HttpPost) invocationOnMock.getArguments()[0];

                assertEquals("application/json", request.getLastHeader(HTTP.CONTENT_TYPE).getValue());

                return successfulSearchResponse;
            }
        });
        when(spy.getHttpClient()).thenReturn(httpClient);

        IngestWizardDataDto result = spy.selectTrackDrops(data);

        assertEquals(data.getSuid(), result.getSuid());
        assertEquals(9, result.getDrops().size());
        assertNotNull(result.getDrops().get(0).getTracks());
        verify(spy, times(1)).getSecuredHeaders();
    }

    @Test
    public void testSelectTrackDrops_Fail() throws Exception {
        HttpClient httpClient = getFailedHttpClient();
        TrackRepositoryHttpClientImpl spy = spy(client);
        when(spy.getHttpClient()).thenReturn(httpClient);

        final String inputJson =
            "{\"suid\":\"1374224031997\",\"drops\":[{\"name\":\"manifest.00000000000002472000.txt\",\"date\":1374219880000,\"tracks\":[{\"productCode\":\"828768551424\",\"title\":\"Mr. Tambourine " +
            "Man\",\"artist\":\"The Byrds\",\"isrc\":\"USSM16500019\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}],\"selected\":true}," +
            "{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/warner_cdu/new_release/20111011_0926_13\",\"date\":1374219880000," +
            "\"tracks\":[{\"productCode\":\"A10302B0001242753E\",\"title\":\"Talking To The Moon\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001985\",\"exists\":false,\"type\":\"INSERT\"," +
            "\"selected\":true},{\"productCode\":\"A10302B0001239465G\",\"title\":\"Our First Time\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001884\",\"exists\":false,\"type\":\"INSERT\"," +
            "\"selected\":true},{\"productCode\":\"A10302B00000004D0H\",\"title\":\"The Blower's Daughter\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100003\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001239462M\",\"title\":\"Runaway Baby\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001885\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001177364N\",\"title\":\"Just The Way You Are\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001269\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004D2D\",\"title\":\"Older Chests\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100005\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001239286M\",\"title\":\"Grenade\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001883\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001239463K\",\"title\":\"Marry You\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001887\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004D57\",\"title\":\"Cold Water\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100008\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001138697X\",\"title\":\"Somewhere In Brooklyn\",\"artist\":\"Bruno Mars\",\"isrc\":\"USEE11000166\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001239468A\",\"title\":\"The Lazy Song\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001886\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004D1F\",\"title\":\"Cannonball\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100004\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004C85\",\"title\":\"Delicate\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0000001\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004CZN\",\"title\":\"Volcano\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100002\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001230215J\",\"title\":\"Liquor Store Blues\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001881\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001138699T\",\"title\":\"The Other Side\",\"artist\":\"Bruno Mars\",\"isrc\":\"USEE11000167\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001138698V\",\"title\":\"Count On Me\",\"artist\":\"Bruno Mars\",\"isrc\":\"USEE11000168\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0000078780Z\",\"title\":\"Eskimo\",\"artist\":\"Damien Rice\",\"isrc\":\"GBFTG0300054\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001239466E\",\"title\":\"Talking To The Moon\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001777\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004D65\",\"title\":\"I Remember\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100009\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004D49\",\"title\":\"Cheers Darlin'\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100007\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004D3B\",\"title\":\"Amie\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100006\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true}],\"selected\":true},{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/warner_cdu/new_release_3" +
            ".4/20130103060213909\",\"date\":1374219880000,\"tracks\":[{\"productCode\":\"A10302B00008091667\",\"title\":\"Falling Down\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900017\"," +
            "\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00008091667\",\"title\":\"Showbiz\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900008\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00008091667\",\"title\":\"Escape\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900009\",\"exists\":false,\"type\":\"INSERT\"," +
            "\"selected\":true},{\"productCode\":\"A10302B00008091667\",\"title\":\"Unintended\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900015\",\"exists\":false,\"type\":\"INSERT\"," +
            "\"selected\":true},{\"productCode\":\"A10302B00008091667\",\"title\":\"Hate This and I'll Love You\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900013\",\"exists\":false,\"type\":\"INSERT\"," +
            "\"selected\":true},{\"productCode\":\"A10302B00008091667\",\"title\":\"Sunburn\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900012\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Uno\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900001\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Cave\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900007\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Fillip\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900010\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Sober\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900016\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Muscle Museum\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900014\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Spiral Static\",\"artist\":\"Muse\",\"isrc\":\"GBCVT0000001\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Overdue\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900011\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}],\"selected\":true}," +
            "{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/fuga_cdu/5051823094641_20130606112518431\",\"date\":1374219880000," +
            "\"tracks\":[{\"productCode\":\"5051823094641\",\"title\":\"Higher\",\"artist\":\"The Golden Boy\",\"isrc\":\"GBCEN1300409\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"5051823094641\",\"title\":\"Out of My Mind\",\"artist\":\"The Golden Boy\",\"isrc\":\"GBCEN1300411\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"5051823094641\",\"title\":\"Gonna Do\",\"artist\":\"The Golden Boy\",\"isrc\":\"GBCEN1300410\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}]," +
            "\"selected\":true},{\"name\":\"3000007191631\",\"date\":1374219880000,\"tracks\":[{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"GBSXS1100209\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"ROCRP1002941\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"GBSXS1100265\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"GBSXS1100266\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"GBSXS1100210\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"GBSXS1100212\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"GBSXS1100211\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"ROCRP1002948\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}],\"selected\":true}," +
            "{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/ioda_cdu/chartsnow_20121016_70734/477929\",\"date\":1374219880000," +
            "\"tracks\":[{\"productCode\":\"5506206\",\"title\":\"Cedar Hives\",\"artist\":\"Agar Agar\",\"isrc\":\"US5881200011\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"5506205\",\"title\":\"Quarry Diving\",\"artist\":\"Agar Agar\",\"isrc\":\"US5881200012\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"5506203\",\"title\":\"Argaarg\",\"artist\":\"Agar Agar\",\"isrc\":\"US5881200014\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"5506204\",\"title\":\"You brought me to the country!\",\"artist\":\"Agar Agar\",\"isrc\":\"US5881200013\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"5506207\",\"title\":\"Trees Sweet Heat\",\"artist\":\"Agar Agar\",\"isrc\":\"US5881200010\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}]," +
            "\"selected\":true},{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/ci_cdu/PIAS/20130321103906984\",\"date\":1374219880000," +
            "\"tracks\":[],\"selected\":true},{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/manual/020313/020313.csv\",\"date\":1374219880000," +
            "\"tracks\":[],\"selected\":true},{\"name\":\"fake.xml\",\"date\":1374224024000,\"tracks\":[],\"selected\":true}]}";
        Type type = new TypeToken<IngestWizardDataDto>() {}.getType();
        IngestWizardDataDto data = spy.gsonMillis.fromJson(new StringReader(inputJson), type);

        IngestWizardDataDto result = spy.selectTrackDrops(data);

        assertEquals(0, result.getDrops().size());
        assertEquals(null, result.getSuid());
        verify(spy, times(1)).getSecuredHeaders();
    }

    @Test
    public void testCommitDrops_Successful() throws Exception {
        TrackRepositoryHttpClientImpl spy = spy(client);

        final String inputJson =
            "{\"suid\":\"1374224031997\",\"drops\":[{\"name\":\"manifest.00000000000002472000.txt\",\"date\":1374219880000,\"tracks\":[{\"productCode\":\"828768551424\",\"title\":\"Mr. Tambourine " +
            "Man\",\"artist\":\"The Byrds\",\"isrc\":\"USSM16500019\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}],\"selected\":true}," +
            "{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/warner_cdu/new_release/20111011_0926_13\",\"date\":1374219880000," +
            "\"tracks\":[{\"productCode\":\"A10302B0001242753E\",\"title\":\"Talking To The Moon\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001985\",\"exists\":false,\"type\":\"INSERT\"," +
            "\"selected\":true},{\"productCode\":\"A10302B0001239465G\",\"title\":\"Our First Time\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001884\",\"exists\":false,\"type\":\"INSERT\"," +
            "\"selected\":true},{\"productCode\":\"A10302B00000004D0H\",\"title\":\"The Blower's Daughter\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100003\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001239462M\",\"title\":\"Runaway Baby\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001885\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001177364N\",\"title\":\"Just The Way You Are\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001269\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004D2D\",\"title\":\"Older Chests\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100005\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001239286M\",\"title\":\"Grenade\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001883\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001239463K\",\"title\":\"Marry You\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001887\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004D57\",\"title\":\"Cold Water\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100008\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001138697X\",\"title\":\"Somewhere In Brooklyn\",\"artist\":\"Bruno Mars\",\"isrc\":\"USEE11000166\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001239468A\",\"title\":\"The Lazy Song\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001886\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004D1F\",\"title\":\"Cannonball\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100004\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004C85\",\"title\":\"Delicate\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0000001\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004CZN\",\"title\":\"Volcano\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100002\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001230215J\",\"title\":\"Liquor Store Blues\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001881\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001138699T\",\"title\":\"The Other Side\",\"artist\":\"Bruno Mars\",\"isrc\":\"USEE11000167\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001138698V\",\"title\":\"Count On Me\",\"artist\":\"Bruno Mars\",\"isrc\":\"USEE11000168\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0000078780Z\",\"title\":\"Eskimo\",\"artist\":\"Damien Rice\",\"isrc\":\"GBFTG0300054\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001239466E\",\"title\":\"Talking To The Moon\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001777\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004D65\",\"title\":\"I Remember\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100009\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004D49\",\"title\":\"Cheers Darlin'\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100007\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004D3B\",\"title\":\"Amie\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100006\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true}],\"selected\":true},{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/warner_cdu/new_release_3" +
            ".4/20130103060213909\",\"date\":1374219880000,\"tracks\":[{\"productCode\":\"A10302B00008091667\",\"title\":\"Falling Down\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900017\"," +
            "\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00008091667\",\"title\":\"Showbiz\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900008\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00008091667\",\"title\":\"Escape\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900009\",\"exists\":false,\"type\":\"INSERT\"," +
            "\"selected\":true},{\"productCode\":\"A10302B00008091667\",\"title\":\"Unintended\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900015\",\"exists\":false,\"type\":\"INSERT\"," +
            "\"selected\":true},{\"productCode\":\"A10302B00008091667\",\"title\":\"Hate This and I'll Love You\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900013\",\"exists\":false,\"type\":\"INSERT\"," +
            "\"selected\":true},{\"productCode\":\"A10302B00008091667\",\"title\":\"Sunburn\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900012\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Uno\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900001\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Cave\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900007\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Fillip\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900010\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Sober\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900016\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Muscle Museum\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900014\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Spiral Static\",\"artist\":\"Muse\",\"isrc\":\"GBCVT0000001\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Overdue\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900011\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}],\"selected\":true}," +
            "{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/fuga_cdu/5051823094641_20130606112518431\",\"date\":1374219880000," +
            "\"tracks\":[{\"productCode\":\"5051823094641\",\"title\":\"Higher\",\"artist\":\"The Golden Boy\",\"isrc\":\"GBCEN1300409\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"5051823094641\",\"title\":\"Out of My Mind\",\"artist\":\"The Golden Boy\",\"isrc\":\"GBCEN1300411\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"5051823094641\",\"title\":\"Gonna Do\",\"artist\":\"The Golden Boy\",\"isrc\":\"GBCEN1300410\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}]," +
            "\"selected\":true},{\"name\":\"3000007191631\",\"date\":1374219880000,\"tracks\":[{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"GBSXS1100209\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"ROCRP1002941\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"GBSXS1100265\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"GBSXS1100266\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"GBSXS1100210\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"GBSXS1100212\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"GBSXS1100211\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"ROCRP1002948\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}],\"selected\":true}," +
            "{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/ioda_cdu/chartsnow_20121016_70734/477929\",\"date\":1374219880000," +
            "\"tracks\":[{\"productCode\":\"5506206\",\"title\":\"Cedar Hives\",\"artist\":\"Agar Agar\",\"isrc\":\"US5881200011\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"5506205\",\"title\":\"Quarry Diving\",\"artist\":\"Agar Agar\",\"isrc\":\"US5881200012\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"5506203\",\"title\":\"Argaarg\",\"artist\":\"Agar Agar\",\"isrc\":\"US5881200014\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"5506204\",\"title\":\"You brought me to the country!\",\"artist\":\"Agar Agar\",\"isrc\":\"US5881200013\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"5506207\",\"title\":\"Trees Sweet Heat\",\"artist\":\"Agar Agar\",\"isrc\":\"US5881200010\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}]," +
            "\"selected\":true},{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/ci_cdu/PIAS/20130321103906984\",\"date\":1374219880000," +
            "\"tracks\":[],\"selected\":true},{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/manual/020313/020313.csv\",\"date\":1374219880000," +
            "\"tracks\":[],\"selected\":true},{\"name\":\"fake.xml\",\"date\":1374224024000,\"tracks\":[],\"selected\":true}]}";
        Type type = new TypeToken<IngestWizardDataDto>() {}.getType();
        IngestWizardDataDto data = spy.gsonMillis.fromJson(new StringReader(inputJson), type);
        final String outputJson =
            "{\"suid\":\"1374224031997\",\"drops\":[{\"name\":\"manifest.00000000000002472000.txt\",\"date\":1374219880000,\"tracks\":[{\"productCode\":\"828768551424\",\"title\":\"Mr. Tambourine " +
            "Man\",\"artist\":\"The Byrds\",\"isrc\":\"USSM16500019\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}],\"selected\":true}," +
            "{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/warner_cdu/new_release/20111011_0926_13\",\"date\":1374219880000," +
            "\"tracks\":[{\"productCode\":\"A10302B0001242753E\",\"title\":\"Talking To The Moon\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001985\",\"exists\":false,\"type\":\"INSERT\"," +
            "\"selected\":true},{\"productCode\":\"A10302B0001239465G\",\"title\":\"Our First Time\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001884\",\"exists\":false,\"type\":\"INSERT\"," +
            "\"selected\":true},{\"productCode\":\"A10302B00000004D0H\",\"title\":\"The Blower's Daughter\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100003\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001239462M\",\"title\":\"Runaway Baby\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001885\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001177364N\",\"title\":\"Just The Way You Are\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001269\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004D2D\",\"title\":\"Older Chests\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100005\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001239286M\",\"title\":\"Grenade\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001883\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001239463K\",\"title\":\"Marry You\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001887\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004D57\",\"title\":\"Cold Water\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100008\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001138697X\",\"title\":\"Somewhere In Brooklyn\",\"artist\":\"Bruno Mars\",\"isrc\":\"USEE11000166\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001239468A\",\"title\":\"The Lazy Song\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001886\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004D1F\",\"title\":\"Cannonball\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100004\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004C85\",\"title\":\"Delicate\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0000001\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004CZN\",\"title\":\"Volcano\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100002\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001230215J\",\"title\":\"Liquor Store Blues\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001881\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001138699T\",\"title\":\"The Other Side\",\"artist\":\"Bruno Mars\",\"isrc\":\"USEE11000167\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001138698V\",\"title\":\"Count On Me\",\"artist\":\"Bruno Mars\",\"isrc\":\"USEE11000168\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0000078780Z\",\"title\":\"Eskimo\",\"artist\":\"Damien Rice\",\"isrc\":\"GBFTG0300054\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001239466E\",\"title\":\"Talking To The Moon\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001777\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004D65\",\"title\":\"I Remember\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100009\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004D49\",\"title\":\"Cheers Darlin'\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100007\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004D3B\",\"title\":\"Amie\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100006\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true}],\"selected\":true},{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/warner_cdu/new_release_3" +
            ".4/20130103060213909\",\"date\":1374219880000,\"tracks\":[{\"productCode\":\"A10302B00008091667\",\"title\":\"Falling Down\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900017\"," +
            "\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00008091667\",\"title\":\"Showbiz\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900008\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00008091667\",\"title\":\"Escape\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900009\",\"exists\":false,\"type\":\"INSERT\"," +
            "\"selected\":true},{\"productCode\":\"A10302B00008091667\",\"title\":\"Unintended\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900015\",\"exists\":false,\"type\":\"INSERT\"," +
            "\"selected\":true},{\"productCode\":\"A10302B00008091667\",\"title\":\"Hate This and I'll Love You\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900013\",\"exists\":false,\"type\":\"INSERT\"," +
            "\"selected\":true},{\"productCode\":\"A10302B00008091667\",\"title\":\"Sunburn\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900012\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Uno\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900001\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Cave\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900007\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Fillip\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900010\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Sober\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900016\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Muscle Museum\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900014\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Spiral Static\",\"artist\":\"Muse\",\"isrc\":\"GBCVT0000001\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Overdue\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900011\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}],\"selected\":true}," +
            "{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/fuga_cdu/5051823094641_20130606112518431\",\"date\":1374219880000," +
            "\"tracks\":[{\"productCode\":\"5051823094641\",\"title\":\"Higher\",\"artist\":\"The Golden Boy\",\"isrc\":\"GBCEN1300409\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"5051823094641\",\"title\":\"Out of My Mind\",\"artist\":\"The Golden Boy\",\"isrc\":\"GBCEN1300411\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"5051823094641\",\"title\":\"Gonna Do\",\"artist\":\"The Golden Boy\",\"isrc\":\"GBCEN1300410\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}]," +
            "\"selected\":true},{\"name\":\"3000007191631\",\"date\":1374219880000,\"tracks\":[{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"GBSXS1100209\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"ROCRP1002941\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"GBSXS1100265\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"GBSXS1100266\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"GBSXS1100210\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"GBSXS1100212\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"GBSXS1100211\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"ROCRP1002948\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}],\"selected\":true}," +
            "{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/ioda_cdu/chartsnow_20121016_70734/477929\",\"date\":1374219880000," +
            "\"tracks\":[{\"productCode\":\"5506206\",\"title\":\"Cedar Hives\",\"artist\":\"Agar Agar\",\"isrc\":\"US5881200011\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"5506205\",\"title\":\"Quarry Diving\",\"artist\":\"Agar Agar\",\"isrc\":\"US5881200012\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"5506203\",\"title\":\"Argaarg\",\"artist\":\"Agar Agar\",\"isrc\":\"US5881200014\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"5506204\",\"title\":\"You brought me to the country!\",\"artist\":\"Agar Agar\",\"isrc\":\"US5881200013\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"5506207\",\"title\":\"Trees Sweet Heat\",\"artist\":\"Agar Agar\",\"isrc\":\"US5881200010\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}]," +
            "\"selected\":true},{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/ci_cdu/PIAS/20130321103906984\",\"date\":1374219880000," +
            "\"tracks\":[],\"selected\":true},{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/manual/020313/020313.csv\",\"date\":1374219880000," +
            "\"tracks\":[],\"selected\":true},{\"name\":\"fake.xml\",\"date\":1374224024000,\"tracks\":[],\"selected\":true}]}";

        HttpClient httpClient = mock(HttpClient.class);
        final HttpResponse successfulSearchResponse = new BasicHttpResponse(new BasicStatusLine(HTTP_1_1, 200, "OK")) {
            @Override
            public HttpEntity getEntity() {
                try {
                    return new StringEntity("true");
                } catch (UnsupportedEncodingException e) {
                    return null;
                }
            }
        };
        when(httpClient.execute(any(HttpUriRequest.class))).thenAnswer(new Answer<HttpResponse>() {
            @Override
            public HttpResponse answer(InvocationOnMock invocationOnMock) throws Throwable {
                HttpPost request = (HttpPost) invocationOnMock.getArguments()[0];

                assertEquals("application/json", request.getLastHeader(HTTP.CONTENT_TYPE).getValue());

                return successfulSearchResponse;
            }
        });
        when(spy.getHttpClient()).thenReturn(httpClient);

        Boolean result = spy.commitDrops(data);

        assertTrue(result);
        verify(spy, times(1)).getSecuredHeaders();
    }

    @Test
    public void testCommitDrops_Fail() throws Exception {
        HttpClient httpClient = getFailedHttpClient();
        TrackRepositoryHttpClientImpl spy = spy(client);
        when(spy.getHttpClient()).thenReturn(httpClient);

        final String inputJson =
            "{\"suid\":\"1374224031997\",\"drops\":[{\"name\":\"manifest.00000000000002472000.txt\",\"date\":1374219880000,\"tracks\":[{\"productCode\":\"828768551424\",\"title\":\"Mr. Tambourine " +
            "Man\",\"artist\":\"The Byrds\",\"isrc\":\"USSM16500019\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}],\"selected\":true}," +
            "{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/warner_cdu/new_release/20111011_0926_13\",\"date\":1374219880000," +
            "\"tracks\":[{\"productCode\":\"A10302B0001242753E\",\"title\":\"Talking To The Moon\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001985\",\"exists\":false,\"type\":\"INSERT\"," +
            "\"selected\":true},{\"productCode\":\"A10302B0001239465G\",\"title\":\"Our First Time\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001884\",\"exists\":false,\"type\":\"INSERT\"," +
            "\"selected\":true},{\"productCode\":\"A10302B00000004D0H\",\"title\":\"The Blower's Daughter\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100003\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001239462M\",\"title\":\"Runaway Baby\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001885\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001177364N\",\"title\":\"Just The Way You Are\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001269\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004D2D\",\"title\":\"Older Chests\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100005\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001239286M\",\"title\":\"Grenade\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001883\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001239463K\",\"title\":\"Marry You\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001887\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004D57\",\"title\":\"Cold Water\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100008\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001138697X\",\"title\":\"Somewhere In Brooklyn\",\"artist\":\"Bruno Mars\",\"isrc\":\"USEE11000166\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001239468A\",\"title\":\"The Lazy Song\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001886\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004D1F\",\"title\":\"Cannonball\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100004\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004C85\",\"title\":\"Delicate\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0000001\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004CZN\",\"title\":\"Volcano\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100002\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001230215J\",\"title\":\"Liquor Store Blues\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001881\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001138699T\",\"title\":\"The Other Side\",\"artist\":\"Bruno Mars\",\"isrc\":\"USEE11000167\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001138698V\",\"title\":\"Count On Me\",\"artist\":\"Bruno Mars\",\"isrc\":\"USEE11000168\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0000078780Z\",\"title\":\"Eskimo\",\"artist\":\"Damien Rice\",\"isrc\":\"GBFTG0300054\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B0001239466E\",\"title\":\"Talking To The Moon\",\"artist\":\"Bruno Mars\",\"isrc\":\"USAT21001777\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004D65\",\"title\":\"I Remember\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100009\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004D49\",\"title\":\"Cheers Darlin'\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100007\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00000004D3B\",\"title\":\"Amie\",\"artist\":\"Damien Rice\",\"isrc\":\"IEABD0100006\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true}],\"selected\":true},{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/warner_cdu/new_release_3" +
            ".4/20130103060213909\",\"date\":1374219880000,\"tracks\":[{\"productCode\":\"A10302B00008091667\",\"title\":\"Falling Down\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900017\"," +
            "\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00008091667\",\"title\":\"Showbiz\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900008\",\"exists\":false," +
            "\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"A10302B00008091667\",\"title\":\"Escape\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900009\",\"exists\":false,\"type\":\"INSERT\"," +
            "\"selected\":true},{\"productCode\":\"A10302B00008091667\",\"title\":\"Unintended\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900015\",\"exists\":false,\"type\":\"INSERT\"," +
            "\"selected\":true},{\"productCode\":\"A10302B00008091667\",\"title\":\"Hate This and I'll Love You\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900013\",\"exists\":false,\"type\":\"INSERT\"," +
            "\"selected\":true},{\"productCode\":\"A10302B00008091667\",\"title\":\"Sunburn\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900012\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Uno\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900001\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Cave\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900007\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Fillip\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900010\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Sober\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900016\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Muscle Museum\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900014\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Spiral Static\",\"artist\":\"Muse\",\"isrc\":\"GBCVT0000001\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"A10302B00008091667\",\"title\":\"Overdue\",\"artist\":\"Muse\",\"isrc\":\"GBCVT9900011\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}],\"selected\":true}," +
            "{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/fuga_cdu/5051823094641_20130606112518431\",\"date\":1374219880000," +
            "\"tracks\":[{\"productCode\":\"5051823094641\",\"title\":\"Higher\",\"artist\":\"The Golden Boy\",\"isrc\":\"GBCEN1300409\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"5051823094641\",\"title\":\"Out of My Mind\",\"artist\":\"The Golden Boy\",\"isrc\":\"GBCEN1300411\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"5051823094641\",\"title\":\"Gonna Do\",\"artist\":\"The Golden Boy\",\"isrc\":\"GBCEN1300410\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}]," +
            "\"selected\":true},{\"name\":\"3000007191631\",\"date\":1374219880000,\"tracks\":[{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"GBSXS1100209\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"ROCRP1002941\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"GBSXS1100265\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"GBSXS1100266\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"GBSXS1100210\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"GBSXS1100212\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"GBSXS1100211\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true},{\"productCode\":\"05037128167051\",\"title\":\"Get Back (ASAP)\",\"artist\":\"Alexandra Stan\"," +
            "\"isrc\":\"ROCRP1002948\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}],\"selected\":true}," +
            "{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/ioda_cdu/chartsnow_20121016_70734/477929\",\"date\":1374219880000," +
            "\"tracks\":[{\"productCode\":\"5506206\",\"title\":\"Cedar Hives\",\"artist\":\"Agar Agar\",\"isrc\":\"US5881200011\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"5506205\",\"title\":\"Quarry Diving\",\"artist\":\"Agar Agar\",\"isrc\":\"US5881200012\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"5506203\",\"title\":\"Argaarg\",\"artist\":\"Agar Agar\",\"isrc\":\"US5881200014\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"5506204\",\"title\":\"You brought me to the country!\",\"artist\":\"Agar Agar\",\"isrc\":\"US5881200013\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}," +
            "{\"productCode\":\"5506207\",\"title\":\"Trees Sweet Heat\",\"artist\":\"Agar Agar\",\"isrc\":\"US5881200010\",\"exists\":false,\"type\":\"INSERT\",\"selected\":true}]," +
            "\"selected\":true},{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/ci_cdu/PIAS/20130321103906984\",\"date\":1374219880000," +
            "\"tracks\":[],\"selected\":true},{\"name\":\"/home/sanya/WORKSPACE/git/MusicQubed/server/server/track-repo/target/test-classes/media/manual/020313/020313.csv\",\"date\":1374219880000," +
            "\"tracks\":[],\"selected\":true},{\"name\":\"fake.xml\",\"date\":1374224024000,\"tracks\":[],\"selected\":true}]}";
        Type type = new TypeToken<IngestWizardDataDto>() {}.getType();
        IngestWizardDataDto data = spy.gsonMillis.fromJson(new StringReader(inputJson), type);

        Boolean result = spy.commitDrops(data);

        assertFalse(result);
        verify(spy, times(1)).getSecuredHeaders();
    }

    @Test
    public void isLoggedIn_Successful() throws Exception {

        HttpClient httpClient = mock(HttpClient.class);
        HttpResponse successfulLoginResponse = new BasicHttpResponse(new BasicStatusLine(HTTP_1_1, 200, "OK"));
        when(httpClient.execute(any(HttpUriRequest.class))).thenReturn(successfulLoginResponse);
        TrackRepositoryHttpClientImpl spy = spy(client);
        when(spy.getHttpClient()).thenReturn(httpClient);

        boolean loggedIn = spy.isLoggedIn();
        assertEquals(true, loggedIn);
    }

    @Test
    public void isLoggedIn_Fail() throws Exception {
        HttpClient httpClient = getFailedHttpClient();
        TrackRepositoryHttpClientImpl spy = spy(client);
        when(spy.getHttpClient()).thenReturn(httpClient);

        boolean loggedIn = spy.isLoggedIn();

        assertEquals(false, loggedIn);
    }

    @Test
    public void getSecuredHeader_Successful() {
        Header[] securedHeaders = client.getSecuredHeaders();

        String securedToken = new String(Base64.encode(clientUsername.concat(":").concat(clientPassword).getBytes()));

        assertEquals(1, securedHeaders.length);
        assertEquals("Authorization", securedHeaders[0].getName());
        assertEquals(true, securedHeaders[0].getValue().startsWith("Basic "));
        assertEquals("Basic ".concat(securedToken), securedHeaders[0].getValue());
    }

    @Test
    public void search_Successful() throws Exception {

        HttpClient httpClient = mock(HttpClient.class);
        HttpResponse successfulSearchResponse = new BasicHttpResponse(new BasicStatusLine(HTTP_1_1, 200, "OK")) {
            @Override
            public HttpEntity getEntity() {
                try {
                    return new StringEntity("{\"total\":10,\"page\":1,\"size\":10,\"list\":[{\"id\":383,\"ingestor\":null,\"isrc\":\"123456789\",\"title\":\"2011\",\"artist\":\"Paul Simon\"," +
                                            "\"ingestionDate\":\"2011-09-28\",\"status\":\"NONE\",\"subTitle\":null,\"productId\":null,\"productCode\":null,\"genre\":\"Pop\",\"copyright\":\"(P) " +
                                            "2010 Paul Simon under " +
                                            "exclusive license of Sony Music Entertainment\",\"year\":null,\"album\":\"Hearts And Bones\",\"info\":null,\"licensed\":true," +
                                            "\"ingestionUpdateDate\":\"2011-11-14\"," +
                                            "\"publishDate\":null,\"files\":null}]}");
                } catch (UnsupportedEncodingException e) {
                    return null;
                }
            }
        };
        when(httpClient.execute(any(HttpUriRequest.class))).thenReturn(successfulSearchResponse);
        TrackRepositoryHttpClientImpl spy = spy(client);
        when(spy.getHttpClient()).thenReturn(httpClient);

        Pageable page = new PageRequest(0, 30);
        PageListDto<TrackDto> searchResult = spy.search("red music", page);

        assertEquals(10, searchResult.getTotal());
        assertEquals(1, searchResult.getPage());
        assertEquals(10, searchResult.getSize());
        assertEquals(1, searchResult.getList().size());
        assertEquals("2011", searchResult.getList().get(0).getTitle());
        assertEquals("Paul Simon", searchResult.getList().get(0).getArtist());
        assertEquals("Pop", searchResult.getList().get(0).getGenre());
        assertEquals("123456789", searchResult.getList().get(0).getIsrc());
        assertEquals(TrackStatus.NONE, searchResult.getList().get(0).getStatus());
        verify(spy, times(1)).getSecuredHeaders();
    }

    @Test
    public void search_By() throws Exception {

        HttpClient httpClient = mock(HttpClient.class);
        HttpResponse successfulSearchResponse = new BasicHttpResponse(new BasicStatusLine(HTTP_1_1, 200, "OK")) {
            @Override
            public HttpEntity getEntity() {
                try {
                    return new StringEntity("{\"total\":10,\"page\":1,\"size\":10,\"list\":[{\"id\":383,\"ingestor\":null,\"isrc\":\"123456789\",\"title\":\"2011\",\"artist\":\"Paul Simon\"," +
                                            "\"ingestionDate\":\"2011-09-28\",\"status\":\"NONE\",\"subTitle\":null,\"productId\":null,\"productCode\":null,\"genre\":\"Pop\",\"copyright\":\"(P) " +
                                            "2010 Paul Simon under " +
                                            "exclusive license of Sony Music Entertainment\",\"year\":null,\"album\":\"Hearts And Bones\",\"info\":null,\"licensed\":true," +
                                            "\"ingestionUpdateDate\":\"2011-11-14\"," +
                                            "\"publishDate\":null,\"files\":null}]}");
                } catch (UnsupportedEncodingException e) {
                    return null;
                }
            }
        };
        when(httpClient.execute(any(HttpUriRequest.class))).thenReturn(successfulSearchResponse);
        TrackRepositoryHttpClientImpl spy = spy(client);
        when(spy.getHttpClient()).thenReturn(httpClient);

        Pageable page = new PageRequest(0, 30);
        PageListDto<TrackDto> searchResult = spy.search("red music", page);

        assertEquals(10, searchResult.getTotal());
        assertEquals(1, searchResult.getPage());
        assertEquals(10, searchResult.getSize());
        assertEquals(1, searchResult.getList().size());
        assertEquals("2011", searchResult.getList().get(0).getTitle());
        assertEquals("Paul Simon", searchResult.getList().get(0).getArtist());
        assertEquals("Pop", searchResult.getList().get(0).getGenre());
        assertEquals("123456789", searchResult.getList().get(0).getIsrc());
        assertEquals(TrackStatus.NONE, searchResult.getList().get(0).getStatus());
        verify(spy, times(1)).getSecuredHeaders();
    }

    @Test
    public void search_Fail() throws Exception {
        HttpClient httpClient = getFailedHttpClient();
        TrackRepositoryHttpClientImpl spy = spy(client);
        when(spy.getHttpClient()).thenReturn(httpClient);

        Pageable page = new PageRequest(0, 30);
        PageListDto<TrackDto> searchResult = spy.search("red music", page);

        assertEquals(0, searchResult.getList().size());
        verify(spy, times(1)).getSecuredHeaders();
    }

    protected HttpClient getFailedHttpClient() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);
        HttpResponse failedResponse = new BasicHttpResponse(new BasicStatusLine(HTTP_1_1, 500, "OK"));
        when(httpClient.execute(any(HttpUriRequest.class))).thenReturn(failedResponse);
        return httpClient;
    }

    @Test
    public void pull_Successful() throws Exception {
        HttpClient httpClient = getMockedHttpClientWithOneTrack();
        TrackRepositoryHttpClientImpl spy = spy(client);
        when(spy.getHttpClient()).thenReturn(httpClient);

        TrackDto pulledTrack = spy.pullTrack(383L);

        assertNotNull(pulledTrack);
        assertEquals(2, pulledTrack.getFiles().size());
        verify(spy, times(1)).getSecuredHeaders();
    }

    protected HttpClient getMockedHttpClientWithOneTrack() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);
        HttpResponse successfulPullResponse = new BasicHttpResponse(new BasicStatusLine(HTTP_1_1, 200, "OK")) {
            @Override
            public HttpEntity getEntity() {
                try {
                    return new StringEntity(
                        "{\"id\":383,\"ingestor\":null,\"isrc\":\"123456789\",\"title\":\"2011\",\"artist\":\"Paul Simon\",\"ingestionDate\":\"2011-09-28\",\"status\":\"ENCODED\",\"subTitle\":null," +
                        "\"productId\":null,\"productCode\":null,\"genre\":\"Pop\",\"copyright\":\"(P) 2010 Paul Simon under exclusive license of Sony Music Entertainment\",\"year\":null," +
                        "\"album\":\"Hearts And Bones\",\"info\":null,\"licensed\":true,\"ingestionUpdateDate\":\"2011-11-14\",\"publishDate\":\"2011-11-14\",\"files\":[{\"type\":\"DOWNLOAD\"," +
                        "\"filename\":\"/global/path/temp.aud\",\"resolution\":\"RATE_ORIGINAL\"},{\"type\":\"\",\"filename\":\"/global/path/temp.aud\",\"resolution\":\"RATE_ORIGINAL\"}]}");
                } catch (UnsupportedEncodingException e) {
                    return null;
                }
            }
        };
        when(httpClient.execute(any(HttpUriRequest.class))).thenReturn(successfulPullResponse);

        return httpClient;
    }

    @Test
    public void pull_With_Null_Id() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);
        HttpResponse successfulPullResponse = new BasicHttpResponse(new BasicStatusLine(HTTP_1_1, 200, "OK"));
        when(httpClient.execute(any(HttpUriRequest.class))).thenReturn(successfulPullResponse);
        TrackRepositoryHttpClientImpl spy = spy(client);
        when(spy.getHttpClient()).thenReturn(httpClient);

        TrackDto pulledTrack = spy.pullTrack(null);

        assertNull(pulledTrack);
        verify(spy, never()).getSecuredHeaders();
    }

    @Test
    public void pull_Fail() throws Exception {
        HttpClient httpClient = getFailedHttpClient();
        TrackRepositoryHttpClientImpl spy = spy(client);
        when(spy.getHttpClient()).thenReturn(httpClient);

        TrackDto pulledTrack = spy.pullTrack(383L);

        assertNull(pulledTrack);
        verify(spy, times(1)).getSecuredHeaders();
    }

    @Test
    public void encode_HttpStatus200_Successful() throws Exception {

        final long id = 383L;
        final DateFormat dateInstance = new SimpleDateFormat("yyyy-MM-dd");

        client.setUsername(clientUsername);
        client.setPassword(clientPassword);

        List<ResourceFileDto> resourceFileDtos = new LinkedList<ResourceFileDto>();

        ResourceFileDto resourceFileDto1 = getResourceFileDtoInstance();
        ResourceFileDto resourceFileDto2 = getResourceFileDtoInstance();

        resourceFileDtos.add(resourceFileDto1);
        resourceFileDtos.add(resourceFileDto2);

        TrackDto expectedTrackDto = getTrackDto(dateInstance, resourceFileDtos, id);

        final HttpEntity httpEntity = getHttpEntity(dateInstance, resourceFileDto1, resourceFileDto2, expectedTrackDto);

        PowerMockito.mockStatic(TrackRepositoryHttpClientImpl.HttpClientFactory.class);
        HttpClient mockDefaultHttpClient = Mockito.mock(HttpClient.class);
        PowerMockito.when(HttpClientFactory.getHttpClient()).thenReturn(mockDefaultHttpClient);
        Mockito.when(mockDefaultHttpClient.execute(Mockito.any(HttpPost.class))).thenAnswer(new Answer<HttpResponse>() {
            @Override
            public HttpResponse answer(InvocationOnMock invocation) throws Throwable {
                HttpPost httpPost = (HttpPost) invocation.getArguments()[0];
                Header[] actualHeaders = httpPost.getAllHeaders();

                Assert.assertNotNull(actualHeaders);

                URI actualUri = httpPost.getURI();

                Assert.assertNotNull(actualUri);
                Assert.assertEquals("http://localhost:8080/trackrepo/tracks/" + id + "/encode.json?isHighRate=false&licensed=true", actualUri.toString());

                Map<String, String> actualHeaderMap = new HashMap<String, String>();

                for (Header actualHeader : actualHeaders) {
                    actualHeaderMap.put(actualHeader.getName(), actualHeader.getValue());
                }

                Header[] expectedHeaders = getSecuredHeaders();
                for (Header expectedHeader : expectedHeaders) {
                    String expectedHeaderName = expectedHeader.getName();
                    String actualHeaderValue = actualHeaderMap.get(expectedHeaderName);
                    Assert.assertEquals(actualHeaderValue, expectedHeader.getValue());
                }

                HttpResponse httpResponse = new BasicHttpResponse(new BasicStatusLine(HTTP_1_1, HttpURLConnection.HTTP_OK, "OK")) {
                    @Override
                    public HttpEntity getEntity() {
                        return httpEntity;
                    }
                };
                return httpResponse;
            }
        });

        TrackDto actualTrackDto = client.encodeTrack(id, false, true);
        assertNotNull(actualTrackDto);
        assertEquals(expectedTrackDto, actualTrackDto);
    }

    @Test
    public void encode_HttpStatus201_Successful() throws Exception {

        final long id = 383L;
        final DateFormat dateInstance = new SimpleDateFormat("yyyy-MM-dd");

        client.setUsername(clientUsername);
        client.setPassword(clientPassword);

        List<ResourceFileDto> resourceFileDtos = new LinkedList<ResourceFileDto>();

        ResourceFileDto resourceFileDto1 = getResourceFileDtoInstance();
        ResourceFileDto resourceFileDto2 = getResourceFileDtoInstance();

        resourceFileDtos.add(resourceFileDto1);
        resourceFileDtos.add(resourceFileDto2);

        TrackDto expectedTrackDto = getTrackDto(dateInstance, resourceFileDtos, id);

        final HttpEntity httpEntity = getHttpEntity(dateInstance, resourceFileDto1, resourceFileDto2, expectedTrackDto);

        PowerMockito.mockStatic(TrackRepositoryHttpClientImpl.HttpClientFactory.class);
        HttpClient mockDefaultHttpClient = Mockito.mock(HttpClient.class);
        PowerMockito.when(HttpClientFactory.getHttpClient()).thenReturn(mockDefaultHttpClient);
        Mockito.when(mockDefaultHttpClient.execute(Mockito.any(HttpPost.class))).thenAnswer(new Answer<HttpResponse>() {
            @Override
            public HttpResponse answer(InvocationOnMock invocation) throws Throwable {
                HttpPost httpPost = (HttpPost) invocation.getArguments()[0];
                Header[] actualHeaders = httpPost.getAllHeaders();

                Assert.assertNotNull(actualHeaders);

                URI actualUri = httpPost.getURI();

                Assert.assertNotNull(actualUri);
                Assert.assertEquals("http://localhost:8080/trackrepo/tracks/" + id + "/encode.json?isHighRate=false&licensed=true", actualUri.toString());

                Map<String, String> actualHeaderMap = new HashMap<String, String>();

                for (Header actualHeader : actualHeaders) {
                    actualHeaderMap.put(actualHeader.getName(), actualHeader.getValue());
                }

                Header[] expectedHeaders = getSecuredHeaders();
                for (Header expectedHeader : expectedHeaders) {
                    String expectedHeaderName = expectedHeader.getName();
                    String actualHeaderValue = actualHeaderMap.get(expectedHeaderName);
                    Assert.assertEquals(actualHeaderValue, expectedHeader.getValue());
                }

                HttpResponse httpResponse = new BasicHttpResponse(new BasicStatusLine(HTTP_1_1, HttpURLConnection.HTTP_CREATED, "OK")) {
                    @Override
                    public HttpEntity getEntity() {
                        return httpEntity;
                    }
                };
                return httpResponse;
            }
        });

        TrackDto actualTrackDto = client.encodeTrack(id, false, true);
        assertNotNull(actualTrackDto);
        assertEquals(expectedTrackDto, actualTrackDto);
    }

    @Test
    public void encode_HttpStatus204_Successful() throws Exception {
        final long id = 383L;

        PowerMockito.mockStatic(TrackRepositoryHttpClientImpl.HttpClientFactory.class);
        HttpClient mockDefaultHttpClient = Mockito.mock(HttpClient.class);
        PowerMockito.when(HttpClientFactory.getHttpClient()).thenReturn(mockDefaultHttpClient);
        Mockito.when(mockDefaultHttpClient.execute(Mockito.any(HttpPost.class))).thenAnswer(new Answer<HttpResponse>() {
            @Override
            public HttpResponse answer(InvocationOnMock invocation) throws Throwable {
                HttpPost httpPost = (HttpPost) invocation.getArguments()[0];
                Header[] actualHeaders = httpPost.getAllHeaders();

                Assert.assertNotNull(actualHeaders);

                URI actualUri = httpPost.getURI();

                Assert.assertNotNull(actualUri);
                Assert.assertEquals("http://localhost:8080/trackrepo/tracks/" + id + "/encode.json?isHighRate=false&licensed=true", actualUri.toString());

                Map<String, String> actualHeaderMap = new HashMap<String, String>();

                for (Header actualHeader : actualHeaders) {
                    actualHeaderMap.put(actualHeader.getName(), actualHeader.getValue());
                }

                Header[] expectedHeaders = getSecuredHeaders();
                for (Header expectedHeader : expectedHeaders) {
                    String expectedHeaderName = expectedHeader.getName();
                    String actualHeaderValue = actualHeaderMap.get(expectedHeaderName);
                    Assert.assertEquals(actualHeaderValue, expectedHeader.getValue());
                }

                HttpResponse httpResponse = new BasicHttpResponse(new BasicStatusLine(HTTP_1_1, HttpURLConnection.HTTP_NO_CONTENT, "OK")) {
                    @Override
                    public HttpEntity getEntity() {
                        return null;
                    }
                };
                return httpResponse;
            }
        });

        TrackDto actualTrackDto = client.encodeTrack(id, false, true);
        assertNull(actualTrackDto);
    }

    @Test(expected = NullPointerException.class)
    public void encode_IdIsNull_Failure() throws Exception {
        final Long id = null;

        client.encodeTrack(id, false, true);
    }

    @Test(expected = Exception.class)
    public void encode_NotParsableEntity_Failure() throws Exception {
        final long id = 383L;

        final HttpEntity httpEntity = new StringEntity("encode_NotParsableEntity_Failure");

        PowerMockito.mockStatic(TrackRepositoryHttpClientImpl.HttpClientFactory.class);
        HttpClient mockDefaultHttpClient = Mockito.mock(HttpClient.class);
        PowerMockito.when(HttpClientFactory.getHttpClient()).thenReturn(mockDefaultHttpClient);
        Mockito.when(mockDefaultHttpClient.execute(Mockito.any(HttpPost.class))).thenAnswer(new Answer<HttpResponse>() {
            @Override
            public HttpResponse answer(InvocationOnMock invocation) throws Throwable {
                HttpPost httpPost = (HttpPost) invocation.getArguments()[0];
                Header[] actualHeaders = httpPost.getAllHeaders();

                Assert.assertNotNull(actualHeaders);

                URI actualUri = httpPost.getURI();

                Assert.assertNotNull(actualUri);
                String uri = actualUri.toString();
                Assert.assertEquals("http://localhost:8080/trackrepo/tracks/" + id + "/encode.json?isHighRate=false&licensed=true", uri);

                Map<String, String> actualHeaderMap = new HashMap<String, String>();

                for (Header actualHeader : actualHeaders) {
                    actualHeaderMap.put(actualHeader.getName(), actualHeader.getValue());
                }

                Header[] expectedHeaders = getSecuredHeaders();
                for (Header expectedHeader : expectedHeaders) {
                    String expectedHeaderName = expectedHeader.getName();
                    String actualHeaderValue = actualHeaderMap.get(expectedHeaderName);
                    Assert.assertEquals(actualHeaderValue, expectedHeader.getValue());
                }

                HttpResponse httpResponse = new BasicHttpResponse(new BasicStatusLine(HTTP_1_1, HttpURLConnection.HTTP_OK, "OK")) {
                    @Override
                    public HttpEntity getEntity() {
                        return httpEntity;
                    }
                };
                return httpResponse;
            }
        });

        client.encodeTrack(id, false, true);
    }

    @Test(expected = Exception.class)
    public void encode_HttpStatus500_Successful() throws Exception {
        final long id = 383L;

        final DateFormat dateInstance = new SimpleDateFormat("yyyy-MM-dd");

        client.setUsername(clientUsername);
        client.setPassword(clientPassword);

        List<ResourceFileDto> resourceFileDtos = new LinkedList<ResourceFileDto>();

        ResourceFileDto resourceFileDto1 = getResourceFileDtoInstance();
        ResourceFileDto resourceFileDto2 = getResourceFileDtoInstance();

        resourceFileDtos.add(resourceFileDto1);
        resourceFileDtos.add(resourceFileDto2);

        TrackDto expectedTrackDto = getTrackDto(dateInstance, resourceFileDtos, id);

        final HttpEntity httpEntity = getHttpEntity(dateInstance, resourceFileDto1, resourceFileDto2, expectedTrackDto);

        PowerMockito.mockStatic(TrackRepositoryHttpClientImpl.HttpClientFactory.class);
        HttpClient mockDefaultHttpClient = Mockito.mock(HttpClient.class);
        PowerMockito.when(HttpClientFactory.getHttpClient()).thenReturn(mockDefaultHttpClient);
        Mockito.when(mockDefaultHttpClient.execute(Mockito.any(HttpPost.class))).thenAnswer(new Answer<HttpResponse>() {
            @Override
            public HttpResponse answer(InvocationOnMock invocation) throws Throwable {
                HttpPost httpPost = (HttpPost) invocation.getArguments()[0];
                Header[] actualHeaders = httpPost.getAllHeaders();

                Assert.assertNotNull(actualHeaders);

                URI actualUri = httpPost.getURI();

                Assert.assertNotNull(actualUri);
                Assert.assertEquals("http://localhost:8080/trackrepo/tracks/" + id + "/encode.json?isHighRate=false&licensed=true", actualUri.toString());

                Map<String, String> actualHeaderMap = new HashMap<String, String>();

                for (Header actualHeader : actualHeaders) {
                    actualHeaderMap.put(actualHeader.getName(), actualHeader.getValue());
                }

                Header[] expectedHeaders = getSecuredHeaders();
                for (Header expectedHeader : expectedHeaders) {
                    String expectedHeaderName = expectedHeader.getName();
                    String actualHeaderValue = actualHeaderMap.get(expectedHeaderName);
                    Assert.assertEquals(actualHeaderValue, expectedHeader.getValue());
                }

                HttpResponse httpResponse = new BasicHttpResponse(new BasicStatusLine(HTTP_1_1, HttpURLConnection.HTTP_INTERNAL_ERROR, "OK")) {
                    @Override
                    public HttpEntity getEntity() {
                        return httpEntity;
                    }
                };
                return httpResponse;
            }
        });

        TrackDto actualTrackDto = client.encodeTrack(id, false, true);
        assertNull(actualTrackDto);
    }

    private HttpEntity getHttpEntity(final DateFormat dateInstance, ResourceFileDto resourceFileDto1, ResourceFileDto resourceFileDto2, TrackDto expectedTrackDto) throws UnsupportedEncodingException {
        final Date publishDate = expectedTrackDto.getPublishDate();
        final String publishDateString = publishDate == null ? "null" : dateInstance.format(publishDate);

        final HttpEntity httpEntity = new StringEntity(
            "{\"id\":" + expectedTrackDto.getId() + ",\"ingestor\":" + expectedTrackDto.getIngestor() + ",\"isrc\":\"" + expectedTrackDto.getIsrc() + "\",\"title\":\"" + expectedTrackDto.getTitle() +
            "\",\"artist\":\"" + expectedTrackDto.getArtist() + "\",\"ingestionDate\":\"" + dateInstance.format(expectedTrackDto.getIngestionDate()) + "\",\"status\":\"" +
            expectedTrackDto.getStatus() + "\",\"subTitle\":" + expectedTrackDto.getSubTitle() + ",\"productId\":" + expectedTrackDto.getProductId() + ",\"productCode\":" +
            expectedTrackDto.getProductCode() + ",\"genre\":\"" + expectedTrackDto.getGenre() + "\",\"copyright\":\"" + expectedTrackDto.getCopyright() + "\",\"year\":" + expectedTrackDto.getYear() +
            ",\"album\":\"" + expectedTrackDto.getAlbum() + "\",\"info\":" + expectedTrackDto.getInfo() + ",\"licensed\":" + expectedTrackDto.getLicensed() + ",\"ingestionUpdateDate\":\"" +
            dateInstance.format(expectedTrackDto.getIngestionUpdateDate()) + "\",\"publishDate\":" + publishDateString + ",\"files\":[{\"type\":\"" + resourceFileDto1.getType() +
            "\",\"filename\":\"" + resourceFileDto1.getFilename() + "\",\"resolution\":\"" + resourceFileDto1.getResolution() + "\"},{\"type\":\"" + resourceFileDto2.getType() + "\",\"filename\":\"" +
            resourceFileDto2.getFilename() + "\",\"resolution\":\"" + resourceFileDto2.getResolution() + "\"}]}");
        return httpEntity;
    }

    private TrackDto getTrackDto(final DateFormat dateInstance, List<ResourceFileDto> resourceFileDtos, final long id) throws ParseException {
        String ingestor = null;
        String isrc = "123456789";
        String title = "2011";
        String artist = "Paul Simon";
        final String ingestionDateString = "2011-09-28";
        Date ingestionDate = dateInstance.parse(ingestionDateString);
        TrackStatus status = TrackStatus.ENCODED;
        String subTitle = null;
        String productId = null;
        String productCode = null;
        String genre = "Pop";
        String copyright = "(P) 2010 Paul Simon under exclusive license of Sony Music Entertainment";
        String year = null;
        String album = "Hearts And Bones";
        String info = null;
        Boolean licensed = true;
        String ingestionUpdateDateString = "2011-11-14";
        Date ingestionUpdateDate = dateInstance.parse(ingestionUpdateDateString);
        String publishDateString = null;
        Date publishDate = publishDateString == null ? null : dateInstance.parse(publishDateString);

        TrackDto expectedTrackDto = new TrackDto();
        expectedTrackDto.setId(id);
        expectedTrackDto.setIngestor(ingestor);
        expectedTrackDto.setIsrc(isrc);
        expectedTrackDto.setTitle(title);
        expectedTrackDto.setArtist(artist);
        expectedTrackDto.setIngestionDate(ingestionDate);
        expectedTrackDto.setStatus(status);
        expectedTrackDto.setSubTitle(subTitle);
        expectedTrackDto.setProductId(productId);
        expectedTrackDto.setProductCode(productCode);
        expectedTrackDto.setGenre(genre);
        expectedTrackDto.setCopyright(copyright);
        expectedTrackDto.setYear(year);
        expectedTrackDto.setAlbum(album);
        expectedTrackDto.setInfo(info);
        expectedTrackDto.setLicensed(licensed);
        expectedTrackDto.setIngestionUpdateDate(ingestionUpdateDate);
        expectedTrackDto.setPublishDate(publishDate);
        expectedTrackDto.setFiles(resourceFileDtos);
        return expectedTrackDto;
    }

    private ResourceFileDto getResourceFileDtoInstance() {
        final FileType fileType1 = FileType.DOWNLOAD;
        Resolution resolution1 = AudioResolution.RATE_ORIGINAL;
        String fileName1 = "/global/path/temp.aud";
        ResourceFileDto resourceFileDto1 = new ResourceFileDto(fileType1, resolution1, fileName1);
        return resourceFileDto1;
    }

    private Header[] getSecuredHeaders() {
        byte[] secToken = Base64.encode(clientUsername.concat(":").concat(clientPassword).getBytes());
        BasicHeader[] headers = {new BasicHeader("Authorization", "Basic ".concat(new String(secToken)))};
        return headers;
    }
}