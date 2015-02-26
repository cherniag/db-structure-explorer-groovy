<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:choose>
    <c:when test="${paymentsPage.subscriptionInfo.ios}">
        <c:choose>
            <c:when test="${paymentsPage.subscriptionInfo.premium}">
                <c:choose>
                    <c:when test="${paymentData.vf}">
                        <jsp:include page="manage/vf_any_device.jsp">
                            <jsp:param name="callingPage" value="payments_inapp" />
                        </jsp:include>
                    </c:when>
                    <c:otherwise>
                        <jsp:include page="manage/not_vf_ios.jsp">
                            <jsp:param name="callingPage" value="payments_inapp" />
                        </jsp:include>
                    </c:otherwise>
                </c:choose>
            </c:when>
            <c:otherwise>
                <jsp:include page="subscribe/start_ios.jsp">
                    <jsp:param name="callingPage" value="payments_inapp" />
                </jsp:include>
            </c:otherwise>
        </c:choose>
    </c:when>
    <c:otherwise>
        <c:choose>
            <c:when test="${paymentsPage.subscriptionInfo.premium}">
                <c:choose>
                    <c:when test="${paymentData.vf}">
                        <jsp:include page="manage/vf_any_device.jsp">
                            <jsp:param name="callingPage" value="payments_inapp" />
                        </jsp:include>
                    </c:when>
                    <c:otherwise>
                        <jsp:include page="manage/not_vf_not_ios.jsp">
                            <jsp:param name="callingPage" value="payments_inapp" />
                        </jsp:include>
                    </c:otherwise>
                </c:choose>
            </c:when>
            <c:otherwise>
                <jsp:include page="subscribe/start_not_ios.jsp">
                    <jsp:param name="callingPage" value="payments_inapp" />
                </jsp:include>
            </c:otherwise>
        </c:choose>
    </c:otherwise>
</c:choose>
