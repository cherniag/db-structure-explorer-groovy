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
@Table(name="tb_news")
public class NewsShallow {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="i")
	private byte id;
	private byte numEntries;
	private int timestamp;
	private byte community;
	
	public byte getId() {
		return this.id;
	}

	public void setI(byte id) {
		this.id = id;
	}
	
	public byte getNumEntries() {
		return this.numEntries;
	}

	public void setNumEntries(byte numEntries) {
		this.numEntries = numEntries;
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
