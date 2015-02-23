<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:set var="error" value="You have reached the daily pin codes limit. Please try again tomorrow" scope="request"/>
<c:set var="showBackToTheAppCodeButton" value="true" scope="request"/>
<jsp:include page="control.jsp">
    <jsp:param name="callingPage" value="check_pin" />
</jsp:include>
