package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.persistence.domain.streamzine.FilenameAlias;
import mobi.nowtechnologies.server.persistence.domain.streamzine.badge.BadgeMapping;

import static org.mockito.Mockito.*;

public class BadgeMappingFactory {

    public static BadgeMapping general(String fileName) {
        FilenameAlias alias = mock(FilenameAlias.class);
        when(alias.getFileName()).thenReturn(fileName);

        BadgeMapping m = mock(BadgeMapping.class);
        when(m.getFilenameAlias()).thenReturn(alias);
        when(m.getOriginalFilenameAlias()).thenReturn(alias);

        return m;
    }


    public static BadgeMapping specific(String generalFileName, String specificFileName) {
        FilenameAlias general = mock(FilenameAlias.class);
        when(general.getFileName()).thenReturn(generalFileName);

        FilenameAlias specific = mock(FilenameAlias.class);
        when(specific.getFileName()).thenReturn(specificFileName);

        BadgeMapping m = mock(BadgeMapping.class);
        when(m.getOriginalFilenameAlias()).thenReturn(general);
        when(m.getFilenameAlias()).thenReturn(specific);

        return m;
    }
}
