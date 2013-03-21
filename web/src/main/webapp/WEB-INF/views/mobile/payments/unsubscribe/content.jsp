<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<div class="header">
<div class="gradient_border">&#160;</div>
    <a href="payments.html" class="button-small button-left"><s:message code='m.page.main.menu.back' /></a>
    <span class="logo"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo.png" alt="" /></span>
    <a href="account.html" class="button-small button-right"><s:message code='m.page.main.menu.close' /></a>
</div>
<div class="container">		
	<div class="content">
		<c:choose>
			<c:when test="${result == null||result == 'fail'}">
				<h1><s:message code="unsub.page.header" /></h1>
				<p><s:message code="unsub.page.description" /></p>
				
				<form:form modelAttribute="unsubscribeDto" method="post">							
					<div class="oneField">
						<form:textarea path="reason"/>
					</div>
					<s:hasBindErrors name="unsubscribeDto">
							<div class="note" id="note">
								<form:errors path="reason" />
							</div>
					</s:hasBindErrors>
					<!--button -->
					<div class="contentButton formButton rad5 rel" >
						<input class="button-turquoise" title="payments.html" type="button" onClick="location.href=this.title" value="<s:message code='unsub.page.form.btn.cancel' />" />
						<span class="leftButtonArrow">
							&nbsp;
						</span>
					</div>
					<!--button -->
					<div class="contentButton contentButtonGrey formButton formButtonGrey rad5 rel" >
						<input type="submit" class="button-turquoise" value="<s:message code='unsub.page.form.submit' />" />
						<span class="rightButtonArrowBlack">
							&nbsp;
						</span>
					</div>
				</form:form>
			</c:when>
			<c:otherwise>
				<h1><s:message code="unsub.page.header" /></h1>
				<p><s:message code="unsub.page.description.unsubscribed" /></p>
				<div class="addSpace"></div>			
				<div class="rel" >
					<input class="button-turquoise" title="account.html" type="button" onClick="location.href=this.title" value="<s:message code='unsub.inapp.form.btn.back' />" />
					<span class="button-arrow"/>
				</div>
			</c:otherwise>
		</c:choose>
	</div>	
</div>