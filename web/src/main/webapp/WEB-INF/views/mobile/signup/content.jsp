<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div class="container">
	<div class="marketingMaterials">		
	</div>
	<div class="content">
		<p class="bigSizeText"><s:message code="signup.page.h1" /></p>
		<div class="contentButton facebookLoginButton rad10" >
			<a href="facebook_signin?registration=true"><s:message code="signin.form.link.facebook.signin" /></a>
		</div>
		<hr />
		<p><s:message code="m.signin.form.header" /></p>
		<form:form method="post" modelAttribute="UserRegDetailsDto" class="contact">
			<form:hidden path="appVersion" />
			<form:hidden path="apiVersion" />
			<form:hidden path="communityName" />
			<form:hidden path="newsDeliveringConfirmed"/>
			<form:hidden path="termsConfirmed" />
			<div class="oneField">
				<label><s:message code="signup.form.email" /></label>
				<form:input path="email" />
				<s:hasBindErrors name="UserRegDetailsDto">
					<div class="note" id="note">
						<form:errors path="email" />
					</div>
				</s:hasBindErrors>
			</div>
			<div class="oneField">
				<label><s:message code="signup.form.password" /></label>
				<form:password path="password"  />
				<s:hasBindErrors name="UserRegDetailsDto">
					<div class="note" id="note">
						<form:errors path="password" />
					</div>
				</s:hasBindErrors>
			</div>
			<div class="oneField">
				<label><s:message code="signup.form.conf.password" /></label>
				<form:password path="confirmPassword"  />
				<s:hasBindErrors name="UserRegDetailsDto">
					<div class="note" id="note">
						<form:errors path="confirmPassword" />
					</div>
				</s:hasBindErrors>
			</div>
			<div class="contentButton formButton rad5" >
				<input class="button" type="submit" value="<s:message code='signup.form.submit' />" />
			</div>
		</form:form>
	</div>
</div>