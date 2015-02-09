<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div class="go-premium-container go-premium-container-device">
    <div class="go-premium-button-target go-premium-button-close go-premium-button-close-device" onclick="returnToApp();">
    </div>
    <div class="go-premium-unsubscribe-pass-banner go-premium-unsubscribe-banner go-premium-unsubscribe-logo-device">
    </div>
    <div class="go-premium-body-title go-premium-unsubscribe-title-device">
        <div class="go-premium-unsubscribe-title">
            <s:message code='head.unsubscribe.1.pass' />
        </div>
        <div class="go-premium-unsubscribe-title go-premium-unsubscribe-title-bottom">
            <s:message code='head.unsubscribe.2.pass.${currentPaymentPolicy.durationUnit}' arguments='${currentPaymentPolicy.duration}' />
        </div>
    </div>
    <div class="go-premium-text-message-wrapper go-premium-text-message-wrapper-device">
        <span class="go-premium-text-message go-premium-text-message-device">
            <s:message code='message.unsubscribe.pass' />
        </span>
        <br/>
    </div>
    <a class="go-premium-button go-premium-button-device go-premium-body-button-up go-premium-button-target go-premium-body-cancel" onclick="submitForm('unsubscribeFormId')">
        <span><s:message code='button.yes.title' /></span>
    </a>
    <a class="go-premium-button go-premium-button-device go-premium-body-button-below go-premium-button-target go-premium-body-ok" onclick="returnToApp();">
        <span><s:message code='button.cancel.title' /></span>
    </a>
    <form:form modelAttribute="unsubscribeDto" method="post" id="unsubscribeFormId" action="payments_inapp/unsubscribeAndRedirect.html">
        <s:hasBindErrors name="unsubscribeDto">
            <div class="note" id="note">
                <form:errors path="reason" />
            </div>
        </s:hasBindErrors>
    </form:form>
</div>
