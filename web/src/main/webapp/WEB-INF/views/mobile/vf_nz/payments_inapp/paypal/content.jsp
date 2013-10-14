<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>


<style type="text/css">
body {
	background-color: #fff;
}
.container {
	background-color: #fff;
}
</style>

<div class="container">
<div class="content" style="padding: 3px;">

	<div class="paypalImageContainer">
		<img src="${requestScope.assetsPathAccordingToCommunity}imgs/icon_paypal.png" align="bottom" class="pypalImage" style="margin: 0px;" />
	</div>

<c:choose>
<c:when test="result!='fail'">
	<%--successful paypal payment --%>
	<div class="paypalMessgeHeader"><s:message code='pay.paypal.dialog.successful.title' /></div>
	<div class="paypalMessageText"><s:message code='pay.paypal.dialog.successful.message' /></div>
	<input class="button-turquoise pie" title="${pageContext.request.contextPath}/payments_inapp.html" type="button" onClick="location.href=this.title" value="<s:message code='pay.paypal.dialog.button' />" />
</c:when>
<c:otherwise>
	<%--some error occurred --%>
	<div class="paypalMessageText">
		<c:choose>
			<c:when test="${not empty external_error}">
				<span><s:message code="pay.paypal.result.fail" />${external_error}</span>
			</c:when>
			<c:otherwise>
				<span>${internal_error}</span>
			</c:otherwise>
		</c:choose>
	</div>
	<input class="button-turquoise pie" title="${pageContext.request.contextPath}/payments_inapp.html" type="button" onClick="location.href=this.title" value="<s:message code='pay.paypal.dialog.button.payments' />" />
</c:otherwise>
</c:choose>

<div class="content no-bg">
	<div class="rel" style="text-align: center; margin-top: 15px;">
		<img width="79" height="12" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/label_secure_payment.png" />
	</div>
</div>

</div>
</div>

<%-- <div class="container">
	<div class="content">
	<c:choose>
		<c:when test="${result!=null&&result!='fail'}">
			<h1><s:message code='pay.paypal.dialog.successful.title' /></h1>
			<p><s:message code='pay.paypal.dialog.successful.body.inapp' /></p>
			<div class="clr"></div>				
			<div class="addSmallSpace"></div>		
			<!--button-->
			<div class="contentButton formButton rad10 rel" >
				<input class="button accounts button-turquoise pie" title="${pageContext.request.contextPath}/payments_inapp.html" type="button" onClick="location.href=this.title" value="<s:message code='m.pay.paypal.dialog.successful.button.payments' />" />
				<span class="button-arrow"/>
			</div>
		</c:when>
		<c:otherwise>		
			<form:form modelAttribute="payPalDto" method="post">
				<input type="hidden" name="paymentPolicyId" value="${paymentPolicy.id}"/>
				<h1><s:message code="pay.paypal.form.title" /></h1>
				<div class="payDetails">
					<p><s:message code="pay.paypal.form.description"
								arguments="${paymentPolicy.subweeks};${paymentPolicy.subcost}"
       							htmlEscape="false"
       							argumentSeparator=";"/>
					</p>
					<!--end one record in profile-->			
				</div>
				
				<!--button-->
				<p><input type="image" style="with:145px; height:42px; border: none!important;" src="https://www.paypal.com/en_US/i/btn/btn_xpressCheckout.gif"></p>		
				<!--button-->
				<c:choose>
					<c:when test="${result=='fail'}">
						<div class="note" id="note">
							<c:choose>
								<c:when test="${not empty external_error}">
									<span><s:message code="pay.paypal.result.fail" />${external_error}</span>
								</c:when>
								<c:otherwise>
									<span>${internal_error}</span>
								</c:otherwise>
							</c:choose>
						</div>
					</c:when>
				</c:choose>
			</form:form>
		</c:otherwise>
	</c:choose>
	</div>
</div>
 --%>