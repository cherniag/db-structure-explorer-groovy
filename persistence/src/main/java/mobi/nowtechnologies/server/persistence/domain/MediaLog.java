package mobi.nowtechnologies.server.persistence.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * @author Alexander Kolpakov (akolpakov)
 */
@Entity
@Table(name = "tb_mediaLog")
public class MediaLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int i;
    private int logTimestamp;
    private byte logType;
    @Column(name = "mediaUID", insertable = false, updatable = false)
    private int mediaUID;
    private int userUID;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mediaUID")
    @Fetch(FetchMode.JOIN)
    private Media media;
    @Transient
    private boolean alreadyDownloadedOriginal;

    public MediaLog() {
    }

    public int getI() {
        return this.i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public int getLogTimestamp() {
        return this.logTimestamp;
    }

    public void setLogTimestamp(int logTimestamp) {
        this.logTimestamp = logTimestamp;
    }

    public byte getLogType() {
        return this.logType;
    }

    public void setLogType(byte logType) {
        this.logType = logType;
    }

    public int getMediaUID() {
        return this.mediaUID;
    }

    public int getUserUID() {
        return this.userUID;
    }

    public void setUserUID(int userUID) {
        this.userUID = userUID;
    }

    public boolean isAlreadyDownloadedOriginal() {
        return alreadyDownloadedOriginal;
    }

    public void setAlreadyDownloadedOriginal(boolean alreadyDownloadedOriginal) {
        this.alreadyDownloadedOriginal = alreadyDownloadedOriginal;
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
        this.mediaUID = media.getI();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("i", i).append("logTimestamp", logTimestamp).append("logType", logType).append("mediaUID", mediaUID).append("userUID", userUID)
                                        .append("alreadyDownloadedOriginal", alreadyDownloadedOriginal).toString();
    }


}