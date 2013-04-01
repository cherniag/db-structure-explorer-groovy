<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<div class="footer">					
	<div class="copyright">
		<s:message code="m.footer.copyright" />
		<img class="brought" width="65%" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/bgs/brought.png" alt="" />
	</div>
</div> 