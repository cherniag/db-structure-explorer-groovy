package mobi.nowtechnologies.server.service.impl;

import com.rackspacecloud.client.cloudfiles.FilesClient;
import com.rackspacecloud.client.cloudfiles.FilesObject;
import mobi.nowtechnologies.server.service.CloudFileService;
import mobi.nowtechnologies.server.service.exception.ExternalServiceException;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

/**
 * @author Titov Mykhaylo (titov)
 */
public class CloudFileServiceImpl implements CloudFileService {

	private static final Logger LOGGER = LoggerFactory.getLogger(CloudFileServiceImpl.class);
    public static final int DELETE_BATCH_SIZE = 1000;

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
        DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
		filesClient = new FilesClient(defaultHttpClient, userName, password, authenticationURL, account, connectionTimeOutMilliseconds);
		if (userAgent != null)
			filesClient.setUserAgent(userAgent);
		filesClient.setUseETag(useETag);

		boolean isConfigured = true;
		LOGGER.debug("Output parameter [{}]", isConfigured);
		return isConfigured;
	}

	private boolean login() {
		LOGGER.info("login on cloud");

		boolean isLogged;
		try {
			isLogged = filesClient.login();
		} catch (IllegalStateException e) {
            LOGGER.error(e.getMessage(), e);
			isLogged = true;// On java.lang.IllegalStateException: Invalid use of SingleClientConnManager: connection still allocated.
		} catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ExternalServiceException("cloudFile.service.externalError.couldnotlogin", "Couldn't login");
		}

		if (!isLogged)
            throw new ExternalServiceException("cloudFile.service.externalError.couldnotlogin", "Couldn't login");

		return isLogged;
	}

    @Override
    public Collection<FilesObject> findFilesStartWith(String prefix, int limit) {
        if (!StringUtils.isEmpty(prefix)) {
            login();
            try {
                Collection<FilesObject> result = filesClient.listObjectsStartingWith(containerName,  prefix, null, limit, null);
                return isEmpty(result) ? Collections.<FilesObject>emptyList() : result;
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                throw new ExternalServiceException("cloudFile.service.externalError.cantfindfiles", "Coudn't find files");
            }
        }
        return Collections.emptyList();
    }

    @Override
    public void deleteByPrefix(String prefix) {
        LOGGER.info("delete files in container [] by prefix", containerName, prefix);
        Collection<FilesObject> files = findFilesStartWith(prefix, DELETE_BATCH_SIZE);
        if (!isEmpty(files)) {
            login();
            for (FilesObject filesObject : files) {
                deleteFile(filesObject.getName());
            }
        }
    }

    @Override
    public void deleteFile(String fileName) {
        LOGGER.info("delete file in container [] by fileName", containerName, fileName);
        if (!StringUtils.isEmpty(fileName)) {
            login();
            try {
                filesClient.deleteObject(containerName, fileName);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                throw new ExternalServiceException("cloudFile.service.externalError.couldnotdelete", "Coudn't delete file");
            }
        }
    }

	@SuppressWarnings("unchecked")
	@Override
    public synchronized boolean uploadFile(MultipartFile file, String fileName, Map metadata) {
		LOGGER.info("Updating file {} on cloud with name {}", file, fileName);

		boolean uploaded = false;
		if (file != null && !file.isEmpty()) {

			login();
			try {
                Map map = MapUtils.isEmpty(metadata) ? Collections.EMPTY_MAP : metadata;
                filesClient.storeStreamedObject(containerName, file.getInputStream(), "application/octet-stream", fileName, map);
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
        LOGGER.info("Copy file {} from one cloud container to other container {}", new Object[]{destFileName, destContainerName, srcFileName, srcContainerName});
        boolean copied = false;
        login();
        try {
            filesClient.copyObject(srcContainerName, srcFileName, destContainerName, destFileName);
            copied = true;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ExternalServiceException("cloudFile.service.externalError.couldnotcopyfile", "Couldn't copy file on cloud");
        }
        return copied;
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

}
