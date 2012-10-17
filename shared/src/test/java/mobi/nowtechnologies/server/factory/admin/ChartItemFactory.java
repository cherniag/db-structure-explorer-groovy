/**
 * 
 */
package mobi.nowtechnologies.server.factory.admin;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import mobi.nowtechnologies.server.shared.dto.admin.ChartItemDto;
import mobi.nowtechnologies.server.shared.enums.ChgPosition;

/**
 * @author Mayboroda Dmytro
 *
 */
public class ChartItemFactory {
	
	public static ChartItemDto anyChartItemDto(Byte chartId, Date publishDate) {
		ChartItemDto expectedItemDto = new ChartItemDto();
		expectedItemDto.setId(Integer.MAX_VALUE);
		expectedItemDto.setChartId(chartId);
		expectedItemDto.setChannel("Some channel");
		expectedItemDto.setChgPosition(ChgPosition.NONE);
		expectedItemDto.setInfo("Some info");
		expectedItemDto.setPosition((byte)1);
		expectedItemDto.setPrevPosition((byte)0);
		expectedItemDto.setPublishTime(publishDate);
		expectedItemDto.setMediaDto(MediaFactory.anyMediaDto());
		
		return expectedItemDto;
	}
	
	public static String anyChartItemJSON(Byte chartId, Date publishDate) {
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd_HH:mm:ss").create();
		Type type = new TypeToken<ChartItemDto>(){}.getType();
		
		return gson.toJson(anyChartItemDto(chartId, publishDate), type);
	}
	
	public static String anyChartItemListJSON(int amount, Byte chartId, Date publishDate) {
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd_HH:mm:ss").create();
		Type type = new TypeToken<ArrayList<ChartItemDto>>(){}.getType();
		
		return gson.toJson(getChartItemDtos(amount, chartId, publishDate), type);
	}
	
	/**
	 * @return
	 */
	public static List<ChartItemDto> getChartItemDtos(int amount, Byte chartId, Date publishDate) {
		List<ChartItemDto> items = new ArrayList<ChartItemDto>();
		for (int i=0; i<amount; i++)
			items.add(anyChartItemDto(chartId, publishDate));
		return items;
	}
	
	public static List<ChartItemDto> getChartItemDtos(ChartItemDto item, int amount) {
		List<ChartItemDto> items = new ArrayList<ChartItemDto>();
		for (int i=0; i<amount; i++)
			items.add(item);
		return items;
	}

	/**
	 * @return
	 */
	public static List<ChartItemDto> getEmptyChartItemDtos() {
		return new ArrayList<ChartItemDto>();
	}
}
