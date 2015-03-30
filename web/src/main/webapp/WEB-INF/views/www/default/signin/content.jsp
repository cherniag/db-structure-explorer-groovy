<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div class="contentContainer">
	<!-- start of two columns content -->
	<div class="content rel fixWidth twoColumns">
		<div class="leftColumnShadow">
			<div class="leftColumn">
				<h1><s:message code="signin.page.h1" /></h1>
				<div class="formContainer signInForm">
					<form action="signin" method="post">
					
						<label><s:message code="signin.form.email" /></label>
						<input name="email" id="email" type="text" />
						<c:if test="${param.error != null}"><span class="errorSign">&nbsp;</span></c:if>
						
						<label><s:message code="signin.form.password" /></label>
						<input name="token" id="token" type="password" />
						<c:if test="${param.error != null}"><span class="errorSign">&nbsp;</span></c:if>

						<c:if test="${param.error != null}">
							<!--error-->
								<div class="errorBoxContainer">
									<div class="errorBox rad7">
										<span><s:message code="signin.error.on.sign.in" /></span>
									</div>
								</div>
						</c:if>
						
						<c:if test="${param.facebook_error != null}">
							<!--error-->
								<div class="errorBoxContainer">
									<div class="errorBox rad7">
										<span><s:message code="signin.error.on.facebook_sign.in" /></span>
									</div>
								</div>
						</c:if>
						
						<!--sign into account button-->
						<div class="buttonShadow formButton signInButton rad1">
							<div class="buttonBox rad1">
								<div class="buttonContent">
									<input class="button" type="submit" value="<s:message code='signin.form.submit' />" />
								</div>
							</div>
						</div>
						<!--end of sign into account button-->
						
						<p class="shortParagrapf"><s:message code="signin.form.or.text" /></p>
					
						<!--facebook login button-->
						<div class="facebookLogin">
							<div class="buttonShadow formButton rad1">
								<div class="buttonBox rad1">
									<div class="buttonContent fbBg">
										<a href="facebook_signin" class="button"><s:message code="signin.form.link.facebook.signin" /></a>
									</div>
								</div>
							</div>
						</div>
						<!--end facebook login-->
					</form>
				</div>
			</div>
		</div>
		<div class="rightColumnHome">						
			<div class="marketingBox">
				<div class="marketingBoxContent">
					<h2><s:message code="signin.page.rcolumn.h2" /></h2>
					<p><s:message code="signin.page.rcolumn.content" /></p>
				</div>
			</div>
		</div>
	</div>
	<!-- end  of two columns content -->
	<div class="clr verticalSpace"></div>
</div>