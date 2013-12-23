<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>

<div style="background-color: inherit;margin-top: 16px; margin-bottom: 18px;">
	<div class="vfL S12 grayBlackColor bottom7">
		<c:if test="${not empty paymentsPage.paymentPageData and not empty paymentsPage.paymentPageData.subscriptionTexts and not empty paymentsPage.paymentPageData.subscriptionTexts.statusText}">
			<div class="redColor vfR S16 bottom5"><c:out value="${paymentsPage.paymentPageData.subscriptionTexts.statusText}" /></div>
			
			<%-- <c:if test="${not empty paymentsPage.paymentPageData.subscriptionTexts.futureText}">
				&nbsp;/&nbsp;<c:out value="${paymentsPage.paymentPageData.subscriptionTexts.futureText}" />
			</c:if> --%>

            <c:if test="${not empty paymentsPage.paymentPageData.subscriptionTexts.nextBillingText}">
                ${paymentsPage.paymentPageData.subscriptionTexts.nextBillingText}
                <c:if test="${paymentsPage.paymentPageData.subscriptionTexts.nextSubPaymentMillis>0}">
                    <s:message var="nextSubPaymentMillisFormat" code='paymentsPage.paymentPageData.subscriptionTexts.nextSubPaymentMillisFormat' />
                    <script>
                        var nextSubPaymentDate = new Date(${paymentsPage.paymentPageData.subscriptionTexts.nextSubPaymentMillis});
                        document.write("&nbsp;" + nextSubPaymentDate.format('${nextSubPaymentMillisFormat}'));
                    </script>
                </c:if>
            </c:if>
		</c:if>
	</div>
	<div class="vfR S16 blackColor">
		<img style="width: 9px; height: 17px; margin-right: 4px" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/ic_phone_account.png" />
		${paymentsPage.mobilePhoneNumber}
	</div>
</div>