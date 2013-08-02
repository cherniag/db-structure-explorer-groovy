<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<script type="text/javascript">

$(document).ready( function(){videoSelected()} );

function videoSelected() {
	var videoCheckbox = $("#videoCheckbox");
	if ( videoCheckbox.length === 0 ) {
		return;
	}
	
	var paymentButtons = $("div.rel");
	var isVideoSelected = videoCheckbox.is(':checked');
	
	if ( isVideoSelected ) {
		$("div.videoOption").addClass("videoOptionHighlight");
	} else {
		$("div.videoOption").removeClass("videoOptionHighlight");
	}
	
	paymentButtons.each(function(){
		var attrib = $(this).attr("data-hasvideo");
		if ( typeof attrib === 'undefined' || attrib === false ) {
			return;
		}
		var updateButtonShow = ($(this).attr("data-updatesubbutton") == "1");
		var videoAttr = (attrib == "1");
		var updateSubscriptionButton = $("#updateSubscriptionButton");
		
		if ( videoAttr == isVideoSelected ) {
			$(this).show();
			if ( updateButtonShow == true && updateSubscriptionButton.length > 0 ) {
				updateSubscriptionButton.show();
			}
		} else {
			$(this).hide();
			if ( updateButtonShow == true && updateSubscriptionButton.length > 0 ) {
				updateSubscriptionButton.hide();
			}
		}
	});
}
</script>

<div class="header pie">
    <span class="logo"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo.png"/></span>
</div>

<div class="container" style="padding-top:6px;">

            <c:set var="accountBannerON"> <s:message code="pays.page.note.account.on"/> </c:set>
            <c:set var="error_code" value="payment.${paymentDetails.paymentType}.error.msg.${paymentDetails.errorCode}" />
            <s:message code='${error_code}' var="error_msg" />
            <c:if test="${error_msg != error_code && error_msg != ''}">
    			<div class="pane-red rel pie">
                	<img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/icon_banner_alert.png"/>
                    <span class="alert-text-close">${error_msg}</span>
                    <span class="alert-button-close"/>
                </div>
            </c:if>
            <c:if test="${accountBannerON eq 'true'}">
                <c:choose>
                    <c:when test="${trialExpiredOrLimited}">
                        <div class="pane-red rel pie">
                            <img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/icon_banner_alert.png"/>
                            <span class="alert-text">${paymentAccountNotes}</span>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="banner-pane pie">
                            <c:if test="${not empty paymentAccountBanner}">
                                <img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}'/>${paymentAccountBanner}" align="middle"/>
                            </c:if>
                            <span>${paymentAccountNotes}</span>
                        </div>
                    </c:otherwise>
                </c:choose>

            </c:if>
</div>

<img style="width:100%;" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/img_header_payment.png" />

