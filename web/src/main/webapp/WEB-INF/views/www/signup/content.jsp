<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div class="contentContainer">
	<!-- start of two columns content -->
	<div class="content rel fixWidth twoColumns">
		<div class="leftColumnShadow">
			<div class="leftColumn">
				<h1><s:message code="signup.page.h1" /></h1>
				<!--facebook login-->
				<div class="borderBottomBox facebookLogin">
					<div class="buttonShadow formButton rad4">
						<div class="buttonBox rad4">
							<div class="buttonContent fbBg">
								<a href="facebook_signin?registration=true" class="button"><s:message code="signup.form.link.facebook.signin" /></a>
							</div>
						</div>
					</div>
				</div>
				<!--end facebook login-->
				<h2><s:message code="signup.page.h2" /></h2>
				<div class="formContainer signInForm">
					<form:form method="post" modelAttribute="UserRegDetailsDto">
						<label><s:message code="signup.form.email" /></label>
						<form:input path="email" />
						<form:errors path="email" cssClass="errorSign" delimiter=" " />
						
						<label><s:message code="signup.form.password" /></label>
						<form:password path="password"  />
						<form:errors path="password" cssClass="errorSign" delimiter=" "/>
						
						<label><s:message code="signup.form.conf.password" /></label>
						<form:password path="confirmPassword"  />
						<form:errors path="confirmPassword" cssClass="errorSign" delimiter=" "/>
						
						<div class="checkbox">
							<form:checkbox path="termsConfirmed" /><s:message code="signup.form.chbx.tm.agree" /> <a href="javascript:showPopup('popupTerms');"><s:message code="signup.form.chbx.tm" /></a>
							<div class="clr"></div>
							<form:checkbox path="newsDeliveringConfirmed"/><s:message code="signup.form.chbx.updates" />
						</div>
						
						<s:hasBindErrors name="UserRegDetailsDto">
							<div class="errorBoxContainer">
								<div class="errorBox rad7" style="display: block;">
									<form:errors path="*" />
								</div>
							</div>
						</s:hasBindErrors>
						
						<div class="buttonShadow formButton rad4">
							<div class="buttonBox rad4">
								<div class="buttonContent">
									<input class="button" type="submit" value="<s:message code='signup.form.submit' />" />
								</div>
							</div>
						</div>
						<form:hidden path="appVersion" />
						<form:hidden path="apiVersion" />
						<form:hidden path="communityName" />
					</form:form>
				</div>
			</div>
		</div>
		<div class="rightColumnHome">						
			<div class="marketingBox">
				<div class="marketingBoxContent">
					<h2><s:message code="signup.page.rcolumn.header1" /></h2>
					<p><s:message code="signup.page.rcolumn.content1" /></p>
				</div>
			</div>
			<p class="additionalInfo smallFont"><s:message code="signup.page.rcolumn.add.info" /></p>
		</div>
	</div>
	<!-- end  of two columns content -->
	<div class="clr verticalSpace"></div>
</div>