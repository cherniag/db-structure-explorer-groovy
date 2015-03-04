package mobi.nowtechnologies.server.trackrepo.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import static javax.persistence.EnumType.ORDINAL;
import static javax.persistence.InheritanceType.JOINED;

import org.apache.commons.lang3.builder.ToStringBuilder;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/**
 * @author Alexander Kolpakov (akolpakov)
 */
@Entity
@Inheritance(strategy = JOINED)
public class AssetFile extends AbstractEntity {

    @Enumerated(ORDINAL)
    protected FileType type;
    @Column(nullable = false)
    protected String path;
    @Column(name = "MD5")
    protected String md5;
    protected Integer duration;
    @Column(name = "external_id")
    protected String externalId;

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public FileType getType() {
        return type;
    }

    public void setType(FileType type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("type", type).append("path", path).append("md5", md5).append("duration", duration)
                                                            .append("externalId", externalId).toString();
    }

    public enum FileType {MOBILE, DOWNLOAD, IMAGE, PREVIEW, VIDEO;}
}