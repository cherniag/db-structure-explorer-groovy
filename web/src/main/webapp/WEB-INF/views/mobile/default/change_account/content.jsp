<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<div class="header">
<div class="gradient_border">&#160;</div>
	<a href="" class="logo"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo.png" alt="" /></a>		
	<div class="buttonBox">
		<span class="arrow">&nbsp;</span>
		<a href="account.html" class="button buttonTop"><s:message code="page.main.menu.my.account" /></a>	
	</div>			
</div>
<div class="container">
	<div class="content">
		<h1><s:message code='change_account.page.changeAccountForm.title' /></h1>
		<p><s:message code='change_account.page.changeAccountForm.description' /></p>					
 		<form:form commandName="accountDto" method="post" cssClass="formDetails">
			<div class="oneInputLine">
				<div class="nameCell"><s:message code='change_account.page.changeAccountForm.email' /></div>
				<div class="valueCell">
					<form:input path="email" readonly="true" type="email" />
				</div>	
				<div class="helper"></div>
			</div>
			<div class="clr"></div>
			<div class="oneInputLine">
				<div class="nameCell"><s:message code='change_account.page.changeAccountForm.currentPassword' /></div>
				<div class="valueCell">
					<form:password path="currentPassword" maxlength="20" />
					<s:hasBindErrors name="accountDto">
						<div class="note" id="note">
							<form:errors path="currentPassword" cssClass="rad7"/>
						</div>
					</s:hasBindErrors>
				</div>
				<div class="helper"></div>					
			</div>	
			
			<div class="clr"></div>
			<div class="oneInputLine">
				<div class="nameCell"><s:message code='change_account.page.changeAccountForm.newPassword' /></div>
				<div class="valueCell">
					<form:password path="newPassword" maxlength="20" />
					<s:hasBindErrors name="accountDto">
						<div class="note" id="note">
							<form:errors path="newPassword" cssClass="rad7"/>
						</div>
					</s:hasBindErrors>
				</div>
				<div class="helper"></div>						
			</div>	
			<div class="clr"></div>
			<div class="oneInputLine">
				<div class="nameCell"><s:message code='change_account.page.changeAccountForm.confirmPassword' /></div>
				<div class="valueCell">
					<form:password path="confirmPassword" maxlength="20" />
					<s:hasBindErrors name="accountDto">
						<div class="note" id="note">
							<form:errors path="confirmPassword" cssClass="rad7"/>
						</div>
					</s:hasBindErrors>
				</div>
				<div class="helper"></div>					
			</div>	
			<div class="clr"></div>
			<div class="oneInputLine">
				<div class="nameCell"><s:message code='change_account.page.changeAccountForm.mobileNumber' /></div>
				<div class="valueCell">
						<form:input path="phoneNumber" />
						<s:hasBindErrors name="accountDto">
							<div class="note" id="note">
								<form:errors path="phoneNumber" cssClass="rad7"/>
							</div>
						</s:hasBindErrors>
				</div>
				<div class="helper"></div>			
			</div>	
			<div class="clr"></div>
			<div class="contentButton formButton rad5 rel" >
				<input class="button" type="submit" value="<s:message code='change_account.page.changeAccountForm.button.save' />" />
				<span class="rightButtonArrow">
					&nbsp;
				</span>
			</div>
			
		</form:form>
	</div>
</div>