<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<div class="container">

<div class="itunesheader">
	<c:set var="optionPrice" />
	<c:set var="iTunesUrl" />
	<c:forEach var="paymentPolicy" items="${paymentPolicies}">
		<c:if test="${paymentPolicy.paymentType == 'iTunesSubscription'}">
			<c:set var="optionPrice" value="${paymentPolicy.subcost}" />
			<c:set var="paymentPage"><%=request.getParameter("callingPage")%></c:set>
			<c:set var="iTunesUrl" value="${pageContext.request.contextPath}/${paymentPage}/iTunesSubscription.html?paymentPolicyId=${paymentPolicy.id}" />
		</c:if>
	</c:forEach>

	<div class="frR15"><s:message code='pays.page.header.txt.itunes' /></div>

	<hr class="itunesHr" />

	<div class="frL11 iTunesHeader">
		<s:message code='pays.page.header.txt' />
	</div>

	<input class="button-turquoise no-margin pie" title="${iTunesUrl}" type="button" onClick="location.href=this.title"	value="&#163;${optionPrice}/month" />
</div>

</div>