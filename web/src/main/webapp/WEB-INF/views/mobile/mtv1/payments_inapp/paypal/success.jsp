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
    <div class="go-premium-success-solutation-container go-premium-success-solutation-container-device">
        <div class="go-premium-success-solutation-container-text">
            <span class="go-premium-success-solutation go-premium-success-solutation-device"><s:message code="paypal.success.salutation.head" /> ${customerName}</span>
            <br/>
            <span class="go-premium-success-solutation-title go-premium-success-solutation-title-1-device"><s:message code="paypal.success.salutation.title1" /></span>
            <br/>
            <span class="go-premium-success-solutation-title go-premium-success-solutation-title-2-device"><s:message code="paypal.success.salutation.title2" /></span>
        </div>
    </div>
    <div class="go-premium-success-avatar-container">
        <div class="go-premium-success-avatar">
            <div class="go-premium-success-avatar-customer">
                <img src="${customerAvatar}" class="go-premium-success-avatar-customer-img" onload="adjustImage(this, this.parentNode.clientWidth + 4)"/>
            </div>
            <img src="${pageContext.request.contextPath}/assets/mobile/mtv1/imgs/img_stars_no_border.png" class="go-premium-success-avatar-border" />
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
