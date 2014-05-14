package mobi.nowtechnologies.server.transport.controller;

import org.junit.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Oleg Artomov on 5/7/2014.
 */
public class MigControllerIT extends AbstractControllerTestIT {

    @Test
    public void testDRListener() throws Exception {
        mockMvc.perform(get("/DRListener")
                .param("MESSAGEID", "1")
                .param("STATUSTYPE", "1")
                .param("GUID", "1")
                .param("STATUS", "1")).andExpect(status().isOk()).andDo(print())
        ;
    }

    @Test
    public void testMOListenerr() throws Exception {
        mockMvc.perform(get("/MOListener")
                        .param("BODY", "1")
                        .param("OADC", "1")
                        .param("CONNECTION", "1")
        ).andExpect(status().isOk()).andDo(print());
    }

}
