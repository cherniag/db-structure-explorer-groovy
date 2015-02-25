<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div class="root-container root-container-selection">
    <div class="go-premium-header container-option">
        <div class="go-premium-header-logo"></div>
        <div class="go-premium-header-ad">
            <div class="go-premium-header-title">
                <s:message code='subscription.header.block.headline1'/>
            </div>
            <div class="go-premium-header-title-options">
                <s:message code='subscription.header.block.headline2'/>
            </div>
        </div>
    </div>

    <div class="container-option">
        <div>
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
            <s:message code='vf.itunes.title'/>
        </div>
        <a class="go-premium-button go-premium-button-device go-premium-button-target go-premium-body-ok"
           onclick="goTo('${pageContext.request.contextPath}/payments/iTunesSubscription.html?productId=${paymentData.iTunesPaymentPolicy.appStoreProductId}');">
            <span>
                <s:message code='payment.per.${paymentData.iTunesPaymentPolicy.durationUnit}' arguments="${paymentData.iTunesPaymentPolicy.subCost}"/>
            </span>
        </a>
    </div>

    <div class="container-option">
        <a class="go-premium-button go-premium-button-device go-premium-button-target go-premium-body-cancel" onclick="returnToApp();">
            <span><s:message code='button.cancel.title' /></span>
        </a>
    </div>
</div>



