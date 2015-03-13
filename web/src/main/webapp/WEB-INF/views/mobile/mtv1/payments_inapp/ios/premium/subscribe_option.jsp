<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="S" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="subscribe_option_holder subscribe_option_holder_device">
  <a class="go-premium-button subscribe-button-device go-premium-button-target go-premium-body-ok"
     onclick="goTo('${pageContext.request.contextPath}/payments/iTunesSubscription.html?productId=${paymentPolicyDto.appStoreProductId}');">
    <span><s:message code="subscribe.button.premium.${paymentPolicyDto.durationUnit}" arguments="${paymentPolicyDto.subcost}"/></span>
  </a>
</div>
