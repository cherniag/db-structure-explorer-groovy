<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<div class="footer">					
	<div class="copyright">
		<s:message code="m.footer.copyright" />
		<img style="width: 100px; height: 24px;" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/footer_non_O2.png" alt="" />
	</div>
</div> 