package mobi.nowtechnologies.server.web.asm;

import mobi.nowtechnologies.server.TimeService;
import mobi.nowtechnologies.server.device.domain.DeviceType;
import mobi.nowtechnologies.server.dto.payment.PaymentPolicyDto;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.service.itunes.payment.ITunesPaymentService;
import mobi.nowtechnologies.server.web.controller.SubscriptionInfo;
import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails.ITUNES_SUBSCRIPTION;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SubscriptionInfoAsm {

    private TimeService timeService;
    private ITunesPaymentService iTunesPaymentService;

    public SubscriptionInfo createSubscriptionInfo(User user, List<PaymentPolicyDto> paymentPolicyDtos) {
        final boolean isIos = DeviceType.IOS.equals(user.getDeviceType().getName());

        SubscriptionInfo info = new SubscriptionInfo();
        info.setIos(isIos);
        info.setPremium(user.isPremium(timeService.now()));
        info.setFreeTrial(user.isOnFreeTrial());
        info.setOnPaidPeriod(isOnPaidPeriod(user));

        PaymentPolicyDto currentPaymentPolicyDto = getCurrentPaymentPolicy(user);
        info.setCurrentPaymentPolicy(currentPaymentPolicyDto);

        List<PaymentPolicyDto> included = filterPaymentPolicyDTOs(paymentPolicyDtos, isIos);
        Collections.sort(included, new PaymentPolicyDto.ByOrderAscAndDurationAsc());
        info.addPaymentPolicyDto(included);

        return info;
    }

    private boolean isOnPaidPeriod(User user) {
        return !user.isOnFreeTrial() && user.isSubscribedStatus() && user.getLastSubscribedPaymentSystem() != null && user.getNextSubPayment() > timeService.nowSeconds();
    }

    public PaymentPolicyDto getCurrentPaymentPolicy(User user) {
        if (user.hasActivePaymentDetails()) {
            return new PaymentPolicyDto(user.getCurrentPaymentDetails().getPaymentPolicy());
        } else if (hasITunesSubscription(user)) {
            PaymentPolicy currentPaymentPolicy = iTunesPaymentService.getCurrentSubscribedPaymentPolicy(user);
            if (currentPaymentPolicy != null) {
                return new PaymentPolicyDto(currentPaymentPolicy);
            }
        }
        return null;
    }

    private boolean hasITunesSubscription(User user) {
        return ITUNES_SUBSCRIPTION.equals(user.getLastSubscribedPaymentSystem()) && user.isSubscribedStatus();
    }

    private List<PaymentPolicyDto> filterPaymentPolicyDTOs(List<PaymentPolicyDto> paymentPolicyDtos, boolean isIos) {
        List<PaymentPolicyDto> included = new ArrayList<>();
        for (PaymentPolicyDto paymentPolicyDto : paymentPolicyDtos) {
            String paymentType = paymentPolicyDto.getPaymentType();
            if (isIos && ITUNES_SUBSCRIPTION.equals(paymentType) || !isIos && !ITUNES_SUBSCRIPTION.equals(paymentType)) {
                included.add(paymentPolicyDto);
            }
        }
        return included;
    }

    public void setTimeService(TimeService timeService) {
        this.timeService = timeService;
    }

    public void setiTunesPaymentService(ITunesPaymentService iTunesPaymentService) {
        this.iTunesPaymentService = iTunesPaymentService;
    }
}
