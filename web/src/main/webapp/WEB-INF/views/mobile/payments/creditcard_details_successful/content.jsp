<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<div class="header">
<div class="gradient_border">&#160;</div>
	<span class="logo"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo.png" alt="" /></span>
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
			<span class="rightButtonArrow">
				&nbsp;
			</span>
		</div>
	</div>
</div>