package mobi.nowtechnologies.server.service.streamzine.asm;


import mobi.nowtechnologies.server.domain.streamzine.TypesMappingInfoItem;
import mobi.nowtechnologies.server.dto.streamzine.mapping.ContentWithSubTypesMappingDto;
import mobi.nowtechnologies.server.dto.streamzine.mapping.ShapeTypeMappingDto;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.ContentType;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.*;

public class TypesMappingAsm {
    private MessageSource messageSource;

    public List<ShapeTypeMappingDto> toDtos(Collection<TypesMappingInfoItem> typesMappingInfoItems) {
        List<ShapeTypeMappingDto> dtos = new ArrayList<ShapeTypeMappingDto>();

        for (TypesMappingInfoItem info : typesMappingInfoItems) {
            dtos.add(createDto(info));
        }

        return dtos;
    }

    private ShapeTypeMappingDto createDto(TypesMappingInfoItem info) {
        ShapeTypeMappingDto dto = new ShapeTypeMappingDto();
        dto.setShapeType(info.getShapeType());
        for (Map.Entry<ContentType, List<Enum<?>>> contentTypeListEntry : info.getTypeWithSubtypes().entrySet()) {
            dto.getDtos().add(createContentTypeDto(contentTypeListEntry));
        }
        return dto;
    }

    private ContentWithSubTypesMappingDto createContentTypeDto(Map.Entry<ContentType, List<Enum<?>>> contentTypeListEntry) {
        ContentType contentType = contentTypeListEntry.getKey();

        ContentWithSubTypesMappingDto dto = new ContentWithSubTypesMappingDto(contentType);
        dto.setTitle(messageSource.getMessage("streamzine.contenttypes." + contentType.name(), null, getLocale()));

        for (Enum<?> anEnum : contentTypeListEntry.getValue()) {
            String title = messageSource.getMessage("streamzine.contenttype." + contentType.name() + "." + anEnum.name(), null, getLocale());
            dto.getSubTypes().put(anEnum.name(), title);
        }

        return dto;
    }

    private Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
