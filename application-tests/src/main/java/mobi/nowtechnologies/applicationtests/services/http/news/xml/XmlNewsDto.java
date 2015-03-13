package mobi.nowtechnologies.applicationtests.services.http.news.xml;


import mobi.nowtechnologies.server.shared.dto.NewsDetailDto;

import java.util.Arrays;

public class XmlNewsDto {

    public NewsDetailDto[] item;

    public NewsDetailDto[] getNewsDetailDtos() {
        return item;
    }

    @Override
    public String toString() {
        return "NewsDto [newsDetailDtos=" + Arrays.toString(item) + "]";
    }
}

