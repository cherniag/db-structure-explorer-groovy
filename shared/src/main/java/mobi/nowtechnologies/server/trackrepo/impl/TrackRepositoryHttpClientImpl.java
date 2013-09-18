/**
 * @author Alexander Kolpakov (akolpakov)
 */
package mobi.nowtechnologies.server.trackrepo.impl;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import mobi.nowtechnologies.server.shared.dto.PageListDto;
import mobi.nowtechnologies.server.trackrepo.TrackRepositoryClient;
import mobi.nowtechnologies.server.trackrepo.dto.DropDto;
import mobi.nowtechnologies.server.trackrepo.dto.IngestWizardDataDto;
import mobi.nowtechnologies.server.trackrepo.dto.SearchTrackDto;
import mobi.nowtechnologies.server.trackrepo.dto.TrackDto;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.springframework.util.StringUtils.hasText;

/**
 * @author Titov Mykhaylo (titov)
 * @author Mayboroda Dmytro
 * 
 */
public class TrackRepositoryHttpClientImpl implements TrackRepositoryClient {
	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(TrackRepositoryHttpClientImpl.class);
	private String username = "admin";
	private String password = "admin";
	private String trackRepoUrl = "http://localhost:8080/trackrepo/";
	
	protected static final String DATE_FORMAT = "yyyy-MM-dd";
	protected static final String DATE_TIME_FORMAT = "yyyy-MM-dd_HH:mm:ss";
	
	protected DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
	protected DateFormat dateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
    protected Gson gson = new GsonBuilder().setDateFormat(DATE_FORMAT).create();
    protected Gson gsonMillis;
    protected Integer numQuerySchedulerThreads ;
    protected ScheduledExecutorService queryScheduler;

    public void init(){
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

    protected abstract class QueryTask<T> implements Runnable {
        private T data;
        private Throwable failure;
        private ScheduledFuture<?> future;
        private final Lock lock = new ReentrantLock();
        private final Condition isProcessing  = lock.newCondition();

        public void setData(T data) {
            this.data = data;
        }

        public T getData() {
            return data;
        }

        public Throwable getFailure() {
            return failure;
        }

        public void setFailure(Throwable failure) {
            this.failure = failure;
        }

        public void start(ScheduledFuture<?> future) throws InterruptedException {
            lock.lock();

            this.future = future;
            isProcessing.await();

            lock.unlock();
        }

        public void stop(){
            lock.lock();

            isProcessing.signal();
            this.future.cancel(false);

            lock.unlock();
        }
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
			if (200 == response.getStatusLine().getStatusCode())
				return true;
		} catch (ClientProtocolException e) {
			LOGGER.error("Cannot login into track repository. {}", e);
		} catch (IOException e) {
			LOGGER.error("Communication exception while loginin into track repository. {}", e);
		}
		return false;
	}

