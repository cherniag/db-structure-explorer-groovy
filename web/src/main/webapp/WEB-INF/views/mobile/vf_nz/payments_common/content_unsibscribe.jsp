<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>

<c:set var="hrefValue" value='href="${pageContext.request.contextPath}/<%=request.getParameter("callingPage")%>/unsubscribe.html"' />
<c:if test="${paymentsPage.awaitingPaymentStatus}">
    <c:set var="disabledAttrib">disabled="true"</c:set>
    <c:set var="disabledStyle">disabled</c:set>
    <c:set var="hrefValue" value="" />
</c:if>
<c:if test="${paymentsPage.paymentDetailsActivated}">
    <div class="rel" style="padding: 0px 5px 10px 5px;">
        <a class="button-white no-margin pie S15 lightGray ${disabledStyle}" ${hrefValue} ${disabledAttrib}><s:message code='pays.deactivate.submit' /></a>
    </div>
</c:if>

