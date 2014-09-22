package mobi.nowtechnologies.server.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mobi.nowtechnologies.server.shared.dto.admin.ArtistDto;
import mobi.nowtechnologies.server.shared.dto.admin.ChartItemDto;
import mobi.nowtechnologies.server.shared.dto.admin.MediaDto;
import mobi.nowtechnologies.server.shared.dto.admin.MediaFileDto;
import mobi.nowtechnologies.server.trackrepo.enums.FileType;
import org.junit.Test;
import org.springframework.http.MediaType;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Oleg Artomov on 9/22/2014.
 */
public class ITunesLinksControllerIT extends AbstractAdminITTest {

    @Resource
    private ObjectMapper objectMapper;

    @Test
    public void testErrorItunesLinks() throws Exception {
        String communityUrl = "hl_uk";
        List<ChartItemDto> dto = new ArrayList<ChartItemDto>();
        dto.add(buildDto("", "a", "a", ""));
        mockMvc.perform(post("/validateITunesLinks").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)).
                cookie(getCommunityCoockie(communityUrl)).headers(getHttpHeaders(true))).
                andExpect(status().isBadRequest()).andDo(print()).andExpect(jsonPath("$.[0].message").value("[0]"));
    }

    @Test
    public void testSuccessValidateItunesLinks() throws Exception {
        String communityUrl = "hl_uk";
        List<ChartItemDto> dto = new ArrayList<ChartItemDto>();
        dto.add(buildDto("", "Appcast", "a", ""));
        mockMvc.perform(post("/validateITunesLinks").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)).
                cookie(getCommunityCoockie(communityUrl)).headers(getHttpHeaders(true))).
                andExpect(status().isOk());
    }

    private ChartItemDto buildDto(String itunesUrl, String channel, String artistName, String label) {
        ChartItemDto result = new ChartItemDto();
        MediaDto mediaDto = new MediaDto();
        mediaDto.setITunesUrl(itunesUrl);
        MediaFileDto audioFileDto = new MediaFileDto();
        audioFileDto.setFileType(FileType.MOBILE_AUDIO);
        mediaDto.setAudioFileDto(audioFileDto);
        ArtistDto artistDto = new ArtistDto();
        artistDto.setName(artistName);
        mediaDto.setArtistDto(artistDto);
        mediaDto.setLabel(label);
        result.setChannel(channel);
        result.setMediaDto(mediaDto);
        return result;
    }
}
