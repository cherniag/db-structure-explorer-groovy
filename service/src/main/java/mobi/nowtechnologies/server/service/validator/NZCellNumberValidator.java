package mobi.nowtechnologies.server.service.validator;

/**
 * @author Alexsandr_Kolpakov
 */
public class NZCellNumberValidator extends AbstractCellNumberValidator {

    private static final String NZ_CELL_PHONE_PATTERN = "2[0-9]{7,9}";
    private static final String NZ_LOCAL_CODE_PATTERN = "0";
    private static final String NZ_NATIONAL_CODE = "64";

    @Override
    protected String getLocalPhonePattern() {
        return NZ_CELL_PHONE_PATTERN;
    }

    @Override
    protected String getLocalCodePattern() {
        return NZ_LOCAL_CODE_PATTERN;
    }

    @Override
    protected String getNationalCode() {
        return NZ_NATIONAL_CODE;
    }
}
