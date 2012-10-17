<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>

<div class="header panel">
	<a href="homepage.html" class="logo"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo.png" alt="ChartsNow Logo" /></a>
	<div class="authorization innerPage">
		<div class="signIn normalFont">
			<a href="javascript: history.go(-1)" class="bold"><s:message code="m.page.main.menu.back" /></a>	
		</div>
	</div>
</div>