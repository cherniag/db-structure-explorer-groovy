<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div class="header">
<div class="gradient_border">&#160;</div>
	<a href="" class="logo"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo_inapp.png" alt="" /></a>
	<div class="buttonBox">
		<span class="arrow">&nbsp;</span>
		<input class="button accounts" class="buttonTop" title="account.html" type="button" onClick="location.href=this.title" value="<s:message code='page.main.menu.my.account' />" />
	</div>			
</div>
<div class="container">
	<div class="content">
		<c:if test="${paymentDetailsByPaymentDto!=null&&paymentDetailsByPaymentDto.activated==false}">
			<h1><s:message code="pays.subscription.header" /></h1>
			<p>
			<c:set var="paymentType">
				<s:message code="${paymentDetailsByPaymentDto.paymentType}"/>
			</c:set>
			<c:set var="currencyISO">
				<s:message code="${paymentDetailsByPaymentDto.paymentPolicyDto.currencyISO}"/>
			</c:set>
			<s:message code="pays.subscription.description" arguments='${paymentType},${paymentDetailsByPaymentDto.paymentPolicyDto.subweeks},${currencyISO},${paymentDetailsByPaymentDto.paymentPolicyDto.subcost}'/></p>
			<div class="contentButton formButton rad5 rel" >
				<form action="payments_inapp/paymentDetails/${paymentDetailsByPaymentDto.paymentDetailsId}" method="post">
					<input class="button buttonSmall accounts" type="submit" value="<s:message code='pays.activate.submit' />" />
					<span class="rightButtonArrow">
						&nbsp;
					</span>
				</form>
			</div>
			<hr />
		</c:if>
		
		<h1><s:message code="pays.page.h1.options" /></h1>
		<p>${paymentPoliciesNote}</p>
		
		<div class="setOfButtons">
			<c:forEach var="paymentPolicy" items="${paymentPolicies}">
				<c:if test="${paymentPolicy.paymentType == 'creditCard'}">
					<c:set var="method_name" value="creditcard" />
					<s:message code='pays.select.creditcard' var="method_readable" />
				</c:if>
				<c:if test="${paymentPolicy.paymentType == 'PAY_PAL'}">
					<c:set var="method_name" value="paypal" />
					<s:message code='pays.select.paypal' var="method_readable" />
				</c:if>
				<c:if test="${paymentPolicy.paymentType == 'PSMS'}">
					<c:set var="method_name" value="psms" />
					<s:message code='pays.select.psms' var="method_readable" />
				</c:if>
				
				<div class="contentButton formButton rad5 rel" >
					<input class="button" title="payments_inapp/${method_name}.html" type="button" onClick="location.href=this.title" value="<s:message code="pays.select.payby" /> ${method_readable}" />
					<span class="rightButtonArrow">
						&nbsp;
					</span>
				</div>
				
			</c:forEach>
		</div>
		<c:if test="${(paymentDetails!=null) && (true==paymentDetails.activated)}">
			<hr />
			<h1><s:message code="pays.deactivate.header" /></h1>
			<div class="contentButton contentButtonGrey formButton rad5 rel formButtonGrey" >
				<input class="button buttonSmall accounts" title="payments_inapp/unsubscribe.html" type="button" onClick="location.href=this.title" value="<s:message code='pays.deactivate.submit' />" />
				<span class="rightButtonArrowBlack">
					&nbsp;
				</span>
			</div>
		</c:if>
	</div>
</div>