<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:forEach var="paymentPolicy" items="${paymentPolicies}">
<c:if test="${paymentPolicy.paymentType != 'iTunesSubscription'}">

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

	<div class="rel tapArea" data-hasvideo="${paymentPolicy.videoAndAudio4GSubscription ? '1' : '0'}">
		<div class="subscription-container" style="margin-bottom: 5px;">
		    <a class="subscription-selector option-3" style="margin-bottom: 0px;" href="${pageContext.request.contextPath}/<%=request.getParameter("callingPage")%>/${method_name}.html?paymentPolicyId=${paymentPolicy.id}" type="button">
		 		<img style="width:51px; height:51px;" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/ic_option_other.png" />
		         <div class="rel" style="padding-top: 14px;">
		             <span style="font-family: frutigerRoman,Helvetica,Arial,sans-serif; font-size: 15px"><s:message code="${payment_label}" /></span>
		         </div>
		
		         <c:set var="cssClass" value="button-off" />
		         <c:if test="${paymentDetailsType != null && paymentDetailsType == method_name && paymentDetails.activated eq 'true'}">
		         	<c:set var="cssClass" value="button-on" />
		         </c:if>
		         
		         <span class="${cssClass}"></span>
			</a>
		
		    <div class="rel" style="margin:0 6px; padding-top:3px; border-top: 1px solid #a0a0a0; text-align: center;">
				<img style="height:13px;" src="${requestScope.assetsPathAccordingToCommunity}imgs/ic_${method_name}.png" />
		    </div>
		</div>
	</div>

</c:if>
</c:forEach>
<div style="height: 5px">&nbsp;</div><%--clearfix --%>