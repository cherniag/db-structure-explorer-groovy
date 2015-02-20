<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:choose>
    <c:when test="${result.ok}">
        <jsp:include page="result.jsp">
            <jsp:param name="callingPage" value="check_phone" />
        </jsp:include>
    </c:when>
    <c:when test="${result.error}">
        <jsp:include page="not-valid.jsp">
            <jsp:param name="callingPage" value="check_phone" />
        </jsp:include>
    </c:when>
    <c:when test="${result.maxAttempts}">
        <jsp:include page="result.jsp">
            <jsp:param name="callingPage" value="check_phone" />
        </jsp:include>
    </c:when>
    <c:otherwise>
        <jsp:include page="expired.jsp">
            <jsp:param name="callingPage" value="check_phone" />
        </jsp:include>
    </c:otherwise>
</c:choose>
