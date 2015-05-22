<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<div class="header panel">
	<a href="" class="logo"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo.png" alt="<s:message code='page.img.logo.alt' />" /></a>
	<sec:authorize access="authenticated">
		<div class="authorization">
			<div class="menu">
				<a id="faq" href="faq.html"><s:message code='page.header.link.faq' /></a>
				<span>|</span>
				<a href="signout" class="rad3"><s:message code='page.header.link.signout' /></a>
			</div>
		</div>
	</sec:authorize>
	<sec:authorize access="anonymous">
	<div class="authorization innerPage">
		<div class="signIn normalFont">
			<s:message code="signup.page.service.text" />
			<a href="signin.html" class="bold"><s:message code="signup.page.service.link.singin" /></a>
		</div>
	</div>
	</sec:authorize>
</div>