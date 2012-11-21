<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<div class="header">
<div class="gradient_border">&#160;</div>
	<span class="logo"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo.png" alt="" /></span>
</div>
<div class="container">
	<div class="content">
		<h1><s:message code='offer.pay.cc.dialog.successful.title' /></h1>
		<p><s:message code='offer.pay.cc.dialog.successful.body' /></p>
		<div class="clr"></div>				
		<div class="addSmallSpace"></div>		
		<!--button-->
		<div class="contentButton formButton rad10 rel" >
			<input class="button accounts" title="purchased_offers.html" type="button" onClick="location.href=this.title" value="<s:message code='offer.pay.cc.dialog.successful.button' />" />
			<span class="rightButtonArrow">
				&nbsp;
			</span>
		</div>
	</div>
</div>