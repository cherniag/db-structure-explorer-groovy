package mobi.nowtechnologies.server.persistence.domain;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the tb_newsUpdates database table.
 * 
 */
@Entity
@Table(name="tb_newsUpdates")
public class NewsUpdate implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int i;

	private int news;

	private int timestamp;

    public NewsUpdate() {
    }

	public int getI() {
		return this.i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public int getNews() {
		return this.news;
	}

	public void setNews(int news) {
		this.news = news;
	}

	public int getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

}