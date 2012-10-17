package mobi.nowtechnologies.server.service.impl;

import java.io.IOException;
import java.util.Collections;

import mobi.nowtechnologies.server.service.CloudFileService;
import mobi.nowtechnologies.server.service.exception.ExternalServiceException;

import org.apache.http.HttpException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.rackspacecloud.client.cloudfiles.FilesClient;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class CloudFileServiceImpl implements CloudFileService {

	private static final Logger LOGGER = LoggerFactory.getLogger(CloudFileServiceImpl.class);

	private FilesClient filesClient;

	private String userName;
	private String password;
	private String account;
	private int connectionTimeOutMilliseconds;
	private String authenticationURL;
	private boolean useETag;
	private String userAgent;
	private String containerName;
	private String filesURL;

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public void setConnectionTimeOutMilliseconds(int connectionTimeOutMilliseconds) {
		this.connectionTimeOutMilliseconds = connectionTimeOutMilliseconds;
	}

	public void setAuthenticationURL(String authenticationURL) {
		this.authenticationURL = authenticationURL;
	}

	public void setUseETag(boolean useETag) {
		this.useETag = useETag;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}
	
	public void setFilesURL(String filesURL) {
		this.filesURL = filesURL;
	}
	
	public String getFilesURL() {
		return filesURL;
	}

	public boolean init() throws IOException, HttpException {
		DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
		filesClient = new FilesClient(defaultHttpClient, userName, password, authenticationURL, account, connectionTimeOutMilliseconds);
		if (userAgent != null)
			filesClient.setUserAgent(userAgent);
		filesClient.setUseETag(useETag);
//		boolean isLogged = filesClient.login();
//		if (!isLogged)
//			throw new ExternalServiceException("cloudFile.service.externalError.couldnotlogin", "Couldn't login");
//
//		LOGGER.debug("Output parameter isLogged=[{}]", isLogged);
//		return isLogged;
		boolean isConfigured=true;
		LOGGER.debug("Output parameter [{}]", isConfigured);
		return isConfigured;
	}

	/* (non-Javadoc)
	 * @see mobi.nowtechnologies.server.service.CloudFileService#uploadFile(org.springframework.web.multipart.MultipartFile, java.lang.String)
	 * 
	 * This method synchronized because the filesClient isn't thread-safe
	 * 
	 */
	@SuppressWarnings("unchecked")
	public synchronized boolean uploadFile(MultipartFile file, String fileName) {
		LOGGER.info("Updating file {} on cloud with name {}", file, fileName);

		boolean uploaded = false;	
		if (file != null && !file.isEmpty()) {

			boolean isLogged;
			try {
				isLogged = filesClient.login();
			}catch(IllegalStateException e){
				LOGGER.error(e.getMessage(), e);
				isLogged=true;// On java.lang.IllegalStateException: Invalid use of SingleClientConnManager: connection still allocated.
			}catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				throw new ExternalServiceException("cloudFile.service.externalError.couldnotlogin", "Couldn't login");
			}

			if (!isLogged)
				throw new ExternalServiceException("cloudFile.service.externalError.couldnotlogin", "Couldn't login");

			try {
				filesClient.storeStreamedObject(containerName, file.getInputStream(), "application/octet-stream", fileName, Collections.EMPTY_MAP);
				uploaded = true;
				LOGGER.info("Done updating file on cloud. File was successfuly uploaded {}", uploaded);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				throw new ExternalServiceException("cloudFile.service.externalError.couldnotsavefile", "Coudn't save file");
			}
		}

		return uploaded;
	}
}
