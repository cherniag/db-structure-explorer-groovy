<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:choose>
    <c:when test="${currentPaymentPolicy.paymentPolicyType == 'ONETIME'}">
        <jsp:include page="pass/confirmation.jsp">
            <jsp:param name="callingPage" value="payments_inapp" />
        </jsp:include>
    </c:when>
    <c:otherwise>
        <jsp:include page="premium/confirmation.jsp">
            <jsp:param name="callingPage" value="payments_inapp" />
        </jsp:include>
    </c:otherwise>
</c:choose>