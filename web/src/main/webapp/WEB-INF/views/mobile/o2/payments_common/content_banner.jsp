<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>

<div class="paymentscontainer" style="background-color: inherit;margin-top: 20px; margin-bottom: 17px;">
	<div class="userStatusText">
		<c:if test="${not empty paymentsPage.paymentPageData and not empty paymentsPage.paymentPageData.subscriptionTexts and not empty paymentsPage.paymentPageData.subscriptionTexts.statusText}">
			<c:out value="${paymentsPage.paymentPageData.subscriptionTexts.statusText}" />
			
			<%-- <c:if test="${not empty paymentsPage.paymentPageData.subscriptionTexts.futureText}">
				&nbsp;/&nbsp;<c:out value="${paymentsPage.paymentPageData.subscriptionTexts.futureText}" />
			</c:if> --%>
			
			<c:if test="${not empty paymentsPage.paymentPageData.subscriptionTexts.nextBillingText}">
                ${paymentsPage.paymentPageData.subscriptionTexts.nextBillingText}
                <c:if test="${paymentsPage.paymentPageData.subscriptionTexts.nextSubPaymentMillis>0}">
                    <s:message var="nextSubPaymentMillisFormat" code='paymentsPage.paymentPageData.subscriptionTexts.nextSubPaymentMillisFormat' />
                    <script>
                        var nextSubPaymentDate = new Date(${paymentsPage.paymentPageData.subscriptionTexts.nextSubPaymentMillis});
                        document.write(nextSubPaymentDate.format('${nextSubPaymentMillisFormat}'));
                    </script>
                </c:if>
			</c:if>
		</c:if>
	</div>
	<div class="userStatusPhone">
		<img style="width: 9px; height: 15px; margin-right: 4px" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/ic_phone_account.png" />
		${paymentsPage.mobilePhoneNumber}
	</div>
</div>