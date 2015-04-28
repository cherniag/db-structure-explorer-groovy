<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div class="subscribe_option_holder subscribe_option_holder_device">
  <a class="go-premium-button subscribe-button-device go-premium-button-target go-premium-body-ok"
     onclick="goTo('${pageContext.request.contextPath}/payments/iTunesSubscription.html?productId=${paymentPolicyDto.appStoreProductId}');">
    <span>
        <c:choose>
            <c:when test="${payAsYouGoIOSProductIds.containsKey(paymentPolicyDto.appStoreProductId)}">
                <s:message code='subscribe.button.pass.payg' arguments="${paymentPolicyDto.subcost},${payAsYouGoIOSProductIds[paymentPolicyDto.appStoreProductId]}"/>
            </c:when>
            <c:otherwise>
                <s:message code='subscribe.button.pass.${paymentPolicyDto.durationUnit}' arguments="${paymentPolicyDto.subcost},${paymentPolicyDto.duration}"/>
            </c:otherwise>
        </c:choose>
    </span>
  </a>
</div>
