<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div class="subscription_root_container">
    <div class="subscription_header_block subscription_header_block_device">
        <div class="go-premium-header-logo go-premium-header-logo-device"></div>
        <div class="go-premium-header-ad">
            <div class="go-premium-header-title go-premium-header-title-device"><s:message
                    code='subscription.header.block.headline1'/></div>
            <div class="go-premium-header-title-options go-premium-header-title-options-device"><s:message
                    code='subscription.header.block.headline2'/></div>
        </div>
    </div>

    <c:forEach var="paymentPolicyDto" items="${paymentsPage.subscriptionInfo.paymentPolicyDTOs}">
        <c:choose>
            <c:when test="${paymentPolicyDto.paymentPolicyType == 'ONETIME'}">
                <%@include file="pass/subscribe_option.jsp"%>
            </c:when>
            <c:otherwise>
                <%@include file="premium/subscribe_option.jsp"%>
            </c:otherwise>
        </c:choose>
    </c:forEach>

    <div class="subscription_option_container subscription_option_container_device">
        <a class="go-premium-button subscribe-button-device go-premium-button-target go-premium-body-cancel" onclick="returnToApp();">
            <span><s:message code='button.cancel.title' /></span>
        </a>
    </div>
</div>

