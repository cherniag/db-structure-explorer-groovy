package mobi.nowtechnologies.server.persistence.domain.filter;

import mobi.nowtechnologies.common.util.DateTimeUtils;
import mobi.nowtechnologies.server.persistence.domain.AbstractFilter;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserStatus;
import mobi.nowtechnologies.server.persistence.domain.UserStatusType;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.shared.dto.NewsDetailDto;
import mobi.nowtechnologies.server.shared.dto.NewsDetailDto.UserState;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@DiscriminatorValue("PromotionUserStateFilter")
public class PUserStateFilter extends AbstractFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(PUserStateFilter.class);

    private static final int ONE_DAY_SECONDS = 24 * 60 * 60;

    @ElementCollection(targetClass = UserState.class)
    @Enumerated(EnumType.STRING)
    @Column(name = "userStates", nullable = true)
    @CollectionTable(name = "tb_filter_params")
    private List<UserState> userStates;

    @Override
    public boolean doFilter(User user, Object param) {
        final UserStatus userStatus = user.getStatus();
        final PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();

        List<UserState> userStates = new LinkedList<NewsDetailDto.UserState>();

        if (currentPaymentDetails == null || !currentPaymentDetails.isActivated()) {
            userStates.add(UserState.NOT_ACTIVE_PAYMENT_DETAILS_OR_NO_PAYMENT_DETAILS);
        }
        if (UserStatusType.LIMITED.name().equals(userStatus.getName())) {
            userStates.add(UserState.LIMITED);
        }
        if (UserStatusType.SUBSCRIBED.name().equals(userStatus.getName()) && currentPaymentDetails == null) {
            int nextSubPaymentSeconds = user.getNextSubPayment();
            int currentTimeSeconds = DateTimeUtils.getEpochSeconds();
            if (nextSubPaymentSeconds - currentTimeSeconds < ONE_DAY_SECONDS) {
                userStates.add(UserState.LAST_TRIAL_DAY);
            }
        }
        if (UserStatusType.SUBSCRIBED.name().equals(userStatus.getName()) && currentPaymentDetails == null) {
            userStates.add(UserState.FREE_TRIAL);
        }
        if (currentPaymentDetails != null && currentPaymentDetails.getLastPaymentStatus().equals(PaymentDetailsStatus.ERROR)) {
            userStates.add(UserState.PAYMENT_ERROR);
        }
        if (UserStatusType.LIMITED.name().equals(userStatus.getName()) && currentPaymentDetails == null) {
            userStates.add(UserState.LIMITED_AFTER_TRIAL);
        }
        for (UserState state : userStates) {
            if (this.userStates.contains(state)) {
                return true;
            }
        }
        return false;
    }

    public List<UserState> getUserStates() {
        return userStates;
    }

    public void setUserStates(List<UserState> userStates) {
        this.userStates = userStates;
    }

}