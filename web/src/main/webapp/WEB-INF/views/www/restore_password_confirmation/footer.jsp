<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<div class="footerContainer">
	<div class="footer fixWidth">
		<div class="socialPagesSet floatLeft">
			<span class="lightGrey"><s:message code="footer.find.us.on.text" /></span>
			<a href="<s:message code='page.find.us.on.twitter.link' />" target="_blank"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/icons/twitter.png" alt="<s:message code='footer.find.us.on.twitter' />" /></a>
			<a href="<s:message code='page.find.us.on.facebook.link' />" target="_blank"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/icons/facebook.png" alt="<s:message code='footer.find.us.on.facebook' />" /></a>
			<a href="<s:message code='page.find.us.on.youtube.link' />" target="_blank"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/icons/youtube.png" alt="<s:message code='footer.find.us.on.youtube' />" /></a>
		</div>
		<div class="appStoresSet floatLeft">
			<s:message code='page.available.on.market.link' var="androidMarket" />
			<s:message code='page.available.on.store.link' var="appStore" />
			<s:message code='page.available.on.bbmarket.link' var="bbMarket" />
			
			<c:if test="${not empty androidMarket or not empty appStore or not empty bbMarket}">
				<span class="lightGrey"><s:message code="footer.available.on.text" /></span>
			</c:if>
			
			<c:if test="${not empty androidMarket}">
				<a href="${androidMarket}" target="_blank"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/icons/android.gif" alt="<s:message code='footer.available.on.market' />" /></a>
			</c:if>
			
			<c:if test="${not empty appStore}">
				<a href="${appStore}" target="_blank"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/icons/apple.gif" alt="<s:message code='footer.available.on.store' />" /></a>
			</c:if>
			
			<c:if test="${not empty bbMarket}">
				<a href="${bbMarket}" target="_blank"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/icons/blackberry.gif" alt="<s:message code='footer.available.on.bbmarket' />" /></a>
			</c:if>
		</div>
		<div class="clr"></div>
		<div class="ownerInfo lightGrey">
			<div class="allRights floatLeft" >
				<s:message code="footer.owner.info" />
		 	</div>
			<div class="fotterLinks floatRight">
				 <a href="javascript:showPopup('popupTerms');"><s:message code="footer.link.terms" /></a>
				 <a href="privacy_policy.html"><s:message code="footer.link.privacy.policy" /></a>     
			</div>
		</div>
	</div>
</div>