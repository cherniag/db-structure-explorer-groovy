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
	<input class="button-turquoise pie" title="${pageContext.request.contextPath}/account.html" type="button" onClick="location.href=this.title" value="<s:message code='pay.paypal.dialog.button' />" />
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
	<input class="button-turquoise pie" title="${pageContext.request.contextPath}/account.html" type="button" onClick="location.href=this.title" value="<s:message code='pay.paypal.dialog.button' />" />
</c:otherwise>
</c:choose>

<div class="content no-bg">
	<div class="rel" style="text-align: center; margin-top: 15px;">
		<img width="79" height="12" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/label_secure_payment.png" />
	</div>
</div>

</div>
</div>
