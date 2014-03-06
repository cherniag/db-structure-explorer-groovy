package mobi.nowtechnologies.server.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import mobi.nowtechnologies.server.persistence.dao.UserStatusDao;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetailsType;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.service.ITunesService;
import mobi.nowtechnologies.server.service.PaymentPolicyService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.event.PaymentEvent;
import mobi.nowtechnologies.server.service.payment.SubmitedPaymentService;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.ITunesInAppSubscriptionRequestDto;
import mobi.nowtechnologies.server.shared.dto.ITunesInAppSubscriptionResponseDto;
import mobi.nowtechnologies.server.shared.dto.ITunesInAppSubscriptionResponseDto.Receipt;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import mobi.nowtechnologies.server.shared.service.BasicResponse;
import mobi.nowtechnologies.server.shared.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.http.HttpStatus;

/**
 * @author Titov Mykhaylo (titov)
 */
public class ITunesServiceImpl implements ITunesService, ApplicationEventPublisherAware {

	private static final Logger LOGGER = LoggerFactory.getLogger(ITunesServiceImpl.class);

    private CommunityResourceBundleMessageSource communityResourceBundleMessageSource;
	private PostService postService;
	private PaymentPolicyService paymentPolicyService;
	private UserService userService;
	private ApplicationEventPublisher applicationEventPublisher;
	private SubmitedPaymentService submitedPaymentService;

    private static JsonParser jsonParser = new JsonParser();
	private static Gson gson = new Gson();

	public void setPostService(PostService postService) {
		this.postService = postService;
	}

	public void setPaymentPolicyService(PaymentPolicyService paymentPolicyService) {
		this.paymentPolicyService = paymentPolicyService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}
	
	public void setSubmitedPaymentService(SubmitedPaymentService submitedPaymentService) {
		this.submitedPaymentService = submitedPaymentService;
	}

    public void setCommunityResourceBundleMessageSource(CommunityResourceBundleMessageSource communityResourceBundleMessageSource) {
        this.communityResourceBundleMessageSource = communityResourceBundleMessageSource;
    }

    @Override
	public BasicResponse processInAppSubscription(int userId, String transactionReceipt) {
		LOGGER.debug("input parameters userId, transactionReceipt: [{}], [{}]", userId, transactionReceipt);

		final BasicResponse BasicResponse;
		User user = userService.findById(userId);

		if (user != null &&(user.getCurrentPaymentDetails() == null || !user.getCurrentPaymentDetails().isActivated()) && (((user.getBase64EncodedAppStoreReceipt() != null && user.getStatus().getI() == UserStatusDao.getLimitedUserStatus().getI())
				|| transactionReceipt != null))) {

            String iTunesUrl = communityResourceBundleMessageSource.getMessage(user.getUserGroup().getCommunity().getRewriteUrlParameter(), "apple.inApp.iTunesUrl", null, null);
            String password = communityResourceBundleMessageSource.getMessage(user.getUserGroup().getCommunity().getRewriteUrlParameter(), "apple.inApp.password", null, null);

            final String base64EncodedAppStoreReceipt;
            if (user.getBase64EncodedAppStoreReceipt() == null || transactionReceipt != null) {
                base64EncodedAppStoreReceipt = transactionReceipt;
            } else {
                base64EncodedAppStoreReceipt = user.getBase64EncodedAppStoreReceipt();
            }

            ITunesInAppSubscriptionRequestDto iTunesInAppSubscriptionRequestDto = new ITunesInAppSubscriptionRequestDto();
            iTunesInAppSubscriptionRequestDto.setPassword(password);
            iTunesInAppSubscriptionRequestDto.setReceiptData(base64EncodedAppStoreReceipt);

            String body = gson.toJson(iTunesInAppSubscriptionRequestDto);


			LOGGER.info("Trying to validate in-app subscription with following params [{}]", iTunesInAppSubscriptionRequestDto);
			BasicResponse = postService.sendHttpPost(iTunesUrl, null, body);

			if (BasicResponse.getStatusCode() == HttpStatus.OK.value()) {
                ITunesInAppSubscriptionResponseDto iTunesResponseDTO = convertToResponseDTO(BasicResponse);

				if (iTunesResponseDTO.isSuccess()) {
					LOGGER.info("ITunes confirmed that encoded receipt [{}] is valid by BasicResponse [{}]", base64EncodedAppStoreReceipt, iTunesResponseDTO);
					
					Receipt latestReceiptInfo = iTunesResponseDTO.getLatestReceiptInfo();
					PaymentPolicy paymentPolicy = paymentPolicyService.findByCommunityAndAppStoreProductId(user.getUserGroup().getCommunity(), latestReceiptInfo.getProductId());
					
					final PaymentDetailsType paymentDetailsType;
					if (user.getLastSuccessfulPaymentTimeMillis() == 0) {
						paymentDetailsType = PaymentDetailsType.FIRST;
					} else {
						paymentDetailsType = PaymentDetailsType.REGULAR;
					}

					SubmittedPayment submittedPayment = new SubmittedPayment();
					submittedPayment.setStatus(PaymentDetailsStatus.SUCCESSFUL);
					submittedPayment.setUser(user);
					submittedPayment.setTimestamp(Utils.getEpochMillis());
					submittedPayment.setAmount(paymentPolicy.getSubcost());
					submittedPayment.setExternalTxId(latestReceiptInfo.getOriginalTransactionId());
					submittedPayment.setType(paymentDetailsType);
					submittedPayment.setCurrencyISO(paymentPolicy.getCurrencyISO());
					submittedPayment.setNextSubPayment(latestReceiptInfo.getExpiresDateSeconds());
					submittedPayment.setAppStoreOriginalTransactionId(latestReceiptInfo.getOriginalTransactionId());
					submittedPayment.setPaymentSystem(PaymentDetails.ITUNES_SUBSCRIPTION);
					submittedPayment.setBase64EncodedAppStoreReceipt(base64EncodedAppStoreReceipt);
					
					submitedPaymentService.save(submittedPayment);

					PaymentEvent paymentEvent = new PaymentEvent(submittedPayment);
					applicationEventPublisher.publishEvent(paymentEvent);
				} else {
					LOGGER.info("ITunes rejected the encoded receipt [{}] by BasicResponse [{}]", base64EncodedAppStoreReceipt, BasicResponse);
				}
			}else{
				LOGGER.info("The request of in-app subscription validation returned unexpected BasicResponse [{}]", BasicResponse);
			}
		} else {
			BasicResponse = null;
		}
		
		LOGGER.debug("Output parameter BasicResponse=[{}]", BasicResponse);
		return BasicResponse;
	}

