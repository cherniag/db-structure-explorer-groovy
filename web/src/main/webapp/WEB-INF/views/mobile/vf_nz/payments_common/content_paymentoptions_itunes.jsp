<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:set var="iTunesUrl" />
<c:forEach var="paymentPolicy" items="${paymentsPage.paymentPolicies}">
	<c:if test="${paymentPolicy.paymentType == 'iTunesSubscription'}">
		<c:set var="paymentPage"><%=request.getParameter("callingPage")%></c:set>
		<c:set var="iTunesUrl" value="${pageContext.request.contextPath}/${paymentPage}/iTunesSubscription.html?paymentPolicyId=${paymentPolicy.id}" />
	</c:if>
</c:forEach>

<div class="itunespaymentheader">
	<input class="button-turquoise no-margin pie" title="${iTunesUrl}" type="button" onClick="location.href=this.title"	value="<s:message code='pays.page.header.txt.itunes.paynowbutton' />" />
</div>