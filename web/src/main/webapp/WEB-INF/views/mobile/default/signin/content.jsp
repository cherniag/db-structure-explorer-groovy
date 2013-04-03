<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div class="container">		
	<div class="content">
		
		<p class="firstPar"><s:message code="signin.form.header.facebook.signin" /></p>
		<div class="contentButton facebookLoginButton rad10" >
			<a href="facebook_signin"><s:message code="signin.form.link.facebook.signin" /></a>
		</div>
		
		<form action="signin" method="post" class="contact">
			<hr />
			<p><s:message code="m.signin.form.header" /></p>
			<c:if test="${param.error != null}">
				<p class="note" id="note">
					<span id="confirmPassword.errors"><s:message code="signin.error.on.sign.in" /></span>
				</p>
			</c:if>
			<div class="oneField">
				<label><s:message code="signin.form.email" /></label>
				<input name="email" id="email" type="email" />
			</div>
			<div class="oneField">
				<label><s:message code="signin.form.password" /></label>
				<input name="token" id="token" type="password" />
			</div>
			<!--create account button login-->
			<div class="contentButton formButton rad5" >
				<input class="button" type="submit" value="<s:message code='signin.form.submit' />" />
			</div>
		</form>
	</div>
</div>
