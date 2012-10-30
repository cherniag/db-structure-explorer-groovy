<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@page import="mobi.nowtechnologies.server.shared.util.EmailValidator"%>
<%@page import="mobi.nowtechnologies.server.shared.dto.web.AccountDto.Subscription"%>

<div class="contentContainer">
	<div class="verticalSpace"></div>
	<div class="content rel oneWideColumn">
		<!-- top part of content -->
		<h1 class="azHeader"><s:message code="page.account.header.h1" /><span><s:message code="page.account.header.description" /></span></h1>
		<!-- tabs  -->
		<%@ include file="/WEB-INF/views/www/menu.jsp"%>
		<div class="widerContainer boxWithBorder lessBottomPad oneClickSubs">
			<h1><s:message code='one.click.subscription.dialog.successful.title' /></h1>
				<c:set var="paymentType">
					<s:message code="${paymentDetailsByPaymentDto.paymentType}"/>
				</c:set>
				<c:set var="currencyISO">
					<s:message code="${paymentDetailsByPaymentDto.paymentPolicyDto.currencyISO}"/>
				</c:set>
				<p><s:message code='one.click.subscription.dialog.successful.body' arguments='${paymentType},${paymentDetailsByPaymentDto.paymentPolicyDto.subweeks},${currencyISO},${paymentDetailsByPaymentDto.paymentPolicyDto.subcost}' /></p>
				<div class="clr"></div>				
				<div class="addSmallSpace"></div>		
				<!--button-->
				<div class="buttonShadow formButton rad4">
					<div class="buttonBox rad4">
						<div class="buttonContent">
							<input class="button accounts" title="account.html" type="button" onClick="location.href=this.title" value="<s:message code='one.click.subscription.dialog.successful.button'/>" />
						</div>
					</div>
				</div>
			<div class="clr"></div>
		</div>
	</div>
	<!--end of main account content area-->
	<!-- end  of two columns content -->
	<div class="clr verticalSpaceMiddle"></div>
