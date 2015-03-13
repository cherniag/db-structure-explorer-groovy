package mobi.nowtechnologies.server.shared.dto;


import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Titov Mykhaylo (titov)
 */
@XmlRootElement(name = "news")
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
