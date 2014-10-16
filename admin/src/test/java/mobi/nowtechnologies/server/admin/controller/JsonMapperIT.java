package mobi.nowtechnologies.server.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mobi.nowtechnologies.server.shared.dto.admin.ChartItemDto;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Oleg Artomov on 10/3/2014.
 */
public class JsonMapperIT extends AbstractAdminITTest {

    @Resource
    private ObjectMapper objectMapper;

    @Value("classpath:chartItem.json")
    private org.springframework.core.io.Resource resource;

    @Test
    public void testDateMappingForChartDetails() throws IOException, ParseException {
        String content = IOUtils.toString(resource.getInputStream());
        ChartItemDto dto = objectMapper.readValue(content, ChartItemDto.class);
        Date publishDate = dto.getPublishTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(publishDate);
        assertEquals(13, calendar.get(Calendar.HOUR_OF_DAY));
    }

}
