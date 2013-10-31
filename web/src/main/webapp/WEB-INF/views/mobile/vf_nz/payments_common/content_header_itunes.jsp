<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:set var="optionPrice" />
<c:forEach var="paymentPolicy" items="${paymentsPage.paymentPolicies}">
	<c:if test="${paymentPolicy.paymentType == 'iTunesSubscription'}">
		<c:set var="optionPrice" value="${paymentPolicy.subcost}" />
	</c:if>
</c:forEach>

<div class="itunesHeader vfR S15 redColor">
	<div style="float: left;">
		<s:message code='pays.page.header.txt.itunes' /> <img src="${requestScope.assetsPathAccordingToCommunity}imgs/icon_itunes.png" align="bottom" class="itunesSubImage" />
	</div>
	<div style="float: right;padding-top:6px;">
		$<fmt:formatNumber pattern="0.00" value="${optionPrice}" /><s:message code='pays.page.header.txt.itunes.month' />
	</div>
	<div style="clear: both; height: 13px;">&nbsp;</div>

	<div class="itunesheaderText">
		<s:message code='pays.page.header.txt.itunes.middleText' />
	</div>

</div>