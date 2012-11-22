package mobi.nowtechnologies.server.shared.dto;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Arrays;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
@XmlRootElement(name="news")
public class NewsDto {
	
	private NewsDetailDto[] newsDetailDtos;

	@XmlAnyElement
	public NewsDetailDto[] getNewsDetailDtos() {
		return newsDetailDtos;
	}

	public void setNewsDetailDtos(NewsDetailDto[] newsDetailDtos) {
		this.newsDetailDtos = newsDetailDtos;
	}

	@Override
	public String toString() {
		return "NewsDto [newsDetailDtos=" + Arrays.toString(newsDetailDtos) + "]";
	} 
	
	
}
