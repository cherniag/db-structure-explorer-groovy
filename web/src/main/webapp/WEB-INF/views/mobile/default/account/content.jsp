<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@page import="mobi.nowtechnologies.server.web.controller.AccountDto.Subscription"%>
<div class="header">
<div class="gradient_border">&#160;</div>
	<a href="" class="logo"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo.png" alt="" /></a>			
</div>
<div class="container">
	<div class="content">
		<h1><s:message code="page.account.header.h1" /></h1>
		<p><s:message code="page.account.header.description" /></p>
		<h1><s:message code='account.page.leftPart.details' /></h1>
		<form:form commandName="accountDto">
			<div class="oneLine">
				<s:message code='account.page.accountDetails.${accountDto.subscription}' />												
			</div>
			<c:set var="freeTrialSubscription" value="<%=Subscription.freeTrialSubscription%>"/>
			<c:if test="${accountDto.subscription == freeTrialSubscription}">
				<div class="clr"></div>
				<div class="oneLine">
					<div class="nameCell"><s:message code='account.page.leftPart.trialEndingDate' /></div>
                    <s:message var="trialEndingDateFormat" code='account.page.leftPart.trialEndingDateFormat' />
					<div class="bold pink">
                        <script>
                            var timeOfMovingToLimitedStatusDate = new Date(${accountDto.timeOfMovingToLimitedStatus});
                            document.write(timeOfMovingToLimitedStatusDate.format('${trialEndingDateFormat}'));
                        </script>
                    </div>
				</div>
			</c:if>
			<div class="clr"></div>
			<div class="oneLine">
				<div class="nameCell"><s:message code='account.page.leftPart.credits' /></div>
				<div class="bold pink">
					${accountDto.subBalance} <s:message code="pays.select.weeks" />
					<a href="payments_inapp.html"><s:message code='account.page.leftPart.link.upgrade' /></a>
				</div>					
			</div>	
			<!--buttons -->
			<div class="setOfButtons">
				<div class="contentButton formButton rad5 rel" >
					<input class="button" title="payments_inapp.html" type="button" onClick="location.href=this.title" value="<s:message code='account.page.menu.payments' />" />
					<span class="rightButtonArrow">
						&nbsp;
					</span>
				</div>
				<s:message code='account.page.rightPart.submit' var="account_page_rightPart_submit" />
				<c:if test="${not empty account_page_rightPart_submit}">
					<div class="contentButton formButton rad5 rel" >
						<input class="button accounts" title="change_account.html" type="button" onClick="location.href=this.title" value="<s:message code='account.page.rightPart.submit' />" />
						<span class="rightButtonArrow">
							&nbsp;
						</span>
					</div>
				</c:if>
			</div>
		</form:form>
	</div>
</div>