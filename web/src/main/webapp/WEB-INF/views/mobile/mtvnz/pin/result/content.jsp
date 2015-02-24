<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:choose>
    <c:when test="${check}">
        <jsp:include page="ok.jsp">
            <jsp:param name="callingPage" value="check_phone" />
        </jsp:include>
    </c:when>
    <c:otherwise>
        <jsp:include page="control.jsp">
            <jsp:param name="callingPage" value="check_phone" />
        </jsp:include>
    </c:otherwise>
</c:choose>
