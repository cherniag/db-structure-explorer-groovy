package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.Country;
import mobi.nowtechnologies.server.persistence.repository.CountryRepository;

import javax.annotation.Resource;

import java.util.List;

/**
 * @author Titov Mykhaylo (titov)
 */
public class CountryService {
    @Resource
    CountryRepository countryRepository;

    public Country findIdByName(String name) {
        return countryRepository.findByName(name);
    }

    public List<Country> getAllCountries() {
        return countryRepository.findAll();
    }

}
