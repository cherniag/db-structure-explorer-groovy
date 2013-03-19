<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div class="header">
<div class="gradient_border">&#160;</div>
	<span class="logo"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo.png" alt="" /></span>
</div>
<div class="container">
    <c:set var="accountBannerON">
        <s:message code="pays.page.note.account.on"/>
    </c:set>
    <c:if test="${accountBannerON eq 'true'}">
        <div class="banner-pane">
            <c:if test="${not empty paymentAccountBanner}">
                <img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}'/>${paymentAccountBanner}" align="middle"/>
            </c:if>
            <span>${paymentAccountNotes}</span>
        </div>
    </c:if>
	<div class="content">
		
		<c:if test="${paymentDetailsByPaymentDto!=null&&paymentDetailsByPaymentDto.activated==false}">
			<h1><s:message code="pays.subscription.header" /></h1>
			<p>
			<c:set var="paymentType">
				<s:message code="${paymentDetailsByPaymentDto.paymentType}"/>
			</c:set>
			<c:set var="currencyISO">
				<s:message code="${paymentDetailsByPaymentDto.paymentPolicyDto.currencyISO}"/>
			</c:set>
			<s:message code="pays.subscription.description" arguments='${paymentType},${paymentDetailsByPaymentDto.paymentPolicyDto.subweeks},${currencyISO},${paymentDetailsByPaymentDto.paymentPolicyDto.subcost}'/></p>
			<div class="contentButton formButton rad5 rel" >
				<form action="payments/paymentDetails/${paymentDetailsByPaymentDto.paymentDetailsId}" method="post">
					<input class="button buttonSmall accounts" type="submit" value="<s:message code='pays.activate.submit' />" />
					<span class="rightButtonArrow">
						&nbsp;
					</span>
				</form>
			</div>
			<hr />
		</c:if>
		
		<h1><s:message code="pays.page.h1.options" /></h1>
		<p>${paymentPoliciesNote}</p>
		
		<div class="setOfButtons">
			<c:forEach var="paymentPolicy" items="${paymentPolicies}">
				<c:if test="${paymentPolicy.paymentType == 'creditCard'}">
					<c:set var="method_name" value="creditcard" />
					<s:message code='pays.select.payby.creditcard' var="payment_label" />
				</c:if>
				<c:if test="${paymentPolicy.paymentType == 'PAY_PAL'}">
					<c:set var="method_name" value="paypal" />
					<s:message code='pays.select.payby.paypal' var="payment_label" />
				</c:if>
				<c:if test="${paymentPolicy.paymentType == 'PSMS'}">
					<c:set var="method_name" value="psms" />
					<s:message code='pays.select.payby.psms' var="payment_label" />
				</c:if>
                <c:if test="${paymentPolicy.paymentType == 'o2Psms'}">
                    <c:set var="method_name" value="o2psms" />
                    <c:set var="payment_label" value="${paymentPolicy.subcost}&#163; for ${paymentPolicy.subweeks} week"/>
                </c:if>
				<c:if test="${paymentPolicy.paymentType == 'iTunesSubscription'}">
					<c:set var="method_name" value="iTunesSubscription" />
					<s:message code='pays.select.iTunesSubscription' var="payment_label" />
				</c:if>
				
				<div class="rel">
					<input class="button-turquoise" title="payments/${method_name}.html?policyId=${paymentPolicy.id}" type="button" onClick="location.href=this.title" value="<s:message code="${payment_label}" />" />
									
					<span class="rightButtonArrow">
						&nbsp;
					</span>
				</div>
				
			</c:forEach>
		</div>
		<c:if test="${(paymentDetails!=null) && (true==paymentDetails.activated)}">
			<hr />
			<h1><s:message code="pays.deactivate.header" /></h1>
			<div class="contentButton contentButtonGrey formButton rel formButtonGrey" >
				<input class="button-turquoise" title="payments/unsubscribe.html" type="button" onClick="location.href=this.title" value="<s:message code='pays.deactivate.submit' />" />
				<span class="rightButtonArrowBlack">
					&nbsp;
				</span>
			</div>
		</c:if>
		
	</div>
</div>