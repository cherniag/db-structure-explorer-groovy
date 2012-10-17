package mobi.nowtechnologies.server.persistence.domain;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the tb_support database table.
 * 
 */
@Entity
@Table(name="tb_support")
public class Support implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int i;

	@Column(name="message",columnDefinition="text")
    @Lob()
	private String message;

	private byte status;

	@Column(name="subject",columnDefinition="char(50)")
	private String subject;

	private int timestamp;

	private int user;

    public Support() {
    }

	public int getI() {
		return this.i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public byte getStatus() {
		return this.status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public String getSubject() {
		return this.subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public int getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public int getUser() {
		return this.user;
	}

	public void setUser(int user) {
		this.user = user;
	}

}