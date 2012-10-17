package mobi.nowtechnologies.server.persistence.domain;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the tb_newsUpdateDetail database table.
 * 
 */
@Entity
@Table(name="tb_newsUpdateDetail")
public class NewsUpdateDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int i;

	@Column(name="item",columnDefinition="char(255)")
	private String item;

	private int newsUpdate;

	private byte position;

    public NewsUpdateDetail() {
    }

	public int getI() {
		return this.i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public String getItem() {
		return this.item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public int getNewsUpdate() {
		return this.newsUpdate;
	}

	public void setNewsUpdate(int newsUpdate) {
		this.newsUpdate = newsUpdate;
	}

	public byte getPosition() {
		return this.position;
	}

	public void setPosition(byte position) {
		this.position = position;
	}

}