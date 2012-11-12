package mobi.nowtechnologies.server.service;

import com.maxmind.geoip.Country;
import com.maxmind.geoip.LookupService;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static mobi.nowtechnologies.server.shared.AppConstants.GEO_IP_FILE_NAME;
import static mobi.nowtechnologies.server.shared.AppConstants.SEPARATOR;

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

        File file = new File(storePath + SEPARATOR + GEO_IP_FILE_NAME);
		try {
            LOOKUP_SERVICE = new LookupService(file, LookupService.GEOIP_MEMORY_CACHE
					| LookupService.GEOIP_CHECK_CACHE);
		} catch (IOException e) {
			throw new ServiceException("failed to initialise LOOKUP_SERVICE " +
                    "geoIpFileName = " + file.getAbsolutePath(), e);
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