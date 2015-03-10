package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.service.exception.ServiceException;
import static mobi.nowtechnologies.server.shared.AppConstants.GEO_IP_FILE_NAME;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.maxmind.geoip.Country;
import com.maxmind.geoip.LookupService;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.io.Resource;

/**
 * @author Titov Mykhaylo (titov)
 * @author Maksym Chernolevskyi (maksym)
 */
public class CountryByIpService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CountryByIpService.class);
    private static final String AN_OBJECT = "--";
    private LookupService LOOKUP_SERVICE = null;
    private Pattern ipPattern = Pattern.compile("\\d{1,3}.\\d{1,3}.\\d{1,3}.1[6-9]\\d");

    public void init() throws Exception {
        Validate.notNull(LOOKUP_SERVICE, "LOOKUP_SERVICE not initialised");
    }

    public void setStorePath(Resource storePath) {
        LOGGER.info("store.path = " + storePath);
        try {
            File file = new File(storePath.getFile(), GEO_IP_FILE_NAME);
            Validate.isTrue(file.exists(), "File does not exist: " + file.getAbsolutePath() + ". Amend store.path property");
            LOOKUP_SERVICE = new LookupService(file, LookupService.GEOIP_MEMORY_CACHE | LookupService.GEOIP_CHECK_CACHE);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new ServiceException("failded to initialise LOOKUP_SERVICE. store.path=" + storePath.getFilename(), e);
        }
    }

    public String findCountryCodeByIp(String ip) {
        Validate.notNull(ip, "The parameter ip is null");
        Matcher matcher = ipPattern.matcher(ip);
        if (ip.startsWith("10.20.") && !matcher.matches()) {
            return "GB";
        }
        Country country = LOOKUP_SERVICE.getCountry(ip);
        if (country == null || country.getCode().equals(AN_OBJECT)) {
            throw ServiceException.getInstance("registerUser.command.error.unknownCountry");
        }
        return country.getCode();
    }
}