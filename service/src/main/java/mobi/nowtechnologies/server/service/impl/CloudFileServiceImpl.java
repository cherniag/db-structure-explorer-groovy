package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.service.CloudFileService;
import mobi.nowtechnologies.server.service.exception.ExternalServiceException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;

import com.google.common.io.Closeables;
import com.rackspacecloud.client.cloudfiles.FilesClient;
import com.rackspacecloud.client.cloudfiles.FilesException;
import com.rackspacecloud.client.cloudfiles.FilesNotFoundException;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpException;
import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Titov Mykhaylo (titov)
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
    private HttpClient httpClient;
    private int copyFileOnCloudAttemptCount;

    public boolean init() throws IOException, HttpException {
        filesClient = new FilesClient(httpClient, userName, password, authenticationURL, account, connectionTimeOutMilliseconds);
        if (userAgent != null) {
            filesClient.setUserAgent(userAgent);
        }
        filesClient.setUseETag(useETag);

        LOGGER.debug("Service has been configured");
        return true;
    }

    private boolean login() {
        LOGGER.info("login on cloud");

        boolean isLogged;
        try {
            isLogged = filesClient.login();
        }
        catch (IllegalStateException e) {
            LOGGER.error("Exception on login on cloud : {}", e.getMessage(), e);
            isLogged = true;// On java.lang.IllegalStateException: Invalid use of SingleClientConnManager: connection still allocated.
        }
        catch (Exception e) {
            LOGGER.error("Exception on login on cloud : {}", e.getMessage(), e);
            throw new ExternalServiceException("cloudFile.service.externalError.couldnotlogin", "Couldn't login");
        }

        if (!isLogged) {
            LOGGER.error("Login was not successful");
            throw new ExternalServiceException("cloudFile.service.externalError.couldnotlogin", "Couldn't login");
        }
        LOGGER.info("Logged in successfully");
        return isLogged;
    }

    @Override
    public void deleteFile(String fileName) {
        LOGGER.info("delete file in container [] by fileName", containerName, fileName);
        if (!StringUtils.isEmpty(fileName)) {
            login();
            try {
                filesClient.deleteObject(containerName, fileName);
            }
            catch (Exception e) {
                LOGGER.error("Exception while deleteFile on cloud {}: {}", fileName, e.getMessage(), e);
                throw new ExternalServiceException("cloudFile.service.externalError.couldnotdelete", "Coudn't delete file");
            }
        }
    }

    @Override
    public InputStream getInputStream(String destinationContainer, String fileName) throws FilesNotFoundException {
        LOGGER.info("get InputStream for file file in container [] by fileName", destinationContainer, fileName);
        Assert.hasText(fileName);
        login();
        try {
            return filesClient.getObjectAsStream(destinationContainer, fileName);
        }
        catch (FilesNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
        catch (Exception e) {
            LOGGER.error("Exception while getInputStream on cloud {}: {}", fileName, e.getMessage(), e);
            throw new ExternalServiceException("cloudFile.service.externalError.couldnotopenstream", "Couldn't find  file");
        }

    }

    @Override
    public boolean fileExists(String destinationContainer, String fileName) {
        LOGGER.info("get InputStream for file file in container [] by fileName", destinationContainer, fileName);
        Assert.hasText(fileName);
        login();
        try {
            filesClient.getObjectMetaData(destinationContainer, fileName);
            return true;
        }
        catch (FilesNotFoundException e) {
            return false;
        }
        catch (Exception e) {
            LOGGER.error("Exception while fileExists on cloud {}: {}", fileName, e.getMessage(), e);
            throw new ExternalServiceException("cloudFile.service.externalError.couldnotopenstream", "Couldn't find  file");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized boolean uploadFile(MultipartFile file, String fileName, Map metadata) {
        LOGGER.info("Updating file {} on cloud with name {}", file, fileName);

        if (file != null && !file.isEmpty()) {
            try {
                uploadFromStream(file.getInputStream(), fileName, metadata);
            }
            catch (IOException e) {
                LOGGER.error("Exception while uploadFile on cloud {}: {}", fileName, e.getMessage(), e);
                throw new ExternalServiceException("cloudFile.service.externalError.couldnotsavefile", "Coudn't save file");
            }
        }

        return false;
    }

    @Override
    public void uploadFromStream(InputStream stream, String fileName, Map metadata) {
        Assert.notNull(stream);

        Map map = MapUtils.isEmpty(metadata) ?
                  Collections.EMPTY_MAP :
                  metadata;
        LOGGER.info("Updating file on cloud with name {} and data {}", fileName, map);

        login();
        try {
            filesClient.storeStreamedObject(containerName, stream, "application/octet-stream", fileName, map);
            LOGGER.info("Done updating file on cloud. File was successfully uploaded");
        }
        catch (Exception e) {
            LOGGER.error("Exception while uploadFromStream on cloud {}: {}", fileName, e.getMessage(), e);
            throw new ExternalServiceException("cloudFile.service.externalError.couldnotsavefile", "Coudn't save file");
        }
        finally {
            Closeables.closeQuietly(stream);
        }
    }

    @Override
    public void downloadToStream(OutputStream stream, String fileName) {
        Assert.notNull(stream);

        LOGGER.info("Downloading file on cloud with name {}", fileName);

        login();
        try {
            InputStream inputStream = filesClient.getObjectAsStream(containerName, fileName);

            Streams.copy(inputStream, stream, false);
        }
        catch (Exception e) {
            LOGGER.error("Exception while downloadToStream {}: {}", fileName, e.getMessage(), e);
            throw new ExternalServiceException("cloudFile.service.externalError.couldnotsavefile", "Coudn't save file");
        }
        finally {
            IOUtils.closeQuietly(stream);
        }
    }

    @Override
    public synchronized boolean copyFile(String srcContainerName, String srcFileName, String targetContainerName, String targetFileName) {
        LOGGER.info("Copy file {} from {} cloud container to {} in {} container", srcFileName, srcContainerName, targetFileName, targetContainerName);

        login();

        for (int i = 1; i <= copyFileOnCloudAttemptCount; i++) {
            LOGGER.debug("Starting attempt {} of {} for copy file {}", i, copyFileOnCloudAttemptCount, srcFileName);
            try {
                filesClient.copyObject(srcContainerName, srcFileName, targetContainerName, targetFileName);
                LOGGER.info("File {} has been copied", srcFileName);
                return true;
            }
            catch (FilesException fe) {
                LOGGER.error("Can't copy file [{}] from source container [{}] as [{}] file to target container [{}]. Some http error occurred with {} http status code.", srcFileName, srcContainerName,
                             targetFileName, targetContainerName, fe.getHttpStatusCode(), fe);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        throw new ExternalServiceException("cloudFile.service.externalError.couldnotcopyfile", "Couldn't copy file on cloud");
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
            }
            catch (Exception e) {
                LOGGER.error("Exception while uploadFile on cloud {}: {}", fileName, e.getMessage(), e);
                throw new ExternalServiceException("cloudFile.service.externalError.couldnotsavefile", "Coudn't save file");
            }
        }

        return uploaded;
    }

    @Override
    public synchronized boolean uploadFile(File file, String fileName, String contentType, String destinationContainer) {
        LOGGER.info("Updating file {} on cloud in {}/{} with contentType {}", file.getAbsolutePath(), destinationContainer, fileName, contentType);

        login();

        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            filesClient.storeStreamedObject(destinationContainer, fileInputStream, contentType, fileName, Collections.<String, String>emptyMap());
            LOGGER.info("File {} has been uploaded", file.getAbsolutePath());
            return true;
        }
        catch (Exception e) {
            LOGGER.error("Exception while uploadFile with contentType on cloud {}: {}", fileName, e.getMessage(), e);
        }
        finally {
            IOUtils.closeQuietly(fileInputStream);
        }

        throw new ExternalServiceException("cloudFile.service.externalError.couldnotsavefile", "Coudn't save file");
    }

    @Override
    public String getFilesURL() {
        return filesURL;
    }

    public void setFilesURL(String filesURL) {
        this.filesURL = filesURL;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

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

    public void setFilesClient(FilesClient filesClient) {
        this.filesClient = filesClient;
    }

    public void setCopyFileOnCloudAttemptCount(int copyFileOnCloudAttemptCount) {
        Assert.isTrue(copyFileOnCloudAttemptCount > 0, "copyFileOnCloudAttemptCount should be > 0, but was " + copyFileOnCloudAttemptCount);
        this.copyFileOnCloudAttemptCount = copyFileOnCloudAttemptCount;
    }
}
