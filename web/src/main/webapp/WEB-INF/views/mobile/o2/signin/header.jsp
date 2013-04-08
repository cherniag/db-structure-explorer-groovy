<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<div class="header pie">
    <div class="gradient_border">&#160;</div>
    <a href="<s:message code='page.dashboard.link' />" class="logo"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo.png" alt="" /></a>
    <s:message code='signin.page.service.link.singup' var="signin_page_service_link_singup" />
    <c:if test="${not empty signin_page_service_link_singup}">
        <div class="buttonBox">
            <a href="signup.html" class="button buttonTop" id="loginButton"><s:message code="signin.page.service.link.singup" /></a>
            <span class="arrow">&nbsp;</span>
        </div>
    </c:if>
</div>