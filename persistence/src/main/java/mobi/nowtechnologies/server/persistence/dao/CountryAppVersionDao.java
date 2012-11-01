/**
 * 
 */
package mobi.nowtechnologies.server.persistence.dao;

import mobi.nowtechnologies.server.persistence.domain.AppVersion;
import mobi.nowtechnologies.server.persistence.domain.AppVersionCountry;
import mobi.nowtechnologies.server.persistence.domain.Country;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.support.JpaDaoSupport;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class CountryAppVersionDao extends JpaDaoSupport {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(CountryAppVersionDao.class.getName());

	public boolean isAppVersionLinkedWithCountry(String appVersion,
			String countryCode) {
		if (appVersion == null)
			throw new PersistenceException("The parameter appVersion is null");
		if (countryCode == null)
			throw new PersistenceException(
					"The parameter countryCode is null");
		Long foundedRecordsCount = (Long) getJpaTemplate().find(
						"select count(*) from "
								+ AppVersionCountry.class.getSimpleName()
								+ " appVersionCountry, "
								+ Country.class.getSimpleName()
								+ " country, "
								+ AppVersion.class.getSimpleName()
								+ " appVersion "
								+ " where appVersionCountry.countryId=country.i "
								+ "and appVersionCountry.appVersionId=appVersion.i"
								+ " and country.name=?1 and appVersion.name = ?2",
						countryCode, appVersion).get(0);
		if (Long.valueOf(1L).equals(foundedRecordsCount))
			return true;
		boolean noRecords = Long.valueOf(0L).equals(foundedRecordsCount);
		if (noRecords)
			return false;
		else {
			String message = "There are " + foundedRecordsCount
					+ " records in " + AppVersionCountry.class.getName()
					+ " class table for appVersion = " + appVersion
					+ ", countryFullName = " + countryCode;
			LOGGER.error(message);
			throw new PersistenceException(message);
		}
	}
}