package mobi.nowtechnologies.server.persistence.domain;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The persistent class for the tb_files database table.
 * 
 */
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

	@Override
	public String toString() {
		return "MediaFile [fileTypeId=" + fileTypeId + ", filename=" + filename + ", i=" + i + ", size=" + size + ", version=" + version + "]";
	}

}