package mobi.nowtechnologies.server.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import mobi.nowtechnologies.server.service.exception.UserCredentialsException;
import mobi.nowtechnologies.server.shared.AppConstants;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import mobi.nowtechnologies.server.shared.service.PostService;
import mobi.nowtechnologies.server.shared.service.PostService.Response;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class FacebookService {
	
	private CommunityResourceBundleMessageSource messageSource;
	
	public void setMessageSource(CommunityResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	
	public static class UserCredentions {
		private String id;
		private String name = "";
		private String first_name = "";
		private String last_name = "";
		private String link = "";
		private String birthday = "";
		private String gender = "";
		private String email = "";
		private String timezone = "";
		private String locale = "";
		private String verified = "";
		private String updated_time = "";
		 
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getFirst_name() {
			return first_name;
		}
		public void setFirst_name(String first_name) {
			this.first_name = first_name;
		}
		public String getLast_name() {
			return last_name;
		}
		public void setLast_name(String last_name) {
			this.last_name = last_name;
		}
		public String getLink() {
			return link;
		}
		public void setLink(String link) {
			this.link = link;
		}
		public String getBirthday() {
			return birthday;
		}
		public void setBirthday(String birthday) {
			this.birthday = birthday;
		}
		public String getGender() {
			return gender;
		}
		public void setGender(String gender) {
			this.gender = gender;
		}
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public String getTimezone() {
			return timezone;
		}
		public void setTimezone(String timezone) {
			this.timezone = timezone;
		}
		public String getLocale() {
			return locale;
		}
		public void setLocale(String locale) {
			this.locale = locale;
		}
		public String getVerified() {
			return verified;
		}
		public void setVerified(String verified) {
			this.verified = verified;
		}
		public String getUpdated_time() {
			return updated_time;
		}
		public void setUpdated_time(String updated_time) {
			this.updated_time = updated_time;
		}
		@Override
		public String toString() {
			return "UserCredentions [id=" + id + ", name=" + name
					+ ", first_name=" + first_name + ", last_name=" + last_name
					+ ", link=" + link + ", birthday=" + birthday + ", gender="
					+ gender + ", email=" + email + ", timezone=" + timezone
					+ ", locale=" + locale + ", verified=" + verified
					+ ", updated_time=" + updated_time + "]";
		}
	}
	
	private String fbAppSecret;
	private String fbAppId;
	
	
	public void setFbAppSecret(String fbAppSecret) {
		this.fbAppSecret = fbAppSecret;
	}

	public void setFbAppId(String fbAppId) {
		this.fbAppId = fbAppId;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(FacebookService.class);
	
	private final String REQUEST_PARAM_TOKEN = "access_token";
	private final String REQUEST_PARAM_SCOPE = "scope";
	
	private PostService postService;
	public void setPostService(PostService postService) {
		this.postService = postService;
	}
	
	public String getAppId() {
		return fbAppId;
	}
	
	public String getAppSecret() {
		return fbAppSecret;
	}
	
	public UserCredentions getUserCredentions(String communityName, String facebookToken) {
		LOGGER.debug("input parameters communityName, facebookToken: [{}], [{}]", communityName, facebookToken);
		String urlFacebookUserCredentions = AppConstants.FB_URL_USER_CREDENTIONS;
		
		Map<String, String> requestParams = new HashMap<String, String>();
		requestParams.put(REQUEST_PARAM_TOKEN, facebookToken);
		requestParams.put(REQUEST_PARAM_SCOPE, "email");
		
		LOGGER.info("facebook params [{}]", requestParams);
		
		Response response = postService.sendHttpGet(urlFacebookUserCredentions, requestParams);
		
		if (response.getStatusCode() != HttpStatus.SC_OK) {
			throw new UserCredentialsException(messageSource.getMessage(communityName, "facebook.checkCredentials.response.wrongHttpStatusCode", null, null));
		}
		
		String sResponse = response.getMessage();
		try {
			Gson gson = new Gson();
			return gson.fromJson(sResponse, UserCredentions.class); 
		} catch (JsonSyntaxException e) {
			LOGGER.error(e.getMessage(), e);
			String message = messageSource.getMessage(communityName, "facebook.checkCredentials.response.wrongMessageFormat", null, null);
			throw new UserCredentialsException(message);
		}
	}
	
}
