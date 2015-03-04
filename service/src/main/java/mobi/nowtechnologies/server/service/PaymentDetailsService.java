package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.common.dto.PaymentDetailsDto;
import mobi.nowtechnologies.server.persistence.dao.PaymentDetailsDao;
import mobi.nowtechnologies.server.persistence.dao.PaymentPolicyDao;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.domain.payment.*;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.exception.CanNotDeactivatePaymentDetailsException;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.payment.MigPaymentService;
import mobi.nowtechnologies.server.service.payment.PSMSPaymentService;
import mobi.nowtechnologies.server.service.payment.PayPalPaymentService;
import mobi.nowtechnologies.server.service.payment.SagePayPaymentService;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.web.PaymentDetailsByPaymentDto;
import mobi.nowtechnologies.server.shared.dto.web.payment.CreditCardDto;
import mobi.nowtechnologies.server.shared.dto.web.payment.PSmsDto;
import mobi.nowtechnologies.server.shared.dto.web.payment.PayPalDto;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static mobi.nowtechnologies.common.dto.UserRegInfo.PaymentType.*;
import static mobi.nowtechnologies.server.persistence.domain.PromoCode.PROMO_CODE_FOR_FREE_TRIAL_BEFORE_SUBSCRIBE;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNotNull;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNull;
import static org.apache.commons.lang.Validate.notNull;

/**
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kolpakov (akolpakov)
 */
