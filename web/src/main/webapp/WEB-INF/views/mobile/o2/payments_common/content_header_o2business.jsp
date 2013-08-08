<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:set var="optionPrice" />
<c:forEach var="paymentPolicy" items="${paymentPolicies}">
	<c:set var="optionPrice" value="${paymentPolicy.subcost}" />
</c:forEach>

<div class="o2BusinessHeader">
	<div class="frR15">
		<s:message code='pays.page.header.txt.business_1' /><fmt:formatNumber pattern="0.00" value="${optionPrice}" /><span style="font-size: 12px"><s:message code='pays.page.header.txt.business_2' /></span>
	</div>

	<hr class="o2Businesshr" />

	<div class="frL11" style="line-height: 16px">
		<s:message code='pays.page.header.txt' />
	</div>
</div>