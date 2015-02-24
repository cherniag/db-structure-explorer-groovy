<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:set var="check" value="true" scope="request" />
<jsp:include page="../../pin/result/control.jsp">
    <jsp:param name="callingPage" value="check_pin" />
</jsp:include>
