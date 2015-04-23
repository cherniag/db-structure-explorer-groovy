package mobi.nowtechnologies.server.shared.dto.web.payment;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

public class PSmsDto {

    public static final String NAME = "pSmsDto";
    public static final String PARAM_PHONE = "phone";
    public static final String PARAM_OPERATOR = "operator";

    @NotEmpty
    private String phone;
    @NotNull
    private Integer operator;
    private Integer paymentPolicyId;

    public PSmsDto() {
    }

    public PSmsDto(String phone, Integer operator) {
        super();
        this.phone = phone;
        this.operator = operator;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getOperator() {
        return operator;
    }

    public void setOperator(Integer operator) {
        this.operator = operator;
    }

    public Integer getPaymentPolicyId() {
        return paymentPolicyId;
    }

    public void setPaymentPolicyId(Integer paymentPolicyId) {
        this.paymentPolicyId = paymentPolicyId;
    }
}