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

	<div class="container">

		<c:choose>
			<c:when test="${isIOSDevice eq true && isO2User eq false}">
				<jsp:include page="../payments_common/content_header_itunes.jsp">
					<jsp:param name="callingPage" value="payments_inapp" />
				</jsp:include>
			</c:when>
			<c:when test="${isO2User eq true and isBussinesUser eq false}">
				<jsp:include page="../payments_common/content_header_o2user.jsp">
					<jsp:param name="callingPage" value="payments_inapp" />
				</jsp:include>
			</c:when>
			<c:otherwise>
				<%--isBussinesOrNonO2User --%>
				<jsp:include page="../payments_common/content_header_o2business.jsp">
					<jsp:param name="callingPage" value="payments_inapp" />
				</jsp:include>
			</c:otherwise>
		</c:choose>
		
		<c:choose>
			<c:when test="${isIOSDevice eq true && isO2User eq false}">
				<%-- for iTunes is already displayed --%>
			</c:when>
			<c:when test="${isO2User eq true and isBussinesUser eq false}">
				<jsp:include page="../payments_common/content_paymentoptions_o2user.jsp">
					<jsp:param name="callingPage" value="payments_inapp" />
				</jsp:include>
			</c:when>
			<c:otherwise>
				<%--isBussinesOrNonO2User --%>
				<jsp:include page="../payments_common/content_paymentoptions_business.jsp">
					<jsp:param name="callingPage" value="payments_inapp" />
				</jsp:include>
			</c:otherwise>
		</c:choose>

	</div>

	<div class="content no-bg">

		<c:if test="${(paymentDetails!=null) && (true==paymentDetails.activated)}">
			<div class="rel" style="margin-top: 5px;">
				<a class="button-grey no-margin pie" href="${pageContext.request.contextPath}/payments_inapp/unsubscribe.html"><s:message code='pays.deactivate.submit' /></a>
			</div>
		</c:if>

		<div class="rel" style="text-align: center; margin-top: 10px;">
			<img style="width: 33%;" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/label_secure_payment.png" />
		</div>

	</div>
</div>
