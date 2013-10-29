package mobi.nowtechnologies.server.assembler;

import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentStatus;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.shared.dto.OAuthProvider;
import mobi.nowtechnologies.server.shared.dto.admin.PaymentDetailsDto;
import mobi.nowtechnologies.server.shared.dto.admin.PromotionDto;
import mobi.nowtechnologies.server.shared.dto.admin.UserDto;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.*;

import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails.*;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNotNull;
import static mobi.nowtechnologies.server.shared.enums.ActivationStatus.ACTIVATED;

/**
 * @author Titov Mykhaylo (titov)
 * 
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
		if (currentPaymentDetails!=null){
			currentPaymentDetailsDto = PaymentDetailsAsm.toPaymentDetailsDto(currentPaymentDetails);
			userDto.setPaymentEnabled(currentPaymentDetails.isActivated());
		}
		else{
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
		if (firstDeviceLoginMillis != null)
			userDto.setFirstDeviceLogin(new Date(firstDeviceLoginMillis));

		userDto.setFirstName(user.getFirstName());

		final Long firstUserLoginMillis = user.getFirstUserLoginMillis();
		if (firstUserLoginMillis != null)
			userDto.setFirstUserLogin(new Date(firstUserLoginMillis));

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
		userDto.setUserGroupId(userGroup.getI());
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
        if(userDto.getFreeTrialExpiredMillis() != 0)
        user.setFreeTrialExpired(userDto.getFreeTrialExpiredAsDate());

		LOGGER.debug("Output parameter user=[{}]", user);
		return user;
	}

    public static AccountCheckDTO toAccountCheckDTO(User user, String rememberMeToken, List<String> appStoreProductIds, boolean canActivateVideoTrial){
        LOGGER.debug("user=[{}]", user);
        UserGroup userGroup = user.getUserGroup();
        String lastSubscribedPaymentSystem = user.getLastSubscribedPaymentSystem();
        UserStatus status = user.getStatus();
        int nextSubPayment = user.getNextSubPayment();
        int subBalance = user.getSubBalance();
        String userName = user.getUserName();
        Promotion potentialPromotion = user.getPotentialPromotion();

        Chart chart = userGroup.getChart();
        News news = userGroup.getNews();
        DrmPolicy drmPolicy = userGroup.getDrmPolicy();

        PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();
        boolean paymentEnabled = ((null != currentPaymentDetails && currentPaymentDetails.isActivated() && (currentPaymentDetails.getLastPaymentStatus().equals(PaymentDetailsStatus.NONE) || currentPaymentDetails
                .getLastPaymentStatus().equals(PaymentDetailsStatus.SUCCESSFUL))) || (lastSubscribedPaymentSystem != null
                && lastSubscribedPaymentSystem.equals(ITUNES_SUBSCRIPTION) && status != null
                && status.getName().equals(mobi.nowtechnologies.server.shared.enums.UserStatus.SUBSCRIBED.name())));
        String oldPaymentType = getOldPaymentType(currentPaymentDetails, lastSubscribedPaymentSystem, status);
        String oldPaymentStatus = getOldPaymentStatus(currentPaymentDetails);

        AccountCheckDTO accountCheckDTO = new AccountCheckDTO();
        accountCheckDTO.chartTimestamp = chart.getTimestamp();
        accountCheckDTO.chartItems = chart.getNumTracks();
        setNewsItemsAndTimestamp(news, accountCheckDTO);

        accountCheckDTO.timeOfMovingToLimitedStatusSeconds = Utils.getTimeOfMovingToLimitedStatus(nextSubPayment, subBalance);
        if (null != currentPaymentDetails)
            accountCheckDTO.lastPaymentStatus = currentPaymentDetails.getLastPaymentStatus();

        accountCheckDTO.drmType = drmPolicy.getDrmType().getName();
        accountCheckDTO.drmValue = drmPolicy.getDrmValue();
        accountCheckDTO.status = status.getName();
        accountCheckDTO.displayName = user.getDisplayName();
        accountCheckDTO.subBalance = (byte) subBalance;
        accountCheckDTO.deviceType = user.getDeviceType().getName();
        accountCheckDTO.deviceUID = user.getDeviceString();
        accountCheckDTO.paymentType = oldPaymentType;
        accountCheckDTO.paymentEnabled = paymentEnabled;
        accountCheckDTO.phoneNumber = user.getMobile();
        accountCheckDTO.operator = user.getOperator();
        accountCheckDTO.paymentStatus = oldPaymentStatus;
        accountCheckDTO.userName = userName;
        accountCheckDTO.userToken = user.getToken();
        accountCheckDTO.rememberMeToken = rememberMeToken;
        accountCheckDTO.freeTrial = user.isOnFreeTrial();
        accountCheckDTO.provider = isNotNull(user.getProvider()) ? user.getProvider().getKey() : null;
        accountCheckDTO.contract = user.getContract();
        accountCheckDTO.segment = user.getSegment();
        accountCheckDTO.tariff = user.getTariff();
        accountCheckDTO.lastSubscribedPaymentSystem = lastSubscribedPaymentSystem;

        accountCheckDTO.canGetVideo = true;
        accountCheckDTO.canPlayVideo = user.canPlayVideo();
        accountCheckDTO.canActivateVideoTrial = canActivateVideoTrial;
        accountCheckDTO.hasAllDetails = user.hasAllDetails();
        accountCheckDTO.showFreeTrial = true;
        accountCheckDTO.subscriptionChanged = user.getSubscriptionDirection();
        accountCheckDTO.eligibleForVideo = user.isEligibleForVideo();

        accountCheckDTO.oAuthProvider = (StringUtils.hasText(user.getFacebookId()) ? OAuthProvider.FACEBOOK : OAuthProvider.NONE);
        accountCheckDTO.nextSubPaymentSeconds = nextSubPayment;

        if (potentialPromotion != null)
            accountCheckDTO.promotionLabel = potentialPromotion.getLabel();
        accountCheckDTO.hasPotentialPromoCodePromotion = (user.getPotentialPromoCodePromotion() != null);

        ActivationStatus activationStatus = user.getActivationStatus();
        accountCheckDTO.activation = activationStatus;
        accountCheckDTO.fullyRegistred = ACTIVATED.equals(activationStatus);
        accountCheckDTO.subjectToAutoOptIn = user.isSubjectToAutoOptIn();
        accountCheckDTO.user = user;

        if (appStoreProductIds != null) {
            StringBuilder temp = new StringBuilder();
            for (String appStoreProductId : appStoreProductIds) {
                if (appStoreProductId != null) {
                    temp.append("," + appStoreProductId);
                }
            }
            if (temp.length() != 0)
                accountCheckDTO.appStoreProductId = temp.substring(1);
        }

        LOGGER.debug("Output parameter accountCheckDTO=[{}]", accountCheckDTO);
        return accountCheckDTO;
    }

    // TODO Review this code after client refactoring
    private static String getOldPaymentType(PaymentDetails paymentDetails, String lastSubscribedPaymentSystem, UserStatus status) {
        if (lastSubscribedPaymentSystem != null && lastSubscribedPaymentSystem.equals(ITUNES_SUBSCRIPTION) && status != null
                && status.getName().equals(mobi.nowtechnologies.server.shared.enums.UserStatus.SUBSCRIBED.name())) {
            return "ITUNES_SUBSCRIPTION";
        } else if (null == paymentDetails)
            return "UNKNOWN";
        if (SAGEPAY_CREDITCARD_TYPE.equals(paymentDetails.getPaymentType())) {
            return "creditCard";
        } else if (PAYPAL_TYPE.equals(paymentDetails.getPaymentType())) {
            return "PAY_PAL";
        } else if (MIG_SMS_TYPE.equals(paymentDetails.getPaymentType())) {
            return "PSMS";
        } else if (O2_PSMS_TYPE.equals(paymentDetails.getPaymentType())) {
            return "O2_PSMS";
        }
        return "UNKNOWN";
    }

    private static String getOldPaymentStatus(PaymentDetails paymentDetails) {
        if (null == paymentDetails)
            return PaymentStatus.NULL;
        if (SAGEPAY_CREDITCARD_TYPE.equals(paymentDetails.getPaymentType())) {
            switch (paymentDetails.getLastPaymentStatus()) {
                case AWAITING:
                    return PaymentStatus.AWAITING_PAYMENT;
                case SUCCESSFUL:
                    return PaymentStatus.OK;
                case ERROR:
                case EXTERNAL_ERROR:
                    return PaymentStatus.OK;
                case NONE:
                    return PaymentStatus.NULL;
            }
        } else if (PAYPAL_TYPE.equals(paymentDetails.getPaymentType())) {
            switch (paymentDetails.getLastPaymentStatus()) {
                case AWAITING:
                    return PaymentStatus.AWAITING_PAY_PAL;
                case SUCCESSFUL:
                    return PaymentStatus.OK;
                case ERROR:
                case EXTERNAL_ERROR:
                    return PaymentStatus.PAY_PAL_ERROR;
                case NONE:
                    return PaymentStatus.NULL;
            }
        } else if (MIG_SMS_TYPE.equals(paymentDetails.getPaymentType())) {
            switch (paymentDetails.getLastPaymentStatus()) {
                case AWAITING:
                    return PaymentStatus.AWAITING_PSMS;
                case SUCCESSFUL:
                    return PaymentStatus.OK;
                case ERROR:
                case EXTERNAL_ERROR:
                    return PaymentStatus.PSMS_ERROR;
            }
            if (paymentDetails.getLastPaymentStatus().equals(PaymentDetailsStatus.NONE) && !paymentDetails.isActivated()) {
                return PaymentStatus.PIN_PENDING;
            } else if (paymentDetails.getLastPaymentStatus().equals(PaymentDetailsStatus.NONE) && paymentDetails.isActivated()) {
                return PaymentStatus.NULL;
            }
        }
        return null;
    }

    private static void setNewsItemsAndTimestamp(News news, AccountCheckDTO accountCheckDTO) {
        if (news == null)
            return;
        accountCheckDTO.newsTimestamp = news.getTimestamp();
        accountCheckDTO.newsItems = news.getNumEntries();
    }

}
