package mobi.nowtechnologies.server.trackrepo;

import mobi.nowtechnologies.server.trackrepo.dto.TrackReportingOptionsDto;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@RunWith(PowerMockRunner.class)
@PrepareForTest(TrackRepositoryHttpClientImpl.class)
public class TrackRepositoryHttpClientImplTest {

    public static final String HTTP_LOCALHOST_8080_TRACK_REPO = "http://localhost:8080/trackrepo/";

    @Mock RestTemplate restTemplateMock;

    @InjectMocks TrackRepositoryHttpClientImpl trackRepositoryHttpClient;

    @Before
    public void setUp() {
        initMocks(this);
        trackRepositoryHttpClient.setTrackRepoUrl(HTTP_LOCALHOST_8080_TRACK_REPO);
        trackRepositoryHttpClient.setUsername("admin");
        trackRepositoryHttpClient.setPassword("admin");
    }

    @Test
    public void shouldAssignReportingOptions() throws Exception {
        //given
        TrackReportingOptionsDto trackReportingOptionsDto = new TrackReportingOptionsDto();

        whenNew(RestTemplate.class).withNoArguments().thenReturn(restTemplateMock);

        ResponseEntity<String> responseEntityMock = new ResponseEntity<String>(OK);

        ArgumentCaptor<HttpEntity> httpEntityArgumentCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        when(restTemplateMock.exchange(eq(HTTP_LOCALHOST_8080_TRACK_REPO + "/reportingOptions"), eq(PUT), httpEntityArgumentCaptor.capture(), eq(String.class))).thenReturn(responseEntityMock);

        //when
        ResponseEntity<String> responseEntity = trackRepositoryHttpClient.assignReportingOptions(trackReportingOptionsDto);

        //then
        assertThat(responseEntity, is(responseEntityMock));

        HttpEntity httpEntity = httpEntityArgumentCaptor.getValue();
        assertThat(httpEntity.getBody(), Is.<Object>is(trackReportingOptionsDto));

        assertThat(httpEntity, is(notNullValue()));
        HttpHeaders headers = httpEntity.getHeaders();

        assertThat(headers, is(notNullValue()));
        assertThat(headers.size(), is(1));

        List<String> authorization = headers.get("Authorization");
        assertThat(authorization.size(), is(1));
        assertThat(authorization.get(0), is("Basic YWRtaW46YWRtaW4="));

    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowRuntimeExceptionWhenCanNotAssignReportingOptions() throws Exception {
        //given
        TrackReportingOptionsDto trackReportingOptionsDto = new TrackReportingOptionsDto();

        whenNew(RestTemplate.class).withNoArguments().thenReturn(restTemplateMock);

        ResponseEntity<String> responseEntityMock = new ResponseEntity<String>(BAD_REQUEST);

        ArgumentCaptor<HttpEntity> httpEntityArgumentCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        when(restTemplateMock.exchange(eq(HTTP_LOCALHOST_8080_TRACK_REPO + "/reportingOptions"), eq(PUT), httpEntityArgumentCaptor.capture(), eq(String.class))).thenReturn(responseEntityMock);

        //when
        trackRepositoryHttpClient.assignReportingOptions(trackReportingOptionsDto);
    }
}