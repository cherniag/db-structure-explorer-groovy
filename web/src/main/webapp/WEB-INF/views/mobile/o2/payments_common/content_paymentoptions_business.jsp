<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:forEach var="paymentPolicy" items="${paymentPolicies}">
<c:if test="${paymentPolicy.paymentType != 'iTunesSubscription'}">

	<c:if test="${paymentPolicy.paymentType == 'creditCard'}">
        <c:set var="method_name" value="creditcard" />
        <s:message code='pays.select.payby.creditcard' var="payment_label" />
        <c:set var="image_name" value="ic_debit_credit_card.png" />
        <c:set var="image_width" value="117px" />
    </c:if>
    <c:if test="${paymentPolicy.paymentType == 'PAY_PAL'}">
        <c:set var="method_name" value="paypal" />
        <s:message code='pays.select.payby.paypal' var="payment_label" />
        <c:set var="image_name" value="ic_paypal.png" />
        <c:set var="image_width" value="37px" />
    </c:if>

	<div class="rel tapArea" data-hasvideo="${paymentPolicy.videoAndAudio4GSubscription ? '1' : '0'}" id="paymentOption${paymentPolicy.id}">
		<div class="subscription-container">
		    <a class="subscription-selector option-3" style="margin-bottom: 0px;" href="${pageContext.request.contextPath}/<%=request.getParameter("callingPage")%>/${method_name}.html?paymentPolicyId=${paymentPolicy.id}" type="button">
		 		<img width="51px" height="51px" style="margin-right: 6px" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/ic_option_other.png" />
		         <div class="rel o2BusinessSubscription">
		             <span class="frR15"><s:message code="${payment_label}" /></span>
		         </div>
		
		         <c:set var="cssClass" value="button-off" />
		         <c:if test="${paymentDetailsType != null && paymentDetailsType == method_name && paymentDetails.activated eq 'true'}">
		         	<c:set var="cssClass" value="button-on" />
		         </c:if>
		         
		         <span class="${cssClass}"></span>
			</a>
		
		    <div class="rel o2businessPaymentType">
		    	<img height="15px" width="${image_width}" src="${requestScope.assetsPathAccordingToCommunity}imgs/${image_name}" />
		    </div>
		</div>
	</div>

</c:if>
</c:forEach>
<div style="height: 5px">&nbsp;</div><%--clearfix --%>