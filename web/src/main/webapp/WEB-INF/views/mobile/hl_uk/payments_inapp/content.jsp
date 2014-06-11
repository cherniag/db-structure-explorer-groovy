<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="../payments_common/content_js.jsp"></jsp:include>

<div class="header pie">
    <span class="logo"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo.png"/></span>
</div>

<div style="margin: 0 14px">

<jsp:include page="../payments_common/content_banner.jsp" />

<img style="width:100%;display: block" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/img_header_payment.png" />

<div class="maincontainer">
	<c:choose>
		<c:when test="${paymentsPage.consumerUser eq true}">
			<jsp:include page="../payments_common/content_header_o2user.jsp">
				<jsp:param name="callingPage" value="payments_inapp" />
			</jsp:include>
		</c:when>
		<c:when test="${paymentsPage.appleIOSAndNotBusiness}">
			<jsp:include page="../payments_common/content_header_itunes.jsp">
				<jsp:param name="callingPage" value="payments_inapp" />
			</jsp:include>
		</c:when>
		<c:otherwise>
			<%-- CreditCard/Paypal  --%>
			<jsp:include page="../payments_common/content_header_o2business.jsp">
				<jsp:param name="callingPage" value="payments_inapp" />
			</jsp:include>
		</c:otherwise>
	</c:choose>
		
	<div class="paymentscontainer">
		<c:choose>
			<c:when test="${paymentsPage.consumerUser eq true}">
				<jsp:include page="../payments_common/content_paymentoptions_o2user.jsp">
					<jsp:param name="callingPage" value="payments_inapp" />
				</jsp:include>
			</c:when>
            <c:when test="${paymentsPage.appleIOSAndNotBusiness}">
                <%--we are not including the iTunes payment option because this is an external page and the link will not be intercepted (as it's happening in the page loaded by O2 Tracks app...) --%>
            </c:when>
			<c:otherwise>
				<%-- CreditCard/Paypal  --%>
				<jsp:include page="../payments_common/content_paymentoptions_business.jsp">
					<jsp:param name="callingPage" value="payments_inapp" />
				</jsp:include>
			</c:otherwise>
		</c:choose>

	</div>
	<c:if test="${paymentsPage.paymentDetailsActivated}">
        <div class="rel" style="padding: 0px 5px 10px 5px;">
            <c:choose>
                <c:when test="${paymentsPage.awaitingPaymentStatus}">
                    <a class="button-disabled no-margin pie"><s:message code='pays.deactivate.submit' /></a>
                </c:when>
                <c:otherwise>
                    <a class="button-grey no-margin pie" href="${pageContext.request.contextPath}/payments_inapp/unsubscribe.html"><s:message code='pays.deactivate.submit' /></a>
                </c:otherwise>
            </c:choose>
        </div>
    </c:if>
</div>

	<div class="content no-bg">

		<div class="rel" style="text-align: center; margin-top: 10px;">
			<img width="79" height="12" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/label_secure_payment.png" />
		</div>

	</div>
</div>
