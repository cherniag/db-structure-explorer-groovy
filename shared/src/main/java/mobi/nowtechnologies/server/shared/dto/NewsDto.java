package mobi.nowtechnologies.server.shared.dto;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeName;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Arrays;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
@XmlRootElement(name="news")
@JsonTypeName("news")
public class NewsDto {

    @JsonProperty("items")
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