    private ITunesInAppSubscriptionResponseDto convertToResponseDTO(BasicResponse basicResponse) {
        ITunesInAppSubscriptionResponseDto iTunesResponseDTO =  new ITunesInAppSubscriptionResponseDto();
        JsonObject rootObject = jsonParser.parse(basicResponse.getMessage()).getAsJsonObject();
        iTunesResponseDTO.setStatus(getStringFromJsonObject(rootObject, "status"));
        iTunesResponseDTO.setLatestReceipt(getStringFromJsonObject(rootObject, "latest_receipt"));
        iTunesResponseDTO.setReceipt(getObjectFromJsonObject(rootObject, "receipt", Receipt.class));
        fillLatestReceiptInfo(rootObject, iTunesResponseDTO);
        return iTunesResponseDTO;
    }

    private void fillLatestReceiptInfo(JsonObject rootObject, ITunesInAppSubscriptionResponseDto iTunesResponseDTO) {
        if (!rootObject.has("latest_receipt_info")){
            throw new IllegalArgumentException(String.format("Json object [%s] doesn't contain latest_receipt_info", rootObject));
        }
        JsonElement latestReceiptInfo = rootObject.get("latest_receipt_info");
        if(latestReceiptInfo.isJsonArray()){
            if (latestReceiptInfo.getAsJsonArray().size() != 1){
                throw new IllegalArgumentException(String.format("latest_receipt_info [%s] have a wrong size. Must be 1", latestReceiptInfo));
            }
            iTunesResponseDTO.setLatestReceiptInfo(gson.fromJson(latestReceiptInfo.getAsJsonArray().get(0), Receipt.class));
        } else if(latestReceiptInfo.isJsonObject()){
            iTunesResponseDTO.setLatestReceiptInfo(gson.fromJson(latestReceiptInfo, Receipt.class));
        } else {
            throw new IllegalArgumentException(String.format("latest_receipt_info [%s] is neither array nor object", latestReceiptInfo));
        }
    }

    private String getStringFromJsonObject(JsonObject rootObject, String name) {
        JsonElement jsonElement = rootObject.get(name);
        return jsonElement != null ? jsonElement.getAsString() : null;
    }

    private <T> T getObjectFromJsonObject(JsonObject rootObject, String name, Class<T> clazz){
        JsonElement jsonElement = rootObject.get(name);
        return jsonElement != null ? gson.fromJson(jsonElement, clazz) : null;
	}

}
