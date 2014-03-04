package mobi.nowtechnologies.server.admin.controller;

import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;

import javax.servlet.http.Cookie;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


public class OfferControllerIT extends AbstractAdminITTest{

    @Test
    public void givenOfferWithOutPrice_WhenSaveOffer_ShouldThroughValidationExceptionAndWillNotSaveThisOffer()throws Exception{

        String community = "runningtrax";
        mockMvc.perform(
                fileUpload("/offers/new")
                        .file(mockFile())
                        .param("title", "MyTitle")
                        .param("description", "MyDescription")
                        .headers(getHttpHeaders(true))
                        .cookie(new Cookie("_chartsnow_community", community))

        ).andExpect(status().isOk())
                .andExpect(view().name("offer/add"));
    }

    private MockMultipartFile mockFile() {
        return new MockMultipartFile("file", "content".getBytes());
    }
}