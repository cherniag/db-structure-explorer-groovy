<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="S" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="subscription_option_container subscription_option_container_device">
  <div class="subscription_premium_logo subscription_premium_logo_device"></div>
  <div class="subscription_option_price_message subscription_option_price_message_device"><s:message code='payment.policy.message.premium.${paymentPolicyDto.durationUnit}'/></div>
  <a class="go-premium-button subscribe-button-device go-premium-button-target go-premium-body-ok"
     onclick="goTo('${pageContext.request.contextPath}/payments/iTunesSubscription.html?productId=${paymentPolicyDto.appStoreProductId}');">
    <span><s:message code="subscribe.button.premium" arguments="${paymentPolicyDto.subcost}"/></span>
  </a>
</div>
