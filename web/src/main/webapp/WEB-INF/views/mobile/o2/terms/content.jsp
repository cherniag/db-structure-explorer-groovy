<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<div class="header pie">

<div class="gradient_border">&#160;</div>
	<c:if test="${showBackButton eq true}">
		<a href="javascript: window.history.back()" class="button-small button-left pie"><s:message code='m.page.main.menu.back' /></a>
	</c:if>
	<a href="${pageContext.request.contextPath}/account.html" class="logo"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo.png" alt="" /> </a>
</div>
<form:form method="post" cssClass="contact" modelAttribute="contactUsDto">
	<div class="container">
		<div class="content terms">
			<h1><s:message code="dialog.terms.h1" /></h1>
			<s:message code="dialog.terms.content" />
		</div>
	</div>
</form:form>