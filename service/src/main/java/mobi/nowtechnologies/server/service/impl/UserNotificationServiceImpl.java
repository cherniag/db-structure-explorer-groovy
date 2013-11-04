package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.persistence.repository.UserGroupRepository;
import mobi.nowtechnologies.server.security.NowTechTokenBasedRememberMeServices;
import mobi.nowtechnologies.server.service.DeviceService;
import mobi.nowtechnologies.server.service.PaymentDetailsService;
import mobi.nowtechnologies.server.service.UserNotificationService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.sms.SMSGatewayService;
import mobi.nowtechnologies.server.service.sms.SMSResponse;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.*;
import mobi.nowtechnologies.server.shared.log.LogUtils;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import static mobi.nowtechnologies.server.persistence.domain.Community.O2_COMMUNITY_REWRITE_URL;
import static mobi.nowtechnologies.server.persistence.domain.Community.VF_NZ_COMMUNITY_REWRITE_URL;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNull;

/**
 * @author Titov Mykhaylo (titov)
 */
public class UserNotificationServiceImpl implements UserNotificationService, ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserNotificationServiceImpl.class);
    private UserService userService;
    private PaymentDetailsService paymentDetailsService;
    private CommunityResourceBundleMessageSource messageSource;
    private List<String> availableCommunities = Collections.emptyList();
    private NowTechTokenBasedRememberMeServices rememberMeServices;
    private RestTemplate restTemplate = new RestTemplate();
    private String paymentsUrl;
    private String unsubscribeUrl;
    private String tinyUrlService;
    private String rememberMeTokenCookieName;
    private DeviceService deviceService;
    private ApplicationContext applicationContext;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setPaymentDetailsService(PaymentDetailsService paymentDetailsService) {
        this.paymentDetailsService = paymentDetailsService;
    }

    public void setMessageSource(CommunityResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void setDeviceService(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    public void setAvailableCommunities(String[] availableCommunities) {
        if (availableCommunities == null)
            return;

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

    @Async
    @Override
    public Future<Boolean> notifyUserAboutSuccesfullPayment(User user) {
        try {
            LOGGER.debug("input parameters user: [{}]", user);
            if (user == null)
                throw new NullPointerException("The parameter user is null");

            LogUtils.putPaymentMDC(String.valueOf(user.getId()), String.valueOf(user.getUserName()), String.valueOf(user.getUserGroup().getCommunity()
                    .getName()), UserNotificationService.class);

            Future<Boolean> result;
            try {

                result = userService.makeSuccesfullPaymentFreeSMSRequest(user);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                result = new AsyncResult<Boolean>(Boolean.FALSE);
            }
            LOGGER.info("Output parameter result=[{}]", result);
            return result;
        } catch (RuntimeException e){
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

            if (user == null)
                throw new NullPointerException("The parameter user is null");
            if (user.getCurrentPaymentDetails() == null)
                throw new NullPointerException("The parameter user.getCurrentPaymentDetails() is null");

            final UserGroup userGroup = user.getUserGroup();
            final Community community = userGroup.getCommunity();

            LogUtils.putGlobalMDC(user.getId(), user.getMobile(), user.getUserName(), community.getName(), "", this.getClass(), "");

            Future<Boolean> result = new AsyncResult<Boolean>(Boolean.FALSE);

            LOGGER.info("Attempt to send unsubscription confirmation sms async in memory");

            Integer days = Days.daysBetween(new DateTime(Utils.getEpochMillis()).toDateMidnight(), new DateTime(user.getNextSubPayment() * 1000L).toDateMidnight()).getDays();
            if (!rejectDevice(user, "sms.notification.unsubscribed.not.for.device.type")) {
                boolean wasSmsSentSuccessfully = sendSMSWithUrl(user, "sms.unsubscribe.after.text",
                        new String[]{paymentsUrl, days.toString()});

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
        } catch (RuntimeException e){
            LOGGER.error(e.getMessage(), e);
            throw e;
        } finally {
            LogUtils.removeGlobalMDC();
        }
    }

    @Async
    @Override
    public Future<Boolean> sendUnsubscribePotentialSMS(User user) throws UnsupportedEncodingException {
        try {
            LOGGER.debug("input parameters user: [{}]", user);
            if (user == null)
                throw new NullPointerException("The parameter user is null");
            if (user.getCurrentPaymentDetails() == null)
                throw new NullPointerException("The parameter user.getCurrentPaymentDetails() is null");

            final UserGroup userGroup = user.getUserGroup();
            final Community community = userGroup.getCommunity();

            LogUtils.putGlobalMDC(user.getId(), user.getMobile(), user.getUserName(), community.getName(), "", this.getClass(), "");

            Future<Boolean> result = new AsyncResult<Boolean>(Boolean.FALSE);

            LOGGER.info("Attempt to send subscription confirmation sms async in memory");

            if (!rejectDevice(user, "sms.notification.subscribed.not.for.device.type")) {
                PaymentPolicy paymentPolicy = user.getCurrentPaymentDetails().getPaymentPolicy();
                String subcost = paymentPolicy.getSubcost().toString();
                String subweeks = String.valueOf(paymentPolicy.getSubweeks());
                String currencyISO = paymentPolicy.getCurrencyISO();

                boolean wasSmsSentSuccessfully = sendSMSWithUrl(user, "sms.unsubscribe.potential.text", new String[]{unsubscribeUrl, currencyISO, subcost, subweeks});

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
        } catch (RuntimeException e){
            LOGGER.error(e.getMessage(), e);
            throw e;
        } finally {
            LogUtils.removeGlobalMDC();
        }
    }

    @Async
    @Override
    public Future<Boolean> sendSmsOnFreeTrialExpired(User user) throws UnsupportedEncodingException {
        try {
            LOGGER.debug("input parameters user: [{}]", user);
            if (user == null)
                throw new NullPointerException("The parameter user is null");

            final mobi.nowtechnologies.server.persistence.domain.UserStatus userStatus = user.getStatus();
            final String userStatusName = userStatus.getName();
            final List<PaymentDetails> paymentDetailsList = user.getPaymentDetailsList();

            if (userStatusName == null)
                throw new NullPointerException("The parameter userStatusName is null");
            if (paymentDetailsList == null)
                throw new NullPointerException("The parameter paymentDetailsList is null");

            final UserGroup userGroup = user.getUserGroup();
            final Community community = userGroup.getCommunity();

            LogUtils.putGlobalMDC(user.getId(), user.getMobile(), user.getUserName(), community.getName(), "", this.getClass(), "");
            Future<Boolean> result = new AsyncResult<Boolean>(Boolean.FALSE);

            if (userStatusName.equals(UserStatus.LIMITED.name()) && paymentDetailsList.isEmpty()) {
                if (!rejectDevice(user, "sms.notification.limited.not.for.device.type")) {

                    boolean wasSmsSentSuccessfully = sendSMSWithUrl(user, "sms.freeTrialExpired.text", new String[]{paymentsUrl});

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
        } catch (RuntimeException e){
            LOGGER.error(e.getMessage(), e);
            throw e;
        } finally {
            LogUtils.removeGlobalMDC();
        }
    }

    @Async
    @Override
    public Future<Boolean> sendLowBalanceWarning(User user) throws UnsupportedEncodingException {
        try {
            LOGGER.debug("input parameters user: [{}]", user);

            if (user == null)
                throw new NullPointerException("The parameter user is null");
            if (user.getCurrentPaymentDetails() == null)
                throw new NullPointerException("The parameter user.getCurrentPaymentDetails() is null");

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
        } catch (RuntimeException e){
            LOGGER.error(e.getMessage(), e);
            throw e;
        } finally {
            LogUtils.removeGlobalMDC();
        }
    }

    @Async
    @Override
    public Future<Boolean> sendPaymentFailSMS(PendingPayment pendingPayment) {
        try{
            PaymentDetails paymentDetails = pendingPayment.getPaymentDetails();
            User user = pendingPayment.getUser();

            final UserGroup userGroup = user.getUserGroup();
            final Community community = userGroup.getCommunity();

            LogUtils.putGlobalMDC(user.getId(), user.getMobile(), user.getUserName(), community.getName(), "", this.getClass(), "");

            if (!VF_NZ_COMMUNITY_REWRITE_URL.equals(community.getRewriteUrlParameter())) {
                int hoursBefore = pendingPayment.getUser().isBeforeExpiration(pendingPayment.getTimestamp(), 0) ? 0 : 24;
                return new AsyncResult<Boolean>(sendPaymentFailSMS(paymentDetails, hoursBefore));
            }else{
                LOGGER.info("The payment fail sms for vf_nz community user wasn't sent cause it should be send between 8am and 8 pm by separate job");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            LogUtils.removeGlobalMDC();
        }
        return new AsyncResult<Boolean>(Boolean.FALSE);
     }

    @Override
    @Transactional
    public boolean sendPaymentFailSMS(PaymentDetails paymentDetails, int hoursBefore) {
        try {
            LOGGER.debug("input parameters paymentDetails: [{}]", paymentDetails);

            User user = paymentDetails.getOwner();

            final UserGroup userGroup = user.getUserGroup();
            final Community community = userGroup.getCommunity();

            LogUtils.putGlobalMDC(user.getId(), user.getMobile(), user.getUserName(), community.getName(), "", this.getClass(), "");
            boolean wasSmsSentSuccessfully = false;

            final int madeRetries = paymentDetails.getMadeRetries();
            final int retriesOnError = paymentDetails.getRetriesOnError();

            if (madeRetries == retriesOnError) {
                if (!rejectDevice(user, "sms.notification.paymentFail.at." + hoursBefore + "h.not.for.device.type")) {
                    wasSmsSentSuccessfully = sendSMSWithUrl(user, "sms.paymentFail.at." + hoursBefore + "h.text", new String[]{paymentsUrl});

                    if (wasSmsSentSuccessfully) {
                        LOGGER.info("The payment fail sms was sent successfully");
                        paymentDetailsService.update(paymentDetails.withLastFailedPaymentNotificationMillis(Utils.getEpochMillis()));
                    } else {
                        LOGGER.info("The payment fail sms wasn't sent");
                    }
                } else {
                    LOGGER.info("The payment fail sms wasn't sent cause rejecting");
                }
            } else {
                LOGGER.info("The payment fail sms wasn't sent cause madeRetries [{}] and retriesOnError [{}] aren't matched", madeRetries, retriesOnError);
            }
            LOGGER.debug("Output parameter wasSmsSentSuccessfully=[{}]", wasSmsSentSuccessfully);
            return wasSmsSentSuccessfully;
        }catch (Exception e){
            LOGGER.error(e.getMessage(), e);
        } finally {
            LogUtils.removeGlobalMDC();
        }
        return false;
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

                boolean wasSmsSentSuccessfully = sendSMSWithUrl(user, smsPrefix, new String[]{paymentsUrl});

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
        } catch (RuntimeException e){
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

            if(user.getProvider() != null){
                if (!rejectDevice(user, "sms.notification.activation.pin.not.for.device.type")) {

                    String smsPrefix = "sms.activation.pin.text";

                    boolean wasSmsSentSuccessfully = sendSMSWithUrl(user, smsPrefix, new String[]{null, user.getPin()});

                    if (wasSmsSentSuccessfully) {
                        LOGGER.info("The activation pin sms was sent successfully");
                        result = new AsyncResult<Boolean>(Boolean.TRUE);
                    } else {
                        LOGGER.info("The activation pin sms wasn't sent");
                    }
                } else {
                    LOGGER.info("The activation pin sms wasn't sent cause rejecting");
                }
            } else {
                LOGGER.info("The activation pin sms wasn't sent cause user has not enough details(provider etc.)");
            }
            LOGGER.debug("Output parameter result=[{}]", result);
            return result;
        } catch (RuntimeException e){
            LOGGER.error(e.getMessage(), e);
            throw e;
        } finally {
            LogUtils.removeGlobalMDC();
        }
    }

    public boolean sendSMSWithUrl(User user, String msgCode, String[] msgArgs) throws UnsupportedEncodingException {
        LOGGER.debug("input parameters user, msgCode, msgArgs: [{}], [{}]", user, msgCode, msgArgs);

        if (msgCode == null)
            throw new NullPointerException("The parameter msgCode is null");

        final UserGroup userGroup = user.getUserGroup();
        Community community = userGroup.getCommunity();
        String communityUrl = community.getRewriteUrlParameter();

        boolean wasSmsSentSuccessfully = false;

        if (!rejectDevice(user, "sms.notification.not.for.device.type")) {
            if (!deviceService.isPromotedDevicePhone(community, user.getMobile(), null)) {
                if (availableCommunities.contains(communityUrl)) {

                    String baseUrl = msgArgs != null ? msgArgs[0] : null;
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

                    String message = getMessage(user, community, msgCode, msgArgs);

                    if (!StringUtils.isBlank(message)) {
                        String title = messageSource.getMessage(communityUrl, "sms.title", null, null);
                        SMSResponse smsResponse = getSMSProvider(communityUrl).send(user.getMobile(), message, title);
                        if (smsResponse.isSuccessful()) {
                            wasSmsSentSuccessfully = true;
                        } else {
                            LOGGER.error("The sms wasn't sent cause unexpected MIG response [{}]", smsResponse);
                        }
                    } else {
                        LOGGER.info("The sms wasn't sent cause empty sms text message");
                    }
                } else {
                    LOGGER.info("The sms wasn't sent cause unsupported communityUrl [{}]", communityUrl);
                }
            } else {
                LOGGER.info("The sms wasn't sent cause promoted phoneNumber [{}] for communityUrl [{}]", new Object[]{user.getMobile(), communityUrl});
            }
        } else {
            LOGGER.info("The sms wasn't sent cause rejecting");
        }

        LOGGER.debug("Output parameter wasSmsSentSuccessfully=[{}]", wasSmsSentSuccessfully);
        return wasSmsSentSuccessfully;
    }

    public SMSGatewayService getSMSProvider(String communityUrl) {
        String smsProviderBeanName = messageSource.getMessage(communityUrl, "service.bean.smsProvider", null, null);
        return (SMSGatewayService) applicationContext.getBean(smsProviderBeanName);
    }

    public boolean rejectDevice(User user, String code) {
        Community community = user.getUserGroup().getCommunity();
        String communityUrl = community.getRewriteUrlParameter();
        String devices = messageSource.getMessage(communityUrl, code, null, null, null);
        for (String device : devices.split(",")) {
            if (user.getDeviceTypeIdString().equalsIgnoreCase(device)) {
                LOGGER.warn("SMS will not send for User[{}]. See prop:[{}]", user.getUserName(), code);
                return true;
            }
        }
        return false;
    }

    protected String getMessage(User user, Community community, String msgCodeBase, String[] msgArgs) {
        LOGGER.debug("input parameters user, community, msgCodeBase, msgArgs: [{}], [{}], [{}], [{}]", user, community, msgCodeBase, msgArgs);

        if (msgCodeBase == null)
            throw new NullPointerException("The parameter msgCodeBase is null");

        String msg = null;

        String[] codes = new String[25];

        final String providerKey = isNull(user.getProvider()) ? null : user.getProvider().getKey();
        final SegmentType segment = user.getSegment();
        final Contract contract = user.getContract();
        final DeviceType deviceType = user.getDeviceType();
        final PaymentDetails paymentDetails = user.getCurrentPaymentDetails();
        String deviceTypeName = null;

        if (deviceType != null) {
            deviceTypeName = deviceType.getName();
        }

        codes[0] = msgCodeBase;
        codes[1] = getCode(codes, 0, providerKey, true);
        codes[2] = getCode(codes, 1, segment, true);
        codes[3] = getCode(codes, 2, contract, true);
        codes[4] = getCode(codes, 3, deviceTypeName, true);
        codes[5] = getCode(codes, 4, Tariff._4G.equals(user.getTariff()) ? "VIDEO" : null, true);
        codes[6] = getCode(codes, 5, paymentDetails != null ? paymentDetails.getPaymentType() : null, true);

        if(paymentDetails != null){
            PaymentPolicy paymentPolicy = paymentDetails.getPaymentPolicy();
            String prefix = "before";
            final String preProviderKey = isNull(paymentPolicy.getProvider()) ? null : paymentPolicy.getProvider().getKey();
            final SegmentType preSegment = paymentPolicy.getSegment();
            final Contract preContract = paymentPolicy.getContract();
            final String providerSuffix = prefix+"."+preProviderKey;
            final String segmentSuffix = providerSuffix+"."+preSegment;
            final String contractSuffix = segmentSuffix+"."+preContract;
            for(int i = 1; i <= 6; i++){
                if(!StringUtils.equals(preProviderKey, providerKey))
                    codes[1*6+i] = getCode(codes, i, providerSuffix, false);
                if(segment != preSegment)
                    codes[2*6+i] = getCode(codes, i, segmentSuffix, false);
                if(contract != preContract)
                    codes[3*6+i] = getCode(codes, i, contractSuffix, false);
            }
        }

        for (int i = codes.length - 1; i >= 0; i--) {
            if (codes[i] != null) {
                msg = messageSource.getMessage(community.getRewriteUrlParameter(), codes[i], msgArgs, "", null);
                if (StringUtils.isNotEmpty(msg))
                    break;
            }
        }

        if (msg == null) {
            LOGGER.warn("A user has not received sms notification because no message was found. getMessage( [{}], [{}])", user.getId(), msgCodeBase);
        }

        LOGGER.debug("Output parameter msg=[{}]", msg);
        return msg;
    }

    private String getCode(String[] codes, int i, Object value, boolean recursive) {
        LOGGER.debug("input parameters codes, i, value: [{}], [{}], [{}]", codes, i, value);

        if (codes == null)
            throw new NullPointerException("The parameter codes is null");
        if (codes.length == 0)
            throw new IllegalArgumentException("The parameter codes of array type has 0 size");
        if (codes[0] == null) {
            throw new IllegalArgumentException("The parameter codes of array type has null value as first element");
        }
        if (i >= codes.length)
            throw new IllegalArgumentException("The parameter i>=codes.length. i=" + i);
        if (i < 0)
            throw new IllegalArgumentException("The parameter i less than 0. i=" + i);

        String code = null;

        if (value != null) {
            final String prefix = codes[i];
            if (prefix != null) {
                if (i == 0) {
                    code = prefix + ".for." + value;
                } else {
                    code = prefix + "." + value;
                }
            } else if(recursive){
                code = getCode(codes, i - 1, value, recursive);
            }
        }
        LOGGER.debug("Output parameter code=[{}]", code);
        return code;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
