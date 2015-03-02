<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<div class="root-container root-container-selection">
    <div class="container-option">
        <div class="go-premium-header">
            <div class="go-premium-header-logo">
                <img src="${requestScope.assetsPathAccordingToCommunity}imgs/badge_mtv.png"
                     class="go-premium-header-logo-img"/>
            </div>
            <div class="go-premium-header-ad">
                <div class="go-premium-header-title">
                    <s:message code='subscription.header.block.headline1'/>
                </div>
                <div class="go-premium-header-title-options">
                    <s:message code='subscription.header.block.headline2'/>
                </div>
            </div>
        </div>
    </div>

    <div class="container-option">
        <div>
            <img src="${requestScope.assetsPathAccordingToCommunity}imgs/icon_payment_vf.png" class="payment-logo" />
        </div>
        <div class="message">
            <s:message code='vf.payment.title'/>
        </div>
        <a class="go-premium-button go-premium-button-device go-premium-button-target go-premium-body-ok" href="phone/check.html">
            <span>
                <s:message code='from.payment.per.${paymentData.smsPaymentPolicy.durationUnit}' arguments="${paymentData.smsPaymentPolicy.subCost}"/>
            </span>
        </a>
    </div>

    <div class="container-option">
        <div>
            <img src="${requestScope.assetsPathAccordingToCommunity}imgs/icon_payment_paypal.png" class="payment-logo" />
        </div>
        <div class="message">
            <s:message code='vf.paypal.title'/>
        </div>
        <a class="go-premium-button go-premium-button-device go-premium-button-target go-premium-body-ok" onclick="submitForm('subscribe_form_${paymentData.payPalPaymentPolicy.id}');">
            <span><s:message code="payment.per.${paymentData.payPalPaymentPolicy.durationUnit}" arguments="${paymentData.payPalPaymentPolicy.subCost}"/></span>
        </a>
        <form:form modelAttribute="payPalDto" method="post" id="subscribe_form_${paymentData.payPalPaymentPolicy.id}" action="payments_inapp/paypal.html">
            <input type="hidden" name="paymentPolicyId" value="${paymentData.payPalPaymentPolicy.id}"/>
            <jsp:include page="paypal_display_error.jsp"/>
        </form:form>
    </div>

    <div class="container-option">
        <a class="go-premium-button go-premium-button-device go-premium-button-target go-premium-body-cancel" onclick="returnToApp();">
            <span><s:message code='button.cancel.title' /></span>
        </a>
    </div>
</div>



