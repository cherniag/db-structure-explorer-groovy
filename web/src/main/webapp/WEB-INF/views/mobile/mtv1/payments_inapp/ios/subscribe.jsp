<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

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
            <div class="subscribe_option_border_ios subscribe_option_border_ios_${optionNumber.index + 1}" onclick="alert('Option')">
                <div class="subscribe_option_holder_ios">
                    <div class="subscribe_option_text_ios">


                        <s:message code="subscribe.ios.button.${paymentPolicyDto.paymentPolicyType}.${paymentPolicyDto.durationUnit}" arguments="${paymentPolicyDto.duration}"/>


                    </div>
                    <div class="subscribe_option_price_ios">&#163;${paymentPolicyDto.subcost}</div>
                </div>
            </div>
            <div class="subscribe_option_info_ios subscribe_option_info_ios_${optionNumber.index + 1}" onclick="alert('Info')">
                <div class="subscribe_option_info_text_ios"></div>
            </div>
        </c:forEach>
    </div>
<%--    <div>
        <c:forEach var="paymentPolicyDto" items="${paymentsPage.subscriptionInfo.paymentPolicyDTOs}" varStatus="optionNumber">
            <c:choose>
                <c:when test="${paymentPolicyDto.paymentPolicyType == 'ONETIME'}">
                    <%@include file="pass/subscribe_option.jsp"%>
                </c:when>
                <c:otherwise>
                    <%@include file="premium/subscribe_option.jsp"%>
                </c:otherwise>
            </c:choose>
            <div class="subscribe_option_discount_text subscribe_option_discount_text_device">
                <s:message code="subscribe.option.discount.text.${optionNumber.index + 1}"/>
            </div>
        </c:forEach>

    </div>--%>
    <div class="subscribe_option_holder subscribe_option_holder_device">
        <a class="go-premium-button subscribe-button-device go-premium-button-target go-premium-body-cancel" onclick="returnToApp();">
            <span>Maybe later</span>
        </a>
    </div>
</div>
