package mobi.nowtechnologies.server.dto.streamzine;

import mobi.nowtechnologies.server.assembler.streamzine.DeepLinkInfoService;
import mobi.nowtechnologies.server.assembler.streamzine.DeepLinkInfoService.PlaylistData;
import mobi.nowtechnologies.server.assembler.streamzine.DeepLinkInfoService.TrackData;
import mobi.nowtechnologies.server.persistence.domain.streamzine.rules.DeeplinkInfoData;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.ContentType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.HasVip;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.LinkLocationType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.MusicType;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNull;

import java.util.Comparator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

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

    @JsonProperty(value = "badgeId")
    private Long badgeId;

    @JsonIgnore
    private FileNameAliasDto badgeFileNameAlias;

    private DeepLinkInfoService.ApplicationPageData applicationPageData;

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

    // for the ui json (themyleaf)
    public String getContentTypeTitle() {
        return contentTypeTitle;
    }

    public void setContentTypeTitle(String contentTypeTitle) {
        this.contentTypeTitle = contentTypeTitle;
    }

    public String provideKeyString() {
        return trimToEmpty(key);
    }

    public String provideValueString() {
        return trimToEmpty(value);
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

    public Long getBadgeId() {
        return badgeId;
    }

    // for the ui json (themyleaf)
    public FileNameAliasDto getBadgeFileNameAlias() {
        return badgeFileNameAlias;
    }

    public void setBadgeFileNameAlias(FileNameAliasDto badgeFileNameAlias) {
        this.badgeFileNameAlias = badgeFileNameAlias;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("title", title).append("subTitle", subTitle).append("coverUrl", coverUrl).append("position", position)
                                        .append("contentType", contentType).append("key", key).append("value", value).append("data", data).append("vip", vip).append("expanded", expanded)
                                        .append("contentTypeTitle", contentTypeTitle).append("badgeId", badgeId).toString();
    }


    private DeepLinkInfoService.ApplicationPageData getApplicationPageData() {
        if (isNull(applicationPageData)) {
            applicationPageData = new DeepLinkInfoService.ApplicationPageData(defaultString(value));
        }
        return applicationPageData;
    }

    @JsonIgnore
    public String getValueOpener() {
        if (key.equals(LinkLocationType.EXTERNAL_AD.name()) || key.equals(LinkLocationType.INTERNAL_AD.name())) {
            return getApplicationPageData().getAction();
        }
        return null;
    }

    @JsonIgnore
    public String getValuePlayerType() {
        if (key.equals(MusicType.PLAYLIST.name())) {
            return new PlaylistData(value).getPlayerTypeString();
        } else if (key.equals(MusicType.TRACK.name())) {
            return new TrackData(value).getPlayerTypeString();
        }
        return null;
    }

    @JsonIgnore
    public String getValueLink() {
        return getApplicationPageData().getUrl();
    }
}
