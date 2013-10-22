package mobi.nowtechnologies.server.admin.controller;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.server.MockMvc;

import javax.servlet.http.Cookie;

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.server.setup.MockMvcBuilders.xmlConfigSetup;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/shared.xml"})
@Ignore
public class OfferControllerIT {

    MockMvc mockMvc;

    @Before
    public void setUp() {

        mockMvc = xmlConfigSetup(
                "classpath:META-INF/dao-test.xml",
                "classpath:META-INF/service.xml",
                "classpath:META-INF/shared.xml",
                "classpath:admin-test.xml",
                "classpath:security.xml")
                .configureWebAppRootDir("admin/src/main/webapp/", false).build();
    }

    @Test
    public void givenOfferWithOutPrice_WhenSaveOffer_ShouldThroughValidationExceptionAndWillNotSaveThisOffer()throws Exception{

        String community = "runningtrax";
        mockMvc.perform(
                fileUpload("/offers/new")
                        .file(mockFile())
                        .param("title", "MyTitle")
                        .param("description", "MyDescription")
                        .cookie(new Cookie("_chartsnow_community", community))

        ).andExpect(status().isOk())
                .andExpect(view().name("offer/add"));
    }

    private MockMultipartFile mockFile() {
        return new MockMultipartFile("file", "content".getBytes());
    }
}