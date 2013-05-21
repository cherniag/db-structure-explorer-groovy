package mobi.nowtechnologies.server.shared.web.interceptor;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mobi.nowtechnologies.server.persistence.domain.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.enums.SegmentType;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.security.SecurityContextDetails;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.PaymentPolicyDto;
import mobi.nowtechnologies.server.shared.enums.Contract;
import mobi.nowtechnologies.server.shared.log.LogUtils;
import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
		return super.preHandle(request, response, handler);
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		profile(request, ex);
		LogUtils.removeGlobalMDC();
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
				String userProvider = null;
				

				final String result;
				String errorMessages;
				final Object externalErrorObject = request.getAttribute("external_error");
				final Object internalErrorObject = request.getAttribute("internal_error");
				if (ex == null && externalErrorObject != null && internalErrorObject != null) {
					result = "success";
					errorMessages = null;
				} else {
					result = "fail";
					errorMessages = "";
					if (ex != null) {
						errorMessages += "{ " + ex.getMessage() +"} ";
					}
					if (externalErrorObject != null) {
						errorMessages += "{ " + externalErrorObject.toString() +"} ";
					}

					if (internalErrorObject != null) {
						errorMessages += "{ " + internalErrorObject.toString()+"} ";
					}
				}

				final Object userIdObject = LogUtils.getUserId();
				if (userIdObject instanceof Integer) {
					final Integer userId = (Integer) userIdObject;
					if (userId != null) {
						User user = userService.findById(userId);
						if (user != null) {
							PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();
							if (currentPaymentDetails != null) {
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
				final String requestURI = request.getRequestURI();
				if (requestURI.contains("/payments_inapp") || requestURI.contains("/payments")) {
					selectedPaymentPolicyId = request.getParameter("paymentPolicyId");

					final Object paymentPoliciesObject = request.getAttribute("paymentPolicies");
					if (paymentPoliciesObject != null) {
						if (paymentPoliciesObject instanceof List<?>) {
							List<?> offeredPaymentPolicyDtoList = (List<?>) paymentPoliciesObject;

							existedPaymentPolicies = offeredPaymentPolicyDtoList.toString();
						} else {
							LOGGER.error("Invalid paymentPolicies type");
						}
					} else {
						LOGGER.error("paymentPolicies request attribete is null");
					}
				}
				PROFILE_LOGGER
						.debug(
								"selectedPaymentPolicyId=[{}]; existedPaymentPolicies=[{}]; userContract=[{}]; userSegment=[{}]; userProvider=[{}]; userPaymentPolicyId=[{}]; userPaymentPolicySubCost=[{}]; userPaymentPolicySubWeeks=[{}]; userPaymentPolicyAdditionalInfo=[{}]; result=[{}]; errorMessages=[{}];",
								selectedPaymentPolicyId, existedPaymentPolicies,
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
