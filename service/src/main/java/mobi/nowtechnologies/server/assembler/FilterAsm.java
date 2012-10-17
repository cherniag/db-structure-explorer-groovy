/**
 * 
 */
package mobi.nowtechnologies.server.assembler;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import mobi.nowtechnologies.server.persistence.domain.AbstractFilterWithCtiteria;
import mobi.nowtechnologies.server.shared.dto.admin.FilterDto;

/**
 * Converts AbstractFilterWithCtiteria to FilterDto and vise versa
 * @author Mayboroda Dmytro
 *
 */
public class FilterAsm {
	
	public static Set<FilterDto> toDtos(Collection<AbstractFilterWithCtiteria> filterWithCtiteria) {
		Set<FilterDto> filterDtos = new HashSet<FilterDto>();
		
		if (null == filterWithCtiteria)
			return filterDtos;
		
		for (AbstractFilterWithCtiteria abstractFilterWithCtiteria : filterWithCtiteria) {
			filterDtos.add(toDto(abstractFilterWithCtiteria));
		}
		return filterDtos;
	}
	
	public static FilterDto toDto(AbstractFilterWithCtiteria filter) {
		FilterDto filterDto = new FilterDto();
			filterDto.setName(filter.getName());
		return filterDto;
	}
}