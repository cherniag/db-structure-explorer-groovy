package mobi.nowtechnologies.server.shared.dto.web.payment;

import javax.validation.constraints.Size;

public class UnsubscribeDto {

    public static final String NAME = "unsubscribeDto";

    @Size(max = 255)
    private String reason;

    public UnsubscribeDto() {
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public UnsubscribeDto withReason(String reason) {
        this.reason = reason;

        return this;
    }
}