<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:set var="optionPrice" />
<c:set var="numWeeks" />
<c:set var="monthlyOrWeekly" />
<c:forEach var="paymentPolicy" items="${paymentPolicies}">
	<c:set var="optionPrice" value="${paymentPolicy.subcost}" />
	<c:set var="numWeeks" value="${paymentPolicy.subweeks}" />
	<c:set var="monthlyOrWeekly" value="${paymentPolicy.monthly}" />
	
</c:forEach>

<div class="paymentscontainer">
	<div class="o2TracksHeader frR15">
		<s:message code='pays.page.header.txt.business_1' />
		<fmt:formatNumber pattern="0.00" value="${optionPrice}" />
		<c:choose>
			<c:when test="${monthlyOrWeekly eq true}">
				<span style="font-size: 12px"><s:message code='pays.page.header.txt.business_2.month' /></span>
			</c:when>
			<c:otherwise>
				<span style="font-size: 12px"><s:message code='pays.page.header.txt.business_2.weeks' arguments="${numWeeks}"/></span>
			</c:otherwise>
		</c:choose>	
	</div>
</div>
	
<hr class="o2Businesshr" />

<div class="paymentscontainer">
	<div class="frL11 o2BusinessHeader">
		<s:message code='pays.page.header.txt' />
	</div>
</div>