<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="s" %>

<c:choose>
  <c:when test="${result=='fail'}">
    <div class="note" id="note">
      <c:choose>
        <c:when test="${not empty external_error}">
          <span><s:message code="pay.paypal.result.fail" />${external_error}</span>
        </c:when>
        <c:otherwise>
          <span>${internal_error}</span>
        </c:otherwise>
      </c:choose>
    </div>
  </c:when>
</c:choose>