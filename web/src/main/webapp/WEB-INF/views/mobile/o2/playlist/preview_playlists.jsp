<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div class="height-header color-header text-center" xmlns="http://www.w3.org/1999/html">
    <div class="in-middle font-main S17 marg-T15">
        <div class="icon-playlist in-middle"></div>
        <div class="in-middle"><s:message code='page.playlists.header.text' /></div>
    </div>
    <a href="${pageContext.request.contextPath}/account.html" class="button-small button-right"><s:message code='m.page.main.menu.close' /></a>
</div>
<div class="container">
	<div class="content">
		<h1><s:message code="page.playlists.preview.header" /></h1>
		<p><s:message code="page.playlists.preview.body" /></p>
	</div>
</div>
