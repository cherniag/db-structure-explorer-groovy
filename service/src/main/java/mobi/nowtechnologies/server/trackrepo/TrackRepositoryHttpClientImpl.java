package mobi.nowtechnologies.server.trackrepo;

import mobi.nowtechnologies.server.shared.dto.PageListDto;
import mobi.nowtechnologies.server.trackrepo.dto.DropDto;
import mobi.nowtechnologies.server.trackrepo.dto.IngestWizardDataDto;
import mobi.nowtechnologies.server.trackrepo.dto.SearchTrackDto;
import mobi.nowtechnologies.server.trackrepo.dto.TrackDto;
import mobi.nowtechnologies.server.trackrepo.dto.TrackReportingOptionsDto;
import mobi.nowtechnologies.server.trackrepo.enums.ReportingType;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.util.StringUtils.hasText;

// @author Alexander Kolpakov (akolpakov)
public class TrackRepositoryHttpClientImpl implements TrackRepositoryClient {

    protected static final String DATE_FORMAT = "yyyy-MM-dd";
    protected static final String DATE_TIME_FORMAT = "yyyy-MM-dd_HH:mm:ss";
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(TrackRepositoryHttpClientImpl.class);
    private static final Integer DEFAULT_NUM_QUERY_THREADS = 1;
    protected DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
    protected DateFormat dateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
    protected Gson gson = new GsonBuilder().setDateFormat(DATE_FORMAT).create();
    protected Gson gsonMillis;
    protected Integer numQuerySchedulerThreads = DEFAULT_NUM_QUERY_THREADS;
    protected ScheduledExecutorService queryScheduler;
    private String username = "admin";
    private String password = "admin";
    private String trackRepoUrl = "http://localhost:8080/trackrepo/";

