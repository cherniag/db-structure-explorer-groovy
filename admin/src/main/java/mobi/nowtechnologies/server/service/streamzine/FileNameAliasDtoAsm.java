package mobi.nowtechnologies.server.service.streamzine;

import mobi.nowtechnologies.server.dto.streamzine.FileNameAliasDto;
import mobi.nowtechnologies.server.persistence.domain.streamzine.FilenameAlias;

import java.util.ArrayList;
import java.util.List;

public class FileNameAliasDtoAsm {
    public List<FileNameAliasDto> convertMany(List<FilenameAlias> filenameAliases) {
        List<FileNameAliasDto> converted = new ArrayList<FileNameAliasDto>();
        for (FilenameAlias filenameAlias : filenameAliases) {
            converted.add(convertOne(filenameAlias));
        }
        return converted;
    }

    public FileNameAliasDto convertOne(FilenameAlias filenameAlias) {
        FileNameAliasDto converted = new FileNameAliasDto();
        converted.setAlias(filenameAlias.getAlias());
        converted.setFileName(filenameAlias.getFileName());
        return converted;
    }
}
