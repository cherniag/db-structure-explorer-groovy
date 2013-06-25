package mobi.nowtechnologies.server.persistence.domain;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the tb_files database table.
 * 
 */
@Entity
@Table(name="tb_files")
public class MediaFile extends CNAbstractEntity implements Serializable {
	private static final long serialVersionUID = 1L;


	@Column(name="filename",columnDefinition="char(40)")
	private String filename;

	private byte fileType;

	private int size;

    public MediaFile() {
    }


	public String getFilename() {
		return this.filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public byte getFileType() {
		return this.fileType;
	}

	public void setFileType(byte fileType) {
		this.fileType = fileType;
	}

	public int getSize() {
		return this.size;
	}

	public void setSize(int size) {
		this.size = size;
	}

}