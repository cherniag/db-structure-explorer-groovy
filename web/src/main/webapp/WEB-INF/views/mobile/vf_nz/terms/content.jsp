<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<style type="text/css">
ol > li {
	margin-top: 10px;
}
ol {
	padding-left: 20px;
}
</style>

 <div class="header pie">
    <span class="logo" style="padding-left: 49px; visibility: hidden;"><s:message code='m.page.main.header' /></span>
    <c:if test="${showBackButton eq true}">
		<a href="javascript: window.history.back()" class="button-small button-right pie"><s:message code='m.page.main.menu.back' /></a>
	</c:if>
</div>


<form:form method="post" cssClass="contact" modelAttribute="contactUsDto">
	<div class="container">
		<%--<div class="content terms">
			<h1><s:message code="dialog.terms.h1" /></h1>
			<s:message code="dialog.terms.content" />
		</div>--%>
	<div class="content terms" style="text-align: left;">
		<s:message code="dialog.terms.content" />
	</div>
	
</div>
</form:form>