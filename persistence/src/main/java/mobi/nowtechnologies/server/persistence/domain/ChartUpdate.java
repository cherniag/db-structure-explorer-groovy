package mobi.nowtechnologies.server.persistence.domain;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the tb_chartUpdates database table.
 * 
 */
@Entity
@Table(name="tb_chartUpdates")
public class ChartUpdate implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="i",columnDefinition="smallint(5) unsigned")
	private int i;

	private byte chart;

	private int timestamp;

    public ChartUpdate() {
    }

	public int getI() {
		return this.i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public byte getChart() {
		return this.chart;
	}

	public void setChart(byte chart) {
		this.chart = chart;
	}

	public int getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

}