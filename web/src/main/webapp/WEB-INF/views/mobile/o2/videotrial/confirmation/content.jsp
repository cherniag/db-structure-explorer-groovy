<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>

<div class="header pie">
	<div class="gradient_border">&#160;</div>
    <span class="logo"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo.png" alt="" /></span>
    <a href="${returnUrl}" class="button-small button-right-abs"><s:message code='m.page.main.menu.close' /></a>
</div>

<div class="container">		
	<div class="content">
		
		<c:choose>
		<c:when test="${not empty hasErrors}">
			<h1><s:message code="videfreetrial.page.header" /></h1>
			
			<div class="note" id="note">
				<s:message code="videfreetrial.page.error" />
			</div>
			
			<div class="addSpace">&nbsp;</div>
			
			<div class="rel" >
				<input class="button-turquoise pie" title="${returnUrl}" type="button" onClick="location.href=this.title" value="<s:message code='unsub.inapp.form.btn.back' />" />
				<span class="button-arrow"/>
			</div>
		</c:when>
		<c:otherwise>
			<h1><s:message code="videfreetrial.page.header" /></h1>

			<p class="centered">
				<s:message code="videfreetrial.page.optinsuccess" />
			</p>
			
			<div class="addSpace">&nbsp;</div>
			
			<div class="rel" >
				<input class="button-turquoise pie" title="${returnUrl}" type="button" onClick="location.href=this.title" value="<s:message code='unsub.inapp.form.btn.back' />" />
				<span class="button-arrow"/>
			</div>
		</c:otherwise>
		</c:choose>
		
	</div>	
</div>