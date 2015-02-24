<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<s:message code='enter.phone.error.maxAttempts' scope="request" var="error"/>
<jsp:include page="../../check/content.jsp">
    <jsp:param name="callingPage" value="check_pin" />
</jsp:include>

