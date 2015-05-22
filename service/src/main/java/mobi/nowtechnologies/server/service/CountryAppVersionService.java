package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.repository.AppVersionRepository;
import mobi.nowtechnologies.server.service.exception.ServiceException;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Titov Mykhaylo (titov)
 */
public class CountryAppVersionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CountryAppVersionService.class);

    @Resource
    AppVersionRepository appVersionRepository;


    public boolean isAppVersionLinkedWithCountry(String appVersion, String countryCode) {
        if (appVersion == null) {
            throw new ServiceException("The parameter appVersion is null");
        }
        if (countryCode == null) {
            throw new ServiceException("The parameter countryCode is null");
        }

        long count = appVersionRepository.countAppVersionLinkedWithCountry(appVersion, countryCode);

        if (count == 1) {
            return true;
        }

        if (count > 1) {
            LOGGER.error( "There are " + count + " records found for appVersion = " + appVersion + ", countryFullName = " + countryCode);
        }

        return false;
    }
}
