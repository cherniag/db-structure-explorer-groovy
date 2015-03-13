package mobi.nowtechnologies.server.dto.streamzine.badge;

import mobi.nowtechnologies.server.persistence.domain.streamzine.badge.BadgeMapping;

public class BadgeMappingDto {

    private long originalId;
    private String title;
    private String fileName;
    private int width;
    private int height;

    public static BadgeMappingDto from(BadgeMapping badgeMapping) {
        BadgeMappingDto dto = new BadgeMappingDto();

        if (badgeMapping != null) {
            dto.originalId = badgeMapping.getOriginalFilenameAlias().getId();
            if (badgeMapping.getFilenameAlias() != null) {
                dto.title = badgeMapping.getFilenameAlias().getAlias();
                dto.fileName = badgeMapping.getFilenameAlias().getFileName();
                dto.width = badgeMapping.getFilenameAlias().getWidth();
                dto.height = badgeMapping.getFilenameAlias().getHeight();
            }
        }

        return dto;
    }

    public String getFileName() {
        return fileName;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getTitle() {
        return title;
    }

    public long getOriginalId() {
        return originalId;
    }
}
