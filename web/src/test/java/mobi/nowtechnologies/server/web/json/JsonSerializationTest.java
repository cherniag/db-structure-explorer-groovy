package mobi.nowtechnologies.server.web.json;

import mobi.nowtechnologies.server.web.dtos.PlaylistDto;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

public class JsonSerializationTest {

    @Test
    public void serializePlaylistDto() throws IOException {
        ObjectMapper om = new ObjectMapper();
        om.writeValue(System.out, new ArrayList<PlaylistDto>(){{
            add(new PlaylistDto());
            add(new PlaylistDto());
        }});
    }
}
