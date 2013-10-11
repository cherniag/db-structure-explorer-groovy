<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div class="container">
	<div class="content" style="padding: 0 3px;">
		<c:choose>
			<c:when test="${result == null||result == 'fail'}">
				<div class="vfR S15 redColor top15"><s:message code="unsub.page.header" /></div>
				<div class="unsubscribeText"><s:message code="unsub.page.description" /></div>
				
				<form:form modelAttribute="unsubscribeDto" method="post">							

					<s:hasBindErrors name="unsubscribeDto">
							<div class="note" id="note">
								<form:errors path="reason" />
							</div>
					</s:hasBindErrors>
					<div class="rel" style="margin-top: 20px;">
						<input type="submit" class="button-white pie" value="<s:message code='unsub.page.form.submit' />" />
						<input class="button-white pie" title="${pageContext.request.contextPath}/payments_inapp.html" type="button" onClick="location.href=this.title" value="<s:message code='unsub.page.form.btn.cancel' />" />
					</div>
				</form:form>
			</c:when>
			<c:otherwise>
				<div class="vfR S15 redColor top15"><s:message code="unsub.page.header" /></div>
				<div class="unsubscribeText bottom15"><s:message code="unsub.page.description.unsubscribed" /></div>
				<div class="rel" >
					<input class="button-turquoise pie" title="${pageContext.request.contextPath}/payments_inapp.html" type="button" onClick="location.href=this.title" value="<s:message code='unsub.page.form.btn.back.payments' />" />
					<span class="button-arrow"/>
				</div>
			</c:otherwise>
		</c:choose>
	</div>	
</div>

<div class="content no-bg">
	<div class="rel" style="text-align: center; margin-top: 5px;">
		<img width="113" height="20" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/footer.png" />
	</div>
</div>