package mobi.nowtechnologies.server.web.model.mtvnz;

import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.shared.enums.DurationUnit;

import java.math.BigDecimal;

public class PaymentPolicyDto implements Comparable<PaymentPolicyDto> {
    private int id;
    private BigDecimal subCost;
    // duration is always is one
    private DurationUnit durationUnit;

    public PaymentPolicyDto(PaymentPolicy paymentPolicy) {
        this.id = paymentPolicy.getId();
        this.subCost = paymentPolicy.getSubcost();
        this.durationUnit = paymentPolicy.getPeriod().getDurationUnit();
    }

    public int getId() {
        return id;
    }

    public BigDecimal getSubCost() {
        return subCost;
    }

    public DurationUnit getDurationUnit() {
        return durationUnit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PaymentPolicyDto that = (PaymentPolicyDto) o;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public int compareTo(PaymentPolicyDto paymentPolicyDto) {
        int thisPeriodWeight = durationUnit.getPeriodWeight();
        int thatPeriodWeight = paymentPolicyDto.durationUnit.getPeriodWeight();
        return Integer.valueOf(thisPeriodWeight).compareTo(thatPeriodWeight);
    }
}
