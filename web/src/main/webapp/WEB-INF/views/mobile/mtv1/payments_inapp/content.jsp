<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:choose>
    <c:when test="${paymentsPage.subscriptionInfo.ios}">
        <c:choose>
            <c:when test="${paymentsPage.subscriptionInfo.premium}">
                <c:choose>
                    <c:when test="${paymentsPage.subscriptionInfo.currentPaymentPolicy.paymentPolicyType == 'ONETIME'}">
                        <jsp:include page="ios/pass/manage_account.jsp">
                            <jsp:param name="callingPage" value="payments_inapp" />
                        </jsp:include>
                    </c:when>
                    <c:otherwise>
                        <jsp:include page="ios/premium/manage_account.jsp">
                            <jsp:param name="callingPage" value="payments_inapp" />
                        </jsp:include>
                    </c:otherwise>
                </c:choose>
            </c:when>
            <c:otherwise>
                <jsp:include page="ios/subscribe.jsp">
                    <jsp:param name="callingPage" value="payments_inapp" />
                </jsp:include>
            </c:otherwise>
        </c:choose>
    </c:when>
    <c:otherwise>
        <c:choose>
            <c:when test="${paymentsPage.subscriptionInfo.premium}">
                <c:choose>
                    <c:when test="${paymentsPage.subscriptionInfo.currentPaymentPolicy.paymentPolicyType == 'ONETIME'}">
                        <c:choose>
                            <c:when test="${paymentsPage.subscriptionInfo.freeTrial}">
                                <%--Pass can be canceled while user is on free trial--%>
                                <jsp:include page="paypal/pass/manage_account.jsp">
                                    <jsp:param name="callingPage" value="payments_inapp" />
                                </jsp:include>
                            </c:when>
                            <c:otherwise>
                                <%--Pass can't be canceled if user has paid by pass--%>
                                <jsp:include page="paypal/pass/account_info.jsp">
                                    <jsp:param name="callingPage" value="payments_inapp" />
                                </jsp:include>
                            </c:otherwise>
                        </c:choose>
                    </c:when>
                    <c:otherwise>
                        <jsp:include page="paypal/premium/manage_account.jsp">
                            <jsp:param name="callingPage" value="payments_inapp" />
                        </jsp:include>
                    </c:otherwise>
                </c:choose>
            </c:when>
            <c:otherwise>
                <c:choose>
                    <c:when test="${paymentsPage.subscriptionInfo.onPaidPeriod}">
                        <%--If user is still on paid period and next subpayment in the future he can't subscribe--%>
                        <jsp:include page="unsubscribe/premium/after_unsubscription.jsp">
                            <jsp:param name="callingPage" value="payments_inapp" />
                        </jsp:include>
                    </c:when>
                    <c:otherwise>
                        <%--User is allowed to subscribe only after subcription has finished--%>
                        <jsp:include page="paypal/subscribe.jsp">
                            <jsp:param name="callingPage" value="payments_inapp" />
                        </jsp:include>
                    </c:otherwise>
                </c:choose>
            </c:otherwise>
        </c:choose>
    </c:otherwise>
</c:choose>
