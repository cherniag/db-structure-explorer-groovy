<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>

<div class="subscription_option_container subscription_option_container_device">
  <div class="subscription_pass_logo subscription_pass_logo_device"></div>
  <div class="subscription_option_price_message subscription_option_price_message_device"><s:message code='payment.policy.message.pass' /></div>
  <a class="go-premium-button subscribe-button-device go-premium-button-target go-premium-body-ok"
     onclick="goTo('${pageContext.request.contextPath}/payments/iTunesSubscription.html?productId=${paymentPolicyDto.appStoreProductId}');">
    <span><s:message code='subscribe.button.pass.${paymentPolicyDto.durationUnit}' arguments="${paymentPolicyDto.duration},${paymentPolicyDto.subcost}"/> </span>
  </a>
</div>
