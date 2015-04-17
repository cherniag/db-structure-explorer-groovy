package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.Country;
import mobi.nowtechnologies.server.persistence.repository.CountryRepository;
import mobi.nowtechnologies.server.service.exception.ServiceException;

import javax.annotation.Resource;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Titov Mykhaylo (titov)
 */
public class CountryService {

    private Map<String, Country> countryMapFullNameAsKey;

    @Resource
    CountryRepository countryRepository;

    public Map<String, Country> getCountryMapFullNameAsKey() {
        if (countryMapFullNameAsKey == null) {
            List<Country> countryList = getAllCountries();
            Map<String, Country> countryMapFullName = new HashMap<String, Country>();
            for (Country country : countryList) {
                countryMapFullName.put(country.getFullName(), country);
            }
            countryMapFullNameAsKey = Collections.unmodifiableMap(countryMapFullName);
        }
        return countryMapFullNameAsKey;
    }

    public Integer findIdByFullName(String countryFullName) {
        if (countryFullName == null) {
            throw new ServiceException("The parameter countryFullName is null");
        }
        Country country = getCountryMapFullNameAsKey().get(countryFullName);
        if (country == null) {
            throw new ServiceException("Couldn't find country with countryFullName=" + countryFullName);
        } else {
            return country.getI();
        }
    }

    public List<Country> getAllCountries() {
        return countryRepository.findAll();
    }

}
