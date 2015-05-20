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

<div class="subscribe_root_container_ios">
    <div class="subscribe_header_block_ios">
        <div class="subscribe_header_block_title_ios">
            &nbsp;&nbsp;CHOOSE HOW TO KEEP&nbsp;&nbsp;<br/>&nbsp;&nbsp;LISTENING&nbsp;&nbsp;
        </div>
    </div>
    <div>
        <div class="subscribe_description_item_ios">Select an option for:</div>
        <div class="subscribe_description_item_ios">
            <div class="subscribe_description_item_logo_ios"></div>
            Unrestricted access to all playlists
        </div>
        <div class="subscribe_description_item_ios">
            <div class="subscribe_description_item_logo_ios"></div>
            Ability to heart your favourites
        </div>
        <div class="subscribe_description_item_ios">
            <div class="subscribe_description_item_logo_ios"></div>
            Offline playback, listen anywhere
        </div>
    </div>
    <div class="subscribe_payment_options_block_ios">
        <c:forEach var="paymentPolicyDto" items="${paymentsPage.subscriptionInfo.paymentPolicyDTOs}" varStatus="optionNumber">
            <div class="subscribe_option_border_ios subscribe_option_border_ios_${optionNumber.index + 1}" style="display:none;">
                <div class="subscribe_option_holder_ios" onclick="goTo('${pageContext.request.contextPath}/payments/iTunesSubscription.html?productId=${paymentPolicyDto.appStoreProductId}');">
                    <div class="subscribe_option_text_ios">


                        <c:choose>
                            <c:when test="${payAsYouGoIOSProductIds.containsKey(paymentPolicyDto.appStoreProductId)}">
                                <span type="PAYG" productId="${paymentPolicyDto.appStoreProductId}">
                                    <s:message code='subscribe.ios.button.pass.payg' arguments="${payAsYouGoIOSProductIds[paymentPolicyDto.appStoreProductId]}, ${paymentPolicyDto.duration}"/>
                                </span>
                            </c:when>
                            <c:otherwise>
                                <span type="${paymentPolicyDto.paymentPolicyType}" productId="${paymentPolicyDto.appStoreProductId}">
                                    <s:message code="subscribe.ios.button.${paymentPolicyDto.paymentPolicyType}.${paymentPolicyDto.durationUnit}" arguments="${paymentPolicyDto.duration}"/>
                                </span>
                            </c:otherwise>
                        </c:choose>


                    </div>
                    <div class="subscribe_option_price_ios">&#163;${paymentPolicyDto.subcost}</div>
                </div>
            </div>
            <div class="subscribe_option_info_ios subscribe_option_info_ios_${optionNumber.index + 1}" style="display:none;"
                 onclick="$.modal('<s:message code='subscribe.ios.option.${optionNumber.index + 1}.info'/>', {overlayClose: true});">
                <div class="subscribe_option_info_text_ios"></div>
            </div>
        </c:forEach>
    </div>
    <div class="subscribe_option_holder subscribe_option_holder_device">
        <a class="go-premium-button subscribe-button-device go-premium-button-target go-premium-body-cancel" onclick="returnToApp();">
            <span><s:message code='button.cancel.ios'/></span>
        </a>
    </div>
</div>
