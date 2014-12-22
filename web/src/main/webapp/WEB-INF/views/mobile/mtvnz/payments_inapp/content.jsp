<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:choose>
    <c:when test="${paymentsPage.subscriptionInfo.ios}">
        <c:choose>
            <c:when test="${paymentsPage.subscriptionInfo.premium}">
                <jsp:include page="ios_manage_account.jsp">
                    <jsp:param name="callingPage" value="payments_inapp" />
                </jsp:include>
            </c:when>
            <c:otherwise>
                <jsp:include page="ios_subscribe.jsp">
                    <jsp:param name="callingPage" value="payments_inapp" />
                </jsp:include>
            </c:otherwise>
        </c:choose>
    </c:when>
    <c:otherwise>
        <c:choose>
            <c:when test="${paymentsPage.subscriptionInfo.premium}">
                <jsp:include page="paypal_manage_account.jsp">
                    <jsp:param name="callingPage" value="payments_inapp" />
                </jsp:include>
            </c:when>
            <c:otherwise>
                <jsp:include page="paypal_subscribe.jsp">
                    <jsp:param name="callingPage" value="payments_inapp" />
                </jsp:include>
            </c:otherwise>
        </c:choose>
    </c:otherwise>
</c:choose>
