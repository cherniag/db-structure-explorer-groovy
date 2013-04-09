<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<div class="header pie">
    <a href="${pageContext.request.contextPath}/payments_inapp.html" class="button-small button-left"><s:message code='m.page.main.menu.back' /></a>
    <span class="logo"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo.png" /></span>
</div>
<div class="container">
	<div class="content">
		<h1><s:message code='pay.cc.dialog.successful.title' /></h1>
		<p><s:message code='pay.cc.dialog.successful.body.inapp' /></p>
		<div class="clr"></div>				
		<div class="addSmallSpace"></div>		
		<!--button-->
		<div class="rel" >
			<input class="button-turquoise pie" title="${pageContext.request.contextPath}/payments_inapp.html" type="button" onClick="location.href=this.title" value="<s:message code='m.pay.cc.dialog.successful.button.payments' />" />
            <span class="button-arrow"/>
		</div>
	</div>
</div>