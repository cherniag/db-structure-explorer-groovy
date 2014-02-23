package mobi.nowtechnologies.server.transport.controller;

import org.junit.Test;

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

/**
 * Created by oar on 12/18/13.
 */
public class VersionControllerTestIT extends AbstractControllerTestIT{

    @Test
    public void testVersion_Success() throws Exception {
        mockMvc.perform(
                get("/version")
        ).andExpect(status().isOk()).andDo(print())
        .andExpect(jsonPath("$.build").exists())
        .andExpect(jsonPath("$.branchName").exists())
        .andExpect(jsonPath("$.revision").exists())
        .andExpect(jsonPath("$.version").exists());
    }
}
