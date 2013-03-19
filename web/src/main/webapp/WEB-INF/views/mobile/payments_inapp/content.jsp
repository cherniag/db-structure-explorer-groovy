<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div class="header">
<div class="gradient_border">&#160;</div>
	<a href="" class="logo"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo_inapp.png" alt="" /></a>
	<div class="buttonBox">
		<span class="arrow">&nbsp;</span>
		<a href="account.html" class="button-small"><s:message code='page.main.menu.my.account' /></a>
	</div>			
</div>
<div class="container">
    <div class="content">
        <img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}'/>${paymentAccountBanner}" align="middle" />
        <span>${paymentAccountNotes}</span>
    </div>
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
                <s:message code="pays.subscription.description"
                           arguments='${paymentType},${paymentDetailsByPaymentDto.paymentPolicyDto.subweeks},${currencyISO},${paymentDetailsByPaymentDto.paymentPolicyDto.subcost}'/>
            </p>
			<div class="contentButton formButton rad5 rel" >
				<form action="payments_inapp/paymentDetails/${paymentDetailsByPaymentDto.paymentDetailsId}" method="post">
					<input class="button-small" type="submit" value="<s:message code='pays.activate.submit' />" />
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
					<s:message code='pays.select.payby.creditcard' var="payment_label"/>
				</c:if>
				<c:if test="${paymentPolicy.paymentType == 'PAY_PAL'}">
					<c:set var="method_name" value="paypal" />
					<s:message code='pays.select.payby.paypal' var="payment_label" />
				</c:if>
                <c:if test="${paymentPolicy.paymentType == 'PSMS'}">
                    <c:set var="method_name" value="psms" />
                    <s:message code='pays.select.payby.psms' var="payment_label" />
                </c:if>
                <c:if test="${paymentPolicy.o2OneWeekPsmsSubscription}">
                    <c:set var="method_name" value="psms" />
                    <s:message code='pays.select.payby.psms.week1' var="payment_label" />
                </c:if>
                <c:if test="${paymentPolicy.o2TwoWeekPsmsSubscription}">
                    <c:set var="method_name" value="psms" />
                    <s:message code='pays.select.payby.psms.week2' var="payment_label" />
                </c:if>
                <c:if test="${paymentPolicy.o2FiveWeekPsmsSubscription}">
                    <c:set var="method_name" value="psms" />
                    <s:message code='pays.select.payby.psms.week5' var="payment_label" />
                </c:if>

				<c:choose>
					<c:when test="${(paymentPolicy.paymentType == 'iTunesSubscription')}">
						<c:set var="to_display" value="none" />
					</c:when>
					<c:otherwise>
						<c:set var="to_display" value="block" />
					</c:otherwise>
				</c:choose>
				
				<div class="contentButton formButton rad5 rel" style="display:${to_display}">
					<input class="button" title="payments_inapp/${method_name}.html?paymentPolicyId=${paymentPolicy.id}" type="button" onClick="location.href=this.title" value="<s:message code="${payment_label}" />" />
					<span class="rightButtonArrow">
						&nbsp;
					</span>
				</div>
				
			</c:forEach>
		</div>
		<c:if test="${(paymentDetails!=null) && (true==paymentDetails.activated)}">
			<hr />
			<h1><s:message code="pays.deactivate.header" /></h1>
			<div class="contentButton contentButtonGrey formButton rad5 rel formButtonGrey" >
				<input class="button-small" title="payments_inapp/unsubscribe.html" type="button" onClick="location.href=this.title" value="<s:message code='pays.deactivate.submit' />" />
				<span class="rightButtonArrowBlack">
					&nbsp;
				</span>
			</div>
		</c:if>
	</div>
</div>
