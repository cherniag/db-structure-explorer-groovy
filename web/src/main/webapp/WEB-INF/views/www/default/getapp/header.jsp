<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<div class="header panel">
	<a href="homepage.html" class="logo"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo.png" alt="<s:message code='page.img.logo.alt' />" /></a>

	<div class="authorization">
		<div class="menu">
			<a href="faq.html"><s:message code='page.header.link.faq' /></a>
			<span>|</span>
			<a href="contactus.html"><s:message code='page.header.link.contactus' /></a>
			<span>|</span>
			<a href="signout" class="rad3"><s:message code='page.header.link.signout' /></a>
		</div>
	</div>
</div>