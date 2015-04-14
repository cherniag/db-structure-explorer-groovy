package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.Period;
import mobi.nowtechnologies.server.persistence.domain.payment.PeriodMessageKeyBuilder;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.security.NowTechTokenBasedRememberMeServices;
import mobi.nowtechnologies.server.service.DevicePromotionsService;
import mobi.nowtechnologies.server.service.MessageNotificationService;
import mobi.nowtechnologies.server.service.UserNotificationService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.sms.SMSGatewayService;
import mobi.nowtechnologies.server.service.sms.SMSResponse;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.UserStatus;
import mobi.nowtechnologies.server.shared.log.LogUtils;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import static mobi.nowtechnologies.server.shared.Utils.preFormatCurrency;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * @author Titov Mykhaylo (titov)
 */
public class UserNotificationServiceImpl implements UserNotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserNotificationServiceImpl.class);
    private UserService userService;
    private PaymentDetailsRepository paymentDetailsRepository;
    private CommunityResourceBundleMessageSource messageSource;
    private List<String> availableCommunities = Collections.emptyList();
    private NowTechTokenBasedRememberMeServices rememberMeServices;
    private RestTemplate restTemplate = new RestTemplate();
    private String paymentsUrl;
    private String unsubscribeUrl;
    private String tinyUrlService;
    private String rememberMeTokenCookieName;
    private DevicePromotionsService deviceService;
    private SmsServiceFacade smsServiceFacade;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setPaymentDetailsRepository(PaymentDetailsRepository paymentDetailsRepository) {
        this.paymentDetailsRepository = paymentDetailsRepository;
    }

    public void setMessageSource(CommunityResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void setDeviceService(DevicePromotionsService deviceService) {
        this.deviceService = deviceService;
    }

    public void setAvailableCommunities(String[] availableCommunities) {
        if (availableCommunities == null) {
            return;
        }

        this.availableCommunities = Arrays.asList(availableCommunities);
    }

    public void setRememberMeServices(NowTechTokenBasedRememberMeServices rememberMeServices) {
        this.rememberMeServices = rememberMeServices;
    }

    public String getPaymentsUrl() {
        return paymentsUrl;
    }

    public void setPaymentsUrl(String paymentsUrl) {
        this.paymentsUrl = paymentsUrl;
    }

    public String getUnsubscribeUrl() {
        return unsubscribeUrl;
    }

    public void setUnsubscribeUrl(String unsubscribeUrl) {
        this.unsubscribeUrl = unsubscribeUrl;
    }

    public void setTinyUrlService(String tinyUrlService) {
        this.tinyUrlService = tinyUrlService;
    }

    public void setRememberMeTokenCookieName(String rememberMeTokenCookieName) {
        this.rememberMeTokenCookieName = rememberMeTokenCookieName;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void setSmsServiceFacade(SmsServiceFacade smsServiceFacade) {
        this.smsServiceFacade = smsServiceFacade;
    }

    @Async
    @Override
    public Future<Boolean> notifyUserAboutSuccessfulPayment(User user) {
        try {
            LOGGER.debug("input parameters user: [{}]", user);
            if (user == null) {
                throw new NullPointerException("The parameter user is null");
            }

            LogUtils.putPaymentMDC(String.valueOf(user.getId()), String.valueOf(user.getUserName()), String.valueOf(user.getUserGroup().getCommunity().getName()), UserNotificationService.class);

            Future<Boolean> result;
            try {

                result = userService.makeSuccessfulPaymentFreeSMSRequest(user);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                result = new AsyncResult<Boolean>(Boolean.FALSE);
            }
            LOGGER.info("Output parameter result=[{}]", result);
            return result;
        } catch (RuntimeException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } finally {
            LogUtils.removePaymentMDC();
        }
    }

    @Async
    @Override
    public Future<Boolean> sendUnsubscribeAfterSMS(User user) throws UnsupportedEncodingException {
        try {
            LOGGER.debug("input parameters user: [{}]", user);

            if (user == null) {
                throw new NullPointerException("The parameter user is null");
            }
            if (user.getCurrentPaymentDetails() == null) {
                throw new NullPointerException("The parameter user.getCurrentPaymentDetails() is null");
            }

            final UserGroup userGroup = user.getUserGroup();
            final Community community = userGroup.getCommunity();

            LogUtils.putGlobalMDC(user.getId(), user.getMobile(), user.getUserName(), community.getName(), "", this.getClass(), "");

            Future<Boolean> result = new AsyncResult<Boolean>(Boolean.FALSE);

            LOGGER.info("Attempt to send unsubscription confirmation sms async in memory");

            Integer days = Days.daysBetween(new DateTime(Utils.getEpochMillis()).toDateMidnight(), new DateTime(user.getNextSubPayment() * 1000L).toDateMidnight()).getDays();
            if (!rejectDevice(user, "sms.notification.unsubscribed.not.for.device.type")) {
                boolean wasSmsSentSuccessfully = sendSMSWithUrl(user, "sms.unsubscribe.after.text", new String[] {paymentsUrl, days.toString()});

                if (wasSmsSentSuccessfully) {
                    LOGGER.info("The unsubscription confirmation sms was sent successfully");
                    result = new AsyncResult<Boolean>(Boolean.TRUE);
                } else {
                    LOGGER.info("The unsubscription confirmation sms wasn't sent");
                }
            } else {
                LOGGER.info("The unsubscription confirmation sms wasn't sent cause rejecting");
            }
            LOGGER.debug("Output parameter result=[{}]", result);
            return result;
        } catch (RuntimeException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } finally {
            LogUtils.removeGlobalMDC();
        }
    }

    @Async
    @Override
    public Future<Boolean> sendSubscriptionChangedSMS(User user) throws UnsupportedEncodingException {
        try {
            LOGGER.debug("input parameters user: [{}]", user);
            if (user == null) {
                throw new NullPointerException("The parameter user is null");
            }
            PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();
            if (currentPaymentDetails == null) {
                throw new NullPointerException("The parameter user.getCurrentPaymentDetails() is null");
            }

            final UserGroup userGroup = user.getUserGroup();
            final Community community = userGroup.getCommunity();

            LogUtils.putGlobalMDC(user.getId(), user.getMobile(), user.getUserName(), community.getName(), "", this.getClass(), "");

            Future<Boolean> result = new AsyncResult<Boolean>(Boolean.FALSE);

            LOGGER.info("Attempt to send subscription confirmation sms async in memory");

            if (!rejectDevice(user, "sms.notification.subscribed.not.for.device.type")) {
                PaymentPolicy paymentPolicy = currentPaymentDetails.getPaymentPolicy();
                String subCost = preFormatCurrency(paymentPolicy.getSubcost());
                Period period = paymentPolicy.getPeriod();
                String durationUnitPart = getDurationUnitPart(community, period);
                String currencyISO = paymentPolicy.getCurrencyISO();
                String shortCode = paymentPolicy.getShortCode();

                boolean wasSmsSentSuccessfully = sendSMSWithUrl(user, "sms.unsubscribe.potential.text", new String[] {unsubscribeUrl, currencyISO, subCost, durationUnitPart, shortCode});

                if (wasSmsSentSuccessfully) {
                    LOGGER.info("The subscription confirmation sms was sent successfully");
                    result = new AsyncResult<Boolean>(Boolean.TRUE);
                } else {
                    LOGGER.info("The subscription confirmation sms wasn't sent");
                }
            } else {
                LOGGER.info("The subscription confirmation sms wasn't sent cause rejecting");
            }
            LOGGER.debug("Output parameter result=[{}]", result);
            return result;
        } catch (RuntimeException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } finally {
            LogUtils.removeGlobalMDC();
        }
    }

    private String getDurationUnitPart(Community community, Period period) {
        String code = PeriodMessageKeyBuilder.of(period.getDurationUnit()).getMessageKey(period);
        return messageSource.getMessage(community.getRewriteUrlParameter(), code, new String[] {String.valueOf(period.getDuration())}, null);
    }

    @Async
    @Override
    public Future<Boolean> sendSmsOnFreeTrialExpired(User user) throws UnsupportedEncodingException {
        try {
            LOGGER.debug("input parameters user: [{}]", user);
            if (user == null) {
                throw new NullPointerException("The parameter user is null");
            }

            final mobi.nowtechnologies.server.persistence.domain.UserStatus userStatus = user.getStatus();
            final String userStatusName = userStatus.getName();
            final List<PaymentDetails> paymentDetailsList = paymentDetailsRepository.findPaymentDetailsByOwner(user);

            if (userStatusName == null) {
                throw new NullPointerException("The parameter userStatusName is null");
            }
            if (paymentDetailsList == null) {
                throw new NullPointerException("The parameter paymentDetailsList is null");
            }

            final UserGroup userGroup = user.getUserGroup();
            final Community community = userGroup.getCommunity();

            LogUtils.putGlobalMDC(user.getId(), user.getMobile(), user.getUserName(), community.getName(), "", this.getClass(), "");
            Future<Boolean> result = new AsyncResult<Boolean>(Boolean.FALSE);

            if (userStatusName.equals(UserStatus.LIMITED.name()) && paymentDetailsList.isEmpty()) {
                if (!rejectDevice(user, "sms.notification.limited.not.for.device.type")) {

                    boolean wasSmsSentSuccessfully = sendSMSWithUrl(user, "sms.freeTrialExpired.text", new String[] {paymentsUrl});

                    if (wasSmsSentSuccessfully) {
                        LOGGER.info("The free trial expired sms was sent successfully");
                        result = new AsyncResult<Boolean>(Boolean.TRUE);
                    } else {
                        LOGGER.info("The free trial expired sms wasn't sent");
                    }
                } else {
                    LOGGER.info("The free trial expired sms wasn't sent cause rejecting");
                }
            } else {
                LOGGER.info("The free trial expired sms wasn't send cause the user has [{}] status and [{}] payment details", userStatusName, paymentDetailsList);
            }
            LOGGER.debug("Output parameter result=[{}]", result);
            return result;
        } catch (RuntimeException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } finally {
            LogUtils.removeGlobalMDC();
        }
    }

    @Override
    public Future<Boolean> sendChargeNotificationReminder(User user) throws UnsupportedEncodingException {
        LOGGER.info("Start sending charge notification reminder for user id={} userName={} mobile={}", user.getId(), user.getUserName(), user.getMobile());
        Community community = user.getUserGroup().getCommunity();
        Future<Boolean> futureResult = null;
        try {
            LogUtils.putGlobalMDC(user.getId(), user.getMobile(), user.getUserName(), community.getName(), "", this.getClass(), "");
            if (!rejectDevice(user, "sms.notification.charge.reminder.not.for.device.type")) {
                boolean result = sendSMSWithUrl(user, "sms.charge.reminder.text", null);
                if (result) {
                    LOGGER.info("Charge notification reminder has been sent");
                    futureResult = new AsyncResult<Boolean>(Boolean.TRUE);
                } else {
                    LOGGER.warn("Charge notification reminder was failed");
                }
            } else {
                LOGGER.warn("Charge notification reminder was rejected for device type {}", user.getDeviceTypeIdString());
            }
        } catch (RuntimeException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } finally {
            LogUtils.removeGlobalMDC();
        }
        return futureResult != null ?
               futureResult :
               new AsyncResult<Boolean>(Boolean.FALSE);
    }

    @Async
    @Override
    public Future<Boolean> sendLowBalanceWarning(User user) throws UnsupportedEncodingException {
        try {
            LOGGER.debug("input parameters user: [{}]", user);

            if (user == null) {
                throw new NullPointerException("The parameter user is null");
            }
            if (user.getCurrentPaymentDetails() == null) {
                throw new NullPointerException("The parameter user.getCurrentPaymentDetails() is null");
            }

            final UserGroup userGroup = user.getUserGroup();
            final Community community = userGroup.getCommunity();

            LogUtils.putGlobalMDC(user.getId(), user.getMobile(), user.getUserName(), community.getName(), "", this.getClass(), "");
            Future<Boolean> result = new AsyncResult<Boolean>(Boolean.FALSE);

            if (user.isO2PAYGConsumer()) {
                if (!rejectDevice(user, "sms.notification.lowBalance.not.for.device.type")) {
                    boolean wasSmsSentSuccessfully = sendSMSWithUrl(user, "sms.lowBalance.text", null);

                    if (wasSmsSentSuccessfully) {
                        LOGGER.info("The low balance sms was sent successfully");
                        result = new AsyncResult<Boolean>(Boolean.TRUE);
                    } else {
                        LOGGER.info("The low balance sms wasn't sent");
                    }
                } else {
                    LOGGER.info("The low balance sms wasn't sent cause rejecting");
                }
            } else {
                LOGGER.info("The low balance sms wasn't sent cause user isn't o2 PAYG consumer [{}]", user);
            }
            LOGGER.debug("Output parameter result=[{}]", result);
            return result;
        } catch (RuntimeException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } finally {
            LogUtils.removeGlobalMDC();
        }
    }

    @Async
    @Override
    public Future<Boolean> sendPaymentFailSMS(PaymentDetails paymentDetails) {
        try {
            LOGGER.debug("input parameters paymentDetails: [{}]", paymentDetails);

            User user = paymentDetails.getOwner();

            final UserGroup userGroup = user.getUserGroup();
            final Community community = userGroup.getCommunity();

            LogUtils.putGlobalMDC(user.getId(), user.getMobile(), user.getUserName(), community.getName(), "", this.getClass(), "");
            boolean wasSmsSentSuccessfully = false;

            int attempt = paymentDetails.getMadeAttempts();

            if (!rejectDevice(user, "sms.notification.paymentFail.at." + attempt + "attempt.not.for.device.type")) {
                String shortCode = paymentDetails.getPaymentPolicy().getShortCode();
                wasSmsSentSuccessfully = sendSMSWithUrl(user, "sms.paymentFail.at." + attempt + "attempt.text", new String[]{paymentsUrl, shortCode});

                if (wasSmsSentSuccessfully) {
                    LOGGER.info("The payment fail sms was sent successfully");
                    paymentDetails.withLastFailedPaymentNotificationMillis(Utils.getEpochMillis());
                    paymentDetailsRepository.save(paymentDetails);
                } else {
                    LOGGER.info("The payment fail sms wasn't sent for paymentDetails {}", paymentDetails);
                }
            } else {
                LOGGER.info("The payment fail sms wasn't sent cause rejecting");
            }

            LOGGER.debug("Output parameter wasSmsSentSuccessfully=[{}]", wasSmsSentSuccessfully);
            return new AsyncResult<>(wasSmsSentSuccessfully);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            LogUtils.removeGlobalMDC();
        }
        return new AsyncResult<>(false);
    }


    @Async
    @Override
    public Future<Boolean> send4GDowngradeSMS(User user, String smsType) throws UnsupportedEncodingException {
        try {
            LOGGER.debug("send4GDowngradeSMS [{}, {}]", user, smsType);
            if (user == null) {
                throw new NullPointerException("The parameter user is null");
            }
            if (smsType == null) {
                throw new NullPointerException("smsType is null");
            }

            final UserGroup userGroup = user.getUserGroup();
            final Community community = userGroup.getCommunity();

            LogUtils.putGlobalMDC(user.getId(), user.getMobile(), user.getUserName(), community.getName(), "", this.getClass(), "");

            Future<Boolean> result = new AsyncResult<Boolean>(Boolean.FALSE);

            LOGGER.info("Attempt to send downgrade sms async in memory");

            if (!rejectDevice(user, "sms.notification.downgrade.not.for.device.type")) {

                String smsPrefix = "sms.downgrade.subscriber.text";
                if (UserNotificationService.DOWNGRADE_FROM_4G_FREETRIAL.equals(smsType)) {
                    smsPrefix = "sms.downgrade.freetrial.text";
                }

                boolean wasSmsSentSuccessfully = sendSMSWithUrl(user, smsPrefix, new String[] {paymentsUrl});

                if (wasSmsSentSuccessfully) {
                    LOGGER.info("The downgrade sms was sent successfully");
                    result = new AsyncResult<Boolean>(Boolean.TRUE);
                } else {
                    LOGGER.info("The downgrade sms wasn't sent");
                }
            } else {
                LOGGER.info("The downgrade sms wasn't sent cause rejecting");
            }
            LOGGER.debug("Output parameter result=[{}]", result);
            return result;
        } catch (RuntimeException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } finally {
            LogUtils.removeGlobalMDC();
        }
    }

    @Async
    @Override
    public Future<Boolean> sendActivationPinSMS(User user) throws UnsupportedEncodingException {
        try {
            LOGGER.debug("sendActivationPinSMS [{}, {}]", user);
            if (user == null) {
                throw new NullPointerException("The parameter user is null");
            }
            final UserGroup userGroup = user.getUserGroup();
            final Community community = userGroup.getCommunity();
            LogUtils.putGlobalMDC(user.getId(), user.getMobile(), user.getUserName(), community.getName(), "", this.getClass(), "");
            Future<Boolean> result = new AsyncResult<Boolean>(Boolean.FALSE);
            LOGGER.info("Attempt to send activation pin sms async in memory");

            if (!rejectDevice(user, "sms.notification.activation.pin.not.for.device.type")) {
                String smsPrefix = "sms.activation.pin.text.for." + community.getRewriteUrlParameter();
                boolean wasSmsSentSuccessfully = sendSMSWithUrl(user, smsPrefix, new String[] {null, user.getPin()});
                if (wasSmsSentSuccessfully) {
                    LOGGER.info("The activation pin sms was sent successfully");
                    result = new AsyncResult<Boolean>(Boolean.TRUE);
                } else {
                    LOGGER.info("The activation pin sms wasn't sent");
                }
            } else {
                LOGGER.info("The activation pin sms wasn't sent cause rejecting");
            }
            LOGGER.debug("Output parameter result=[{}]", result);
            return result;
        } catch (RuntimeException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } finally {
            LogUtils.removeGlobalMDC();
        }
    }

    @Override
    public boolean sendSMSByKey(User user, String phoneNumber, String messageKey) throws UnsupportedEncodingException {
        LOGGER.info("Send SMS by key, user id:{}, phoneNumber:{}, messageKey:{}", user.getId(), phoneNumber, messageKey);

        String communityUrl = user.getCommunityRewriteUrl();
        String rejectByDeviceTypeCode = "sms.notification.not.for.device.type";
        if (rejectDevice(user, rejectByDeviceTypeCode)) {
            LOGGER.warn("The sms wasn't sent cause rejecting, device: {}, code: {}", user.getDeviceTypeIdString(), rejectByDeviceTypeCode);
            return false;
        }

        if (deviceService.isPromotedDevicePhone(user.getCommunity(), phoneNumber, null)) {
            LOGGER.info("The sms wasn't sent cause promoted phoneNumber [{}] for communityUrl [{}]", phoneNumber, communityUrl);
            return false;
        }

        String message = getMessage(user, messageKey, null);

        if (StringUtils.isBlank(message)) {
            LOGGER.error("The sms wasn't sent cause empty sms text message, community: {}, messageKey: {}", communityUrl, messageKey);
            return false;
        }

        String title = messageSource.getMessage(communityUrl, "sms.title", null, null);
        SMSResponse smsResponse = smsServiceFacade.getSMSProvider(communityUrl).send(phoneNumber, message, title);
        LOGGER.info("Sms response: {}, error: {}", smsResponse.isSuccessful(), smsResponse.getDescriptionError());
        return smsResponse.isSuccessful();
    }

    public boolean sendSMSWithUrl(User user, String msgCode, String[] msgArgs) throws UnsupportedEncodingException {
        LOGGER.debug("input parameters user, msgCode, msgArgs: [{}], [{}]", user, msgCode, msgArgs);

        if (msgCode == null) {
            throw new NullPointerException("The parameter msgCode is null");
        }

        final UserGroup userGroup = user.getUserGroup();
        Community community = userGroup.getCommunity();
        String communityUrl = community.getRewriteUrlParameter();

        if (availableCommunities.contains(communityUrl)) {
            if (!rejectDevice(user, "sms.notification.not.for.device.type")) {
                if (!deviceService.isPromotedDevicePhone(community, user.getMobile(), null)) {
                    String baseUrl = msgArgs != null ?
                                     msgArgs[0] :
                                     null;
                    if (baseUrl != null) {
                        String rememberMeToken = rememberMeServices.getRememberMeToken(user.getUserName(), user.getToken());
                        String url = baseUrl + "?community=" + communityUrl + "&" + rememberMeTokenCookieName + "=" + rememberMeToken;

                        MultiValueMap<String, Object> request = new LinkedMultiValueMap<String, Object>();
                        request.add("url", url);

                        try {
                            url = restTemplate.postForEntity(tinyUrlService, request, String.class).getBody();
                        } catch (Exception e) {
                            LOGGER.error("Error get tinyUrl. tinyLink:[{}], error:[{}]", tinyUrlService, e.getMessage());
                        }

                        msgArgs[0] = url;
                    }

                    String message = getMessage(user, msgCode, msgArgs);

                    if (!StringUtils.isBlank(message)) {
                        String title = messageSource.getMessage(communityUrl, "sms.title", null, null);
                        SMSGatewayService smsProvider = smsServiceFacade.getSMSProvider(communityUrl);
                        SMSResponse smsResponse = smsProvider.send(user.getMobile(), message, title);

                        LOGGER.info("SmsResponse, result: [{}], description error: [{}]", smsResponse.isSuccessful(), smsResponse.getDescriptionError());

                        return smsResponse.isSuccessful();
                    } else {
                        LOGGER.info("The sms wasn't sent cause empty sms text message for code: {} and community {}", msgCode, communityUrl);
                    }
                } else {
                    LOGGER.info("The sms wasn't sent cause unsupported communityUrl [{}]", communityUrl);
                }
            } else {
                LOGGER.info("The sms wasn't sent cause promoted phoneNumber [{}] for communityUrl [{}]", new Object[] {user.getMobile(), communityUrl});
            }
        } else {
            LOGGER.info("The sms wasn't sent cause rejecting community {}, all available are: {}", communityUrl, availableCommunities);
        }

        return false;
    }

    public boolean rejectDevice(User user, String code) {
        Community community = user.getUserGroup().getCommunity();
        String communityUrl = community.getRewriteUrlParameter();
        String devices = messageSource.getMessage(communityUrl, code, null, null, null);
        if (devices != null) {
            for (String device : devices.split(",")) {
                if (user.getDeviceTypeIdString().equalsIgnoreCase(device)) {
                    LOGGER.warn("SMS will not send for User[{}]. See prop:[{}]", user.getUserName(), code);
                    return true;
                }
            }
        }
        return false;
    }

    protected String getMessage(User user, String msgCodeBase, String[] msgArgs) {
        MessageNotificationService messageNotificationService = smsServiceFacade.getMessageNotificationService(user.getCommunity().getRewriteUrlParameter());
        return messageNotificationService.getMessage(user, msgCodeBase, msgArgs);
    }
}
