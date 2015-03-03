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
import javax.persistence.Version;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@Table(name = "tb_files")
public class MediaFile implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer i;

    @Column(name = "filename", columnDefinition = "char(40)")
    private String filename;

    @Column(name = "fileType", insertable = false, updatable = false)
    private byte fileTypeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fileType")
    private FileType fileType;

    private int size;

    private int duration;

    @Version
    private Integer version;

    public MediaFile() {
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getI() {
        return this.i;
    }

    public void setI(Integer i) {
        this.i = i;
    }

    public String getFilename() {
        return this.filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
        fileTypeId = fileType.getI();
    }

    public byte getFileTypeId() {
        return fileTypeId;
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Deprecated
    public int getVersionAsPrimitive() {
        return version != null ?
               version :
               0;
    }

    @Deprecated
    public void setVersionAsPrimitive(int version) {
        this.version = version;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public MediaFile withFileType(FileType fileType) {
        setFileType(fileType);
        return this;
    }

    public MediaFile withFileName(String fileName) {
        setFilename(fileName);
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("i", i).append("filename", filename).append("fileTypeId", fileTypeId).append("size", size).append("duration", duration).append("version", version)
                                        .toString();
    }


}