package mobi.nowtechnologies.server.web.asm;

import mobi.nowtechnologies.server.TimeService;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.Period;
import mobi.nowtechnologies.server.persistence.domain.payment.PeriodMessageKeyBuilder;
import mobi.nowtechnologies.server.dto.payment.PaymentPolicyDto;
import mobi.nowtechnologies.server.shared.enums.DurationUnit;
import mobi.nowtechnologies.server.web.controller.SubscriptionInfo;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

public class SubscriptionInfoAsm implements MessageSourceAware {
    private TimeService timeService;
    private MessageSource messageSource;

    public void setTimeService(TimeService timeService) {
        this.timeService = timeService;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public SubscriptionInfo createSubscriptionInfo(Locale locale, User user, List<PaymentPolicyDto> paymentPolicyDtos) {
        final boolean isIos = DeviceType.IOS.equals(user.getDeviceType().getName());

        PaymentPolicyDto matched = matches(isIos, paymentPolicyDtos);

        SubscriptionInfo info = new SubscriptionInfo();
        info.setPaymentPolicyDto(matched);
        info.setIos(isIos);
        info.setPremium(calcIsPremium(isIos, user));
        info.setPaymentPolicyMessage(createPaymentPolicyMessage(locale, matched));
        return info;
    }

    private String createPaymentPolicyMessage(Locale locale, PaymentPolicyDto dto) {
        if(dto == null) {
            return null;
        }

        final DurationUnit durationUnit = dto.getDurationUnit();
        final int duration = dto.getDuration();

        PeriodMessageKeyBuilder keyBuilder = PeriodMessageKeyBuilder.of(durationUnit);
        Period period = new Period(durationUnit, duration);
        String messageKey = "payment." + keyBuilder.getMessageKey(period);
        return messageSource.getMessage(messageKey, createParams(period, dto.getSubcost()), "", locale);
    }

    private PaymentPolicyDto matches(boolean ios, List<PaymentPolicyDto> dtos) {
        for (PaymentPolicyDto dto : dtos) {
            if(ios && PaymentDetails.ITUNES_SUBSCRIPTION.equals(dto.getPaymentType())) {
                return dto;
            }
            if(!ios && "PAY_PAL".equals(dto.getPaymentType())) {
                return dto;
            }
        }
        return null;
    }

    private boolean calcIsPremium(boolean isIos, User user) {
        if(isIos) {
            return PaymentDetails.ITUNES_SUBSCRIPTION.equals(user.getLastSubscribedPaymentSystem()) &&
                    ( user.getCurrentPaymentDetails() == null || user.getCurrentPaymentDetails().isDeactivated() ) &&
                    user.getNextSubPaymentAsDate().after(timeService.now());
        } else {
            return user.getCurrentPaymentDetails() != null && user.getCurrentPaymentDetails().isActivated();
        }
    }

    private Object[] createParams(Period period, BigDecimal subcost) {
        if(period.isOne()) {
            return new Object[]{subcost};
        } else {
            return new Object[]{subcost, period.getDuration()};
        }
    }

}
