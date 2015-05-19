<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<script type="text/javascript">
    var USER_UUID = '${userUuid}',
        LEANPLUM_APP_ID = '<s:message code='leanplum.app.id'/>',
        LEANPLUM_DEV_APP_KEY = '<s:message code='leanplum.app.dev.key'/>',
        LEANPLUM_PROD_APP_KEY = '<s:message code='leanplum.app.prod.key'/>',
        LEANPLUM_IS_DEVELOPMENT = <s:message code='leanplum.is.development.mode'/>;
</script>
<script type="text/javascript" src="${requestScope.assetsPathWithoutCommunity}scripts/leanplum.js"></script>
<script type="text/javascript" src="${requestScope.assetsPathWithoutCommunity}scripts/leanplum.payg.ios.experiment.js"></script>

<div class="subscribe_root_container">
    <div class="subscribe_content_container subscribe_content_container_device">
        <div class="subscribe_header_block">
            <div class="subscribe_header_mtv_logo"></div>
            <div class="subscribe_header_ad_title">
                <div class="subscribe_header_premium_banner"></div>
            </div>
        </div>
        <div class="subscribe_description_block subscribe_description_block_device">
            <div class="subscribe_description_item subscribe_description_item_device">
                <div class="subscribe_description_item_logo subscribe_description_item_logo_device"></div>
                <div class="subscribe_description_item_text subscribe_description_item_text_device">
                    <s:message code='subscribe.description.item.1'/>
                </div>
            </div>
            <div class="subscribe_description_item subscribe_description_item_device">
                <div class="subscribe_description_item_logo subscribe_description_item_logo_device"></div>
                <div class="subscribe_description_item_text subscribe_description_item_text_device">
                    <s:message code='subscribe.description.item.2'/>
                </div>
            </div>
            <div class="subscribe_description_item subscribe_description_item_device">
                <div class="subscribe_description_item_logo subscribe_description_item_logo_device"></div>
                <div class="subscribe_description_item_text subscribe_description_item_text_device">
                    <s:message code='subscribe.description.item.3'/>
                </div>
            </div>
        </div>
        <div>
            <c:forEach var="paymentPolicyDto" items="${paymentsPage.subscriptionInfo.paymentPolicyDTOs}" varStatus="optionNumber">
                <c:choose>
                    <c:when test="${paymentPolicyDto.paymentPolicyType == 'ONETIME'}">
                        <%@include file="pass/subscribe_option.jsp"%>
                    </c:when>
                    <c:otherwise>
                        <%@include file="premium/subscribe_option.jsp"%>
                    </c:otherwise>
                </c:choose>
                <div class="subscribe_option_discount_text subscribe_option_discount_text_device" style="display:none;">
                    <s:message code="subscribe.option.discount.text.${optionNumber.index + 1}"/>
                </div>
            </c:forEach>
            <div class="subscribe_option_holder subscribe_option_holder_device">
                <a class="go-premium-button subscribe-button-device go-premium-button-target go-premium-body-cancel" onclick="returnToApp();">
                    <span><s:message code='button.cancel.title' /></span>
                </a>
            </div>
        </div>
    </div>
</div>
