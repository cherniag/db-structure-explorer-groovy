<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="go-premium-success">
    <div class="go-premium-success-close-button-container go-premium-success-close-button-container-device">
        <div class="go-premium-success-close-button go-premium-success-close-button-device go-premium-button-target go-premium-button-close go-premium-button-close-device"
             onclick="returnToApp();">
        </div>
    </div>
    <div class="go-premium-first-half"></div>
    <div class="go-premium-success-salutation-container go-premium-success-salutation-container-device">
        <div class="go-premium-success-salutation-container-text">
            <span class="go-premium-success-salutation go-premium-success-salutation-device"><s:message code="paypal.success.salutation.head" /> ${customerName}</span>
            <br/>
            <span class="go-premium-success-salutation-title go-premium-success-salutation-title-1-device"><s:message code="paypal.success.pass.salutation.title1" /></span>
            <br/>

            <s:message code="paypal.success.pass.salutation.period.${currentPaymentPolicy.durationUnit}" var="durationUnit"/>

            <span class="go-premium-success-salutation-title go-premium-success-salutation-title-2-device">
                <s:message code="paypal.success.pass.salutation.title2" arguments="${currentPaymentPolicy.duration},${durationUnit}"/>
            </span>
        </div>
    </div>
    <div class="go-premium-success-notification-container">
        <div class="go-premium-success-notification">
            <img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/img_notification_pass.png" class="go-premium-success-notification-img" />
        </div>
    </div>
    <div class="go-premium-success-ad-container go-premium-success-ad-container-device">
        <div class="go-premium-success-ad-text">
            <span class="go-premium-success-ad-text-1 go-premium-success-ad-text-1-device"><s:message code="paypal.success.ad.title1" /></span>
            <br/>
            <span class="go-premium-success-ad-text-2 go-premium-success-ad-text-2-device"><s:message code="paypal.success.ad.title2" /></span>
        </div>
        <a class="go-premium-button go-premium-button-device go-premium-body-button-up go-premium-button-target go-premium-body-ok go-premium-success-button-get-listening" onclick="returnToApp();">
            <span><s:message code='button.get.listening.title' /></span>
        </a>
    </div>
    <div class="go-premium-second-half"></div>
</div>
