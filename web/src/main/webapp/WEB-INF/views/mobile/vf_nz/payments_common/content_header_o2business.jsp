<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:set var="optionPrice" />
<c:set var="numWeeks" />
<c:set var="monthlyOrWeekly" />
<c:forEach var="paymentPolicy" items="${paymentsPage.paymentPolicies}">
	<c:set var="optionPrice" value="${paymentPolicy.subcost}" />
	<c:set var="numWeeks" value="${paymentPolicy.subweeks}" />
	<c:set var="monthlyOrWeekly" value="${paymentPolicy.monthly}" />
</c:forEach>


<div class="paypalHeader vfR S15 redColor">
	<div class="paypalHeaderLeft">
		<s:message code='pays.page.header.txt.itunes' /> <img src="${requestScope.assetsPathAccordingToCommunity}imgs/icon_paypal.png" align="bottom" class="pypalImage" />
	</div>
	<div class="paypalHeaderRight">
		$<fmt:formatNumber pattern="0.00" value="${optionPrice}" /><s:message code='pays.page.header.txt.itunes.month' />
	</div>
	<div style="clear: both; height: 8px;">&nbsp;</div>

	<div class="itunesheaderText">
		<s:message code='pays.page.header.txt.itunes.middleText' />
	</div>

</div>