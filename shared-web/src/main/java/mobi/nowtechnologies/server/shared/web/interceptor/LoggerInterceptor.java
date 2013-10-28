package mobi.nowtechnologies.server.shared.web.interceptor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.security.SecurityContextDetails;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.Contract;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.shared.enums.SegmentType;
import mobi.nowtechnologies.server.shared.log.LogUtils;
import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.WebUtils;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class LoggerInterceptor extends HandlerInterceptorAdapter {

	private UserService userService;

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	protected static final Logger PROFILE_LOGGER = LoggerFactory.getLogger("PROFILE_LOGGER");

	private static final Logger LOGGER = LoggerFactory.getLogger(LoggerInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String remoteAddr = Utils.getIpFromRequest(request);

		String userName = null;
		Object userId = null;
		String userMobile = null;

		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {

			Object principal = authentication.getPrincipal();
			if (principal instanceof SecurityContextDetails) {
				SecurityContextDetails securityContextDetails = (SecurityContextDetails) principal;
				userName = securityContextDetails.getUsername();
				userId = securityContextDetails.getUserId();
				userMobile = securityContextDetails.getUserMobile();
			}
		}

		Cookie communityCookie = WebUtils.getCookie(request, CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME);
		String communityURL = communityCookie != null ? communityCookie.getValue() : "no-community-request";

		LogUtils.putGlobalMDC(userId, userMobile, userName, communityURL, request.getRequestURI(), handler.getClass(), remoteAddr);
		LogUtils.put3rdParyRequestProfileSpecificMDC(userName, userMobile, userId);
		return super.preHandle(request, response, handler);
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		try {
			if (PROFILE_LOGGER.isDebugEnabled() && modelAndView != null) {
				final Map<String, Object> model = modelAndView.getModel();
				Set<String> keySet = model.keySet();
				for (String key : keySet) {
					if (key.startsWith(BindingResult.MODEL_KEY_PREFIX)) {
						final Object keyValue = model.get(key);
						LogUtils.putBindingResultMDC(keyValue);
						break;
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}

		super.postHandle(request, response, handler, modelAndView);
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		profile(request, ex);
		LogUtils.removeGlobalMDC();
		LogUtils.removeAll3rdParyRequestProfileMDC();
		super.afterCompletion(request, response, handler, ex);
	}

	private void profile(HttpServletRequest request, Exception ex) {
		try {
			if (PROFILE_LOGGER.isDebugEnabled()) {
				String userPaymentPolicyId = null;
				String userPaymentPolicySubCost = null;
				String userPaymentPolicySubWeeks = null;
				String userPaymentPolicyAdditionalInfo = null;
				String selectedPaymentPolicyId = null;
				String existedPaymentPolicies = null;
				Contract userContract = null;
				SegmentType userSegment = null;
				ProviderType userProvider = null;
				Object currentPaymentDetailsId = null;
				Boolean isCurrentPaymentDetailsActivated = null;

				BindingResult bindingResult =  (BindingResult) LogUtils.getBindingResultMDC();

				final String result;
				String errorMessages;
				final Object externalErrorObject = request.getAttribute("external_error");
				final Object internalErrorObject = request.getAttribute("internal_error");
				if (ex == null && externalErrorObject == null && internalErrorObject == null && (bindingResult == null || !bindingResult.hasErrors())) {
					result = "success";
					errorMessages = null;
				} else {
					result = "fail";
					errorMessages = "";
					if (ex != null) {
						errorMessages += "{ " + ex.getMessage() + "} ";
					}
					if (externalErrorObject != null) {
						errorMessages += "{ " + externalErrorObject.toString() + "} ";
					}

					if (internalErrorObject != null) {
						errorMessages += "{ " + internalErrorObject.toString() + "} ";
					}

					if (bindingResult != null && bindingResult.hasErrors()) {
						errorMessages += "{ " + bindingResult.toString() + "} ";
					}
				}

				final Object userIdObject = LogUtils.getUserId();
				if (userIdObject != null) {
					if (userIdObject instanceof Integer) {
						final Integer userId = (Integer) userIdObject;
						if (userId != null) {
							User user = userService.findById(userId);
							if (user != null) {
								PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();
								if (currentPaymentDetails != null) {
									currentPaymentDetailsId = currentPaymentDetails.getI();
									isCurrentPaymentDetailsActivated = currentPaymentDetails.isActivated();
									PaymentPolicy paymentPolicy = currentPaymentDetails.getPaymentPolicy();
									if (paymentPolicy != null) {
										userPaymentPolicyAdditionalInfo = paymentPolicy.toString();
										userPaymentPolicyId = String.valueOf(paymentPolicy.getId());
										userPaymentPolicySubWeeks = String.valueOf(paymentPolicy.getSubweeks());
										final BigDecimal subcost = paymentPolicy.getSubcost();
										if (subcost != null) {
											userPaymentPolicySubCost = subcost.toString();
										}
									}
								}
								userContract = user.getContract();
								userSegment = user.getSegment();
								userProvider = user.getProvider();
							}
						}
					} else {
						LOGGER.error("Invalid user id type");
					}
				}
				final String requestURI = request.getRequestURI();
				if (requestURI.contains("/payments_inapp") || requestURI.contains("/payments")) {
					selectedPaymentPolicyId = request.getParameter("paymentPolicyId");

					final Object paymentPoliciesObject = request.getAttribute("paymentPolicies");
					if (paymentPoliciesObject != null) {
						if (paymentPoliciesObject instanceof List<?>) {
							List<?> existedPaymentPoliciesList = (List<?>) paymentPoliciesObject;

							existedPaymentPolicies = existedPaymentPoliciesList.toString();
						} else {
							LOGGER.error("Invalid paymentPolicies type");
						}
					} else {
						LOGGER.error("paymentPolicies request attribute is null");
					}
				}
				Long startTimeNano = LogUtils.getStartTimeNano();
				Long execTimeMillis = null;
				if (startTimeNano != null) {
					execTimeMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTimeNano);
				}

				PROFILE_LOGGER
						.debug(
								"execTimeMillis=[{}]; currentPaymentDetailsId=[{}]; isCurrentPaymentDetailsActivated=[{}]; selectedPaymentPolicyId=[{}]; existedPaymentPolicies=[{}]; userContract=[{}]; userSegment=[{}]; userProvider=[{}]; userPaymentPolicyId=[{}]; userPaymentPolicySubCost=[{}]; userPaymentPolicySubWeeks=[{}]; userPaymentPolicyAdditionalInfo=[{}]; result=[{}]; errorMessages=[{}];",
								execTimeMillis, currentPaymentDetailsId,
								isCurrentPaymentDetailsActivated, selectedPaymentPolicyId, existedPaymentPolicies,
								userContract,
								userSegment,
								userProvider,
								userPaymentPolicyId, userPaymentPolicySubCost, userPaymentPolicySubWeeks, userPaymentPolicyAdditionalInfo,
								result, errorMessages);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
}