    public void init() {
        queryScheduler = Executors.newScheduledThreadPool(numQuerySchedulerThreads);
        gsonMillis = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new Date(json.getAsJsonPrimitive().getAsLong());
            }
        }).create();
    }

    public void setNumQuerySchedulerThreads(Integer numQuerySchedulerThreads) {
        this.numQuerySchedulerThreads = numQuerySchedulerThreads;
    }

    /*
     * (non-Javadoc)
     *
     * @see mobi.nowtechnologies.server.client.trackrepo.TrackRepositoryClient#isLoggedIn ()
     */
    @Override
    public boolean isLoggedIn() {
        HttpGet signin = new HttpGet(trackRepoUrl.concat("signin"));
        signin.setHeaders(getSecuredHeaders());
        try {
            HttpResponse response = getHttpClient().execute(signin);
            if (200 == response.getStatusLine().getStatusCode()) {
                return true;
            }
        } catch (ClientProtocolException e) {
            LOGGER.error("Cannot login into track repository. {}", e);
        } catch (IOException e) {
            LOGGER.error("Communication exception while loginin into track repository. {}", e);
        }
        return false;
    }

    protected Header[] getSecuredHeaders() {
        byte[] secToken = Base64.encode(username.concat(":").concat(password).getBytes());
        BasicHeader[] headers = {new BasicHeader("Authorization", "Basic ".concat(new String(secToken)))};
        return headers;
    }

    /*
     * (non-Javadoc)
     *
     * @see mobi.nowtechnologies.server.client.trackrepo.TrackRepositoryClient#search (java.lang.String)
     */
    @Override
    public PageListDto<TrackDto> search(String criteria, Pageable page) {
        PageListDto<TrackDto> tracks = new PageListDto<TrackDto>(Collections.<TrackDto>emptyList(), 0, page.getPageNumber() + 1, page.getPageSize());
        try {
            if (hasText(criteria)) {
                List<NameValuePair> queryParams = buildPageParams(page);
                queryParams.add(new BasicNameValuePair("query", criteria));

                HttpGet signin = new HttpGet(trackRepoUrl.concat("tracks.json?").concat(buildHttpQuery(queryParams)));
                signin.setHeaders(getSecuredHeaders());
                HttpResponse response = getHttpClient().execute(signin);
                if (200 == response.getStatusLine().getStatusCode()) {
                    Type type = new TypeToken<PageListDto<TrackDto>>() {}.getType();
                    tracks = gson.fromJson(new InputStreamReader(response.getEntity().getContent()), type);
                }
            }
        } catch (ClientProtocolException e) {
            LOGGER.error("Cannot search in track repository. {}", e);
        } catch (IOException e) {
            LOGGER.error("Communication exception while searching for track in track repository. {}", e);
        }
        return tracks;
    }

    /*
    * (non-Javadoc)
    *
    * @see mobi.nowtechnologies.server.client.trackrepo.TrackRepositoryClient#getDrops (java.lang.String)
    */
    @Override
    public IngestWizardDataDto getDrops(String... ingestors) {
        IngestWizardDataDto data = new IngestWizardDataDto();
        data.setDrops(Collections.<DropDto>emptyList());
        try {

            String uri = trackRepoUrl.concat("drops.json");
            if (ingestors != null && ingestors.length > 0) {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                for (String ing : ingestors) {
                    params.add(new BasicNameValuePair("ingestors", ing));
                }
                uri += "?" + URLEncodedUtils.format(params, "utf-8");
            }

            HttpGet query = new HttpGet(uri);
            query.setHeaders(getSecuredHeaders());
            query.setHeader(new BasicHeader(HTTP.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
            HttpResponse response = getHttpClient().execute(query);
            if (200 == response.getStatusLine().getStatusCode()) {
                Type type = new TypeToken<IngestWizardDataDto>() {}.getType();
                data = gsonMillis.fromJson(new InputStreamReader(response.getEntity().getContent()), type);
            }
        } catch (ClientProtocolException e) {
            LOGGER.error("Cannot search drops. {}", e);
        } catch (IOException e) {
            LOGGER.error("Communication exception while searching drops. {}", e);
        }
        return data;
    }

    /*
    * (non-Javadoc)
    *
    * @see mobi.nowtechnologies.server.client.trackrepo.TrackRepositoryClient#selectDrops (java.lang.String)
    */
    @Override
    public IngestWizardDataDto selectDrops(IngestWizardDataDto data) {
        IngestWizardDataDto result = new IngestWizardDataDto();
        result.setDrops(Collections.<DropDto>emptyList());
        try {
            String jsonBody = gsonMillis.toJson(data);

            HttpPost query = new HttpPost(trackRepoUrl.concat("drops/select.json"));
            HttpEntity entity = new StringEntity(jsonBody);
            query.setEntity(entity);
            query.setHeaders(getSecuredHeaders());
            query.setHeader(new BasicHeader(HTTP.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
            HttpResponse response = getHttpClient().execute(query);
            if (200 == response.getStatusLine().getStatusCode()) {
                Type type = new TypeToken<IngestWizardDataDto>() {}.getType();
                result = gsonMillis.fromJson(new InputStreamReader(response.getEntity().getContent()), type);
            }
        } catch (ClientProtocolException e) {
            LOGGER.error("Cannot select drops. {}", e);
        } catch (IOException e) {
            LOGGER.error("Communication exception while selecting drops. {}", e);
        }
        return result;
    }

    /*
    * (non-Javadoc)
    *
    * @see mobi.nowtechnologies.server.client.trackrepo.TrackRepositoryClient#selectTrackDrops (java.lang.String)
    */
    @Override
    public IngestWizardDataDto selectTrackDrops(IngestWizardDataDto data) {
        IngestWizardDataDto result = new IngestWizardDataDto();
        result.setDrops(Collections.<DropDto>emptyList());
        try {
            String jsonBody = gsonMillis.toJson(data);

            HttpPost query = new HttpPost(trackRepoUrl.concat("drops/tracks/select.json"));
            HttpEntity entity = new StringEntity(jsonBody);
            query.setEntity(entity);
            query.setHeaders(getSecuredHeaders());
            query.setHeader(new BasicHeader(HTTP.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
            HttpResponse response = getHttpClient().execute(query);
            if (200 == response.getStatusLine().getStatusCode()) {
                Type type = new TypeToken<IngestWizardDataDto>() {}.getType();
                result = gsonMillis.fromJson(new InputStreamReader(response.getEntity().getContent()), type);
            }
        } catch (ClientProtocolException e) {
            LOGGER.error("Cannot select tracks of the drops. {}", e);
        } catch (IOException e) {
            LOGGER.error("Communication exception while selecting tracks of the drops. {}", e);
        }
        return result;
    }

    /*
    * (non-Javadoc)
    *
    * @see mobi.nowtechnologies.server.client.trackrepo.TrackRepositoryClient#selectTrackDrops (java.lang.String)
    */
    @Override
    public Boolean commitDrops(IngestWizardDataDto data) {
        Boolean result = false;
        try {
            String jsonBody = gsonMillis.toJson(data);

            HttpPost query = new HttpPost(trackRepoUrl.concat("drops/commit.json"));
            HttpEntity entity = new StringEntity(jsonBody);
            query.setEntity(entity);
            query.setHeaders(getSecuredHeaders());
            query.setHeader(new BasicHeader(HTTP.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
            HttpResponse response = getHttpClient().execute(query);
            if (200 == response.getStatusLine().getStatusCode()) {
                Type type = new TypeToken<Boolean>() {}.getType();
                result = gsonMillis.fromJson(new InputStreamReader(response.getEntity().getContent()), type);
            }
        } catch (ClientProtocolException e) {
            LOGGER.error("Cannot commit drops. {}", e);
        } catch (IOException e) {
            LOGGER.error("Communication exception while commiting drops. {}", e);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see mobi.nowtechnologies.server.client.trackrepo.TrackRepositoryClient#pullTrack (java.lang.String)
     */
    @Override
    public TrackDto pullTrack(final Long id) throws Exception {
        return sendPullRequest(id);
    }

    private TrackDto sendPullRequest(final Long id) throws Exception {
        LOGGER.info("Sending pull request to track repo, trackId:{}", id);
        if (id == null) {
            return null;
        }
        try {
            HttpGet pull = new HttpGet(trackRepoUrl.concat("/tracks/").concat(URLEncoder.encode(id.toString(), "utf-8")).concat("/pull.json"));
            pull.setHeaders(getSecuredHeaders());
            HttpResponse response = getHttpClient().execute(pull);
            if (response.getStatusLine().getStatusCode() != 200) {
                LOGGER.error("Track repo server responded on Pull request for track with ID {} with error status code: {}", id, response.getStatusLine().getStatusCode());
                //throw new RuntimeException("Server responded on Pull request for track with ID " + id + " with error status code: " + response.getStatusLine().getStatusCode());
                return null;
            }
            String resJson = IOUtils.toString(response.getEntity().getContent());
            LOGGER.info("Received json from track repo: {}", resJson);
            TrackDto trackDto = gson.fromJson(resJson, TrackDto.class);
            return trackDto;
        } catch (Exception e) {
            LOGGER.error("Error while sending Pull request for track with ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @seemobi.nowtechnologies.server.client.trackrepo.TrackRepositoryClient# encodeTrack(java.lang.String)
     */
    @Override
    public TrackDto encodeTrack(Long id, Boolean isHighRate, Boolean licensed) throws Exception {
        LOGGER.debug("input parameters: [{}, {}, {}]", id, isHighRate, licensed);
        if (id == null) {
            throw new NullPointerException("The parameter id is null");
        }

        final TrackDto trackDto;

        try {
            List<NameValuePair> queryParams = new LinkedList<NameValuePair>();
            if (isHighRate != null) {
                queryParams.add(new BasicNameValuePair("isHighRate", isHighRate.toString()));
            }
            if (licensed != null) {
                queryParams.add(new BasicNameValuePair("licensed", licensed.toString()));
            }

            HttpPost httpPost = new HttpPost(trackRepoUrl.concat("tracks/").concat(id.toString()).concat("/encode.json?").concat(buildHttpQuery(queryParams)));
            httpPost.setHeaders(getSecuredHeaders());
            HttpResponse httpResponse = getHttpClient().execute(httpPost);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == HttpURLConnection.HTTP_OK || statusCode == HttpURLConnection.HTTP_CREATED) {
                trackDto = gson.fromJson(new InputStreamReader(httpResponse.getEntity().getContent()), TrackDto.class);
            } else if (statusCode == HttpURLConnection.HTTP_NO_CONTENT) {
                trackDto = null;
            } else {
                throw new Exception("Wrong status code [" + statusCode + "] of response: [" + httpResponse + "]");
            }
        } catch (Exception e) {
            LOGGER.error("encodeTrack exception: {}", e.getMessage(), e);
            throw e;
        }

        LOGGER.debug("Output parameter trackDto=[{}]", trackDto);
        return trackDto;
    }

    public HttpClient getHttpClient() {
        return HttpClientFactory.getHttpClient();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setBaseUrl(String baseUrl) {
        this.trackRepoUrl = baseUrl;
    }

    @Override
    public PageListDto<TrackDto> search(SearchTrackDto criteria, Pageable page) {
        PageListDto<TrackDto> tracks = new PageListDto<TrackDto>(Collections.<TrackDto>emptyList(), 0, page.getPageNumber() + 1, page.getPageSize());
        try {
            if (criteria != null) {
                List<NameValuePair> queryParams = new LinkedList<NameValuePair>();
                addQParam(criteria.getArtist(), "artist", queryParams);
                addQParam(criteria.getTitle(), "title", queryParams);
                addQParam(criteria.getIsrc(), "isrc", queryParams);
                addDateQParam(criteria.getIngestTo(), "ingestTo", queryParams);
                addDateQParam(criteria.getIngestFrom(), "ingestFrom", queryParams);
                addDateQParam(criteria.getReleaseTo(), "releaseTo", queryParams);
                addDateQParam(criteria.getReleaseFrom(), "releaseFrom", queryParams);
                addQParam(criteria.getLabel(), "label", queryParams);
                addQParam(criteria.getIngestor(), "ingestor", queryParams);
                addQParam(criteria.getAlbum(), "album", queryParams);
                addQParam(criteria.getGenre(), "genre", queryParams);
                addQParam(criteria.getTerritory(), "territory", queryParams);
                ReportingType reportingType = criteria.getReportingType();
                if (isNotNull(reportingType)) {
                    addQParam(reportingType.name(), "reportingType", queryParams);
                }
                if (!CollectionUtils.isEmpty(criteria.getTrackIds())) {
                    addQParam(criteria.getTrackIds().get(0).toString(), "trackIds[0]", queryParams);
                }

                if (queryParams.size() > 0) {
                    addQParam(String.valueOf(criteria.isWithTerritories()), "withTerritories", queryParams);
                    addQParam(String.valueOf(criteria.isWithFiles()), "withFiles", queryParams);

                    if (!("".equals(criteria.getMediaType()))) {
                        addQParam(criteria.getMediaType(), "mediaType", queryParams);
                    }

                    buildPageParams(page, queryParams);

                    String url = trackRepoUrl.concat("tracks.json?").concat(buildHttpQuery(queryParams));
                    HttpGet signin = new HttpGet(url);
                    signin.setHeaders(getSecuredHeaders());
                    HttpResponse response = getHttpClient().execute(signin);
                    if (200 == response.getStatusLine().getStatusCode()) {
                        Gson gson = new GsonBuilder().setDateFormat(DATE_FORMAT).create();
                        Type type = new TypeToken<PageListDto<TrackDto>>() {}.getType();
                        tracks = gson.fromJson(new InputStreamReader(response.getEntity().getContent()), type);
                        tracks.setPage(page.getPageNumber() + 1);
                    }
                }
            }
        } catch (ClientProtocolException e) {
            LOGGER.error("Cannot search in track repository. {}", e);
        } catch (IOException e) {
            LOGGER.error("Communication exception while searching for track in track repository. {}", e);
        }
        return tracks;
    }

    @Override
    public ResponseEntity<String> assignReportingOptions(TrackReportingOptionsDto trackReportingOptionsDto) {
        HttpHeaders httpHeaders = new HttpHeaders();

        Header[] securedHeaders = getSecuredHeaders();
        for (Header securedHeader : securedHeaders) {
            httpHeaders.add(securedHeader.getName(), securedHeader.getValue());
        }

        org.springframework.http.HttpEntity<TrackReportingOptionsDto> requestEntity = new org.springframework.http.HttpEntity<TrackReportingOptionsDto>(trackReportingOptionsDto, httpHeaders);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.exchange(trackRepoUrl + "/reportingOptions", PUT, requestEntity, String.class);

        if (!responseEntity.getStatusCode().equals(OK)) {
            LOGGER.error("Some unexpected error occurred during track reporting option [{}] assigning. {}", trackReportingOptionsDto, responseEntity.getStatusCode().getReasonPhrase());
            throw new RuntimeException("Can't assign track reporting options [" + trackReportingOptionsDto + "]");
        }

        return responseEntity;
    }

    private void addDateQParam(Date param, String key, List<NameValuePair> queryParams) {
        if (param != null) {
            queryParams.add(new BasicNameValuePair(key, dateFormat.format(param)));
        }
    }

    private void addQParam(String param, String key, List<NameValuePair> queryParams) {
        if (hasText(param)) {
            queryParams.add(new BasicNameValuePair(key, param.trim()));
        }
    }

    protected List<NameValuePair> buildPageParams(Pageable page, List<NameValuePair>... paramsArr) {
        List<NameValuePair> params = paramsArr.length == 0 ? new LinkedList<NameValuePair>() : paramsArr[0];

        params.add(new BasicNameValuePair("page.size", String.valueOf(page.getPageSize())));
        params.add(new BasicNameValuePair("page.page", String.valueOf(page.getPageNumber() + 1)));

        return params;
    }

    protected String buildHttpQuery(List<NameValuePair> pairs) throws UnsupportedEncodingException {
        StringBuilder query = new StringBuilder();

        for (NameValuePair pair : pairs) {
            if (query.length() > 0) {
                query.append("&");
            }
            query.append(pair.getName()).append("=").append(URLEncoder.encode(pair.getValue(), "utf-8"));
        }

        return query.toString();
    }

    public String getTrackRepoUrl() {
        return trackRepoUrl;
    }

    public void setTrackRepoUrl(String trackRepoUrl) {
        this.trackRepoUrl = trackRepoUrl;
    }

    static class HttpClientFactory {

        private static final int DEFAULT_SOCKET_TIMEOUT = 0;

        public static HttpClient getHttpClient() {
            SchemeRegistry schreg = new SchemeRegistry();
            schreg.register(new Scheme("http", 8080, PlainSocketFactory.getSocketFactory()));
            schreg.register(new Scheme("https", 443, PlainSocketFactory.getSocketFactory()));
            ClientConnectionManager conman = new ThreadSafeClientConnManager(schreg);
            HttpClient client = new DefaultHttpClient(conman);
            HttpParams httpParams = client.getParams();
            HttpConnectionParams.setSoTimeout(httpParams, DEFAULT_SOCKET_TIMEOUT);
            return client;
        }
    }
}
