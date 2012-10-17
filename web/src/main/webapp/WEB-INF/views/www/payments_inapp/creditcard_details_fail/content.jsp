<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<div class="errorBox rad7" style="display: block;">
	<c:choose>
		<c:when test="${not empty external_error}">
			<s:message code='pay.cc.error.on.creating.payment.details' /><br />
			${external_error}
		</c:when>
		<c:otherwise>
			${internal_error}
		</c:otherwise>
	</c:choose>
</div>