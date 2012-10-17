<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<div class="header">
<div class="gradient_border">&#160;</div>
	<a href="" class="logo"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo_inapp.png" alt="" /></a>
	<%--<div class="buttonBox">
		<span class="arrow">&nbsp;</span>
		<a href="account.html" class="button buttonTop"><s:message code="page.main.menu.my.account" /></a>
	</div>--%>
</div>
<div class="container">
	<div class="content">
		<h1><s:message code='one.click.subscription.dialog.successful.title' /></h1>
		<c:set var="paymentType">
			<s:message code="${paymentDetailsByPaymentDto.paymentType}"/>
		</c:set>
		<c:set var="currencyISO">
			<s:message code="${paymentDetailsByPaymentDto.paymentPolicyDto.currencyISO}"/>
		</c:set>
		<p><s:message code='one.click.subscription.dialog.successful.body' arguments='${paymentType},${paymentDetailsByPaymentDto.paymentPolicyDto.subweeks},${currencyISO},${paymentDetailsByPaymentDto.paymentPolicyDto.subcost}' /></p>
		<div class="clr"></div>				
		<div class="addSmallSpace"></div>		
		<!--button-->
		<div class="contentButton formButton rad10 rel" >
			<input class="button accounts" title="account.html" type="button" onClick="location.href=this.title" value="<s:message code='one.click.subscription.dialog.successful.button'/>" />
			<span class="rightButtonArrow">
				&nbsp;
			</span>
		</div>
	</div>
</div>