<div class="container">
           
            	<c:set var="hasPaymentBaner" value="false" />
                <c:forEach var="paymentPolicy" items="${paymentPolicies}">
                    <c:if test="${paymentPolicy.paymentType == 'creditCard'}">
                        <c:set var="method_name" value="creditcard" />
                        <s:message code='pays.select.payby.creditcard' var="payment_label" />
                    </c:if>
                    <c:if test="${paymentPolicy.paymentType == 'PAY_PAL'}">
                        <c:set var="method_name" value="paypal" />
                        <s:message code='pays.select.payby.paypal' var="payment_label" />
                    </c:if>
                    <c:if test="${paymentPolicy.paymentType == 'PSMS'}">
                        <c:set var="method_name" value="psms" />
                        <s:message code='pays.select.payby.psms' var="payment_label" />
                    </c:if>
                    <c:if test="${paymentPolicy.paymentType == 'o2Psms'}">
                        <c:set var="method_name" value="o2psms" />
                        <s:message code='pays.select.payby.o2psms.${paymentPolicy.subweeks}weeks.${paymentPolicy.subcost}subcost' var="payment_label" />
                    </c:if>
                    <c:if test="${paymentPolicy.paymentType == 'iTunesSubscription'}">
                        <c:set var="method_name" value="iTunesSubscription" />
                        <s:message code='pays.select.iTunesSubscription' var="payment_label" />
                    </c:if>

                    <c:choose>
                        <c:when test="${paymentPolicy.subweeks == 1}">
                            <c:set var="paymentPolicyOptionNo" value="3" />
                        </c:when>
                        <c:when test="${paymentPolicy.subweeks == 2}">
                            <c:set var="paymentPolicyOptionNo" value="2" />
                        </c:when>
                        <c:when test="${paymentPolicy.subweeks == 5}">
                            <c:set var="paymentPolicyOptionNo" value="1" />
                        </c:when>
                        <c:otherwise>
                            <c:set var="paymentPolicyOptionNo" value="0" />
                        </c:otherwise>
                    </c:choose>
                                        
                    <div class="rel">
                        <c:choose>
                            <c:when test="${isIOSDevice && !isO2User}">
                                <c:if test="${paymentPolicy.paymentType == 'iTunesSubscription'}">
                                    <div class="subscription-container">
                                        <div class="subscription-selector option-3">
                                            <img style="width:66px; height:66px;" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/ic_apple_in_app_payment.png" />
                                            <div class="rel" style="padding-top: 8px;">
                                                <span class="title">Payment via iTunes</span><br />
                                                <span class="price">&#163;${paymentPolicy.subcost}</span>per month
                                            </div>
                                        </div>
                                        
                                        <div class="rel" style="margin:0 6px; padding:8px 0; border-top: 1px solid #a0a0a0">
                                            <input class="button-turquoise no-margin pie" title="${pageContext.request.contextPath}/payments_inapp/${method_name}.html?paymentPolicyId=${paymentPolicy.id}" type="button" onClick="location.href=this.title" value="Subscribe via iTunes" />
                                        </div>
                                    </div>
                                </c:if>
                            </c:when>
                            <c:when test="${paymentPolicy.paymentType == 'o2Psms'
                    && paymentDetails != null
                    && activePolicy != null
                    && paymentDetails.activated
                    && activePolicy.subcost == paymentPolicy.subcost
                    && activePolicy.subweeks == paymentPolicy.subweeks }">
                                <a class="subscription-selector option-${paymentPolicyOptionNo}" disabled="true">
                            		<img style="width:66px; height:66px;" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/ic_option_${paymentPolicy.subweeks}.png" />
                                    <div class="rel" style="padding-top: 8px;">
                                        <span class="title"><s:message code='pays.select.payby.o2psms.option${paymentPolicyOptionNo}.title' /></span><br />
                                        <span class="price">&#163;${paymentPolicy.subcost}.00</span> <s:message code='pays.select.payby.o2psms.option${paymentPolicyOptionNo}.weeks' />
                                    </div>
                                    <span class="button-on"></span>
                        		</a>
                            </c:when>
                            <c:when test="${paymentPolicy.paymentType == 'o2Psms'}">
                                <a class="subscription-selector option-${paymentPolicyOptionNo}" href="${pageContext.request.contextPath}/payments_inapp/${method_name}.html?paymentPolicyId=${paymentPolicy.id}" type="button">
                            		<img style="width:66px; height:66px;" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/ic_option_${paymentPolicy.subweeks}.png" />
                                    <div class="rel" style="padding-top: 8px;">
                                        <span class="title"><s:message code='pays.select.payby.o2psms.option${paymentPolicyOptionNo}.title' /></span><br />
                                        <span class="price">&#163;${paymentPolicy.subcost}.00</span> <s:message code='pays.select.payby.o2psms.option${paymentPolicyOptionNo}.weeks' />
                                    </div>
                                    <span class="button-off"></span>
                        		</a>
                            </c:when>
                            <c:otherwise>
                                <c:if test="${paymentPolicy.paymentType != 'iTunesSubscription'}">

                                <div class="subscription-container" style="margin-bottom: 5px;">
                                    <a class="subscription-selector option-3" style="margin-bottom: 0px;" href="${pageContext.request.contextPath}/payments_inapp/${method_name}.html?paymentPolicyId=${paymentPolicy.id}" type="button">
                                		<img style="width:66px; height:66px;" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/ic_option_${method_name}.png" />
                                        <div class="rel" style="padding-top: 8px;">
                                            <span class="title"><s:message code="${payment_label}" /></span><br />
                                            <span class="price">&#163;${paymentPolicy.subcost}</span> 
                                            <c:choose>
                                                <c:when test="${isBussinesUser eq 'true'}">
                                                    <s:message code='pays.select.payby.creditcard.business.subterm' />
                                                </c:when>
                                                <c:otherwise>
                                                    <s:message code='pays.select.payby.creditcard.consumer.subterm' />
                                                </c:otherwise>
                                            </c:choose>

                                            
                                        </div>
                                        <c:choose>
                                            <c:when test="${paymentDetailsType != null && paymentDetailsType == method_name && paymentDetails.activated eq 'true'}">
                                                <span class="button-on"></span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="button-off"></span>
                                            </c:otherwise>
                                        </c:choose>
                            		</a>

                                    <div class="rel" style="margin:0 6px; padding-top:3px; border-top: 1px solid #a0a0a0; text-align: center;">
                                        <img style="height:13px;" src="${requestScope.assetsPathAccordingToCommunity}imgs/ic_${method_name}.png" />
                                    </div>
                                </div>
                                
                        		</c:if>
                            </c:otherwise>
                        </c:choose>
                    </div>

                </c:forEach>
            </div>

            <div class="content no-bg">

                <c:if test="${(paymentDetails!=null) && (true==paymentDetails.activated)}">
                    <div class="rel" style="margin-top: 5px;">
                        <a class="button-grey no-margin pie" href="${pageContext.request.contextPath}/payments_inapp/unsubscribe.html" ><s:message code='pays.deactivate.submit' /></a>
                    </div>
                </c:if>
                
                <div class="rel" style="text-align: center; margin-top: 10px;">
                    <img style="width:33%;" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/label_secure_payment.png"/>
                </div>

            </div>
</div>
