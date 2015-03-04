package mobi.nowtechnologies.server.dto;

import mobi.nowtechnologies.server.shared.dto.admin.FilterDto;
import mobi.nowtechnologies.server.shared.enums.AdActionType;

import java.util.Collections;

import org.springframework.mock.web.MockMultipartFile;

/**
 * @author Titov Mykhaylo (titov)
 */
public class AdItemDtoFactory {


    public static AdItemDto createAdItemDto(String action, AdActionType actionType) {
        AdItemDto adItemDto = new AdItemDto();

        adItemDto.setAction(action);
        adItemDto.setMessage("message");
        adItemDto.setFile(new MockMultipartFile("test", "".getBytes()));
        adItemDto.setImageFileName("imageFileName");
        adItemDto.setActionType(actionType);
        adItemDto.setActivated(true);
        adItemDto.setFilterDtos(Collections.<FilterDto>emptySet());
        adItemDto.setId(new Integer(1));

        return adItemDto;
    }

}