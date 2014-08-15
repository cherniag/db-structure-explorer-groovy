package mobi.nowtechnologies.server.dto.streamzine;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import mobi.nowtechnologies.server.persistence.domain.streamzine.rules.DeeplinkInfoData;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.ContentType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.HasVip;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Comparator;

public class OrdinalBlockDto extends BlockDto implements DeeplinkInfoData, HasVip {
    public static final Comparator<OrdinalBlockDto> COMPARATOR = new Comparator<OrdinalBlockDto>() {
        @Override
        public int compare(OrdinalBlockDto o1, OrdinalBlockDto o2) {
            return o1.getPosition() - o2.getPosition();
        }
    };

    @JsonIgnore
    @JsonProperty(value = "id")
    private String id;

    @JsonProperty(value = "title")
    private String title;

    @JsonProperty(value = "subTitle")
    private String subTitle;

    @JsonProperty(value = "coverUrl")
    @NotEmpty
    private String coverUrl;

    @JsonProperty(value = "position")
    private int position;

    @JsonProperty(value = "contentType")
    private ContentType contentType;

    @JsonProperty(value = "key")
    private String key;

    @JsonProperty(value = "value")
    private String value;

    @JsonProperty(value = "data")
    private Object data;

    @JsonProperty(value = "vip")
    private boolean vip;

    @JsonProperty(value = "expanded")
    private boolean expanded;

    private String contentTypeTitle;

    @JsonProperty(value = "badgeUrl")
    private String badgeUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    @Override
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    @Override
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void setContentTypeTitle(String contentTypeTitle) {
        this.contentTypeTitle = contentTypeTitle;
    }

    public String getContentTypeTitle() {
        return contentTypeTitle;
    }

    public String provideKeyString() {
        if(key == null) {
            return "";
        }
        return key.trim();
    }

    public String provideValueString() {
        if(value == null) {
            return "";
        }
        return value.trim();
    }

    @Override
    public boolean isVip() {
        return vip;
    }

    public void setVip(boolean vip) {
        this.vip = vip;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public String getBadgeUrl() {
        return badgeUrl;
    }

    public void setBadgeUrl(String badgeUrl) {
        this.badgeUrl = badgeUrl;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("title", title)
                .append("subTitle", subTitle)
                .append("coverUrl", coverUrl)
                .append("position", position)
                .append("contentType", contentType)
                .append("key", key)
                .append("value", value)
                .append("data", data)
                .append("vip", vip)
                .append("expanded", expanded)
                .append("contentTypeTitle", contentTypeTitle)
                .append("badgeUrl", badgeUrl)
                .toString();
    }
}
