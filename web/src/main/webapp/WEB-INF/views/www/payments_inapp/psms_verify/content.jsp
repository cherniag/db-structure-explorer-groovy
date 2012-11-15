<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<div class="contentContainer">
	<div class="verticalSpace"></div>
	<!-- top part of content -->
	<div class="content rel oneWideColumn">	
		<!-- top part of content -->
		<h1 class="azHeader"><s:message code="page.psms.header.h1" /><span><s:message code="page.psms.header.description" /></span></h1>				
		<!-- tabs  -->
		<%@ include file="/WEB-INF/views/www/menu.jsp"%>
		
		<div class="widerContainer boxWithBorder lessBottomPad rel">
			<div class="wholePart">
					<div class="details smsBox">
						<h2><s:message code="pay.psms.form.title" /></h2>
						<form:form modelAttribute="verifyDto" method="post">
							<div class="payDetails">
								<p><s:message code="pay.psms.verify.description" /></p>
									<!--one record-->
									<div class="oneLine">
										<div class="nameCell"><s:message code="pay.psms.verify.form.pin" /></div>
										<div class="valueCell">
											<form:input path="pin" />
											<span class="errorSign" style="display: none;">&nbsp;</span>	
										</div>							
									</div>						
									<div class="clr"></div>
									<!--end one record-->
									
									<c:if test="${null!=result}">
										<c:choose>
											<c:when test="${result=='successful'}">
												<div id="popupSuccessfulPinVerification" class="pageWindow simplePopup">
													<div class="pageWindowContent simpleTextPopup">
														<div class="innerBox">
															<h2><s:message code='pay.psms.verify.dialog.success.title' /></h2>
															<p><s:message code='pay.psms.verify.dialog.success.body' /></p>
															<a href="" class="popupButton rad3"><s:message code='pay.psms.verify.dialog.success.button' /></a>
														</div>
													</div>
												</div>
												<script type="text/javascript">showModalDialog("#popupSuccessfulPinVerification");</script>
											</c:when>
											<c:otherwise>
												<div class="errorBoxContainer">
													<div class="errorBox rad7">
														<s:message code="pay.psms.verify.error" />
													</div>
												</div>
											</c:otherwise>
										</c:choose>
									</c:if>
									
									<!--one button-->
									<div class="buttonShadow rad4">
										<div class="buttonBox rad4">
											<div class="buttonContent">
												<input class="button" type="submit" value="<s:message code='pay.psms.verify.submit' />" />
											</div>
										</div>
									</div>	
									<!--end of one button-->	
									<div class="clr"></div>	
							</div>
						</form:form>
					</div>
					<div class="securitySigns abs">
						<a href="" class="secureBox_1"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/bgs/spacer.gif" alt="Secured by thawte" /></a>
					</div>
				</div>
				<div class="pinInfo">
					<form:form modelAttribute="pSmsDto" method="post">
						<form:hidden path="phone"/>
						<form:hidden path="operator"/>
						<div id="resendInfoBlock">
							<p><span class="errorSign">&nbsp;</span><s:message code="pay.psms.verify.resend.description" /></p>
							<div class="buttonShadow rad4">
								<div class="buttonBox rad4">
									<div class="buttonContent">
										<input id="resendSms" class="button" type="button" value="<s:message code='pay.psms.verify.resend' />">
									</div>
								</div>
							</div>
						</div>
						<div id="ajaxLoading" style="display:none;margin: 100px 0 0 140px;">
							<div id="errorBoxContainer" class="errorBoxContainer"></div>
							<div class="oneLine">
								<img alt="" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />/imgs/ajax-loader.gif" />
							</div>
							<div class="clr"></div>
						</div>
					</form:form>
					<script type="text/javascript">
						$("#resendSms").click(function() {
							$("#resendInfoBlock").hide();
							$("#ajaxLoading").show();
							$.ajax({
								url:"payments_inapp/pin.html",
								type:"post",
								data: $("form#pSmsDto").serialize()
							}).done(function(e) {
								$("#ajaxLoading").hide();
								$("#resendInfoBlock").show();
							});
						});
					</script>
					<!--end of one button-->	
				</div>
		</div>
	</div>
	<div class="clr verticalSpace"></div>
</div>