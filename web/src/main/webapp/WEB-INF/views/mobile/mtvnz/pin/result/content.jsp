<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:forEach var="paymentPolicyDto" items="${paymentPolicyDtos}">
    <a class="go-premium-button go-premium-button-device go-premium-body-button-below go-premium-button-target go-premium-body-cancel">
        <span>
            <s:message code='payment.per.${paymentPolicyDto.durationUnit}' arguments="${paymentPolicyDto.subCost}"/>
        </span>
    </a>
</c:forEach>

