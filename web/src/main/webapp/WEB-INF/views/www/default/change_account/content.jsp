<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div class="contentContainer">
	<div class="verticalSpace"></div>
	<div class="content rel oneWideColumn">
		<!-- top part of content -->
		<h1 class="azHeader"><s:message code="page.account.header.h1" /><span><s:message code="page.account.header.description" /></span></h1>
		<!-- tabs  -->
		<%@ include file="/WEB-INF/views/www/default/menu.jsp"%>
		<div class="widerContainer boxWithBorder lessBottomPad">
			<div class="wholePart">
				<form:form commandName="accountDto" id="changeAccountForm" name="changeAccountForm" method="POST">
					<div class="details noBg">
						<h2>
							<s:message code='change_account.page.changeAccountForm.title' />
						</h2>
						<div class="oneInputsGroup rel">
							<div class="oneInputLine">
								<div class="nameCell">
									<s:message code='change_account.page.changeAccountForm.email' />
								</div>
								<s:eval expression="accountDto.email matches '^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$'" var="validMail" />
								<c:choose>
									<c:when test="${validMail}">
										<form:input path="email" readonly="true"/>
									</c:when>
									<c:otherwise>
										<input type="text" readonly="readonly" class="defaultInputText" value="<s:message code='account.page.accountDetails.defaultEmail' />" />
										<form:hidden path="email"/>
									</c:otherwise>
								</c:choose>
								<div class="oneBox box2 rad7 additionalInfo abs">
									<div class="oneBoxContent rad7">
										<p>
											<s:message code='change_account.page.changeAccountForm.email.hint' />
										</p>
									</div>
									<img alt="<s:message code='change_account.page.changeAccountForm.email.hint.alt' />"
										src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/bgs/box_4.png">
								</div>
							</div>
						</div>
						<div class="oneInputsGroup rel">
							<div class="oneInputLine">
								<div class="nameCell">
									<s:message code='change_account.page.changeAccountForm.currentPassword' />
								</div>
								<form:password path="currentPassword"
									maxlength="20" />
								<form:errors cssClass="errorSign" path="currentPassword" delimiter=" " />
								<div class="clr"></div>
							</div>
							<div class="oneInputLine">
								<div class="nameCell">
									<s:message code='change_account.page.changeAccountForm.newPassword' />
								</div>
								<form:password path="newPassword" 			maxlength="20" />
								<form:errors cssClass="errorSign" path="newPassword" delimiter=" " />
								<div class="clr"></div>
							</div>
							<div class="oneInputLine">
								<div class="nameCell">
									<s:message code='change_account.page.changeAccountForm.confirmPassword' />
								</div>
								<form:password path="confirmPassword"
									maxlength="20" />
								<form:errors cssClass="errorSign" path="confirmPassword" delimiter=" " />
								<div class="clr"></div>
							</div>
							<div class="oneBox rad7 additionalInfo box5 abs">
								<div class="oneBoxContent rad7">
									<s:message code='change_account.page.changeAccountForm.confirmPassword.hint' />
								</div>
								<img alt="<s:message code='change_account.page.changeAccountForm.currentPassword.hint.alt' />"
									src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/bgs/box_5.png">
							</div>
						</div>
						<div class="oneInputsGroup noBg">
							<div class="oneInputLine">
								<div class="nameCell"><s:message code='change_account.page.changeAccountForm.mobileNumber' /></div>
								<s:message code="account.page.accountDetails.defaultPhoneNumber" var="phoneTitle" />
								<form:input path="phoneNumber" title="${phoneTitle}" cssClass="defaultInputText" />
								<form:errors cssClass="errorSign" path="phoneNumber" delimiter=" " />
								<div class="clr"></div>
							</div>
						</div>
						<s:hasBindErrors name="accountDto">
							<!--error-->
							<div class="errorBoxContainer">
								<div class="errorBox rad7">
									<form:errors path="*" cssClass="rad7"/>
								</div>
							</div>
							<!--end of error-->
						</s:hasBindErrors>
						<div class="buttons rel">
							<!--one button-->
							<div class="buttonShadow shortBut rad4 abs">
								<div class="buttonBox rad4">
									<div class="buttonContent">
										<a class="button" href="account.html"><s:message code='change_account.page.changeAccountForm.button.cancel' /></a>
									</div>
								</div>
							</div>
							<!--end of one button-->
							<!--one button-->
							<div class="buttonShadow rad4 abs">
								<div class="buttonBox rad4">
									<div class="buttonContent">
										<input class="button" type="submit" value="<s:message code='change_account.page.changeAccountForm.button.save' />" />
									</div>
								</div>
							</div>
							<!--end of one button-->
						</div>
					</div>
				</form:form>
			</div>
		</div>
	</div>
	<!--end of main account content area-->
	<!-- end  of two columns content -->
	<div class="clr verticalSpaceMiddle"></div>
</div>