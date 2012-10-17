package mobi.nowtechnologies.server.persistence.domain;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the tb_chartUpdateDetail database table.
 * 
 */
@Entity
@Table(name="tb_chartUpdateDetail")
public class ChartUpdateDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int i;

	private int chartUpdate;

	private int media;

	private byte position;

    public ChartUpdateDetail() {
    }

	public int getI() {
		return this.i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public int getChartUpdate() {
		return this.chartUpdate;
	}

	public void setChartUpdate(int chartUpdate) {
		this.chartUpdate = chartUpdate;
	}

	public int getMedia() {
		return this.media;
	}

	public void setMedia(int media) {
		this.media = media;
	}

	public byte getPosition() {
		return this.position;
	}

	public void setPosition(byte position) {
		this.position = position;
	}

}