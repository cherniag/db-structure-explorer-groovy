<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:choose>
    <c:when test="${paymentsPage.subscriptionInfo.ios}">
        IOS
        <c:choose>
            <c:when test="${paymentsPage.subscriptionInfo.premium}">
                premium
                <c:choose>
                    <c:when test="${paymentsPage.subscriptionInfo.currentPaymentPolicy.paymentPolicyType == 'ONETIME'}">
                        one time current payment policy
                    </c:when>
                    <c:otherwise>
                        not one time current payment policy
                    </c:otherwise>
                </c:choose>
            </c:when>
            <c:otherwise>
                not premium
            </c:otherwise>
        </c:choose>
    </c:when>
    <c:otherwise>
        NOT IOS
        <c:choose>
            <c:when test="${paymentsPage.subscriptionInfo.premium}">
                premium
                <c:choose>
                    <c:when test="${paymentsPage.subscriptionInfo.currentPaymentPolicy.paymentPolicyType == 'ONETIME'}">
                        one time
                        <c:choose>
                            <c:when test="${paymentsPage.subscriptionInfo.freeTrial}">
                                free trial
                            </c:when>
                            <c:otherwise>
                                not free trial
                            </c:otherwise>
                        </c:choose>
                    </c:when>
                    <c:otherwise>
                        not one time
                    </c:otherwise>
                </c:choose>
            </c:when>
            <c:otherwise>
                not premium
                <c:choose>
                    <c:when test="${paymentsPage.subscriptionInfo.onPaidPeriod}">
                        on paid period
                    </c:when>
                    <c:otherwise>
                        <jsp:include page="subscribe.jsp">
                            <jsp:param name="callingPage" value="payments_inapp" />
                        </jsp:include>
                    </c:otherwise>
                </c:choose>
            </c:otherwise>
        </c:choose>
    </c:otherwise>
</c:choose>
