package mobi.nowtechnologies.server.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobi.nowtechnologies.server.persistence.dao.EntityDao;
import mobi.nowtechnologies.server.persistence.domain.Country;
import mobi.nowtechnologies.server.service.exception.ServiceException;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class CountryService {
	private Map<String,Country> countryMapFullNameAsKey;
	
	private EntityDao entityDao;

	public void setEntityDao(EntityDao entityDao) {
		this.entityDao = entityDao;
	}
	
	public Map<String, Country> getCountryMapFullNameAsKey() {
		if (countryMapFullNameAsKey == null) {
			List<Country> countryList = entityDao.findAll(Country.class);
			Map<String,Country> countryMapFullName = new HashMap<String, Country>();
			for (Country country : countryList) {
				countryMapFullName.put(country.getFullName(), country);
			}
			countryMapFullNameAsKey = 
				Collections.unmodifiableMap(countryMapFullName);
		}
		return countryMapFullNameAsKey;
	}

	public Integer findIdByFullName(String countryFullName) {
		if (countryFullName == null)
			throw new ServiceException("The parameter countryFullName is null");
		Country country = getCountryMapFullNameAsKey().get(
				countryFullName);
		if (country == null)
			throw new ServiceException(
					"Couldn't find country with countryFullName="
							+ countryFullName);
		else
			return country.getI();
	}

	public List<Country> getAllCountries() {
		return entityDao.findAll(Country.class);
	}

}
