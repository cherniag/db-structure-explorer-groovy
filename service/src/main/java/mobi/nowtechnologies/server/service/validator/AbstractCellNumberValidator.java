package mobi.nowtechnologies.server.service.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Alexsandr_Kolpakov
 */
public abstract class AbstractCellNumberValidator {

    private static final String PHONE_DELIMS = "[\\-\\_\\(\\)\\s]";

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCellNumberValidator.class);

    private Pattern pattern = Pattern.compile("(00" + getNationalCode() + "|\\+" + getNationalCode() + "|" + getLocalCodePattern() + "){0,1}(" + getLocalPhonePattern() + ")");

    public String validateAndNormalize(String target) {
        LOGGER.debug("input parameters target, errors: [{}], [{}]", target);

        if (target == null) {
            return null;
        }

        String phoneNUmber = target.replaceAll(PHONE_DELIMS, "");
        Matcher matcher = pattern.matcher(phoneNUmber);

        if (!matcher.matches()) {
            phoneNUmber = null;
        }
        else {
            phoneNUmber = "+" + getNationalCode() + matcher.group(matcher.groupCount());
        }

        LOGGER.debug("Output parameter hasErrors=[{}], phoneNumber=[{}]", new Object[] {phoneNUmber});

        return phoneNUmber;
    }

    protected abstract String getLocalPhonePattern();

    protected abstract String getLocalCodePattern();

    protected abstract String getNationalCode();
}
