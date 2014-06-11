<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="optionPrice" value="${paymentPolicy.subcost}" />
<c:set var="numWeeks" value="${paymentPolicy.subweeks}" />
<c:set var="monthlyOrWeekly" value="${paymentPolicy.monthly}" />

<div class="header pie-pp">
    <a href="${pageContext.request.contextPath}/payments_inapp.html" class="back-pp">Back</a>
    <span class="logo-pp"><s:message code='page.account.title' /></span>
</div>
<div class="container-pp-mq">
    <script>
        function _submitForm() {
            document.getElementById('paypalFormId').submit();
        }
    </script>
	<c:choose>
		<c:when test="${result!=null&&result!='fail'}">
			<h1><s:message code='pay.paypal.dialog.successful.title' /></h1>
			<p><s:message code='pay.paypal.dialog.successful.body.inapp' /></p>
			<div class="clr"></div>				
			<div class="addSmallSpace"></div>		
			<!--button-->
			<div class="contentButton formButton rad10 rel" >
				<input class="button accounts button-turquoise pie" title="${pageContext.request.contextPath}/payments_inapp.html" type="button" onClick="location.href=this.title" value="<s:message code='m.pay.paypal.dialog.successful.button.payments' />" />
				<span class="button-arrow"/>
			</div>
		</c:when>
		<c:otherwise>
            <div class="header-message-pp">
                <div style="float:left;width:60%;">
                    <span class="pay-pp" style="vertical-align: middle;"><s:message code='pay.paypal.form.title' /></span>
                    <img class="paypal-logo-pp" style="vertical-align: middle;" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/img_paypal_logo.png" />
                </div>
                <div class="price-pp">&pound;
                    <fmt:formatNumber pattern="0.00" value="${optionPrice}" />
                    <c:choose>
                        <c:when test="${monthlyOrWeekly eq true}">
                            <s:message code='pays.page.header.txt.business_2.month' />
                        </c:when>
                        <c:otherwise>
                            <s:message code='pays.page.header.txt.business_2.weeks' arguments="${numWeeks}"/>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            <div class="body-message-pp"><s:message code='pay.paypal.form.description' /></div>
            <a href="javascript:;" onclick="_submitForm()" class="button-pp"><span class="button-text-pp"><s:message code='pay.paypal.form.now' /></span></a>

			<form:form modelAttribute="payPalDto" method="post" id="paypalFormId">
				<input type="hidden" name="paymentPolicyId" value="${paymentPolicy.id}"/>
				<c:choose>
					<c:when test="${result=='fail'}">
						<div class="note" id="note">
							<c:choose>
								<c:when test="${not empty external_error}">
									<span><s:message code="pay.paypal.result.fail" />${external_error}</span>
								</c:when>
								<c:otherwise>
									<span>${internal_error}</span>
								</c:otherwise>
							</c:choose>
						</div>
					</c:when>
				</c:choose>
			</form:form>
		</c:otherwise>
	</c:choose>
</div>
