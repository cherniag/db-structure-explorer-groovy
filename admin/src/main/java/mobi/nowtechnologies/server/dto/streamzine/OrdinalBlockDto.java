package mobi.nowtechnologies.server.dto.streamzine;

import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.ContentType;
import mobi.nowtechnologies.server.service.streamzine.DeepLinkInfoService;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Comparator;

public class OrdinalBlockDto extends BlockDto implements DeepLinkInfoService.DeeplinkInfoData, HasVip {
    public static final Comparator<OrdinalBlockDto> COMPARATOR = new Comparator<OrdinalBlockDto>() {
        @Override
        public int compare(OrdinalBlockDto o1, OrdinalBlockDto o2) {
            return o1.getPosition() - o2.getPosition();
        }
    };

    @JsonIgnore
    @JsonProperty(value = "title")
    private String id;

    @NotEmpty
    @JsonProperty(value = "title")
    private String title;

    @JsonProperty(value = "subTitle")
    @NotEmpty
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
        return value.toString().trim();
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

    @Override
    public String toString() {
        return "OrdinalBlockDto{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", coverUrl='" + coverUrl + '\'' +
                ", badgeUrl='" + badgeUrl + '\'' +
                ", position=" + position +
                ", contentType=" + contentType +
                ", key='" + key + '\'' +
                ", value=" + value +
                ", data=" + data +
                ", contentTypeTitle='" + contentTypeTitle + '\'' +
                '}';
    }

    public void setBadgeUrl(String badgeUrl) {
        this.badgeUrl = badgeUrl;
    }
}
