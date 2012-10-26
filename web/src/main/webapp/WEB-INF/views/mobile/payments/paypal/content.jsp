<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<div class="header">
<div class="gradient_border">&#160;</div>
	<a href="" class="logo"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo.png" alt="" /></a>
	<c:if test="${result==null||result=='fail'}">
	<div class="buttonBox">
		<span class="arrow">&nbsp;</span>	
		<input class="button accounts" title="payments.html" type="button" onClick="location.href=this.title" value="<s:message code='m.page.main.menu.back' />" />		
	</div>				
	</c:if>
</div>
<div class="container">
	<div class="content">
	<c:choose>
		<c:when test="${result!=null&&result!='fail'}">
			<h1><s:message code='pay.paypal.dialog.successful.title' /></h1>
			<p><s:message code='pay.paypal.dialog.successful.body.inapp' /></p>
			<div class="clr"></div>				
			<div class="addSmallSpace"></div>		
			<!--button-->
			<div class="contentButton formButton rad10 rel" >
				<input class="button accounts" title="account.html" type="button" onClick="location.href=this.title" value="<s:message code='m.pay.paypal.dialog.successful.button.inapp' />" />
				<span class="rightButtonArrow">
					&nbsp;
				</span>
			</div>
		</c:when>
		<c:otherwise>		
			<form:form modelAttribute="payPalDto" method="post">
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