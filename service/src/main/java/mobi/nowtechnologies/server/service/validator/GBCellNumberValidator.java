package mobi.nowtechnologies.server.service.validator;

/**
 * @author Alexsandr_Kolpakov
 */
public class GBCellNumberValidator extends AbstractCellNumberValidator {

    private static final String GB_CELL_PHONE_PATTERN = "7[0-9]{9}";
    private static final String GB_LOCAL_CODE_PATTERN = "0";
    private static final String GB_NATIONAL_CODE = "44";

    @Override
    protected String getLocalPhonePattern() {
        return GB_CELL_PHONE_PATTERN;
    }

    @Override
    protected String getLocalCodePattern() {
        return GB_LOCAL_CODE_PATTERN;
    }

    @Override
    protected String getNationalCode() {
        return GB_NATIONAL_CODE;
    }
}
