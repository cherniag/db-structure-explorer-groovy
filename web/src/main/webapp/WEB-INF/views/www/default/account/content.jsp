<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@page import="mobi.nowtechnologies.server.web.controller.AccountDto.Subscription"%>

<div class="contentContainer">
	<div class="verticalSpace"></div>
	<div class="content rel oneWideColumn">
		<!-- top part of content -->
		<h1 class="azHeader"><s:message code="page.account.header.h1" /><span><s:message code="page.account.header.description" /></span></h1>
		<!-- tabs  -->
		<%@ include file="/WEB-INF/views/www/default/menu.jsp"%>
		<div class="widerContainer boxWithBorder lessBottomPad">
			<form:form commandName="accountDto">

				<div class="leftPart floatLeft">
					<div class="details rel">
						<h2>
							<s:message code='account.page.leftPart.details' />
						</h2>
						<div class="oneLine">
						<div class="nameCell">
								<s:message code='account.page.leftPart.statusMessage' />
							</div>
							<div class="valueCell pink">
								<s:message code='account.page.accountDetails.${accountDto.subscription}' />
							</div>
						</div>
						<c:set var="freeTrialSubscription" value="<%=Subscription.freeTrialSubscription%>"/>
						<c:if test="${accountDto.subscription == freeTrialSubscription}">
							<div class="clr"></div>
							<div class="oneLine">
								<div class="nameCell">
									<s:message code='account.page.leftPart.trialEndingDate' />
								</div>
								<div class="valueCell pink">
                                    <s:message var="trialEndingDateFormat" code='account.page.leftPart.trialEndingDateFormat' />
                                    <div class="bold pink">
                                        <script>
                                            var timeOfMovingToLimitedStatusDate = new Date(${accountDto.timeOfMovingToLimitedStatus});
                                            document.write(timeOfMovingToLimitedStatusDate.format('${trialEndingDateFormat}'));
                                        </script>
                                    </div>
								</div>
							</div>
						</c:if>
						<div class="clr"></div>
						<div class="oneLine">
							<div class="nameCell">
								<s:message code='account.page.leftPart.credits' />
								<a id="creditsInfo" href=""><img
										src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />/imgs/icons/info.png"
										alt="<s:message code='account.page.leftPart.info.pic.alt' />">
								</a>
							</div>
							<div class="valueCell pink">
								${accountDto.subBalance} <s:message code="pays.select.weeks" />
								<a href="payments_inapp.html"><s:message code='account.page.leftPart.link.upgrade' /></a>
							</div>
						</div>
						<!--info for special offer-->
						<a id="accountOverviewInfo" href="" class="abs InfoSpecialOffer"><img
								src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />/imgs/icons/info.png"
								alt="<s:message code='account.page.leftPart.info.pic.alt' />">
						</a>
						<c:if test="${not empty accountDto.potentialPromotion}"><img alt="" src="${accountDto.potentialPromotion}"></c:if>
						<div class="clr"></div>
					</div>
				</div>
				<div class="rightPart floatLeft">
					<div class="details rel">
						<h2>
							<s:message code='account.page.rightPart.details' />
						</h2>
						<div class="oneLine alignJustify">
							<s:message code='account.page.rightPart..details.desciption' />
						</div>
						<div class="clr"></div>
						<div class="oneLine">
							<div class="nameCell">
								<s:message code='account.page.rightPart.email' />
							</div>
							<div class="valueCell pink">
								<s:eval expression="accountDto.email matches '^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$'" var="validMail" />
								<c:choose>
									<c:when test="${validMail}">${accountDto.email}</c:when>
									<c:otherwise><s:message code='account.page.accountDetails.defaultEmail' /></c:otherwise>
								</c:choose>
							</div>
						</div>
						<div class="oneLine">
							<div class="nameCell">
								<s:message code='account.page.rightPart.password' />
							</div>
							<div class="valueCell pink">
								<s:message code='account.page.rightPart.defaultPassword' />
							</div>
						</div>
						<div class="oneLine">
							<div class="nameCell">
								<s:message code='account.page.rightPart.mobileNumber' />
							</div>
							<div class="valueCell pink">
								${accountDto.phoneNumber}
							</div>
						</div>
						<div class="clr verticalSpace2"></div>
						<s:message code='account.page.rightPart.submit' var="account_page_rightPart_submit" />
						<c:if test="${not empty account_page_rightPart_submit}">
							<div class="buttonShadow rad7">
								<div class="buttonBox rad7">
									<div class="buttonContent rad7">
										<a class="button" href="change_account.html"><s:message
												code='account.page.rightPart.submit' />
										</a>
									</div>
								</div>
							</div>
						</c:if>
					</div>
				</div>
			</form:form>
			<div class="clr"></div>
		</div>
	</div>
	<!--end of main account content area-->
	<!-- end  of two columns content -->
	<div class="clr verticalSpaceMiddle"></div>
</div>

<script type="text/javascript">
	$("#creditsInfo").click(function(e){
		showAsDialog("#popupCreditsInfo");
		e.preventDefault();
	});
	
	$("#accountOverviewInfo").click(function(e){
		showAsDialog("#popupAccountOverviewInfo");
		e.preventDefault();
	});
</script>

<div id="popupCreditsInfo" class="pageWindow simplePopup">
	<div class="pageWindowContent simpleTextPopup">
		<div class="innerBox">
			<h2><s:message code='account.page.leftPart.credits.title' /></h2>
			<s:message code='account.page.leftPart.credits.body' />
		</div>
	</div>
</div>

<div id="popupAccountOverviewInfo" class="pageWindow simplePopup">
	<div class="pageWindowContent simpleTextPopup">
		<div class="innerBox">
			<h2><s:message code='account.page.leftPart.details.title' /></h2>
			<s:message code='account.page.leftPart.details.body' />
		</div>
	</div>
</div>