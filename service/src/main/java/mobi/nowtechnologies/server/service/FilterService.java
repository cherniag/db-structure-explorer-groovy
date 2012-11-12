package mobi.nowtechnologies.server.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mobi.nowtechnologies.server.assembler.FilterAsm;
import mobi.nowtechnologies.server.persistence.domain.AbstractFilterWithCtiteria;
import mobi.nowtechnologies.server.persistence.repository.FilterWithCriteriaRepository;
import mobi.nowtechnologies.server.shared.dto.admin.FilterDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class FilterService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FilterService.class);
	
	private FilterWithCriteriaRepository filterWithCriteriaRepository;
	
	public void setFilterWithCriteriaRepository(FilterWithCriteriaRepository filterWithCriteriaRepository) {
		this.filterWithCriteriaRepository = filterWithCriteriaRepository;
	}
	
	@Transactional(readOnly=true)
	public Set<FilterDto> getAllFilters() {
		LOGGER.info("Getting all available filters");
		Set<FilterDto> filterDtos=FilterAsm.toDtos(filterWithCriteriaRepository.findAll());
		LOGGER.debug("Done getting all available filters");
		return filterDtos;
	}

	@Transactional(readOnly=true)
	public Set<AbstractFilterWithCtiteria> find(Set<FilterDto> filterDtos) {
		LOGGER.debug("input parameters filterDtos: [{}]", filterDtos);
		
		if(filterDtos == null || filterDtos.isEmpty())
			return new HashSet<AbstractFilterWithCtiteria>();
		
		List<String> names = new ArrayList<String>();
		for (FilterDto filterDto : filterDtos) {
			names.add(filterDto.getName());
		}
		
		List<AbstractFilterWithCtiteria> abstractFilterWithCtiterias = filterWithCriteriaRepository.findByNames(names);
		Set<AbstractFilterWithCtiteria> abstractFilterWithCtiteriaSet= new HashSet<AbstractFilterWithCtiteria>(abstractFilterWithCtiterias);
		
		LOGGER.debug("Output parameter abstractFilterWithCtiteriaSet=[{}]", abstractFilterWithCtiteriaSet);
		return abstractFilterWithCtiteriaSet;
	}

}
