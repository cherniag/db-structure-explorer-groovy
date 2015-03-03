package mobi.nowtechnologies.server.trackrepo.controller;

import org.junit.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Created by oar on 12/18/13.
public class VersionControllerTestIT extends AbstractTrackRepoIT {

    @Test
    public void testVersion_Success() throws Exception {
        mockMvc.perform(get("/version")).andExpect(status().isOk()).andDo(print()).andExpect(jsonPath("$.build").exists()).andExpect(jsonPath("$.branchName").exists())
               .andExpect(jsonPath("$.revision").exists()).andExpect(jsonPath("$.version").exists());
    }
}
