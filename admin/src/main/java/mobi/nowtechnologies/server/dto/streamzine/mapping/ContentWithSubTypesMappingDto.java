package mobi.nowtechnologies.server.dto.streamzine.mapping;

import mobi.nowtechnologies.server.persistence.domain.streamzine.types.ContentType;

import java.util.HashMap;
import java.util.Map;

public class ContentWithSubTypesMappingDto {
    private String contentType;
    private String title;

    private Map<String, String> subTypes = new HashMap<String, String>();

    public ContentWithSubTypesMappingDto(ContentType contentType) {
        this.contentType = contentType.name();
    }

    public String getContentType() {
        return contentType;
    }

    public Map<String, String> getSubTypes() {
        return subTypes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "ContentWithSubTypesMappingDto{" +
                "contentType='" + contentType + '\'' +
                ", title='" + title + '\'' +
                ", subTypes=" + subTypes +
                '}';
    }
}
