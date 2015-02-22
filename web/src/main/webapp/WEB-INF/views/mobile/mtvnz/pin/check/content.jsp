<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="showEnterButton" value="Not valid pin" scope="request"/>
<c:set var="showResendCodeButton" value="Not valid pin" scope="request"/>
<c:set var="error" value="Not valid pin" scope="request"/>



<jsp:include page="../result/control.jsp">
    <jsp:param name="callingPage" value="check_pin" />
</jsp:include>
