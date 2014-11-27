<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div class="go-premium-container go-premium-container-device">
    <div class="go-premium-header">
        <div class="go-premium-header-logo go-premium-header-logo-device">
        </div>
        <div class="go-premium-header-ad">
            <div class="go-premium-header-title go-premium-header-title-device"><s:message code='headline1' /> <span class="go-premium-header-title-color"><s:message code='headline2' /></span></div>
            <div class="go-premium-header-price go-premium-header-price-device">${paymentsPage.subscriptionInfo.paymentPolicyMessage}</div>
            <div class="go-premium-header-hint go-premium-header-hint-device"><s:message code='headline3' /></div>
        </div>
    </div>
    <div class="go-premium-header-delimiter"></div>
    <span class="go-premium-body-title go-premium-body-title-device">
        <s:message code='head.go.premium' />
    </span>
    <a class="go-premium-button go-premium-button-device go-premium-body-button-up go-premium-button-target go-premium-body-ok" onclick="goTo('${pageContext.request.contextPath}/payments/iTunesSubscription.html');">
        <span><s:message code='button.itunes.title' /></span>
    </a>
    <a class="go-premium-button go-premium-button-device go-premium-body-button-below go-premium-button-target go-premium-body-cancel" onclick="returnToApp();">
        <span><s:message code='button.cancel.title' /></span>
    </a>
</div>

