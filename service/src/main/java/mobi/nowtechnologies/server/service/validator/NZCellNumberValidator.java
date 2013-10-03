package mobi.nowtechnologies.server.service.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class NZCellNumberValidator {
    private static final String PHONE_PATTERN = "(00642|\\+642|02|2)([0-9]{7,9})";
    private static final String PHONE_DELIMS = "[\\-\\_\\(\\)\\s]";
    private static final String NZ_NATIONAL_MOBILE_CODE = "+642";

	private static final Logger LOGGER = LoggerFactory.getLogger(NZCellNumberValidator.class);

    private Pattern pattern = Pattern.compile(PHONE_PATTERN);

	public String validate(String target) {
		LOGGER.debug("input parameters target, errors: [{}], [{}]", target);
        String phoneNUmber = target.replaceAll(PHONE_DELIMS,"");
        Matcher matcher = pattern.matcher(phoneNUmber);

        if(!matcher.matches()){
            phoneNUmber = null;
        } else {
            phoneNUmber = NZ_NATIONAL_MOBILE_CODE + matcher.group(2);
        }

        LOGGER.debug("Output parameter hasErrors=[{}], phoneNumber=[{}]", new Object[]{phoneNUmber});
		
		return phoneNUmber;
	}
}
