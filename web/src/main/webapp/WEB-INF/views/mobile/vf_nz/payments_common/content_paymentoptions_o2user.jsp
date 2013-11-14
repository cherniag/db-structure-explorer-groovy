<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>


<c:forEach var="paymentPolicy" items="${paymentsPage.paymentPolicies}">
    <c:if test="${paymentPolicy.paymentType == 'vfPsms'}">
        <c:set var="method_name" value="oppsms" />
    </c:if>
    
	<c:choose>
	    <c:when test="${paymentPolicy.subweeks == 1}">
	        <c:set var="paymentPolicyOptionNo" value="2" />
	        <c:set var="imageWeeks" value="2" />
	    </c:when>
	    <c:when test="${paymentPolicy.subweeks == 4}">
	        <c:set var="paymentPolicyOptionNo" value="1" />
	        <c:set var="imageWeeks" value="1" />
	    </c:when>
	    <c:otherwise>
	        <c:set var="paymentPolicyOptionNo" value="0" />
	    </c:otherwise>
	</c:choose>
	
	<div class="rel tapArea" data-hasvideo="${paymentPolicy.videoAndAudio4GSubscription ? '1' : '0'}" id="paymentOption${paymentPolicy.id}">
		
	   	<c:set var="disabledAttrib" />
	   	<c:set var="buttonStyle" value="button-off" />
	   	<c:set var="hrefValue">href="${pageContext.request.contextPath}/${param.callingPage}/${method_name}.html?paymentPolicyId=${paymentPolicy.id}"</c:set>
	   	<c:set var="onClickEvent" value="onclick='markSelected(this)'" />
	   	
	   	<c:if test="${paymentsPage.paymentDetails != null && paymentsPage.activePaymentPolicy != null && paymentsPage.paymentDetails.activated && paymentsPage.activePaymentPolicy.subcost == paymentPolicy.subcost && paymentsPage.activePaymentPolicy.subweeks == paymentPolicy.subweeks}">
	   		<c:set var="disabledAttrib">disabled="true"</c:set>
	   		<c:set var="buttonStyle" value="button-on" />
	   		<c:set var="hrefValue" value="" />
	   		<c:set var="onClickEvent" value="" />
	   	</c:if>
        <c:if test="${paymentsPage.awaitingPaymentStatus}">
            <c:set var="disabledAttrib">disabled="true"</c:set>
            <c:set var="disabledStyle">disabled</c:set>
            <c:set var="hrefValue" value="" />
            <c:set var="onClickEvent" value="" />
        </c:if>
	   	
	    <a class="subscription-selector option-${paymentPolicyOptionNo} ${disabledStyle}" ${onClickEvent} type="button" ${disabledAttrib} ${hrefValue} style="height: 46px;">
			<img width="52px" height="52px" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/ic_option_${imageWeeks}.png" />
	        <div class="rel networkUserPaymentOption">
	            <div class="title"><s:message code='pays.select.payby.networkuserpsms.option${paymentPolicyOptionNo}.title' /></div>
	            <span class="price">$<fmt:formatNumber pattern="0.00" value="${paymentPolicy.subcost}" /></span> <s:message code='pays.select.payby.networkuserpsms.option${paymentPolicyOptionNo}.weeks' />
	        </div>
	        <span class="${buttonStyle}"></span>
		</a>
	</div>
	    
</c:forEach>
<div style="height: 5px">&nbsp;</div><%--clearfix --%>

<script type="text/javascript">

function markSelected(elem) {
	// this function will just set the checkbox selected when the users clicks it
	// it's done to give user feedback if the confirmtion page takes a while to load 
	try{
		_mrkSlctd(elem)
	} catch (ignored) {}
}

function _mrkSlctd(elem) {
	var aElem = $(elem);
	if ( aElem.length > 0 ) {
		var attr = $(this).attr('disabled');
		if (typeof attr !== 'undefined' && attr !== false) {
			return;
		}
		var spanElem = aElem.find("span.button-off");
		if ( spanElem.length == 1 ) {
			spanElem.removeClass("button-off");
			spanElem.addClass("button-on");
		}
	}
}

</script>