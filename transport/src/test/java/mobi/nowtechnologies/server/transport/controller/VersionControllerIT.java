package mobi.nowtechnologies.server.transport.controller;

import org.junit.Test;

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

/**
 * Created by oar on 12/18/13.
 */
public class VersionControllerIT extends AbstractControllerTestIT{

    @Test
    public void testActivateVideoAudioFreeTrial_WithAccCheckDetailsAndVersionMore50_Success() throws Exception {
        mockMvc.perform(
                get("/version")
        ).andExpect(status().isOk()).andDo(print());
    }
}
