<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:set var="playlistId" value="${playlists[0].id}" />
<form:form modelAttribute="playlists" method="post" action="${pageContext.request.contextPath}/playlist/${playlistId}/tracks">
<div class="header pie">
    <a href="${pageContext.request.contextPath}/account.html" class="button-small button-left"><s:message code='m.page.main.menu.cancel' /></a>
    <span class="logo"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo.png" /></span>
    <input type="submit" class="button-small button-right" value="<s:message code='m.page.main.menu.done' />" />
</div>
 </form:form>