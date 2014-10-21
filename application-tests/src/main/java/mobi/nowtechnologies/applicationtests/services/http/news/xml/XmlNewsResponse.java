package mobi.nowtechnologies.applicationtests.services.http.news.xml;

import mobi.nowtechnologies.applicationtests.services.http.common.UserInResponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlNewsResponse {
    private XmlNewsDto news;

    private UserInResponse user;

    public XmlNewsDto getNews() {
        return news;
    }

    public UserInResponse getUser() {
        return user;
    }
}
