<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="optionPrice" value="${paymentPolicy.subcost}" />
<c:set var="duration" value="${paymentPolicy.duration}" />
<c:set var="monthlyOrWeekly" value="${paymentPolicy.monthly}" />

<c:choose>
    <c:when test="${result!=null&&result!='fail'}">
        <jsp:include page="success.jsp">
            <jsp:param name="callingPage" value="paypal" />
        </jsp:include>
    </c:when>
    <c:otherwise>
        <jsp:include page="fail.jsp">
            <jsp:param name="callingPage" value="paypal" />
        </jsp:include>
    </c:otherwise>
</c:choose>