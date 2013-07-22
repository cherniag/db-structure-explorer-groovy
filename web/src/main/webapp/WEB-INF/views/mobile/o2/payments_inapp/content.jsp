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
<div class="container">
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
    <div class="content">

        <h1>${paymentPoliciesHeader}</h1>
        <p>${paymentPoliciesNote}</p>
        <hr />
        
        <c:choose>
        <%--we have 2 cases:
        	(1) user is 4G and opted-in (we display the video options)
        	(2) user is 4g and not opted-in (we display a "link" for the user to opt-in) --%>
        <c:when test="${userIsOptedInToVideo eq true}">
        	<div class="videoOption">
        	
	        	<s:message code='pays.page.note.account.videotitle' var="payment_videotitle" />
	        	<s:message code='pays.page.note.account.videoprice' var="payment_videoprice" />
	        	<s:message code='pays.page.note.account.videoinfo' var="payment_videoinfo" />
	        	
	        	<div class="videoOptionFirstRow">
	        	<div style="float:left; font-weight: bold;">${payment_videotitle}</div>
	        	<div style="float:right">
	        		<label for="videoCheckbox">${payment_videoprice}</label>
	
	        		<c:set var="checkedAttrib" />
	        		<c:if test="${(paymentDetails!=null) && (true==paymentDetails.activated) && (paymentDetails.paymentPolicy.videoPaymentPolicy==true)}">
	        			<c:set var="checkedAttrib">checked="checked"</c:set>
	        			<%--Activate the video checkbox if the user has a video subscription --%>
	        		</c:if>
	        		<c:if test="${(paymentDetails==null) || (false==paymentDetails.activated)}">
	        			<c:set var="checkedAttrib">checked="checked"</c:set>
	        			<%--Activate the video checkbox if the user has no subscription --%>
	        		</c:if>
	        		
	        		<input type="checkbox" onchange="videoSelected()" id="videoCheckbox" ${readOnlyAttrib} ${checkedAttrib} />
	        	</div>
	        	<div>&nbsp;</div>
	        	</div>
	        	
	        	<div class="videoOptionText">${payment_videoinfo}</div>
        	
        	</div>
        </c:when>
        <c:when test="${userIsOptedInToVideo eq false}">
        	<div class="videoOption">
        		<s:message code='pays.select.payby.o2psms.videoOptIn' />
        	</div>
        </c:when>
        </c:choose>
        
        <div class="setOfButtons">
        	<c:set var="hasPaymentBaner" value="false" />
        	<c:set var="updateSubscriptionUrl"></c:set>
            <c:forEach var="paymentPolicy" items="${paymentPolicies}">
                <c:if test="${paymentPolicy.paymentType == 'creditCard'}">
                    <c:set var="method_name" value="creditcard" />
                    <c:set var="hasPaymentBaner" value="true" />
                    <s:message code='pays.select.payby.creditcard' var="payment_label" />
                </c:if>
                <c:if test="${paymentPolicy.paymentType == 'PAY_PAL'}">
                    <c:set var="method_name" value="paypal" />
                    <c:set var="hasPaymentBaner" value="true" />
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

                <div class="rel" data-hasvideo="${paymentPolicy.videoPaymentPolicy ? '1' : '0'}" data-updatesubbutton="${mirrorOfActivePolicy==paymentPolicy.id ? '1' : '0'}">
                    <c:choose>
                        <c:when test="${paymentPolicy.paymentType == 'o2Psms'
                        && paymentDetails != null
                        && activePolicy != null
                        && paymentDetails.activated
                        && activePolicy.subcost == paymentPolicy.subcost
                        && activePolicy.subweeks == paymentPolicy.subweeks }">
                            <a class="button-disabled pie" disabled="true">
                                ${payment_label}
                                
                            	<span class="button-on"/>
                            </a>
                        </c:when>
                        <c:when test="${paymentPolicy.paymentType == 'o2Psms'}">
                            <a class="button-turquoise pie" href="${pageContext.request.contextPath}/payments_inapp/${method_name}.html?paymentPolicyId=${paymentPolicy.id}" type="button" >
                                ${payment_label}
                                
                                <%--by default the buttonClass is button-off - it's button-on only when a user has video activated to display the mirror option --%>
                                <c:set var="buttonClass">button-off</c:set>
                                <c:if test="${mirrorOfActivePolicy == paymentPolicy.id}">
                                	<c:set var="buttonClass">button-on</c:set>
                                	<c:set var="updateSubscriptionUrl">${pageContext.request.contextPath}/payments_inapp/${method_name}.html?paymentPolicyId=${paymentPolicy.id}</c:set>
                                </c:if>
                                
                                <span class="${buttonClass}"/>
                            </a>
                        </c:when>
                        <c:otherwise>
                            <c:if test="${paymentPolicy.paymentType != 'iTunesSubscription'}">
                                <input class="button-turquoise pie" title="${pageContext.request.contextPath}/payments_inapp/${method_name}.html?paymentPolicyId=${paymentPolicy.id}" onClick="location.href=this.title" type="button" value="<s:message code="${payment_label}" />" />
                                <span class="button-arrow"/>
                            </c:if>
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:forEach>
                    <c:if test="${hasPaymentBaner}">
                            <img class="centered" style="width: 100px; height: 15px; margin-top: 15px; margin-bottom: 15px" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/image_secure_payment.png"/>
                            <hr/>
                            <img class="centered" style="width: 100%; margin-top: 10px" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/banner_payment.png"/>
                            <hr/>
                    </c:if>
        </div>
        <c:if test="${(paymentDetails!=null) && (true==paymentDetails.activated)}">
        	<c:if test="${userIsOptedInToVideo eq true}">
	        	<div class="rel" style="display: none" id="updateSubscriptionButton">
	                <a class="button-grey pie" href="${updateSubscriptionUrl}" ><s:message code='pays.page.note.account.updatesubscription' /></a>
	            </div>
            </c:if>
            <div class="rel" >
                <div class="cross-text"><span>  <s:message code="pays.deactivate.header" />  </span>  </div>
                <a class="button-grey pie" href="${pageContext.request.contextPath}/payments_inapp/unsubscribe.html" ><s:message code='pays.deactivate.submit' /></a>
            </div>
        </c:if>

    </div>
</div>