package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.dao.CountryAppVersionDao;
import mobi.nowtechnologies.server.service.exception.ServiceException;

/**
 * @author Titov Mykhaylo (titov)
 */
public class CountryAppVersionService {

    private CountryAppVersionDao countryAppVersionDao;

    public void setCountryAppVersionDao(CountryAppVersionDao countryAppVersionDao) {
        this.countryAppVersionDao = countryAppVersionDao;
    }

    public boolean isAppVersionLinkedWithCountry(String appVersion, String countryCode) {
        if (appVersion == null) {
            throw new ServiceException("The parameter appVersion is null");
        }
        if (countryCode == null) {
            throw new ServiceException("The parameter countryCode is null");
        }
        return countryAppVersionDao.isAppVersionLinkedWithCountry(appVersion, countryCode);
    }
}
