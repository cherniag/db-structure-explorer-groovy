<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<div class="contentContainer">
	<div class="verticalSpace"></div>
	<!-- top part of content -->
	<div class="content rel oneWideColumn">	
		<h1 class="azHeader"><s:message code="page.paypal.header.h1" /><span><s:message code="page.paypal.header.description" /></span></h1>
		<%@ include file="/WEB-INF/views/www/default/menu.jsp"%>
		
		<div class="widerContainer boxWithBorder lessBottomPad">
			<div class="wholePart">
				<div class="details">
					<h2><s:message code="pay.paypal.form.title" /></h2>
					<form:form modelAttribute="payPalDto" method="post">
						<input type="hidden" name="paymentPolicyId" value="${paymentPolicy.id}"/>
						<div class="payDetails">
							<h3><s:message code="pay.paypal.form.description"
								arguments="${paymentPolicy.duration};${paymentPolicy.subcost}"
       							htmlEscape="false"
       							argumentSeparator=";"/>
							</h3>
								<c:choose>
									<c:when test="${result eq 'successful'}">
									
										<div id="popupSuccessfulPayPalVerification" class="pageWindow simplePopup">
											<div class="pageWindowContent simpleTextPopup">
												<div class="innerBox">
													<h2><s:message code="pay.paypal.result.successful.title" /></h2>
													<p><s:message code="pay.paypal.result.successful.description" /></p>
													<a href="" class="popupButton rad3"><s:message code='pay.paypal.dialog.successful.button' /></a>
												</div>
											</div>
										</div>
										<script type="text/javascript">showModalDialog("#popupSuccessfulPayPalVerification");</script>
									</c:when>
									<c:otherwise>
										<c:if test="${result eq 'fail'}">
											<div class="errorBox rad7" style="display: block;">
											<c:choose>
												<c:when test="${not empty external_error}">
													<s:message code="pay.paypal.result.fail" /><br />
													${external_error}
												</c:when>
												<c:otherwise>
													${internal_error}
												</c:otherwise>
											</c:choose>
											</div>
										</c:if>
										<input type="image" style="with:145px; height:42px;" src="https://www.paypal.com/en_US/i/btn/btn_xpressCheckout.gif">
									</c:otherwise>
								</c:choose>
						</div>
					</form:form>
				</div>
			</div>
		</div>
	</div>
</div>