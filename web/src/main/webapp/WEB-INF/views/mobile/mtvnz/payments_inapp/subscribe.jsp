<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div class="go-premium-container go-premium-container-device">
    <a class="go-premium-button go-premium-button-device go-premium-body-button-below go-premium-button-target go-premium-body-cancel" href="phone/check.html">
        <span>Vodafone</span>
    </a>
    <a class="go-premium-button subscribe-button-device go-premium-button-target go-premium-body-ok"
       onclick="goTo('${pageContext.request.contextPath}/payments/iTunesSubscription.html?productId=${paymentPolicyDto.appStoreProductId}');">
        <span><s:message code='subscribe.button.pass.${paymentPolicyDto.durationUnit}' arguments="${paymentPolicyDto.duration},${paymentPolicyDto.subcost}"/> </span>
    </a>
    <a class="go-premium-button go-premium-button-device go-premium-body-button-below go-premium-button-target go-premium-body-cancel" onclick="returnToApp();">
        <span>Cancel</span>
    </a>
</div>
