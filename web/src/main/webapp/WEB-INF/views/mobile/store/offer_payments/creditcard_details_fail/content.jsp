<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<c:choose>
	<c:when test="${not empty external_error}">
	<s:message code='offer.pay.cc.error.on.creating.payment.details' /><br />
		${external_error}
	</c:when>
	<c:otherwise>
		${internal_error}
	</c:otherwise>
</c:choose>