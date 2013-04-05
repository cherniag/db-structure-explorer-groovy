
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<div class="header">
	<span class="logo"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo.png" /></span>
</div>
<div class="container">
	<div class="content">
		<h1><s:message code='one.click.subscription.dialog.successful.title.inapp' /></h1>
		<c:set var="paymentType">
			<s:message code="${paymentDetailsByPaymentDto.paymentType}"/>
		</c:set>
		<c:set var="currencyISO">
			<s:message code="${paymentDetailsByPaymentDto.paymentPolicyDto.currencyISO}"/>
		</c:set>
		<p><s:message code='one.click.subscription.dialog.successful.body.inapp' arguments='${paymentType},${paymentDetailsByPaymentDto.paymentPolicyDto.subweeks},${currencyISO},${paymentDetailsByPaymentDto.paymentPolicyDto.subcost}' /></p>
		<div class="clr"></div>				
		<div class="addSmallSpace"></div>		
		<!--button-->
		<div class="rel" >
		
			<input class="button-turquoise" title="${pageContext.request.contextPath}/account.html" type="button" onClick="location.href=this.title" value="<s:message code='one.click.subscription.dialog.successful.button.inapp'/>" />
            <span class="button-arrow"/>
		</div>
	</div>
</div>