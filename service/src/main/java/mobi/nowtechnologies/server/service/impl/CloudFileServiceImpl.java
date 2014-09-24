package mobi.nowtechnologies.server.service.impl;

import com.google.common.io.Closeables;
import com.rackspacecloud.client.cloudfiles.FilesClient;
import com.rackspacecloud.client.cloudfiles.FilesNotFoundException;
import com.rackspacecloud.client.cloudfiles.FilesObject;
import mobi.nowtechnologies.server.service.CloudFileService;
import mobi.nowtechnologies.server.service.exception.ExternalServiceException;
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

import java.io.*;
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
    private HttpClient httpClient;

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
        filesClient = new FilesClient(httpClient, userName, password, authenticationURL, account, connectionTimeOutMilliseconds);
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

    private Collection<FilesObject> findFilesStartWith(String prefix, int limit) {
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

    @Override
    public InputStream getInputStream(String destinationContainer, String fileName) throws  FilesNotFoundException{
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
            LOGGER.error(e.getMessage(), e);
            throw new ExternalServiceException("cloudFile.service.externalError.couldnotopenstream", "Coudn't find  file");
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized boolean uploadFile(MultipartFile file, String fileName, Map metadata) {
        LOGGER.info("Updating file {} on cloud with name {}", file, fileName);

        if (file != null && !file.isEmpty()) {
            try {
                uploadFromStream(file.getInputStream(), fileName, metadata);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
                throw new ExternalServiceException("cloudFile.service.externalError.couldnotsavefile", "Coudn't save file");
            }
        }

        return false;
    }

    @Override
    public void uploadFromStream(InputStream stream, String fileName, Map metadata) {
        Assert.notNull(stream);

        Map map = MapUtils.isEmpty(metadata) ? Collections.EMPTY_MAP : metadata;
        LOGGER.info("Updating file on cloud with name {} and data {}", fileName, map);

        login();
        try {
            filesClient.storeStreamedObject(containerName, stream, "application/octet-stream", fileName, map);
            LOGGER.info("Done updating file on cloud. File was successfully uploaded");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ExternalServiceException("cloudFile.service.externalError.couldnotsavefile", "Coudn't save file");
        } finally {
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
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ExternalServiceException("cloudFile.service.externalError.couldnotsavefile", "Coudn't save file");
        } finally {
            IOUtils.closeQuietly(stream);
        }
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
