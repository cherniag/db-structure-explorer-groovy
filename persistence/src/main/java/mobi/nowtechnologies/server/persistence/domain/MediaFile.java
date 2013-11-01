package mobi.nowtechnologies.server.persistence.domain;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import javax.persistence.*;

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
	private int version;

	public MediaFile() {
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

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("i", i)
                .append("filename", filename)
                .append("fileTypeId", fileTypeId)
                .append("size", size)
                .append("duration", duration)
                .append("version", version)
                .toString();
    }


}