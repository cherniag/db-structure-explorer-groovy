<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>

<c:set var="buttonSubscribeMessage" value="subscribe.button.pass.${paymentPolicyDto.durationUnit}"/>
<div class="subscription_option_container subscription_option_container_device">
  <div class="subscription_pass_logo subscription_pass_logo_device"></div>
  <div class="subscription_option_price_message subscription_option_price_message_device"><s:message code='payment.policy.message.pass' /></div>
  <a class="go-premium-button subscribe-button-device go-premium-button-target go-premium-body-ok" onclick="submitForm('subscribe_form_${paymentPolicyDto.id}');">
    <span><s:message code="${buttonSubscribeMessage}" arguments="${paymentPolicyDto.duration},${paymentPolicyDto.subcost}"/> </span>
  </a>
  <form:form modelAttribute="payPalDto" method="post" id="subscribe_form_${paymentPolicyDto.id}" action="payments_inapp/paypal.html">
    <input type="hidden" name="paymentPolicyId" value="${paymentPolicyDto.id}"/>
    <jsp:include page="display_error.jsp"/>
  </form:form>
</div>
