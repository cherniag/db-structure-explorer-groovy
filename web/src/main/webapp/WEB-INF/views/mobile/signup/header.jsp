<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>

<div class="header">
<div class="gradient_border">&#160;</div>
	<a href="<s:message code='page.dashboard.link' />" class="logo"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo.png" alt="" /></a>	
	<div class="buttonBox">
		<a href="signin.html" class="button buttonTop" id="loginButton"><s:message code="signup.page.service.link.singin" /></a>
		<span class="arrow">&nbsp;</span>
	</div>
</div>