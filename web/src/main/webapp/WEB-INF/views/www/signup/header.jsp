<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>

<div class="header panel">
	<a href="<s:message code='page.dashboard.link' />" class="logo"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo.png" alt="ChartsNow Logo" /></a>
	<div class="authorization innerPage">
		<div class="signIn normalFont">
			<s:message code="signup.page.service.text" />
			<a href="signin.html" class="bold"><s:message code="signup.page.service.link.singin" /></a>
		</div>
	</div>
</div>