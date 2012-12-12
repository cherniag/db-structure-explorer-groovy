<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div class="header">
<div class="gradient_border">&#160;</div>
	<a href="" class="logo"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo.png" alt="" /></a>
	<div class="buttonBox">
		<span class="arrow">&nbsp;</span>
		<input class="button accounts" class="buttonTop" title="offers/${offerId}" type="button" onClick="location.href=this.title" value="<s:message code='m.page.main.menu.back' />" />
	</div>			
</div>
<div class="container">
	<div class="content">
		<h1><s:message code="store.pays.page.h1.options" /></h1>
		<p><s:message code="store.pays.page.h1.options.note" /></p>
		<div class="setOfButtons">
			<c:forEach var="paymentPolicy" items="${offerPaymentPolicyDtoList}">
				<c:if test="${paymentPolicy.paymentType == 'creditCard'}">
					<c:set var="method_name" value="creditcard" />
					<s:message code='store.pays.select.creditcard' var="method_readable" />
				</c:if>
				<c:if test="${paymentPolicy.paymentType == 'PAY_PAL'}">
					<c:set var="method_name" value="paypal" />
					<s:message code='store.pays.select.paypal' var="method_readable" />
				</c:if>
				<c:if test="${paymentPolicy.paymentType == 'PSMS'}">
					<c:set var="method_name" value="psms" />
					<s:message code='store.pays.select.psms' var="method_readable" />
				</c:if>
				
				<div class="contentButton formButton rad5 rel" >
					<input class="button" title=" offers/${offerId}/payments/${method_name}.html" type="button" onClick="location.href=this.title" value="<s:message code="store.pays.select.payby" /> ${method_readable}" />
									
					<span class="rightButtonArrow">
						&nbsp;
					</span>
				</div>
				
			</c:forEach>
		</div>
	</div>
</div>