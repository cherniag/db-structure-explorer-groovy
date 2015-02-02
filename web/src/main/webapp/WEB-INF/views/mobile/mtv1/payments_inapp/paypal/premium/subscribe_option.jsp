<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="S" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="paymentPolicyMessage" value="payment.policy.message.premium.${paymentPolicyDto.durationUnit}"/>
<div class="subscription_option_container subscription_option_container_device">
  <div class="subscription_premium_logo subscription_premium_logo_device"></div>
  <div class="subscription_option_price_message subscription_option_price_message_device"><s:message code="${paymentPolicyMessage}"/></div>
  <a class="go-premium-button subscribe-button-device go-premium-button-target go-premium-body-ok" onclick="submitForm('subscribe_form_${paymentPolicyDto.id}');">
    <span><s:message code="subscribe.button.premium" arguments="${paymentPolicyDto.subcost}"/></span>
  </a>
  <form:form modelAttribute="payPalDto" method="post" id="subscribe_form_${paymentPolicyDto.id}" action="payments_inapp/paypal.html">
    <input type="hidden" name="paymentPolicyId" value="${paymentPolicyDto.id}"/>
    <jsp:include page="display_error.jsp"/>
  </form:form>
</div>
