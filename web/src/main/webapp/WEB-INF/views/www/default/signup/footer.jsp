<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<div class="footerContainer">
	<div class="footer fixWidth">
		<div class="socialPagesSet floatLeft">
			<span class="lightGrey"><s:message code="footer.find.us.on.text" /></span>
			<a href="<s:message code='page.find.us.on.twitter.link' />"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/icons/twitter.png" alt="<s:message code='footer.find.us.on.twitter' />" /></a>
			<a href="<s:message code='page.find.us.on.facebook.link' />"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/icons/facebook.png" alt="<s:message code='footer.find.us.on.facebook' />" /></a>
			<a href="<s:message code='page.find.us.on.youtube.link' />"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/icons/youtube.png" alt="<s:message code='footer.find.us.on.youtube' />" /></a>
		</div>
		<div class="appStoresSet floatLeft">
			<span class="lightGrey"><s:message code="footer.available.on.text" /></span>
			<a href="<s:message code='page.available.on.market.link' />"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/icons/android.gif" alt="<s:message code='footer.available.on.market' />" /></a>
			<a href="<s:message code='page.available.on.store.link' />"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/icons/apple.gif" alt="<s:message code='footer.available.on.store' />" /></a>
			<a href="<s:message code='page.available.on.bbmarket.link' />"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/icons/blackberry.gif" alt="<s:message code='footer.available.on.bbmarket' />" /></a>
		</div>
		<div class="clr"></div>
		<div class="ownerInfo lightGrey">
			<a href="javascript:showPopup('popupTerms');"><s:message code="footer.owner.info" /></a>
		</div>
	</div>
</div>