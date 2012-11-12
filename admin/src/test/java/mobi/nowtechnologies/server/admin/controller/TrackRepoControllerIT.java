package mobi.nowtechnologies.server.admin.controller;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.ResultActions;
import org.springframework.ui.ModelMap;

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.server.setup.MockMvcBuilders.xmlConfigSetup;

public class TrackRepoControllerIT {

    MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = xmlConfigSetup("classpath:META-INF/dao-test.xml",
                "classpath:META-INF/service-test.xml",
                "classpath:META-INF/shared.xml",
                "classpath:admin-test.xml",
                "classpath:WEB-INF/security.xml")
                .configureWebAppRootDir("admin/src/main/webapp/", false).build();
    }

    @Test
    public void verifyThatTrackCanBeFetchedByAlbomName() throws  Exception{
        ResultActions resultActions = mockMvc.perform(
                get("/tracks/list")
                        .param("label", "IME")
                        .param("page.page", "1")
        )
                .andExpect(status().isOk())
                .andExpect(view().name("tracks/tracks"));
        ModelMap modelMap = resultActions.andReturn().getModelAndView().getModelMap();
    }

}
