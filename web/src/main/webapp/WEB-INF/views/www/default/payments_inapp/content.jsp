<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div class="contentContainer">
	<div class="verticalSpace"></div>
		<div class="content rel oneWideColumn">
			<h1 class="azHeader"><s:message code="page.pays.header.h1" /><span><s:message code="page.pays.header.description" /></span></h1>
				<!--end content of main left area-->				
				<%@ include file="/WEB-INF/views/www/default/menu.jsp"%>
				<div class="widerContainer boxWithBorder lessBottomPad">	
					<div class="wholePart">						
						<div class="details noBg">
							<h2><s:message code="pays.page.h1.options" /></h2>
							<h3>${paymentsPage.paymentPoliciesNote}</h3>
							<div class="oneInputsGroup">
								<div class="methodsBox">
									<c:forEach var="paymentPolicy" items="${paymentsPage.paymentPolicies}">
										<c:if test="${paymentPolicy.paymentType == 'creditCard'}">
											<c:set var="method_name" value="creditcard" />
											<s:message code='pays.select.creditcard' var="payment_label" />
											<c:set var="method_color" value="greyInnerText" />
										</c:if>
										<c:if test="${paymentPolicy.paymentType == 'PAY_PAL'}">
											<c:set var="method_name" value="paypal" />
											<s:message code='pays.select.paypal' var="method_readable" />
											<c:set var="method_color" value="greenInnerText" />
										</c:if>
										<c:if test="${paymentPolicy.paymentType == 'PSMS'}">
											<c:set var="method_name" value="psms" />
											<s:message code='pays.select.psms' var="method_readable" />
											<c:set var="method_color" value="blueInnerText" />
										</c:if>
										
										<div class="oneMethodBox rel">
											<a href="payments_inapp/${method_name}.html?paymentPolicyId=${paymentPolicy.id}" class="rel ${method_color}">
												<img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/icons/${method_name}_method.png" alt="">
												<span class="price abs"><s:message code="pays.select.currency" /><fmt:parseNumber integerOnly="true" value="${paymentPolicy.subcost}"/><br />
													<span style="">${paymentPolicy.subweeks} <s:message code="pays.select.weeks" /></span>
												</span>
												<span class="payBy abs"><s:message code="pays.select.payby" /><br/>
													<span>${payment_label}</span>
												</span>
											</a>
											<a id="popup${method_name}" href="#" class="infoSign"><img alt="" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/icons/info.png"></a>
											<script type="text/javascript">
												$("#popup<c:out value='${method_name}' />").click(function(e){
													showAsDialog("#popupInfo<c:out value='${method_name}' />");
													e.preventDefault();
												});
											</script>
										</div>
										<div id="popupInfo${method_name}" class="pageWindow simplePopup">
											<div class="pageWindowContent simpleTextPopup">
												<div class="innerBox">
													<h2><s:message code='pays.${method_name}.dialog.title' /></h2>
													<s:message code='pays.${method_name}.dialog.body' />
												</div>
											</div>
										</div>
									</c:forEach>
								</div>
							</div>
							<!-- 
							<h2>De-activate Billing Agreement</h2>
							<div class="oneInputsGroup">
								<p>Note about the user's payment/subscription status</p>
								<div class="oneLine">
									<div class="nameCell">Payment method:</div>
									<div class="valueCell pink">Credit Card</div>						
								</div>
								<div class="clr"></div>
								<div class="oneLine">
									<div class="nameCell">Last payment amount:</div>
									<div class="valueCell pink">&pound;5.00</div>															
								</div>
								<div class="clr"></div>
								<div class="oneLine">
									<div class="nameCell">Last payment date:</div>
									<div class="valueCell pink">06/06/2012</div>						
								</div>
								<div class="clr"></div>
							</div>
							 -->
							 
							<c:if test="${(paymentsPage.paymentDetails!=null) && (true==paymentsPage.paymentDetails.activated)}">
								<h1 class="azHeader"><s:message code="pays.deactivate.header" /></h1>
								<div class="buttonShadow rad7">
									<div class="buttonBox rad7">
										<div class="buttonContent rad7">
											<a class="button" href="payments_inapp/unsubscribe.html"><s:message
													code='pays.deactivate.submit' />
											</a>
										</div>
									</div>
								</div>
								<br /><br />
							</c:if>
							
							<c:if test="${paymentDetailsByPaymentDto!=null&&paymentDetailsByPaymentDto.activated==false}">
								<h1 class="azHeader"><s:message code="pays.subscription.header" /></h1>
								<p>
								<c:set var="paymentType">
									<s:message code="${paymentDetailsByPaymentDto.paymentType}"/>
								</c:set>
								<c:set var="currencyISO">
									<s:message code="${paymentDetailsByPaymentDto.paymentPolicyDto.currencyISO}"/>
								</c:set>
								<s:message code="pays.subscription.description" arguments='${paymentType},${paymentDetailsByPaymentDto.paymentPolicyDto.subweeks},${currencyISO},${paymentDetailsByPaymentDto.paymentPolicyDto.subcost}'/></p>
								<form action="payments_inapp/paymentDetails/${paymentDetailsByPaymentDto.paymentDetailsId}" method="post">
									<div class="buttonShadow rad7">
										<div class="buttonBox rad7">
											<div class="buttonContent rad7 buttonWidth">
												<input class="button" type="submit" value="<s:message code='pays.activate.submit' />" />
											</div>
										</div>
									</div>
								</form>
							</c:if>
						</div>
						
						
					</div>
				</div>
		</div>
		<div class="clr verticalSpaceMiddle"></div>
</div>