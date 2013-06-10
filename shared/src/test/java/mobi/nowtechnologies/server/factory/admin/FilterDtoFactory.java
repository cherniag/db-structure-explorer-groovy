package mobi.nowtechnologies.server.factory.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mobi.nowtechnologies.server.shared.dto.admin.FilterDto;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class FilterDtoFactory {
	
	public static FilterDto create(){
		FilterDto filterDto = new FilterDto();
		
		return filterDto;
	}
	
	public static List<FilterDto> createList(int elementsCount){
		List<FilterDto> filterDtos = new ArrayList<FilterDto>(elementsCount);
		for (int i = 0; i < elementsCount; i++) {
			filterDtos.add(create());
		}
		
		return filterDtos;
	}
	
	public static Set<FilterDto> createSet(int elementsCount){
		Set<FilterDto> filterDtos = new HashSet<FilterDto>(createList(elementsCount));		
		return filterDtos;
	}

}
