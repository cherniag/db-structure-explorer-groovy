<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div class="header pie">
    <span class="logo" style="padding-left: 49px;"><s:message code='m.page.main.header' /></span>
    <a href="${pageContext.request.contextPath}/account.html" class="button-small button-right pie"><s:message code='m.page.main.menu.close' /></a>
</div>

<div style="margin: 0 14px">

<jsp:include page="../payments_common/content_banner.jsp" />

<img style="width:100%;display: block" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/img_header_payment.png" />

<div class="maincontainer">
	<c:choose>
		<c:when test="${paymentsPage.consumerUser}">
			<jsp:include page="../payments_common/content_header_o2user.jsp">
				<jsp:param name="callingPage" value="payments" />
			</jsp:include>
		</c:when>
		<c:when test="${paymentsPage.appleIOSAndNotBusiness}">
			<jsp:include page="../payments_common/content_header_itunes.jsp">
				<jsp:param name="callingPage" value="payments" />
			</jsp:include>
		</c:when>
		<c:otherwise>
				<%-- CreditCard/Paypal  --%>
				<jsp:include page="../payments_common/content_header_o2business.jsp">
					<jsp:param name="callingPage" value="payments" />
				</jsp:include>
		</c:otherwise>
	</c:choose>

    <div class="paymentscontainer">
        <c:choose>
            <c:when test="${paymentsPage.consumerUser}">
                <jsp:include page="../payments_common/content_paymentoptions_o2user.jsp">
                    <jsp:param name="callingPage" value="payments" />
                </jsp:include>
            </c:when>
            <c:when test="${paymentsPage.appleIOSAndNotBusiness}">
                <jsp:include page="../payments_common/content_paymentoptions_itunes.jsp">
                    <jsp:param name="callingPage" value="payments" />
                </jsp:include>
            </c:when>
            <c:otherwise>
                <%-- CreditCard/Paypal  --%>
                <jsp:include page="../payments_common/content_paymentoptions_business.jsp">
                    <jsp:param name="callingPage" value="payments" />
                </jsp:include>
            </c:otherwise>
        </c:choose>
    </div>
    <c:if test="${(paymentsPage.paymentDetails!=null) && (true==paymentsPage.paymentDetails.activated)}">
         <div class="rel" style="padding: 0px 5px 10px 5px;">
             <a class="button-grey no-margin pie" href="${pageContext.request.contextPath}/payments/unsubscribe.html" ><s:message code='pays.deactivate.submit' /></a>
         </div>
     </c:if>
</div>

 <div class="content no-bg">

     <div class="rel" style="text-align: center; margin-top: 10px;">
         <img width="79" height="12" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/label_secure_payment.png"/>
     </div>

 </div>
</div>
