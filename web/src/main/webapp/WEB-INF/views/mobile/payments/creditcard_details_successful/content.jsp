<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<div class="header">
    <a href="payments.html" class="button-small button-left"><s:message code='m.page.main.menu.back' /></a>
    <span class="logo"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo.png" /></span>
    <a href="payments.html" class="button-small button-right"><s:message code='m.page.main.menu.close' /></a>
</div>
<div class="container">
	<div class="content">
		<h1><s:message code='pay.cc.dialog.successful.title' /></h1>
		<p><s:message code='pay.cc.dialog.successful.body.inapp' /></p>
		<div class="clr"></div>				
		<div class="addSmallSpace"></div>		
		<!--button-->
		<div class="contentButton formButton rad10 rel" >
			<input class="button-turquoise" title="account.html" type="button" onClick="location.href=this.title" value="<s:message code='m.pay.cc.dialog.successful.button.inapp' />" />
            <span class="button-arrow"/>
		</div>
	</div>
</div>