<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<div class="header pie">
<div class="gradient_border">&#160;</div>
	<a href="javascript:;" onclick="closeForm()" class="logo"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo_holding.png" alt="Logo" /></a>
	<div class="buttonBox">
		<span class="arrow">&nbsp;</span>
		<a href="javascript:;" onclick="closeForm()" class="button buttonTop"><s:message code="m.page.main.menu.back" /></a>
	</div>	
</div>