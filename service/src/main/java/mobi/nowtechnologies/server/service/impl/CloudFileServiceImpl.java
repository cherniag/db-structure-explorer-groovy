package mobi.nowtechnologies.server.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;

import mobi.nowtechnologies.server.service.CloudFileService;
import mobi.nowtechnologies.server.service.exception.ExternalServiceException;

import org.apache.http.HttpException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.rackspacecloud.client.cloudfiles.FilesClient;
import com.rackspacecloud.client.cloudfiles.FilesException;

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

	public void setFilesClient(FilesClient filesClient) {
		this.filesClient = filesClient;
	}

	public boolean init() throws IOException, HttpException {
		DefaultHttpClient defaultHttpClient = new DefaultHttpClient(new PoolingClientConnectionManager());
		filesClient = new FilesClient(defaultHttpClient, userName, password, authenticationURL, account, connectionTimeOutMilliseconds);
		if (userAgent != null)
			filesClient.setUserAgent(userAgent);
		filesClient.setUseETag(useETag);

		boolean isConfigured = true;
		LOGGER.debug("Output parameter [{}]", isConfigured);
		return isConfigured;
	}

	@Override
	public synchronized boolean login() {
		LOGGER.info("login on cloud");

		boolean isLogged;
		try {
			isLogged = filesClient.login();
		} catch (IllegalStateException e) {
			LOGGER.error("On java.lang.IllegalStateException: Invalid use of SingleClientConnManager: connection still allocated! " + e.getMessage(), e);
			isLogged = true;// On java.lang.IllegalStateException: Invalid use of SingleClientConnManager: connection still allocated.
		} catch (Exception e) {
			LOGGER.error("Error while login to files cloud: " + e.getMessage(), e);
			throw new ExternalServiceException("Error while login to files cloud: " + e.getMessage(), e);
		}

		if (!isLogged)
			throw new ExternalServiceException("Could not login to files cloud. Please check credentials!");

		return isLogged;
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized boolean uploadFile(MultipartFile file, String fileName) {
		LOGGER.info("Updating file {} on cloud with name {}", file, fileName);

		boolean uploaded = false;
		if (file != null && !file.isEmpty()) {

			login();

			try {
				filesClient.storeStreamedObject(containerName, file.getInputStream(), "application/octet-stream", fileName, Collections.EMPTY_MAP);
				uploaded = true;
				LOGGER.info("Done updating file on cloud. File was successfully uploaded {}", uploaded);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				throw new ExternalServiceException("cloudFile.service.externalError.couldnotsavefile", "Coudn't save file");
			}
		}

		return uploaded;
	}
	
	@Override
	public synchronized boolean uploadFile(File file, String fileName, String contentType, String destinationContainer) {
		LOGGER.info("Updating file {} on cloud with name {}", file.getAbsolutePath(), fileName);

		boolean uploaded = false;
		if (file != null && file.exists()) {

			login();

			try {
				filesClient.storeStreamedObject(destinationContainer, new FileInputStream(file), contentType, fileName, Collections.EMPTY_MAP);
				uploaded = true;
				LOGGER.info("Done updating file on cloud. File was successfully uploaded {}", uploaded);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				throw new ExternalServiceException("cloudFile.service.externalError.couldnotsavefile", "Coudn't save file");
			}
		}

		return uploaded;
	}

	@Override
	public synchronized boolean copyFile(String destFileName, String destContainerName, String srcFileName, String srcContainerName) {
		LOGGER.info("Copy file on cloud [srcFileName:"+srcFileName+", srcContainerName:"+srcContainerName+", destFileName:"+destFileName+" ,destContainerName:" + destContainerName + "]");
		
		boolean copied = false;

		login();

		try {
			filesClient.copyObject(srcContainerName, srcFileName, destContainerName, destFileName);
			copied = true;
		} catch (FilesException e) {
			LOGGER.error("Couldn't copy file on cloud [srcFileName:"+srcFileName+", srcContainerName:"+srcContainerName+", destFileName:"+destFileName+" ,destContainerName:" + destContainerName + "]: HttpStatusMessage: "+e.getHttpStatusMessage()+", HttpHeaders -> " + e.getHttpHeadersAsString() , e);
			throw new ExternalServiceException("Couldn't copy file on cloud [srcFileName:"+srcFileName+", srcContainerName:"+srcContainerName+", destFileName:"+destFileName+" ,destContainerName:" + destContainerName + "]: " + e.getMessage(), e);
		} catch (Exception e) {
			LOGGER.error("Couldn't copy file on cloud [srcFileName:"+srcFileName+", srcContainerName:"+srcContainerName+", destFileName:"+destFileName+" ,destContainerName:" + destContainerName + "]: " + e.getMessage(), e);
			throw new ExternalServiceException("Couldn't copy file on cloud [srcFileName:"+srcFileName+", srcContainerName:"+srcContainerName+", destFileName:"+destFileName+" ,destContainerName:" + destContainerName + "]: " + e.getMessage(), e);
		}

		return copied;
	}
}
