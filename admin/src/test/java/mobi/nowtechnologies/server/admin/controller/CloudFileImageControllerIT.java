package mobi.nowtechnologies.server.admin.controller;

import mobi.nowtechnologies.server.service.streamzine.ImageDTO;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import org.junit.*;
import org.springframework.test.web.servlet.MvcResult;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static junit.framework.Assert.assertEquals;

/**
 * Created by oar on 2/25/14.
 */
@Ignore
public class CloudFileImageControllerIT extends AbstractAdminITTest {

    @Value("classpath:testData\\testImageUpload.jpg")
    private Resource imageForUpload;

    @Value("${cloudFile.jadmin.directoryName}")
    private String imagesDir;

    @Value("${bambooBuildNumber}")
    private String buildNumber;

    private ObjectMapper mapper = new ObjectMapper();


    private RestTemplate template = new RestTemplate();

    private String prefix;
    private byte[] content;
    private File file;


    private Collection<ImageDTO> findByPrefix(String communityUrl, String fileNameBB) throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/images/find").param("prefix", fileNameBB).cookie(getCommunityCookie(communityUrl)).headers(getHttpHeaders(true))).andExpect(status().isOk())
                                     .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();
        return mapper.readValue(mvcResult.getResponse().getContentAsByteArray(), new TypeReference<Collection<ImageDTO>>() {});
    }


    private MvcResult uploadAndWait(String communityUrl, File fileResource, String fileName) throws Exception {
        MvcResult result = mockMvc.perform(post("/streamzine/upload/image").with(buildProcessorForFileUpload("file", fileName, fileResource)).
            cookie(getCommunityCookie(communityUrl)).headers(getHttpHeaders(true))).andExpect(status().isOk()).andReturn();
        Thread.sleep(3000);
        return result;
    }

    @Before
    public void beforeEachTest() throws IOException {
        prefix = "tFwt" + buildNumber;
        file = imageForUpload.getFile();
        content = FileUtils.readFileToByteArray(file);
    }

    @Test
    public void testUpload() throws Exception {
        String communityUrl = "nowtop40";
        String fileName = file.getName();
        MvcResult result = uploadAndWait(communityUrl, file, fileName);
        ImageDTO imageDTO = (ImageDTO) result.getModelAndView().getModel().get("dto");
        ResponseEntity<byte[]> downloadedImage = template.getForEntity(imageDTO.getUrl(), byte[].class);
        assertTrue(Arrays.equals(downloadedImage.getBody(), content));
    }


    @Test
    public void testFindByPrefix() throws Exception {
        String communityUrl = "nowtop40";
        String fileNameBB = prefix + "BB";
        uploadAndWait(communityUrl, file, fileNameBB);

        String fileNameAA = prefix + "aa";
        uploadAndWait(communityUrl, file, fileNameAA);

        Collection<ImageDTO> images = findByPrefix(communityUrl, fileNameBB);
        assertEquals(images.size(), 1);
    }


    @Test
    public void testDeleteByName() throws Exception {
        String communityUrl = "nowtop40";
        String fileNameBB = prefix + "BB";
        uploadAndWait(communityUrl, file, fileNameBB);
        Collection<ImageDTO> images = findByPrefix(communityUrl, fileNameBB);
        assertEquals(images.size(), 1);
        mockMvc.perform(get("/images/delete").param("fileName", fileNameBB).
            cookie(getCommunityCookie(communityUrl)).headers(getHttpHeaders(true))).
                   andExpect(status().isOk());
        images = findByPrefix(communityUrl, fileNameBB);
        assertEquals(images.size(), 0);
    }


}
