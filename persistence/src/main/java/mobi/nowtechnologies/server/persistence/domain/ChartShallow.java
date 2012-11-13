/**
 * 
 */
package mobi.nowtechnologies.server.persistence.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Titov Mykhaylo (titov)
 *
 */

@Entity
@Table(name="tb_charts")
public class ChartShallow {
	private byte id;
	private byte numTracks;
	private int timestamp;
	private byte community;
	
    @Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="i")
	public byte getId() {
		return this.id;
	}

	public void setId(byte id) {
		this.id = id;
	}
	
	public byte getNumTracks() {
		return this.numTracks;
	}

	public void setNumTracks(byte numTracks) {
		this.numTracks = numTracks;
	}

	public int getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public byte getCommunity() {
		return community;
	}

	public void setCommunity(byte community) {
		this.community = community;
	}

}
