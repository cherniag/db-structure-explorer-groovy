<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<div class="header pie">
<div class="gradient_border">&#160;</div>
	<a href="" class="logo"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo.png" alt="" /></a>			
</div>
<div class="container">
	<div class="content">
		<h1><s:message code="page.account.header.h1" /></h1>
		<p><s:message code="page.account.header.description" /></p>
	</div>
</div>