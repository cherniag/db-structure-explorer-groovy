<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:set var="iTunesUrl" />
<c:forEach var="paymentPolicy" items="${paymentsPage.paymentPolicies}">
	<c:if test="${paymentPolicy.paymentType == 'iTunesSubscription'}">
		<c:set var="paymentPage"><%=request.getParameter("callingPage")%></c:set>
		<c:set var="iTunesUrl" value="${pageContext.request.contextPath}/${paymentPage}/iTunesSubscription.html?paymentPolicyId=${paymentPolicy.id}" />
        <c:if test="${paymentsPage.awaitingPaymentStatus}">
            <c:set var="disabledAttrib">disabled="true"</c:set>
            <c:set var="disabledStyle">disabled</c:set>
            <c:set var="iTunesUrl" value="" />
        </c:if>
	</c:if>
</c:forEach>

<c:if test="${paymentsPage.freeTrialPeriod eq true}">
<%--if IOS user is on free trial, we display the message with losing FT --%>
	<div class="itunesheaderText" style="margin: 0 5px 10px;"><s:message code='pays.page.header.txt.itunes.before.FT.expires' /></div>
</c:if>

<div class="itunespaymentheader">
	<input class="button-turquoise no-margin pie ${disabledStyle}" title="${iTunesUrl}" type="button" ${disabledAttrib} onClick="location.href=this.title"	value="<s:message code='pays.page.header.txt.itunes.paynowbutton' />" />
</div>