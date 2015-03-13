package mobi.nowtechnologies.server.shared.dto.web;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author Titov Mykhaylo (titov)
 */
public class EmailDto {

    public static final String EMAIL_DTO = "EmailDto";
    @Email
    @NotEmpty
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "EmailDto [value=" + value + "]";
    }
}
