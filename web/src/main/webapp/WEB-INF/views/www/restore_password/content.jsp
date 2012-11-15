<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div class="contentContainer">
	<!-- start of two columns content -->
	<div class="content rel fixWidth twoColumns">
		<div class="leftColumnShadow">
			<div class="leftColumn">
				<h1><s:message code="restorePassword.page.h1" /></h1>
				<div class="formContainer forgotPass">
					<form:form name="restorePasswordForm" method="POST" commandName="EmailDto">
					<div class="welcomeBox">
						<s:message code="restorePassword.page.welcomeBox" />
						<div class="aloneInputField">
							<s:message code="restorePassword.field.title" var="emailTitle" />
							<form:input path="value" title="${emailTitle}" cssClass="dinInput defaultInputText" />
							<form:errors cssClass="errorSign" path="value"/>
						</div>
					</div>
					<div class="clr"></div>
					<div class="errorBoxContainer">
						<s:hasBindErrors name="EmailDto">
							<div class="errorBox rad7">
								<form:errors path="*" />
							</div>
						</s:hasBindErrors>
					</div>
					<div class="buttonShadow formButton submitEmailButton rad1">
						<div class="buttonBox rad1">
							<div class="buttonContent">
								<input class="button" type="submit" value="<s:message code='restorePassword.page.submit' />" />
							</div>
						</div>
					</div>
					</form:form>
				</div>
			</div>
		</div>
		<div class="rightColumnHome">
			<div class="marketingBox">
				<div class="marketingBoxContent">
					<h2><s:message code="restorePassword.page.marketingBoxContent.h2" /></h2>
					<p><s:message code="restorePassword.page.marketingBoxContent.info" /></p>
				</div>
			</div>
		</div>
	</div>
	<!-- end  of two columns content -->
	<div class="clr verticalSpace"></div>
</div>