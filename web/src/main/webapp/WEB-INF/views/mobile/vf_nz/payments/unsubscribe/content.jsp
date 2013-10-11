<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<div class="header pie">
<div class="gradient_border">&#160;</div>
    <span class="logo"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo.png" alt="" /></span>
    <a href="${pageContext.request.contextPath}/account.html" class="button-small button-right"><s:message code='m.page.main.menu.close' /></a>
</div>
<div class="container">		
	<div class="content">
		<c:choose>
			<c:when test="${result == null||result == 'fail'}">
				<h1><s:message code="unsub.page.header" /></h1>
				<p class="centered"><s:message code="unsub.page.description" /></p>
				
				<form:form modelAttribute="unsubscribeDto" method="post">							

					<s:hasBindErrors name="unsubscribeDto">
							<div class="note" id="note">
								<form:errors path="reason" />
							</div>
					</s:hasBindErrors>
					<div class="rel" style="margin-top: 20px;">
						<input type="submit" class="button-turquoise pie" value="<s:message code='unsub.page.form.submit' />" />
						<input class="button-grey pie" title="${pageContext.request.contextPath}/payments.html" type="button" onClick="location.href=this.title" value="<s:message code='unsub.page.form.btn.cancel' />" />
					</div>
				</form:form>
			</c:when>
			<c:otherwise>
				<h1><s:message code="unsub.page.header" /></h1>
				<p><s:message code="unsub.page.description.unsubscribed" /></p>
				<div class="addSpace"></div>			
				<div class="rel" >
					<input class="button-turquoise pie" title="${pageContext.request.contextPath}/account.html" type="button" onClick="location.href=this.title" value="<s:message code='unsub.inapp.form.btn.back' />" />
					<span class="button-arrow"/>
				</div>
			</c:otherwise>
		</c:choose>
	</div>	
</div>