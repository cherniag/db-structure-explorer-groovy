package mobi.nowtechnologies.server.admin.controller;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ChartIT extends AbstractAdminITTest {

    @Test
    public void testUpdateChart_Success() throws Exception {
        Integer id = 5;
        String name = "Basic Chart Name";
        String subtitle = "Basic Chart Subtitle";
        byte[] file = "1".getBytes();
        String requestURI = "/charts/" + id + "/" + new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(DateUtils.addHours(new Date(), 10));
        String communityUrl = "nowtop40";
        String imageFileName = "someImageFileName";

        mockMvc.perform(fileUpload(requestURI).file(new MockMultipartFile("file", file)).
                cookie(getCommunityCoockie(communityUrl)).headers(getHttpHeaders(true)).
                param("id", id.toString()).
                param("name", name).
                param("imageFileName", imageFileName).
                param("position", "1").
                param("subtitle", subtitle)
        ).andExpect(status().isMovedTemporarily()).andExpect(redirectedUrl("/charts/5"));
    }


}
