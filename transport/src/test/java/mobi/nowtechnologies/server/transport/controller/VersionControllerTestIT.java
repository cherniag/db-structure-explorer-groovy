package mobi.nowtechnologies.server.transport.controller;

import org.junit.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


/**
 * Created by oar on 12/18/13.
 */
public class VersionControllerTestIT extends AbstractControllerTestIT{

    @Test
    public void testVersion_Success() throws Exception {
        mockMvc.perform(
                get("/version")
        ).andExpect(status().isOk()).andDo(print());
    }
}
