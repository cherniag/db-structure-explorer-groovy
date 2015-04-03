package mobi.nowtechnologies.server.assembler;

import mobi.nowtechnologies.server.device.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.UserStatus;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.admin.PaymentDetailsDto;
import mobi.nowtechnologies.server.shared.dto.admin.PromotionDto;
import mobi.nowtechnologies.server.shared.dto.admin.UserDto;
import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails.ITUNES_SUBSCRIPTION;
import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails.MIG_SMS_TYPE;
import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails.O2_PSMS_TYPE;
import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails.PAYPAL_TYPE;
import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails.SAGEPAY_CREDITCARD_TYPE;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @author Titov Mykhaylo (titov)
 */
public class UserAsm {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAsm.class);

    @SuppressWarnings("unchecked")
    public static List<UserDto> toUserDtos(Collection<User> users) {
        LOGGER.debug("input parameters users: [{}]", users);

        final List<UserDto> userDtos;
        if (users.isEmpty()) {
            userDtos = Collections.EMPTY_LIST;
        } else {
            userDtos = new ArrayList<UserDto>(users.size());
            for (User user : users) {
                userDtos.add(toUserDto(user));
            }
        }

        LOGGER.info("Output parameter userDtos=[{}]", userDtos);
        return userDtos;
    }

    public static UserDto toUserDto(User user) {
        LOGGER.debug("input parameters user: [{}]", user);

        UserDto userDto = new UserDto();

        userDto.setAddress1(user.getAddress1());
        userDto.setAddress2(user.getAddress2());
        userDto.setCanContact(user.getCanContact());
        userDto.setCity(user.getCity());
        userDto.setCode(user.getCode());
        userDto.setConformStoredToken(user.getConformStoredToken());
        userDto.setCountry(user.getCountry());

        final PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();

        final PaymentDetailsDto currentPaymentDetailsDto;
        if (currentPaymentDetails != null) {
            currentPaymentDetailsDto = PaymentDetailsAsm.toPaymentDetailsDto(currentPaymentDetails);
            userDto.setPaymentEnabled(currentPaymentDetails.isActivated());
        } else {
            currentPaymentDetailsDto = null;
        }

        userDto.setCurrentPaymentDetailsDto(currentPaymentDetailsDto);
        userDto.setDevice(user.getDevice());
        userDto.setDeviceModel(user.getDeviceModel());

        final DeviceType deviceType = user.getDeviceType();

        userDto.setDeviceType(deviceType.getName());
        userDto.setDeviceTypeId(deviceType.getI());
        userDto.setDeviceUID(user.getDeviceUID());
        userDto.setDisplayName(user.getDisplayName());
        userDto.setFacebookId(user.getFacebookId());

        final Long firstDeviceLoginMillis = user.getFirstDeviceLoginMillis();
        if (firstDeviceLoginMillis != null) {
            userDto.setFirstDeviceLogin(new Date(firstDeviceLoginMillis));
        }

        userDto.setFirstName(user.getFirstName());

        final Long firstUserLoginMillis = user.getFirstUserLoginMillis();
        if (firstUserLoginMillis != null) {
            userDto.setFirstUserLogin(new Date(firstUserLoginMillis));
        }

        userDto.setFreeTrial(user.isOnFreeTrial());
        userDto.setId(user.getId());
        userDto.setIpAddress(user.getIpAddress());
        userDto.setLastDeviceLogin(Utils.getDateFromInt(user.getLastDeviceLogin()));
        userDto.setLastName(user.getLastName());
        userDto.setLastPaymentTx(user.getLastPaymentTx());
        userDto.setLastSuccessfulPaymentTime(new Date(user.getLastSuccessfulPaymentTimeMillis()));
        userDto.setLastWebLogin(Utils.getDateFromInt(user.getLastWebLogin()));
        userDto.setMobile(user.getMobile());
        userDto.setNewStoredToken(user.getNewStoredToken());
        userDto.setNextSubPayment(Utils.getDateFromInt(user.getNextSubPayment()));
        userDto.setNumPsmsRetries(user.getNumPsmsRetries());
        userDto.setOperator(user.getOperator());

        userDto.setPaymentStatus(user.getPaymentStatus());
        userDto.setPaymentType(user.getPaymentType());
        userDto.setPin(user.getPin());
        userDto.setPostcode(user.getPostcode());

        final Promotion potentialPromoCodePromotion = user.getPotentialPromoCodePromotion();

        PromotionDto potentialPromoCodePromotionDto;
        if (potentialPromoCodePromotion != null) {
            potentialPromoCodePromotionDto = PromotionAsm.toPromotionDto(potentialPromoCodePromotion);
            userDto.setPotentialPromoCodePromotionDto(potentialPromoCodePromotionDto);
            userDto.setPotentialPromoCodePromotionId(potentialPromoCodePromotion.getI());
        }

        final Promotion potentialPromotion = user.getPotentialPromotion();
        if (potentialPromotion != null) {
            PromotionDto potentialPromotionDto = PromotionAsm.toPromotionDto(potentialPromotion);
            userDto.setPotentialPromotionDto(potentialPromotionDto);
        }

        userDto.setSessionID(user.getSessionID());
        userDto.setSubBalance(user.getSubBalance());
        userDto.setTempToken(user.getTempToken());
        userDto.setTitle(user.getTitle());
        userDto.setToken(user.getToken());

        final UserGroup userGroup = user.getUserGroup();

        userDto.setUserGroup(userGroup.getName());
        userDto.setUserGroupId(userGroup.getId());
        userDto.setUserName(user.getUserName());

        final UserStatus userStatus = user.getStatus();

        userDto.setUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.valueOf(userStatus.getName()));
        userDto.setUserStatusId(userStatus.getI());
        userDto.setUserType(user.getUserType());

        userDto.setCurrentPaymentDetailsId(user.getCurrentPaymentDetailsId());
        userDto.setAmountOfMoneyToUserNotification(user.getAmountOfMoneyToUserNotification());
        userDto.setPotentialPromotionId(user.getPotentialPromotionId());
        userDto.setLastSuccesfullPaymentSmsSendingTimestamp(new Date(user.getLastSuccesfullPaymentSmsSendingTimestampMillis()));

        LOGGER.info("Output parameter userDto=[{}]", userDto);
        return userDto;
    }

    public static User fromUserDto(UserDto userDto, User user) {
        LOGGER.debug("input parameters userDto, user: [{}], [{}]", userDto, user);

        user.setDisplayName(userDto.getDisplayName());
        user.setSubBalance(userDto.getSubBalance());
        user.setNextSubPayment((int) (userDto.getNextSubPayment().getTime() / 1000));
        user.setUserType(userDto.getUserType());
        if (userDto.getFreeTrialExpiredMillis() != 0) {
            user.setFreeTrialExpired(userDto.getFreeTrialExpiredAsDate());
        }

        LOGGER.debug("Output parameter user=[{}]", user);
        return user;
    }

    static String getPaymentType(PaymentDetails paymentDetails, String lastSubscribedPaymentSystem) {
        if (isNull(paymentDetails) && ITUNES_SUBSCRIPTION.equals(lastSubscribedPaymentSystem)) {
            return "ITUNES_SUBSCRIPTION";
        } else if (isNull(paymentDetails)) {
            return "UNKNOWN";
        }
        if (SAGEPAY_CREDITCARD_TYPE.equals(paymentDetails.getPaymentType())) {
            return "creditCard";
        } else if (PAYPAL_TYPE.equals(paymentDetails.getPaymentType())) {
            return "PAY_PAL";
        } else if (MIG_SMS_TYPE.equals(paymentDetails.getPaymentType())) {
            return "PSMS";
        } else if (O2_PSMS_TYPE.equals(paymentDetails.getPaymentType())) {
            return "O2_PSMS";
        }
        return paymentDetails.getPaymentType();
    }

}