public class PaymentDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentDetailsService.class);

    private CommunityResourceBundleMessageSource messageSource;
    private PaymentDetailsDao paymentDetailsDao;
    private PaymentPolicyService paymentPolicyService;
    private SagePayPaymentService sagePayPaymentService;
    private PayPalPaymentService payPalPaymentService;
    private MigPaymentService migPaymentService;

    private PSMSPaymentService<O2PSMSPaymentDetails> o2PaymentService;

    private PromotionService promotionService;
    private UserService userService;
    private CommunityService communityService;
    private OfferService offerService;
    private PaymentPolicyRepository paymentPolicyRepository;
    private PaymentPolicyDao paymentPolicyDao;
    private PaymentDetailsRepository paymentDetailsRepository;
    private UserRepository userRepository;

    public void setPaymentDetailsDao(PaymentDetailsDao paymentDetailsDao) {
        this.paymentDetailsDao = paymentDetailsDao;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setPaymentPolicyService(PaymentPolicyService paymentPolicyService) {
        this.paymentPolicyService = paymentPolicyService;
    }

    public void setSagePayPaymentService(SagePayPaymentService sagePayPaymentService) {
        this.sagePayPaymentService = sagePayPaymentService;
    }

    public void setPayPalPaymentService(PayPalPaymentService payPalPaymentService) {
        this.payPalPaymentService = payPalPaymentService;
    }

    public void setMigPaymentService(MigPaymentService migPaymentService) {
        this.migPaymentService = migPaymentService;
    }

    public void setPromotionService(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setCommunityService(CommunityService communityService) {
        this.communityService = communityService;
    }

    public void setOfferService(OfferService offerService) {
        this.offerService = offerService;
    }

    public void setPaymentPolicyRepository(PaymentPolicyRepository paymentPolicyRepository) {
        this.paymentPolicyRepository = paymentPolicyRepository;
    }

    public void setPaymentPolicyDao(PaymentPolicyDao paymentPolicyDao) {
        this.paymentPolicyDao = paymentPolicyDao;
    }

    public void setO2PaymentService(PSMSPaymentService<O2PSMSPaymentDetails> o2PaymentService) {
        this.o2PaymentService = o2PaymentService;
    }

    public void setPaymentDetailsRepository(PaymentDetailsRepository paymentDetailsRepository) {
        this.paymentDetailsRepository = paymentDetailsRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public PaymentDetails createPaymentDetails(PaymentDetailsDto dto, User user, Community community) throws ServiceException {

        PaymentPolicy paymentPolicy = paymentPolicyService.getPaymentPolicy(dto.getPaymentPolicyId());
        Promotion promotion = user.getPotentialPromotion();
        PromotionPaymentPolicy promotionPaymentPolicy = null;
        if (null != promotion) {
            promotionPaymentPolicy = paymentDetailsDao.getPromotionPaymentPolicy(promotion, paymentPolicy);
        }

        PaymentDetails paymentDetails = null;
        if (null != paymentPolicy) {
            if (dto.getPaymentType().equals(CREDIT_CARD)) {
                dto.setCurrency(paymentPolicy.getCurrencyISO());
                dto.setAmount(paymentPolicy.getSubcost().toString());
                dto.setVendorTxCode(UUID.randomUUID().toString());
                dto.setDescription("Creating payment details for user " + user.getUserName());
                paymentDetails = sagePayPaymentService.createPaymentDetails(dto, user, paymentPolicy);
            }
            else if (dto.getPaymentType().equals(PAY_PAL)) {
                paymentDetails = payPalPaymentService.createPaymentDetails(dto.getBillingAgreementDescription(), dto.getSuccessUrl(), dto.getFailUrl(), user, paymentPolicy);
            }
            else if (dto.getPaymentType().equals(PREMIUM_USER)) {
                PaymentDetails pendingPaymentDetails = user.getPendingPaymentDetails();
                if (null != pendingPaymentDetails) {
                    pendingPaymentDetails.setLastPaymentStatus(PaymentDetailsStatus.ERROR);
                    pendingPaymentDetails.setDescriptionError("Was not verified and replaced by another payment details");
                }
                paymentDetails = migPaymentService.createPaymentDetails(dto.getPhoneNumber(), user, community, paymentPolicy);
            }
            else if (dto.getPaymentType().equals(O2_PSMS)) {
                paymentDetails = o2PaymentService.commitPaymentDetails(user, paymentPolicy);
            }

            if (null != paymentDetails) {
                if (null != promotion) {
                    paymentDetails.setPromotionPaymentPolicy(promotionPaymentPolicy);
                    promotionService.incrementUserNumber(promotion);
                }

                paymentDetails = paymentDetailsDao.update(paymentDetails);
            }
        }

        return paymentDetails;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public SagePayCreditCardPaymentDetails createCreditCardPaymentDetails(CreditCardDto dto, String communityUrl, int userId) throws ServiceException {
        User user = userService.findById(userId);
        Community community = communityService.getCommunityByUrl(communityUrl);

        applyPromoToLimitedUsers(user, community);
        PaymentDetailsDto pdto = CreditCardDto.toPaymentDetails(dto);

        return (SagePayCreditCardPaymentDetails) createPaymentDetails(pdto, user, community);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public PayPalPaymentDetails createPayPalPaymentDetails(PayPalDto dto, String communityUrl, int userId) throws ServiceException {
        User user = userService.findById(userId);
        Community community = communityService.getCommunityByUrl(communityUrl);
        PaymentDetailsDto pdto = PayPalDto.toPaymentDetails(dto);
        return (PayPalPaymentDetails) createPaymentDetails(pdto, user, community);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public PayPalPaymentDetails commitPayPalPaymentDetails(String token, Integer paymentPoliceId, String communityUrl, int userId) throws ServiceException {
        User user = userService.findById(userId);

        Community community = communityService.getCommunityByUrl(communityUrl);
        applyPromoToLimitedUsers(user, community);
        PaymentPolicy paymentPolicy = paymentPolicyService.getPaymentPolicy(paymentPoliceId);

        return payPalPaymentService.commitPaymentDetails(token, user, paymentPolicy, true);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public MigPaymentDetails createMigPaymentDetails(PSmsDto dto, String communityUrl, int userId) throws ServiceException {
        User user = userService.findById(userId);

        Community community = communityService.getCommunityByUrl(communityUrl);
        PaymentDetailsDto pdto = PSmsDto.toPaymentDetails(dto);
        String convertedPhone = userService.convertPhoneNumberFromGreatBritainToInternationalFormat(dto.getPhone());
        pdto.setPhoneNumber(userService.getMigPhoneNumber(dto.getOperator(), convertedPhone));
        return (MigPaymentDetails) createPaymentDetails(pdto, user, community);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public O2PSMSPaymentDetails createDefaultO2PsmsPaymentDetails(User user) throws ServiceException {

        PaymentPolicy defaultPaymentPolicy = paymentPolicyService.findDefaultO2PsmsPaymentPolicy(user);

        if (isNull(defaultPaymentPolicy)) {
            throw new ServiceException("could.not.create.default.paymentDetails", "Couldn't create default payment details");
        }

        Community community = user.getUserGroup().getCommunity();
        PaymentDetailsDto paymentDetailsDto = new PaymentDetailsDto();
        paymentDetailsDto.setPaymentType(O2_PSMS);
        paymentDetailsDto.setPaymentPolicyId(defaultPaymentPolicy.getId());

        return (O2PSMSPaymentDetails) createPaymentDetails(paymentDetailsDto, user, community);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public MigPaymentDetails commitMigPaymentDetails(String pin, int userId) {
        User user = userService.findById(userId);
        return migPaymentService.commitPaymnetDetails(user, pin);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public PaymentDetails getPendingPaymentDetails(int userId) {
        User user = userService.findById(userId);
        return user.getPendingPaymentDetails();
    }

    @Transactional(readOnly = true)
    public List<PaymentDetails> findActivatedPaymentDetails(String operatorName, String phoneNumber) {
        return paymentDetailsRepository.findActivatedPaymentDetails(operatorName, phoneNumber);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<Operator> getAvailableOperators(String communityUrl, String paymentType) {
        Community community = communityService.getCommunityByUrl(communityUrl);
        return paymentDetailsDao.getAvailableOperators(community, paymentType);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void buyByPayPalPaymentDetails(String token, String communityUrl, int userId, Integer offerId) throws ServiceException {
        LOGGER.debug("buyByPayPalPaymentDetails input parameters token, communityUrl, userId, offerId: [{}], [{}], [{}], [{}]", new Object[] {token, communityUrl, userId, offerId});

        User user = userService.findById(userId);
        Community community = communityService.getCommunityByUrl(communityUrl);
        PaymentPolicy paymentPolicy = paymentPolicyDao.getPaymentPolicy(user.getOperator(), PAY_PAL, community.getId());

        if (null != paymentPolicy) {
            Offer offer = offerService.getOffer(offerId);

            PaymentDetailsDto pdto = new PaymentDetailsDto();
            pdto.setOfferId(offerId);
            pdto.setCurrency(offer.getCurrency());
            pdto.setAmount(offer.getPrice().toString());
            pdto.setToken(token);

            payPalPaymentService.makePaymentWithPaymentDetails(pdto, user, paymentPolicy);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void buyByCreditCardPaymentDetails(CreditCardDto creditCardDto, String communityUrl, int userId, Integer offerId) {
        LOGGER
            .debug("buyByCreditCardPaymentDetails input parameters creditCardDto, communityUrl, userId, offerId: [{}], [{}], [{}], [{}]", new Object[] {creditCardDto, communityUrl, userId, offerId});

        User user = userService.findById(userId);
        Community community = communityService.getCommunityByUrl(communityUrl);
        PaymentPolicy paymentPolicy = paymentPolicyService.getPaymentPolicy(user.getOperator(), CREDIT_CARD, community.getId());

        if (null != paymentPolicy) {
            Offer offer = offerService.getOffer(offerId);

            PaymentDetailsDto pdto = CreditCardDto.toPaymentDetails(creditCardDto);
            pdto.setOfferId(offerId);
            pdto.setCurrency(offer.getCurrency());
            pdto.setAmount(offer.getPrice().toString());
            pdto.setVendorTxCode(UUID.randomUUID().toString());
            pdto.setDescription("Making payment by Credit Card  for user " + user.getUserName());

            sagePayPaymentService.makePaymentWithPaymentDetails(pdto, user, paymentPolicy);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean resendPin(int userId, String phone, String communityUri) throws ServiceException {
        User user = userService.findById(userId);
        String code = Utils.getRandomString(4);
        user.setPin(code);
        userService.updateUser(user);
        Object[] args = {code};
        return migPaymentService.sendPin(phone, messageSource.getMessage(communityUri, "sms.freeMsg", args, null));
    }

    public void setMessageSource(CommunityResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public PaymentDetails activatePaymentDetailsByPayment(Long paymentDetailsId) {
        LOGGER.debug("input parameters paymentDetailsId: [{}]", paymentDetailsId);

        final PaymentDetails paymentDetails = paymentDetailsDao.find(paymentDetailsId);
        final User user = paymentDetails.getOwner();
        PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();
        if (currentPaymentDetails != null) {
            currentPaymentDetails.setActivated(false);
            paymentDetailsDao.update(currentPaymentDetails);
        }

        paymentDetails.setActivated(true);
        paymentDetailsDao.update(paymentDetails);

        user.setCurrentPaymentDetails(paymentDetails);

        userService.updateUser(user);

        LOGGER.debug("Output parameter [{}]", paymentDetails);
        return paymentDetails;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public PaymentDetailsByPaymentDto getPaymentDetailsTypeByPayment(int userId) {
        LOGGER.debug("input parameters userId: [{}]", userId);

        List<PaymentDetails> paymentDetailsList = paymentDetailsDao.find(userId, PaymentDetailsType.PAYMENT);

        final PaymentDetailsByPaymentDto paymentDetailsByPaymentDto;
        if (paymentDetailsList.isEmpty()) {
            paymentDetailsByPaymentDto = null;
        }
        else {
            paymentDetailsByPaymentDto = paymentDetailsList.get(0).toPaymentDetailsByPaymentDto();
        }

        LOGGER.debug("Output parameter [{}]", paymentDetailsByPaymentDto);
        return paymentDetailsByPaymentDto;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public PaymentDetails update(PaymentDetails paymentDetails) {
        LOGGER.debug("input parameters paymentDetails: [{}]", paymentDetails);

        paymentDetails = paymentDetailsRepository.save(paymentDetails);

        LOGGER.info("Output parameter paymentDetails=[{}]", paymentDetails);
        return paymentDetails;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = CanNotDeactivatePaymentDetailsException.class)
    public User deactivateCurrentPaymentDetailsIfOneExist(User user, String reason) {
        LOGGER.info("Deactivate current payment details for user {} reason {}", user.shortInfo(), reason);


        notNull(user, "The parameter user is null");
        user = userService.setToZeroSmsAccordingToLawAttributes(user);

        PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();

        if (isNotNull(currentPaymentDetails)) {
            boolean inPending = currentPaymentDetails.getLastPaymentStatus() == PaymentDetailsStatus.AWAITING;
            if (inPending) {
                throw new CanNotDeactivatePaymentDetailsException();
            }
            disablePaymentDetails(currentPaymentDetails, reason);
            user = userService.updateUser(user);
        }

        LOGGER.info("Current payment details were deactivated for user {}", user.shortInfo());
        return user;
    }

    private void applyPromoToLimitedUsers(User user, Community community) {
        if (user.isLimited()) {

            Promotion twoWeeksTrial = promotionService.getActivePromotion(PROMO_CODE_FOR_FREE_TRIAL_BEFORE_SUBSCRIBE, community.getName());
            long now = System.currentTimeMillis();
            int dbSecs = (int) (now / 1000); // in db we keep time in seconds not milliseconds
            if (twoWeeksTrial != null && twoWeeksTrial.getStartDate() < dbSecs && dbSecs < twoWeeksTrial.getEndDate()) {
                promotionService.applyPromotionByPromoCode(user, twoWeeksTrial);
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public PaymentDetails disablePaymentDetails(PaymentDetails paymentDetail, String reason) {
        LOGGER.debug("Disable payment details {}", paymentDetail);
        return update(paymentDetail.withActivated(false).withDisableTimestampMillis(Utils.getEpochMillis()).withDescriptionError(reason));
    }

    @Transactional(readOnly = true)
    public List<PaymentDetails> findFailedPaymentWithNoNotificationPaymentDetails(String communityUrl, Pageable pageable) {
        return paymentDetailsRepository.findFailedPaymentWithNoNotificationPaymentDetails(communityUrl, pageable);
    }
}
