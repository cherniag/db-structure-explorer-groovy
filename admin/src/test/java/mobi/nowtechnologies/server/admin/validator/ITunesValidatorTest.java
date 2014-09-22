package mobi.nowtechnologies.server.admin.validator;

import mobi.nowtechnologies.server.admin.controller.itunes.ITunesValidator;
import mobi.nowtechnologies.server.shared.dto.admin.ArtistDto;
import mobi.nowtechnologies.server.shared.dto.admin.ChartItemDto;
import mobi.nowtechnologies.server.shared.dto.admin.MediaDto;
import mobi.nowtechnologies.server.shared.dto.admin.MediaFileDto;
import mobi.nowtechnologies.server.trackrepo.enums.FileType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.assertTrue;

/**
 * Created by Oleg Artomov on 9/22/2014.
 */
@RunWith(PowerMockRunner.class)
public class ITunesValidatorTest {

    private  ITunesValidator itunesValidator;


    private Errors errors = new MapBindingResult(Collections.emptyMap(), "");

    @Before
    public  void setUp() {
        itunesValidator = new ITunesValidator();
    }


    @Test
    public void testWhenChannelIsAppcast(){
        ArrayList<ChartItemDto> dtos = new ArrayList<ChartItemDto>();
        dtos.add(buildDto("", "Appcast", "", "", FileType.MOBILE_AUDIO, ""));
        itunesValidator.validate(dtos, errors);
        assertTrue(!errors.hasErrors());
    }


    @Test
    public void testWhenArtistNamelIsAppcast(){
        ArrayList<ChartItemDto> dtos = new ArrayList<ChartItemDto>();
        dtos.add(buildDto("", "", "Appcast", "", FileType.MOBILE_AUDIO, ""));
        itunesValidator.validate(dtos, errors);
        assertTrue(!errors.hasErrors());
    }

    @Test
    public void testWhenTitlelIsAppcast(){
        ArrayList<ChartItemDto> dtos = new ArrayList<ChartItemDto>();
        dtos.add(buildDto("", "", "", "", FileType.MOBILE_AUDIO, "Appcast"));
        itunesValidator.validate(dtos, errors);
        assertTrue(!errors.hasErrors());
    }


    @Test
    public void testWhenLabellIsMQ(){
        ArrayList<ChartItemDto> dtos = new ArrayList<ChartItemDto>();
        dtos.add(buildDto("", "", "", "MQ", FileType.MOBILE_AUDIO, ""));
        itunesValidator.validate(dtos, errors);
        assertTrue(!errors.hasErrors());
    }

    @Test
    public void testWhenFileTypeIsVideo(){
        ArrayList<ChartItemDto> dtos = new ArrayList<ChartItemDto>();
        dtos.add(buildDto("", "", "", "", FileType.VIDEO, ""));
        itunesValidator.validate(dtos, errors);
        assertTrue(!errors.hasErrors());
    }


    @Test
    public void testWhenITunesUrlIsEmpty(){
        ArrayList<ChartItemDto> dtos = new ArrayList<ChartItemDto>();
        dtos.add(buildDto("", "", "", "", FileType.MOBILE_AUDIO, ""));
        itunesValidator.validate(dtos, errors);
        assertTrue(errors.hasErrors());
    }


    private ChartItemDto buildDto(String itunesUrl, String channel, String artistName, String label, FileType fileType, String title) {
        ChartItemDto result = new ChartItemDto();
        MediaDto mediaDto = new MediaDto();
        mediaDto.setITunesUrl(itunesUrl);
        mediaDto.setTitle(title);
        MediaFileDto audioFileDto = new MediaFileDto();
        audioFileDto.setFileType(fileType);
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
