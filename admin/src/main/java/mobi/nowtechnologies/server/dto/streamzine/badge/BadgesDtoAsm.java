package mobi.nowtechnologies.server.dto.streamzine.badge;

import mobi.nowtechnologies.server.dto.streamzine.FileNameAliasDto;
import mobi.nowtechnologies.server.persistence.domain.streamzine.FilenameAlias;
import mobi.nowtechnologies.server.persistence.domain.streamzine.badge.BadgeMapping;
import mobi.nowtechnologies.server.persistence.domain.streamzine.badge.Resolution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BadgesDtoAsm {

    public Map<Long, Map<Long, BadgeMappingDto>> convert(Map<Resolution, Map<Long, BadgeMapping>> matrix) {
        Map<Long, Map<Long, BadgeMappingDto>> dtos = new HashMap<Long, Map<Long, BadgeMappingDto>>();

        for (Map.Entry<Resolution, Map<Long, BadgeMapping>> entry : matrix.entrySet()) {
            dtos.put(entry.getKey().getId(), toBadgeMappingDtos(entry.getValue()));
        }

        return dtos;
    }

    public List<BadgeMappingDto> toBadgeMappingListDtos(List<BadgeMapping> list) {
        List<BadgeMappingDto> dtos = new ArrayList<BadgeMappingDto>();

        for (BadgeMapping mapping : list) {
            dtos.add(BadgeMappingDto.from(mapping));
        }

        return dtos;
    }

    public Map<Long, BadgeMappingDto> toBadgeMappingDtos(Map<Long, BadgeMapping> mapping) {
        Map<Long, BadgeMappingDto> dtos = new HashMap<Long, BadgeMappingDto>();

        for (Map.Entry<Long, BadgeMapping> entry : mapping.entrySet()) {
            dtos.put(entry.getKey(), BadgeMappingDto.from(entry.getValue()));
        }

        return dtos;
    }

    public List<ResolutionDto> toResolutionDtos(List<Resolution> domains) {
        List<ResolutionDto> dtos = new ArrayList<ResolutionDto>();
        for (Resolution domain : domains) {
            dtos.add(ResolutionDto.from(domain));
        }
        return dtos;
    }

    public Resolution toResolution(ResolutionDto dto) {
        return new Resolution(dto.getDeviceType(), dto.getWidth(), dto.getHeight());
    }

    public List<FileNameAliasDto> toFilenameDtos(List<FilenameAlias> fileNames) {
        List<FileNameAliasDto> dtos = new ArrayList<FileNameAliasDto>();
        for (FilenameAlias fileName : fileNames) {
            FileNameAliasDto dto = new FileNameAliasDto();
            dto.setId(fileName.getId());
            dto.setFileName(fileName.getFileName());
            dto.setAlias(fileName.getAlias());
            dtos.add(dto);
        }
        return dtos;
    }
}
