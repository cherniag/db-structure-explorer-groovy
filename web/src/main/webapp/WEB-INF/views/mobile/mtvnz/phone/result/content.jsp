<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:choose>
    <c:when test="${result.yes}">
        <jsp:include page="vf.jsp">
            <jsp:param name="callingPage" value="check_phone" />
        </jsp:include>
    </c:when>
    <c:when test="${result.connectionError}">
        <jsp:include page="nonvf/subscribe-conn-error.jsp">
            <jsp:param name="callingPage" value="check_phone" />
        </jsp:include>
    </c:when>
    <c:when test="${result.limitReached}">
        <jsp:include page="nonvf/subscribe-limited-reached.jsp">
            <jsp:param name="callingPage" value="check_phone" />
        </jsp:include>
    </c:when>
    <c:when test="${result.notValid}">
        <jsp:include page="nonvf/subscribe-not-valid.jsp">
            <jsp:param name="callingPage" value="check_phone" />
        </jsp:include>
    </c:when>
    <c:otherwise>

        <c:choose>
            <c:when test="${reassigned}">
                <jsp:include page="nonvf/subscribe-non-vf.jsp">
                    <jsp:param name="callingPage" value="check_phone" />
                </jsp:include>
            </c:when>
            <c:otherwise>
                <c:choose>
                    <c:when test="${ios}">
                        <jsp:include page="nonvf/subscribe-ios.jsp">
                            <jsp:param name="callingPage" value="check_phone" />
                        </jsp:include>
                    </c:when>
                    <c:otherwise>
                        <jsp:include page="nonvf/subscribe-not-ios.jsp">
                            <jsp:param name="callingPage" value="check_phone" />
                        </jsp:include>
                    </c:otherwise>
                </c:choose>
            </c:otherwise>
        </c:choose>

    </c:otherwise>
</c:choose>
