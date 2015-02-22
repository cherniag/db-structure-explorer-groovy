<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:set var="showEnterButton" value="true" scope="request"/>
<c:set var="showResendCodeButton" value="true" scope="request"/>
<c:set var="error" value="Invalid code. Please try again." scope="request"/>
<jsp:include page="control.jsp">
    <jsp:param name="callingPage" value="check_pin" />
</jsp:include>