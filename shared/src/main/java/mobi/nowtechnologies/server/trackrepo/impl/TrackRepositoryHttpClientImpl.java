/**
 * @author Alexander Kolpakov (akolpakov)
 */
package mobi.nowtechnologies.server.trackrepo.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import mobi.nowtechnologies.server.shared.dto.PageListDto;
import mobi.nowtechnologies.server.trackrepo.TrackRepositoryClient;
import mobi.nowtechnologies.server.trackrepo.dto.SearchTrackDto;
import mobi.nowtechnologies.server.trackrepo.dto.TrackDto;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
			if (StringUtils.hasText(criteria)) {
				List<NameValuePair> queryParams = buildPageParams(page);
				queryParams.add(new BasicNameValuePair("query", criteria));
				
				HttpGet signin = new HttpGet(trackRepoUrl.concat("tracks.json?").concat(buildHttpQuery(queryParams)));
				signin.setHeaders(getSecuredHeaders());
				HttpResponse response = getHttpClient().execute(signin);
				if (200 == response.getStatusLine().getStatusCode()) {
					Gson gson = new GsonBuilder().setDateFormat(DATE_FORMAT).create();
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
	 * @see mobi.nowtechnologies.server.client.trackrepo.TrackRepositoryClient#pullTrack (java.lang.String)
	 */
	@Override
	public TrackDto pullTrack(Long id) throws Exception{
		TrackDto trackDto = null;
		try {
			if (id != null) {
				HttpGet pull = new HttpGet(trackRepoUrl.concat("/tracks/").concat(URLEncoder.encode(id.toString(), "utf-8")).concat("/pull.json"));
				pull.setHeaders(getSecuredHeaders());
				HttpResponse response = getHttpClient().execute(pull);
				if (200 == response.getStatusLine().getStatusCode()) {
					Gson gson = new GsonBuilder().setDateFormat(DATE_FORMAT).create();
					trackDto = gson.fromJson(new InputStreamReader(response.getEntity().getContent()), TrackDto.class);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Cannot pull the track by id {} from repository. {}", id, e);
			throw e;
		} 
		return trackDto;
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
				Gson gson = new GsonBuilder().setDateFormat(DATE_FORMAT).create();
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
				List<NameValuePair> queryParams = buildPageParams(page);
				if (StringUtils.hasText(criteria.getArtist()))
					queryParams.add(new BasicNameValuePair("artist", criteria.getArtist()));
				if (StringUtils.hasText(criteria.getTitle()))
					queryParams.add(new BasicNameValuePair("title", criteria.getTitle()));
				if (StringUtils.hasText(criteria.getIsrc()))
					queryParams.add(new BasicNameValuePair("isrc", criteria.getIsrc()));
				if (criteria.getIngestTo() != null)
					queryParams.add(new BasicNameValuePair("ingestTo", dateFormat.format(criteria.getIngestTo())));
				if (criteria.getIngestFrom() != null)
					queryParams.add(new BasicNameValuePair("ingestFrom", dateFormat.format(criteria.getIngestFrom())));
				if (criteria.getReleaseTo() != null)
					queryParams.add(new BasicNameValuePair("releaseTo", dateFormat.format(criteria.getReleaseTo())));
				if (criteria.getReleaseFrom() != null)
					queryParams.add(new BasicNameValuePair("releaseFrom", dateFormat.format(criteria.getReleaseFrom())));
				if (StringUtils.hasText(criteria.getLabel()))
					queryParams.add(new BasicNameValuePair("label", criteria.getLabel()));
				if (StringUtils.hasText(criteria.getIngestor()))
					queryParams.add(new BasicNameValuePair("ingestor", criteria.getIngestor()));

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
		} catch (ClientProtocolException e) {
			LOGGER.error("Cannot search in track repository. {}", e);
		} catch (IOException e) {
			LOGGER.error("Communication exception while searching for track in track repository. {}", e);
		} 
		return tracks;
	}
	
	protected List<NameValuePair> buildPageParams(Pageable page){
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		
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
