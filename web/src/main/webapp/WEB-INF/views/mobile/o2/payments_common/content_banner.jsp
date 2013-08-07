<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>

<div class="container" style="background-color: inherit;margin-top: 20px; margin-bottom: 17px;">
	<div class="frR15" style="font-size: 13px; color: #66ccff; margin-bottom: 6px;">
		<c:if test="${not empty paymentPageData and not empty paymentPageData.subscriptionTexts and not empty paymentPageData.subscriptionTexts.statusText}">
			<c:out value="${paymentPageData.subscriptionTexts.statusText}" />
			
			<c:if test="${not empty paymentPageData.subscriptionTexts.futureText}">
				&nbsp;/&nbsp;<c:out value="${paymentPageData.subscriptionTexts.futureText}" />
			</c:if>
			
			<c:if test="${not empty paymentPageData.subscriptionTexts.nextBillingText}">
				<br /><c:out value="${paymentPageData.subscriptionTexts.nextBillingText}" />
			</c:if>
		</c:if>
	</div>
	<div class="frL11" style="font-size: 15px; color: #ffffff">
		<img style="width: 8px; height: 15px; margin-right: 9px" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/ic_phone_account.png" />
		${mobilePhoneNumber}
	</div>
</div>