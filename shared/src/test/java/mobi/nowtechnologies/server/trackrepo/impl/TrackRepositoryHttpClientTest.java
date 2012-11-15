/**
 * 
 */
package mobi.nowtechnologies.server.trackrepo.impl;

import mobi.nowtechnologies.server.shared.dto.PageListDto;
import mobi.nowtechnologies.server.trackrepo.Resolution;
import mobi.nowtechnologies.server.trackrepo.dto.ResourceFileDto;
import mobi.nowtechnologies.server.trackrepo.dto.TrackDto;
import mobi.nowtechnologies.server.trackrepo.enums.AudioResolution;
import mobi.nowtechnologies.server.trackrepo.enums.FileType;
import mobi.nowtechnologies.server.trackrepo.enums.TrackStatus;
import mobi.nowtechnologies.server.trackrepo.impl.TrackRepositoryHttpClientImpl.HttpClientFactory;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.bouncycastle.util.encoders.Base64;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Titov Mykhaylo (titov)
 * @author Mayboroda Dmytro
 * 
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
	}

	@Test
	public void isLoggedIn_Successful() throws ClientProtocolException, IOException {

		HttpClient httpClient = mock(HttpClient.class);
		HttpResponse successfulLoginResponse = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 200, "OK"));
		when(httpClient.execute(any(HttpUriRequest.class))).thenReturn(successfulLoginResponse);
		TrackRepositoryHttpClientImpl spy = spy(client);
		when(spy.getHttpClient()).thenReturn(httpClient);

		boolean loggedIn = spy.isLoggedIn();
		assertEquals(true, loggedIn);
	}

	@Test
	public void isLoggedIn_Fail() throws ClientProtocolException, IOException {
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
	public void search_Successful() throws ClientProtocolException, IOException {

		HttpClient httpClient = mock(HttpClient.class);
		HttpResponse successfulSearchResponse = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 200, "OK")) {
			@Override
			public HttpEntity getEntity() {
				try {
					return new StringEntity(
							"{\"total\":10,\"page\":1,\"size\":10,\"list\":[{\"id\":383,\"ingestor\":null,\"isrc\":\"123456789\",\"title\":\"2011\",\"artist\":\"Paul Simon\",\"ingestionDate\":\"2011-09-28\",\"status\":\"NONE\",\"subTitle\":null,\"productId\":null,\"productCode\":null,\"genre\":\"Pop\",\"copyright\":\"(P) 2010 Paul Simon under exclusive license of Sony Music Entertainment\",\"year\":null,\"album\":\"Hearts And Bones\",\"info\":null,\"licensed\":true,\"ingestionUpdateDate\":\"2011-11-14\",\"publishDate\":null,\"files\":null}]}");
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
	public void search_By() throws ClientProtocolException, IOException {

		HttpClient httpClient = mock(HttpClient.class);
		HttpResponse successfulSearchResponse = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 200, "OK")) {
			@Override
			public HttpEntity getEntity() {
				try {
					return new StringEntity(
							"{\"total\":10,\"page\":1,\"size\":10,\"list\":[{\"id\":383,\"ingestor\":null,\"isrc\":\"123456789\",\"title\":\"2011\",\"artist\":\"Paul Simon\",\"ingestionDate\":\"2011-09-28\",\"status\":\"NONE\",\"subTitle\":null,\"productId\":null,\"productCode\":null,\"genre\":\"Pop\",\"copyright\":\"(P) 2010 Paul Simon under exclusive license of Sony Music Entertainment\",\"year\":null,\"album\":\"Hearts And Bones\",\"info\":null,\"licensed\":true,\"ingestionUpdateDate\":\"2011-11-14\",\"publishDate\":null,\"files\":null}]}");
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
	public void search_Fail() throws ClientProtocolException, IOException {
		HttpClient httpClient = getFailedHttpClient();
		TrackRepositoryHttpClientImpl spy = spy(client);
		when(spy.getHttpClient()).thenReturn(httpClient);

		Pageable page = new PageRequest(0, 30);
		PageListDto<TrackDto> searchResult = spy.search("red music", page);

		assertEquals(0, searchResult.getList().size());
		verify(spy, times(1)).getSecuredHeaders();
	}

	/**
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	protected HttpClient getFailedHttpClient() throws IOException, ClientProtocolException {
		HttpClient httpClient = mock(HttpClient.class);
		HttpResponse failedResponse = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 500, "OK"));
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

	/**
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	protected HttpClient getMockedHttpClientWithOneTrack() throws IOException, ClientProtocolException {
		HttpClient httpClient = mock(HttpClient.class);
		HttpResponse successfulPullResponse = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 200, "OK")) {
			@Override
			public HttpEntity getEntity() {
				try {
					return new StringEntity(
							"{\"id\":383,\"ingestor\":null,\"isrc\":\"123456789\",\"title\":\"2011\",\"artist\":\"Paul Simon\",\"ingestionDate\":\"2011-09-28\",\"status\":\"ENCODED\",\"subTitle\":null,\"productId\":null,\"productCode\":null,\"genre\":\"Pop\",\"copyright\":\"(P) 2010 Paul Simon under exclusive license of Sony Music Entertainment\",\"year\":null,\"album\":\"Hearts And Bones\",\"info\":null,\"licensed\":true,\"ingestionUpdateDate\":\"2011-11-14\",\"publishDate\":null,\"files\":[{\"type\":\"DOWNLOAD\",\"filename\":\"/global/path/temp.aud\",\"resolution\":\"RATE_ORIGINAL\"},{\"type\":\"\",\"filename\":\"/global/path/temp.aud\",\"resolution\":\"RATE_ORIGINAL\"}]}");
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
		HttpResponse successfulPullResponse = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 200, "OK"));
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

				HttpResponse httpResponse = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK")) {
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

				HttpResponse httpResponse = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_CREATED, "OK")) {
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

				HttpResponse httpResponse = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_NO_CONTENT, "OK")) {
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

				HttpResponse httpResponse = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK")) {
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

				HttpResponse httpResponse = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_INTERNAL_SERVER_ERROR, "OK")) {
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

	private HttpEntity getHttpEntity(final DateFormat dateInstance, ResourceFileDto resourceFileDto1, ResourceFileDto resourceFileDto2,
			TrackDto expectedTrackDto) throws UnsupportedEncodingException {
		final Date publishDate = expectedTrackDto.getPublishDate();
		final String publishDateString = publishDate == null ? "null" : dateInstance.format(publishDate);

		final HttpEntity httpEntity = new StringEntity("{\"id\":" + expectedTrackDto.getId() + ",\"ingestor\":" + expectedTrackDto.getIngestor()
				+ ",\"isrc\":\"" + expectedTrackDto.getIsrc() + "\",\"title\":\"" + expectedTrackDto.getTitle() + "\",\"artist\":\""
				+ expectedTrackDto.getArtist() + "\",\"ingestionDate\":\"" + dateInstance.format(expectedTrackDto.getIngestionDate())
				+ "\",\"status\":\"" + expectedTrackDto.getStatus() + "\",\"subTitle\":" + expectedTrackDto.getSubTitle() + ",\"productId\":"
				+ expectedTrackDto.getProductId() + ",\"productCode\":" + expectedTrackDto.getProductCode() + ",\"genre\":\"" + expectedTrackDto.getGenre()
				+ "\",\"copyright\":\"" + expectedTrackDto.getCopyright() + "\",\"year\":" + expectedTrackDto.getYear() + ",\"album\":\""
				+ expectedTrackDto.getAlbum() + "\",\"info\":" + expectedTrackDto.getInfo() + ",\"licensed\":" + expectedTrackDto.getLicensed()
				+ ",\"ingestionUpdateDate\":\"" + dateInstance.format(expectedTrackDto.getIngestionUpdateDate()) + "\",\"publishDate\":" + publishDateString
				+ ",\"files\":[{\"type\":\"" + resourceFileDto1.getType() + "\",\"filename\":\"" + resourceFileDto1.getFilename() + "\",\"resolution\":\""
				+ resourceFileDto1.getResolution() + "\"},{\"type\":\"" + resourceFileDto2.getType() + "\",\"filename\":\"" + resourceFileDto2.getFilename()
				+ "\",\"resolution\":\"" + resourceFileDto2.getResolution() + "\"}]}");
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
		BasicHeader[] headers = { new BasicHeader("Authorization", "Basic ".concat(new String(secToken))) };
		return headers;
	}
}