package mobi.nowtechnologies.server.admin.controller.itunes;

import com.google.common.base.Joiner;
import mobi.nowtechnologies.server.service.util.BaseValidator;
import mobi.nowtechnologies.server.shared.CollectionUtils;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.admin.ChartItemDto;
import mobi.nowtechnologies.server.shared.util.URLValidation;
import mobi.nowtechnologies.server.trackrepo.enums.FileType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Oleg Artomov on 9/22/2014.
 */
public class ITunesValidator extends BaseValidator {

    public static final String APPCAST = "Appcast";
    public static final String MQ_LABEL_NAME = "MQ";

    @Override
    protected boolean customValidate(Object target, Errors errors) {
        Collection<ChartItemDto> chartItems = (Collection<ChartItemDto>) target;
        List<Byte> positions = new ArrayList<Byte>();
        for (ChartItemDto item : chartItems) {
            boolean isValidItem = validateItem(item);
            if (!isValidItem) {
                positions.add(item.getPosition());
            }
        }
        if (!CollectionUtils.isEmpty(positions)) {
            errors.reject("chartItems.page.itunesLink.invalidPositions", new Object[]{Joiner.on(",").join(positions)}, null);
        }
        return true;
    }

    private boolean validateItem(ChartItemDto item) {
        if (FileType.VIDEO.equals(item.getMediaDto().getAudioFileDto().getFileType())) {
            return true;
        }
        if (APPCAST.equalsIgnoreCase(item.getChannel())) {
            return true;
        }
        if (contains(item.getMediaDto().getArtistDto().getName(), APPCAST)){
            return true;
        }
        if (contains(item.getMediaDto().getTitle(), APPCAST)){
            return true;
        }
        if (MQ_LABEL_NAME.equalsIgnoreCase(item.getMediaDto().getLabel())) {
            return true;
        }

        String iTunesUrl = item.getMediaDto().getITunesUrl();
        String decodedUrl = Utils.decodeUrl(iTunesUrl);
        return StringUtils.isNotEmpty(decodedUrl) && URLValidation.validate(decodedUrl);

    }

    private boolean contains(String sourceString, String substring) {
        return StringUtils.isNotEmpty(sourceString) && (sourceString.toLowerCase().contains(substring.toLowerCase()));
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Collection.class.isAssignableFrom(clazz);
    }
}