<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:choose>
    <c:when test="${result != null && result != 'fail'}">
        <c:choose>
            <c:when test="${currentPaymentPolicyType == 'ONETIME'}">
                <jsp:include page="pass/subcription_success.jsp"/>
            </c:when>
            <c:otherwise>
                <jsp:include page="premium/subcription_success.jsp"/>
            </c:otherwise>
        </c:choose>
    </c:when>
    <c:otherwise>
        <jsp:include page="subcription_fail.jsp">
            <jsp:param name="callingPage" value="paypal" />
        </jsp:include>
    </c:otherwise>
</c:choose>