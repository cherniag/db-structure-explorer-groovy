<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>

<div id="popupTerms" class="popupOverflow">
	<div class="popupWindow rel">
		<div class="popupContent">
			<a href="javascript:hidePopup('popupTerms');" id="closeButton2" class="closeButton abs"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/icons/close.png" alt="Close Window" />&nbsp;</a>
			<h1><s:message code="dialog.terms.h1" /></h1>
			<s:message code="dialog.terms.content" />
		</div>
	</div>
</div>