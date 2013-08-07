<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div style="padding-top: 16px; padding-left: 10px; padding-bottom: 20px">
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

	<hr style="color: #ddd; margin: 10px 0 12px 0" />

	<div class="frL11" style="line-height: 16px; margin-bottom: 17px;">
		<s:message code='pays.page.header.txt' />
	</div>

	<input class="button-turquoise no-margin pie" title="${iTunesUrl}" type="button" onClick="location.href=this.title"	value="&#163;${optionPrice}/month" />
</div>