/**
 *
 */

package mobi.nowtechnologies.server.factory.admin;

import mobi.nowtechnologies.server.shared.dto.admin.ChartItemDto;
import mobi.nowtechnologies.server.shared.enums.ChgPosition;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Mayboroda Dmytro
 */
public class ChartItemFactory {

    public static ChartItemDto anyChartItemDto(Integer chartId, Date publishDate) {
        ChartItemDto expectedItemDto = new ChartItemDto();
        expectedItemDto.setId(Integer.MAX_VALUE);
        expectedItemDto.setChartId(chartId);
        expectedItemDto.setChannel("Some channel");
        expectedItemDto.setChgPosition(ChgPosition.NONE);
        expectedItemDto.setInfo("Some info");
        expectedItemDto.setPosition((byte) 1);
        expectedItemDto.setPrevPosition((byte) 0);
        expectedItemDto.setPublishTime(publishDate);
        expectedItemDto.setMediaDto(MediaFactory.anyMediaDto());

        return expectedItemDto;
    }

    /**
     * @return
     */
    public static List<ChartItemDto> getChartItemDtos(int amount, Integer chartId, Date publishDate) {
        List<ChartItemDto> items = new ArrayList<ChartItemDto>();
        for (int i = 0; i < amount; i++) {
            items.add(anyChartItemDto(chartId, publishDate));
        }
        return items;
    }

}
