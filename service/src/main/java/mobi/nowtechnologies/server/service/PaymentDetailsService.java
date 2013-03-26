package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.common.dto.PaymentDetailsDto;
import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.common.dto.UserRegInfo.PaymentType;
import mobi.nowtechnologies.server.persistence.dao.PaymentDetailsDao;
import mobi.nowtechnologies.server.persistence.dao.PaymentPolicyDao;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.domain.enums.SegmentType;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.payment.MigPaymentService;
import mobi.nowtechnologies.server.service.payment.O2PaymentService;
import mobi.nowtechnologies.server.service.payment.PayPalPaymentService;
import mobi.nowtechnologies.server.service.payment.SagePayPaymentService;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.PaymentPolicyDto;
import mobi.nowtechnologies.server.shared.dto.web.PaymentDetailsByPaymentDto;
import mobi.nowtechnologies.server.shared.dto.web.payment.CreditCardDto;
import mobi.nowtechnologies.server.shared.dto.web.payment.PSmsDto;
import mobi.nowtechnologies.server.shared.dto.web.payment.PayPalDto;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static mobi.nowtechnologies.server.shared.Utils.isNull;
import static org.apache.commons.lang.Validate.notNull;

/**
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kolpakov (akolpakov)
 *
 */
public class PaymentDetailsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PaymentDetailsService.class);

	private CommunityResourceBundleMessageSource messageSource;

	private PaymentDetailsDao paymentDetailsDao;

	private PaymentPolicyService paymentPolicyService;

	private SagePayPaymentService sagePayPaymentService;

	private PayPalPaymentService payPalPaymentService;

	private MigPaymentService migPaymentService;
	
	private O2PaymentService o2PaymentService;

	private PromotionService promotionService;

	private UserService userService;
	private CommunityService communityService;

	private OfferService offerService;

    private PaymentPolicyRepository paymentPolicyRepository;
    private PaymentPolicyDao paymentPolicyDao;
    
    private PaymentDetailsRepository paymentDetailsRepository;

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
			if (dto.getPaymentType().equals(PaymentType.CREDIT_CARD)) {
				dto.setCurrency(paymentPolicy.getCurrencyISO());
				dto.setAmount(paymentPolicy.getSubcost().toString());
				dto.setVendorTxCode(UUID.randomUUID().toString());
				dto.setDescription("Creating payment details for user " + user.getUserName());
				paymentDetails = sagePayPaymentService.createPaymentDetails(dto, user, paymentPolicy);
			} else if (dto.getPaymentType().equals(PaymentType.PAY_PAL)) {
				paymentDetails = payPalPaymentService.createPaymentDetails(dto.getBillingAgreementDescription(), dto.getSuccessUrl(), dto.getFailUrl(), user, paymentPolicy);
			} else if (dto.getPaymentType().equals(PaymentType.PREMIUM_USER)) {
				PaymentDetails pendingPaymentDetails = user.getPendingPaymentDetails();
				if (null != pendingPaymentDetails) {
					pendingPaymentDetails.setLastPaymentStatus(PaymentDetailsStatus.ERROR);
					pendingPaymentDetails.setDescriptionError("Was not verified and replaced by another payment details");
				}
				paymentDetails = migPaymentService.createPaymentDetails(dto.getPhoneNumber(), user, community, paymentPolicy);
			}else if(dto.getPaymentType().equals(PaymentType.O2_PSMS)){
				paymentDetails = o2PaymentService.createPaymentDetails(dto.getPhoneNumber(), user, paymentPolicy);
			}

			if (null != paymentDetails) {
				if (null != promotion)
				{
					paymentDetails.setPromotionPaymentPolicy(promotionPaymentPolicy);
					promotionService.incrementUserNumber(promotion);
				}

				paymentDetails = paymentDetailsDao.update(paymentDetails);
			}
		}

		return paymentDetails;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public SagePayCreditCardPaymentDetails createCreditCardPamentDetails(CreditCardDto dto, String communityUrl, int userId) throws ServiceException {
		PaymentDetailsDto pdto = CreditCardDto.toPaymentDetails(dto);
		User user = userService.findById(userId);
		Community community = communityService.getCommunityByUrl(communityUrl);
		return (SagePayCreditCardPaymentDetails) createPaymentDetails(pdto, user, community);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public PayPalPaymentDetails createPayPalPamentDetails(PayPalDto dto, String communityUrl, int userId) throws ServiceException {
		User user = userService.findById(userId);
		Community community = communityService.getCommunityByUrl(communityUrl);
		PaymentDetailsDto pdto = PayPalDto.toPaymentDetails(dto);
		return (PayPalPaymentDetails) createPaymentDetails(pdto, user, community);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public PayPalPaymentDetails commitPayPalPaymentDetails(String token, Integer paymentPoliceId, int userId) throws ServiceException {
		User user = userService.findById(userId);
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
	public MigPaymentDetails commitMigPaymentDetails(String pin, int userId) {
		User user = userService.findById(userId);
		return migPaymentService.commitPaymnetDetails(user, pin);
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<PaymentPolicyDto> getPaymentPolicyWithOutSegment(Community community, User user) {
		List<PaymentPolicy> paymentPolicies = paymentPolicyRepository.getPaymentPoliciesWithOutSegment(community);
		return mergePaymentPolicies(user, paymentPolicies);
	}

    @Transactional
    public List<PaymentPolicyDto> getPaymentPolicy(Community community, User user, SegmentType segment) {
        if(isNull(segment))
            segment = SegmentType.BUSINESS;
        List<PaymentPolicy> paymentPolicies = paymentPolicyRepository.getPaymentPolicies(community, segment);
        return mergePaymentPolicies(user, paymentPolicies);
    }

	private List<PaymentPolicyDto> mergePaymentPolicies(User user, List<PaymentPolicy> paymentPolicies) {
		List<PaymentPolicyDto> result = new LinkedList<PaymentPolicyDto>();
		for (PaymentPolicy paymentPolicy : paymentPolicies) {
            Promotion potentialPromotion = user.getPotentialPromotion();
            if (null != potentialPromotion) {
				List<PromotionPaymentPolicy> promotionPaymentPolicies = potentialPromotion.getPromotionPaymentPolicies();
				boolean inList = false;
				for (PromotionPaymentPolicy promotionPolicy : promotionPaymentPolicies) {
					if (promotionPolicy.getPaymentPolicies().contains(paymentPolicy)) {
						result.add(new PaymentPolicyDto(paymentPolicy, promotionPolicy));
						inList = true;
					}
				}
				if (!inList) {
					result.add(paymentPolicyService.getPaymentPolicy(paymentPolicy, null));
				}
			} else {
				result.add(paymentPolicyService.getPaymentPolicy(paymentPolicy, null));
			}
		}
		return result;
	}
	
	public PaymentPolicyDto getPaymentPolicy(Integer paymentPolicyId)
	{
		PaymentPolicy paymentPolicy = paymentPolicyService.getPaymentPolicy(paymentPolicyId);
		return paymentPolicyService.getPaymentPolicy(paymentPolicy, null);
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public PaymentDetails getPendingPaymentDetails(int userId) {
		User user = userService.findById(userId);
		return user.getPendingPaymentDetails();
	}

	public static PaymentDetails getPaymentDetails(final String paymentType, final String token, final Operator operator, final String phoneNumber) {
		notNull(paymentType, "The parameter paymentType is null");
		LOGGER.debug("input parameters paymentType, token: [{}], [{}]",paymentType, token );

		PaymentDetails paymentDetails;
		if (paymentType.equals(UserRegInfo.PaymentType.CREDIT_CARD)) {
			paymentDetails = new SagePayCreditCardPaymentDetails();
		} else if (paymentType.equals(UserRegInfo.PaymentType.PREMIUM_USER)) {
			paymentDetails = new MigPaymentDetails();
		} else if (paymentType.equals(UserRegInfo.PaymentType.PAY_PAL)) {
			paymentDetails = new PayPalPaymentDetails();
		} else
			throw new ServiceException("Unknown payment type: [" + paymentType
					+ "]");

		paymentDetails = populatePaymentDetails(paymentDetails, paymentType,
				token, operator, phoneNumber);

		LOGGER.debug("Output parameter paymentDetails=[{}]", paymentDetails);
		return paymentDetails;
	}

	public PaymentDetails findPaymentDetails(String paymentType, int userId) {
		if (paymentType == null)
			throw new ServiceException("The parameter paymentType is null");

		LOGGER.debug("input parameters paymentType, userId: [{}], [{}]",
				new Object[] { paymentType, userId });

		PaymentDetails paymentDetails = paymentDetailsDao.findPaymentDetails(
				paymentType, userId);

		LOGGER.debug("Output parameter paymentDetails=[{}]", paymentDetails);
		return paymentDetails;
	}

	public static PaymentDetails populatePaymentDetails(PaymentDetails paymentDetails, final String paymentType, final String token, final Operator operator, final String phoneNumber) {
		notNull(paymentType, "The parameter paymentType is null");
		LOGGER.debug("input parameters paymentDetails, paymentType, token, operator, phoneNumber: [{}], [{}], [{}], [{}], [{}]",
						paymentDetails, paymentType, token, operator, phoneNumber);

		if (paymentType.equals(UserRegInfo.PaymentType.CREDIT_CARD)) {
			SagePayCreditCardPaymentDetails creditCardPaymentDetails = (SagePayCreditCardPaymentDetails) paymentDetails;

            notNull(token, "The parameter token is null");
			creditCardPaymentDetails.setVPSTxId(token);
		} else if (paymentType.equals(UserRegInfo.PaymentType.PREMIUM_USER)) {
			MigPaymentDetails premiumSmsPaymentDetails = (MigPaymentDetails) paymentDetails;

            notNull(operator, "The parameter operator is null");
            notNull(phoneNumber, "The parameter phoneNumber is null");

			premiumSmsPaymentDetails.setMigPhoneNumber(operator.getMigName() + "." + phoneNumber);

		} else if (paymentType.equals(UserRegInfo.PaymentType.PAY_PAL)) {
			PayPalPaymentDetails payPalPaymentDetails = (PayPalPaymentDetails) paymentDetails;

            notNull(token, "The parameter token is null");
			payPalPaymentDetails.setBillingAgreementTxId(token);

		} else
			throw new ServiceException("Unknown payment type: [" + paymentType + "]");

		paymentDetails.setCreationTimestampMillis(System.currentTimeMillis());

		LOGGER.debug("Output parameter paymentDetails=[{}]", paymentDetails);
		return paymentDetails;
	}

	@Transactional(readOnly = true)
	public List<PaymentDetails> findPaymentDetails(String operatorName, String phoneNumber) {
		return paymentDetailsRepository.findPaymentDetails(operatorName, phoneNumber);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public PaymentDetails updatePaymentDetailsWithPromotion(PaymentDetails paymentDetails, PromotionPaymentPolicy promotionPaymentPolicy) {
		paymentDetails.setPromotionPaymentPolicy(promotionPaymentPolicy);
		return paymentDetailsDao.update(paymentDetails);
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<Operator> getAvailableOperators(String communityUrl, String paymentType) {
		Community community = communityService.getCommunityByUrl(communityUrl);
		return paymentDetailsDao.getAvailableOperators(community, paymentType);
	}

	public void setPaymentDetailsDao(PaymentDetailsDao paymentDetailsDao) {
		this.paymentDetailsDao = paymentDetailsDao;
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

	@Transactional(propagation = Propagation.REQUIRED)
	public void buyByPayPalPaymentDetails(String token, String communityUrl, int userId, Integer offerId) throws ServiceException {
		LOGGER.debug("buyByPayPalPaymentDetails input parameters token, communityUrl, userId, offerId: [{}], [{}], [{}], [{}]", new Object[] { token, communityUrl, userId, offerId });
		
		User user = userService.findById(userId);
		Community community = communityService.getCommunityByUrl(communityUrl);
		PaymentPolicy paymentPolicy = paymentPolicyDao.getPaymentPolicy(user.getOperator(), PaymentType.PAY_PAL, community.getId());
		
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
		LOGGER.debug("buyByCreditCardPaymentDetails input parameters creditCardDto, communityUrl, userId, offerId: [{}], [{}], [{}], [{}]", new Object[] { creditCardDto, communityUrl, userId, offerId });
		
		User user = userService.findById(userId);
		Community community = communityService.getCommunityByUrl(communityUrl);
		PaymentPolicy paymentPolicy = paymentPolicyService.getPaymentPolicy(user.getOperator(), PaymentType.CREDIT_CARD, community.getId());

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
		Object[] args = { code };
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
		} else {
			paymentDetailsByPaymentDto = paymentDetailsList.get(0).toPaymentDetailsByPaymentDto();
		}

		LOGGER.debug("Output parameter [{}]", paymentDetailsByPaymentDto);
		return paymentDetailsByPaymentDto;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public PaymentDetails update(PaymentDetails paymentDetails){
		LOGGER.debug("input parameters paymentDetails: [{}]", paymentDetails);
		
		paymentDetails = paymentDetailsDao.update(paymentDetails);
		
		LOGGER.info("Output parameter paymentDetails=[{}]", paymentDetails);
		return paymentDetails;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public User deactivateCurrentPaymentDetailsIfOneExist(User user, String reason) {
		LOGGER.debug("input parameters user, reason: [{}], [{}]", user, reason);

        notNull(user, "The parameter user is null");
		user = userService.setToZeroSmsAccordingToLawAttributes(user);

		PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();
		
		if(currentPaymentDetails!=null){
			currentPaymentDetails.setActivated(false);
			currentPaymentDetails.setDisableTimestampMillis(Utils.getEpochMillis());
			currentPaymentDetails.setDescriptionError(reason);

			currentPaymentDetails = update(currentPaymentDetails);
			
			user.setLastPaymentTryInCycleMillis(0L);
			userService.updateUser(user);
		}
		
		LOGGER.info("Output parameter user=[{}]", user);
		return user;
	}

    public void setPaymentPolicyRepository(PaymentPolicyRepository paymentPolicyRepository) {
        this.paymentPolicyRepository = paymentPolicyRepository;
    }

    public void setPaymentPolicyDao(PaymentPolicyDao paymentPolicyDao) {
        this.paymentPolicyDao = paymentPolicyDao;
    }
    
    public void setO2PaymentService(O2PaymentService o2PaymentService) {
		this.o2PaymentService = o2PaymentService;
	}
    
    public void setPaymentDetailsRepository(PaymentDetailsRepository paymentDetailsRepository) {
		this.paymentDetailsRepository = paymentDetailsRepository;
	}

}