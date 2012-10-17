package mobi.nowtechnologies.server.service;

import static mobi.nowtechnologies.server.shared.AppConstants.GEO_IP_FILE_NAME;
import static mobi.nowtechnologies.server.shared.AppConstants.SEPARATOR;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mobi.nowtechnologies.server.service.exception.ServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maxmind.geoip.Country;
import com.maxmind.geoip.LookupService;

/**
 * CountryByIpService
 * 
 * @author Titov Mykhaylo (titov)
 * @author Maksym Chernolevskyi (maksym)
 * 
 */
public class CountryByIpService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CountryByIpService.class.getName());

	private LookupService LOOKUP_SERVICE = null;

	private static final String AN_OBJECT = "--";

	public void init() throws Exception {
		if (LOOKUP_SERVICE == null)
			throw new Exception("LOOKUP_SERVICE not initialised");
	}

	public void setStorePath(String storePath) {
		LOGGER.info("Store path for GEOIP database is [{}]", storePath);
		
		if(storePath == null)
			return;
		
		try {
			LOOKUP_SERVICE = new LookupService(new File(storePath + SEPARATOR + GEO_IP_FILE_NAME), LookupService.GEOIP_MEMORY_CACHE
					| LookupService.GEOIP_CHECK_CACHE);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			throw new ServiceException("failded to initialise LOOKUP_SERVICE");
		}
	}

	private Pattern ipPattern = Pattern.compile("\\d{1,3}.\\d{1,3}.\\d{1,3}.1[6-9]\\d");

	public String findCountryCodeByIp(String ip) {
		if (ip == null)
			throw new ServiceException("The parameter ip is null");
		Matcher matcher = ipPattern.matcher(ip);
		if (ip.startsWith("10.20.") && !matcher.matches()) {
			return "GB";
		}
		Country country = LOOKUP_SERVICE.getCountry(ip);
		if (country == null || country.getCode().equals(AN_OBJECT))
			throw ServiceException.getInstance("registerUser.command.error.unknownCountry");
		return country.getCode();
	}
}