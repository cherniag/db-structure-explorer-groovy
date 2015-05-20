<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib prefix="S" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="subscribe_option_holder subscribe_option_holder_device" style="display:none;" data-button="${paymentPolicyDto.id}">
    <a class="go-premium-button subscribe-button-device go-premium-button-target go-premium-body-ok"
       onclick="goTo('${pageContext.request.contextPath}/payments/iTunesSubscription.html?productId=${paymentPolicyDto.appStoreProductId}');">
    <span data-id="${paymentPolicyDto.id}" data-type="recurrent" data-productId="${paymentPolicyDto.appStoreProductId}">
        <s:message code="subscribe.button.premium.${paymentPolicyDto.durationUnit}" arguments="${paymentPolicyDto.subcost}"/>
    </span>
    </a>
    <div class="subscribe_option_discount_text subscribe_option_discount_text_device">
        <s:message code="subscribe.option.discount.text.${optionNumber.index + 1}"/>
    </div>
</div>
