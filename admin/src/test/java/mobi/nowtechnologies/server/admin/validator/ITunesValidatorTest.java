package mobi.nowtechnologies.server.admin.validator;

import mobi.nowtechnologies.server.admin.controller.itunes.ITunesValidator;
import mobi.nowtechnologies.server.shared.dto.admin.ArtistDto;
import mobi.nowtechnologies.server.shared.dto.admin.ChartItemDto;
import mobi.nowtechnologies.server.shared.dto.admin.MediaDto;
import mobi.nowtechnologies.server.shared.dto.admin.MediaFileDto;
import mobi.nowtechnologies.server.trackrepo.enums.FileType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 * Created by Oleg Artomov on 9/22/2014.
 */
public class ITunesValidatorTest {

    private ITunesValidator itunesValidator;


    private Errors errors;

    @Before
    public void setUp() {
        itunesValidator = new ITunesValidator();
    }


    @Test
    public void testWhenChannelIsAppcast() {
        check("", "Appcast", "", "", FileType.MOBILE_AUDIO, "");
        assertFalse(errors.hasErrors());
    }


    @Test
    public void testWhenArtistNamelIsAppcast() {
        check("", "", "Appcast", "", FileType.MOBILE_AUDIO, "");
        assertFalse(errors.hasErrors());
        check("", "", "APPCAST", "", FileType.MOBILE_AUDIO, "");
        assertFalse(errors.hasErrors());
        check("", "", "1APPCAST1", "", FileType.MOBILE_AUDIO, "");
        assertFalse(errors.hasErrors());
        check("", "", " APPcaST1", "", FileType.MOBILE_AUDIO, "");
        assertFalse(errors.hasErrors());
    }


    @Test
    public void testWhenTitlelIsAppcast() {
        check("", "", "", "", FileType.MOBILE_AUDIO, "Appcast");
        assertFalse(errors.hasErrors());
        check("", "", "", "", FileType.MOBILE_AUDIO, "APPCAST");
        assertFalse(errors.hasErrors());
        check("", "", "", "", FileType.MOBILE_AUDIO, " AppCaST ");
        assertFalse(errors.hasErrors());
    }


    @Test
    public void testWhenLabellIsMQ() {
        check("", "", "", "MQ", FileType.MOBILE_AUDIO, "");
        assertFalse(errors.hasErrors());
    }

    @Test
    public void testWhenFileTypeIsVideo() {
        check("", "", "", "", FileType.VIDEO, "");
        assertFalse(errors.hasErrors());
    }


    @Test
    public void testWhenITunesUrlIsEmpty() {
        check("", "", "", "", FileType.MOBILE_AUDIO, "");
        hasGlobalError(errors);
    }

    @Test
    public void testWhenITunesUrlIsNotValid() {
        check("fdfd", "", "", "", FileType.MOBILE_AUDIO, "");
        hasGlobalError(errors);
    }

    private void check(String itunesUrl, String channel, String artistName, String label, FileType fileType, String title){
        errors = new MapBindingResult(Collections.emptyMap(), "");
        ArrayList<ChartItemDto> dtos = new ArrayList<ChartItemDto>();
        dtos.add(buildDto(itunesUrl, channel, artistName, label, fileType, title));
        itunesValidator.validate(dtos, errors);
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

    private void hasGlobalError(Errors errors) {
        ObjectError resultError = null;
        for (ObjectError error : errors.getGlobalErrors()) {
            if (error.getCode().equals("chartItems.page.itunesLink.invalidPositions")) {
                resultError = error;
                break;
            }
        }
        if (resultError != null) {
            assertArrayEquals(resultError.getArguments(), new Object[]{"0"});
        } else {
            fail();
        }
    }


}
