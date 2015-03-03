<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>

<div class="subscribe_option_holder subscribe_option_holder_device">
    <a class="go-premium-button subscribe-button-device go-premium-button-target go-premium-body-ok" onclick="submitForm('subscribe_form_${paymentPolicyDto.id}');">
        <span><s:message code="subscribe.button.pass.${paymentPolicyDto.durationUnit}" arguments="${paymentPolicyDto.subcost},${paymentPolicyDto.duration}"/> </span>
    </a>
    <form:form modelAttribute="payPalDto" method="post" id="subscribe_form_${paymentPolicyDto.id}" action="payments_inapp/paypal.html">
        <input type="hidden" name="paymentPolicyId" value="${paymentPolicyDto.id}"/>
        <jsp:include page="display_error.jsp"/>
    </form:form>
</div>
