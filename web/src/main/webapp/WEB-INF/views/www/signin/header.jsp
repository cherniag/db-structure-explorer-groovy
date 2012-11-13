<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>

<div class="header panel">
	<a href="<s:message code='page.dashboard.link' />" class="logo"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo.png" alt="ChartsNow Logo" /></a>
	<s:message code='signin.page.service.link.singup' var="signin_page_service_link_singup" />
	<c:if test="${not empty signin_page_service_link_singup}">
		<div class="authorization innerPage">
			<div class="signIn normalFont">
				<s:message code="signin.page.service.text" />
				<a href="signup.html" class="bold"><s:message code="signin.page.service.link.singup" /></a>
			</div>
		</div>
	</c:if>
</div>