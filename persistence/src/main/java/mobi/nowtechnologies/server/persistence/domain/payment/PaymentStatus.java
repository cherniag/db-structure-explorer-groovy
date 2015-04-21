package mobi.nowtechnologies.server.persistence.domain.payment;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import java.io.Serializable;


/**
 * The persistent class for the tb_paymentTypes database table.
 */
@Entity
@Table(name = "tb_paymentStatus")
public class PaymentStatus implements Serializable {

    public static final String AWAITING_PSMS = "AWAITING_PSMS";
    public static final String PSMS_ERROR = "PSMS_ERROR";
    public static final String NULL = "NULL";
    public static final String OK = "OK";
    public static final String PIN_PENDING = "PIN_PENDING";
    public static final String AWAITING_PAYMENT = "AWAITING_PAYMENT";
    public static final String PAY_PAL_ERROR = "PAY_PAL_ERROR";
    public static final String AWAITING_PAY_PAL = "AWAITING_PAY_PAL";
    public static final int PSMS_ERROR_CODE = 4;
    public static final int AWAITING_PSMS_CODE = 3;
    public static final int NULL_CODE = 1;
    public static final int OK_CODE = 2;
    public static final int PIN_PENDING_CODE = 5;
    public static final int AWAITING_PAYMENT_CODE = 6;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    public PaymentStatus() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "PaymentStatus [id=" + id + ", name=" + name + "]";
    }

}