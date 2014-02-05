<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div class="container">
	<div class="content">
	<h1><s:message code="pays.errors.cant_change_option.title" /></h1>
	<p><s:message code="pays.errors.cant_change_option.description" /></p>
	<div class="clr"></div>
	</div>
	
	<div>
		<input class="button-turquoise pie" title="${pageContext.request.contextPath}/payments.html" type="button" onClick="location.href=this.title" value="<s:message code="pay.paypal.dialog.button.payments" />" />
	</div>
</div>