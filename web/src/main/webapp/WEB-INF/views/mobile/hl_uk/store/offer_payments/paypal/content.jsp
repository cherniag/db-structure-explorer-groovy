<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<div class="header pie">
<div class="gradient_border">&#160;</div>
	<span class="logo"><img
		src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo.png"
		alt="" /></span>
	<c:if test="${result==null||result=='fail'}">
		<div class="buttonBox">
			<span class="arrow">&nbsp;</span> <input class="button accounts"
				title="offers/${offerId}/payments.html" type="button"
				onClick="location.href=this.title"
				value="<s:message code='m.page.main.menu.back' />" />
		</div>
	</c:if>
</div>
<div class="container">
	<div class="content">
		<c:choose>
			<c:when test="${result!=null&&result!='fail'}">
				<h1>
					<s:message code='offer.pay.paypal.dialog.successful.title' />
				</h1>
				<p>
					<s:message code='offer.pay.paypal.dialog.successful.body' />
				</p>
				<div class="clr"></div>
				<div class="addSmallSpace"></div>
				<!--button-->
				<div class="contentButton formButton rad10 rel">
					<input class="button accounts" title="purchased_offers.html" type="button"
						onClick="location.href=this.title"
						value="<s:message code='offer.pay.paypal.dialog.successful.button' />" />
					<span class="rightButtonArrow"> &nbsp; </span>
				</div>
			</c:when>
			<c:otherwise>
				<c:choose>
					<c:when test="${result=='fail'}">
						<div class="note" id="note">
							<c:choose>
								<c:when test="${not empty external_error}">
									<span><s:message code="offer.pay.paypal.result.fail" />${external_error}</span>
								</c:when>
								<c:otherwise>
									<span>${internal_error}</span>
								</c:otherwise>
							</c:choose>
						</div>
					</c:when>
				</c:choose>
			</c:otherwise>
		</c:choose>
	</div>
</div>