	/**
	 * @return
	 */
	protected Header[] getSecuredHeaders() {
		byte[] secToken = Base64.encode(username.concat(":").concat(password).getBytes());
		BasicHeader[] headers = { new BasicHeader("Authorization", "Basic ".concat(new String(secToken))) };
		return headers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mobi.nowtechnologies.server.client.trackrepo.TrackRepositoryClient#search (java.lang.String)
	 */
	@Override
	public PageListDto<TrackDto> search(String criteria, Pageable page) {
		PageListDto<TrackDto> tracks = new PageListDto<TrackDto>(Collections.<TrackDto>emptyList(), 0, page.getPageNumber()+1, page.getPageSize());
		try {
			if (hasText(criteria)) {
				List<NameValuePair> queryParams = buildPageParams(page);
				queryParams.add(new BasicNameValuePair("query", criteria));
				
				HttpGet signin = new HttpGet(trackRepoUrl.concat("tracks.json?").concat(buildHttpQuery(queryParams)));
				signin.setHeaders(getSecuredHeaders());
				HttpResponse response = getHttpClient().execute(signin);
				if (200 == response.getStatusLine().getStatusCode()) {
					Type type = new TypeToken<PageListDto<TrackDto>>() {
					}.getType();
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
    public IngestWizardDataDto getDrops() {
        IngestWizardDataDto data = new IngestWizardDataDto();
        data.setDrops(Collections.<DropDto>emptyList());
        try {
                HttpGet query = new HttpGet(trackRepoUrl.concat("drops.json"));
                query.setHeaders(getSecuredHeaders());
                query.setHeader(new BasicHeader(HTTP.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
                HttpResponse response = getHttpClient().execute(query);
                if (200 == response.getStatusLine().getStatusCode()) {
                    Type type = new TypeToken<IngestWizardDataDto>() {
                    }.getType();
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
                Type type = new TypeToken<IngestWizardDataDto>() {
                }.getType();
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
                Type type = new TypeToken<IngestWizardDataDto>() {
                }.getType();
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
                Type type = new TypeToken<Boolean>() {
                }.getType();
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
    public TrackDto pullTrack(final Long id) throws Exception{
        final QueryTask<TrackDto> pullTask = new QueryTask<TrackDto>(){
            @Override
            synchronized public void run() {
                TrackDto trackDto = null;
                try {
                    if (id != null) {
                        HttpGet pull = new HttpGet(trackRepoUrl.concat("/tracks/").concat(URLEncoder.encode(id.toString(), "utf-8")).concat("/pull.json"));
                        pull.setHeaders(getSecuredHeaders());
                        HttpResponse response = getHttpClient().execute(pull);
                        if (200 == response.getStatusLine().getStatusCode()) {
                            trackDto = gson.fromJson(new InputStreamReader(response.getEntity().getContent()), TrackDto.class);
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("Cannot pull the track by id {} from repository. {}", id, e);
                    this.setFailure(e);
                }

                this.setData(trackDto);

                if(this.getFailure() != null || this.getData() == null || this.getData().getPublishDate() != null){
                    this.stop();
                }
            }
        };

        ScheduledFuture<?> future = queryScheduler.scheduleWithFixedDelay(
                pullTask, 0, 1, TimeUnit.SECONDS
        );
        pullTask.start(future);

        if(pullTask.getFailure() != null){
            throw (Exception) pullTask.getFailure();
        }

        return pullTask.getData();
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @seemobi.nowtechnologies.server.client.trackrepo.TrackRepositoryClient# encodeTrack(java.lang.String)
	 */
	@Override
	public TrackDto encodeTrack(Long id, Boolean isHighRate, Boolean licensed) throws Exception {
		if (id == null)
			throw new NullPointerException("The parameter id is null");
		LOGGER.debug("input parameters id: [{}]", id);

		final TrackDto trackDto;

		try {
			List<NameValuePair> queryParams = new LinkedList<NameValuePair>();
			if (isHighRate != null)
				queryParams.add(new BasicNameValuePair("isHighRate", isHighRate.toString()));
			if (licensed != null)
				queryParams.add(new BasicNameValuePair("licensed", licensed.toString()));
			
			HttpPost httpPost = new HttpPost(trackRepoUrl.concat("tracks/").concat(id.toString()).concat("/encode.json?").concat(buildHttpQuery(queryParams)));
			httpPost.setHeaders(getSecuredHeaders());
			HttpResponse httpResponse = getHttpClient().execute(httpPost);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
				trackDto = gson.fromJson(new InputStreamReader(httpResponse.getEntity().getContent()), TrackDto.class);
			} else if (statusCode == HttpStatus.SC_NO_CONTENT) {
				trackDto = null;
			} else
				throw new Exception("Wrong status code [" + statusCode + "] of response: [" + httpResponse + "]");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw e;
		}

		LOGGER.debug("Output parameter trackDto=[{}]", trackDto);
		return trackDto;
	}

	/**
	 * @return
	 */
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

	static class HttpClientFactory {
		public static HttpClient getHttpClient() {
			SchemeRegistry schreg = new SchemeRegistry();
			schreg.register(new Scheme("http", 8080, PlainSocketFactory.getSocketFactory()));
			schreg.register(new Scheme("https", 443, PlainSocketFactory.getSocketFactory()));
			ClientConnectionManager conman = new ThreadSafeClientConnManager(schreg);
			HttpClient client = new DefaultHttpClient(conman);
			return client;
		}
	}

	@Override
	public PageListDto<TrackDto> search(SearchTrackDto criteria, Pageable page) {
		PageListDto<TrackDto> tracks = new PageListDto<TrackDto>(Collections.<TrackDto>emptyList(), 0, page.getPageNumber()+1, page.getPageSize());
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
                if(criteria.getTrackIds() != null && !criteria.getTrackIds().isEmpty()) {
                    addQParam(criteria.getTrackIds().get(0).toString(), "trackIds[0]", queryParams);
                }

                if(queryParams.size() > 0){
                    addQParam(String.valueOf(criteria.isWithTerritories()), "withTerritories", queryParams);
                    addQParam(String.valueOf(criteria.isWithFiles()), "withFiles", queryParams);
                    buildPageParams(page, queryParams);

                    String url = trackRepoUrl.concat("tracks.json?").concat(buildHttpQuery(queryParams));
                    HttpGet signin = new HttpGet(url);
                    signin.setHeaders(getSecuredHeaders());
                    HttpResponse response = getHttpClient().execute(signin);
                    if (200 == response.getStatusLine().getStatusCode()) {
                        Gson gson = new GsonBuilder().setDateFormat(DATE_FORMAT).create();
                        Type type = new TypeToken<PageListDto<TrackDto>>() {
                        }.getType();
                        tracks = gson.fromJson(new InputStreamReader(response.getEntity().getContent()), type);
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

    private void addDateQParam(Date param, String key, List<NameValuePair> queryParams) {
        if (param != null)
            queryParams.add(new BasicNameValuePair(key, dateFormat.format(param)));
    }

    private void addQParam(String param, String key, List<NameValuePair> queryParams) {
        if (hasText(param))
        	queryParams.add(new BasicNameValuePair(key, param));
    }

    protected List<NameValuePair> buildPageParams(Pageable page, List<NameValuePair>... paramsArr){
        List<NameValuePair> params = paramsArr.length == 0 ? new LinkedList<NameValuePair>() : paramsArr[0];
		
		params.add(new BasicNameValuePair("page.size", String.valueOf(page.getPageSize())));
		params.add(new BasicNameValuePair("page.page", String.valueOf(page.getPageNumber()+1)));
		
		return params;
	}
	
	protected String buildHttpQuery(List<NameValuePair> pairs) throws UnsupportedEncodingException
	{
		StringBuilder query = new StringBuilder();
		
		for (NameValuePair pair : pairs) {
			if(query.length() > 0)
				query.append("&");
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
}
