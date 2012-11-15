<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div class="contentContainer">
	<div class="verticalSpace"></div>
		<div class="content rel oneWideColumn">
			<h1 class="azHeader"><s:message code="page.unsub.header.h1" /><span><s:message code="page.unsub.header.description" /></span></h1>
				<%@ include file="/WEB-INF/views/www/menu.jsp"%>
				<div class="widerContainer boxWithBorder lessBottomPad">
					<div class="wholePart">
						<c:choose>
							<c:when test="${result == null||result == 'fail'}">
								<div class="details phoneRightBg">
									<h2><s:message code="unsub.page.header" /></h2>
									<div class="payDetails deactivateBox">
										<form:form modelAttribute="unsubscribeDto" method="post">
											<p><s:message code="unsub.page.description" /></p>	
											
											<div class="oneInputLine">				
												<div class="valueCell">
													<form:textarea path="reason"/>
												</div>
												<s:hasBindErrors name="unsubscribeDto">
													<div class="note" id="note">
														<form:errors path="reason" />
													</div>
												</s:hasBindErrors>
											</div>
											
											<div class="clr"></div>
											<div class="buttons rel">
												<!--one button-->
												<div class="buttonShadow rad4 shortBut abs">
													<div class="buttonBox rad4">
														<div class="buttonContent">
															<a class="button" href="payments_inapp.html"><s:message code="unsub.page.form.btn.cancel" /></a>
														</div>
													</div>
												</div>	
												
												<div class="buttonShadow rad4 abs">
													<div class="buttonBox rad4">
														<div class="buttonContent">
															<input type="submit" class="button" value="<s:message code='unsub.page.form.submit' />" />
														</div>
													</div>
												</div>
											</div>
											<s:hasBindErrors name="unsubscribeDto">
												<div class="errorBoxContainer">
													<div class="errorBox rad7">
														<form:errors path="*" />
													</div>
												</div>
											</s:hasBindErrors>
										</form:form>
									</div>
								</div>
							</c:when>
							<c:otherwise>
								<div class="details noBg">
									<h2><s:message code="unsub.page.header" /></h2>
									<!--Payment by card details box-->
									<div class="payDetails deactivateBox">
										<s:message code="unsub.page.description.unsubscribed" />																
										<div class="clr"></div>																
										<!--one button-->
										<div class="buttonShadow rad4">
											<div class="buttonBox rad4 width273">
												<div class="buttonContent">
													<a class="button" href="account.html"><s:message code="page.main.menu.my.account" /></a>
												</div>
											</div>
										</div>	
										<!--end of one button-->		
									</div>
								</div>
							</c:otherwise>
						</c:choose>
					</div>
				</div>
		</div>
		<div class="clr verticalSpaceMiddle"></div>
</div>