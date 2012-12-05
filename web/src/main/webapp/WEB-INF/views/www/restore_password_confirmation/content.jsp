<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div class="contentContainer">
	<!-- start of one column content -->
	<div class="content rel fixWidth twoColumns">
		<div class="leftColumnShadow">
			<div class="leftColumn">
				<h1><s:message code="restorePasswordConfirmation.page.h1" /></h1>
				<div class="formContainer">
					<div class="forgottenPasswordText">
						<s:message code="restorePasswordConfirmation.page.forgottenPasswordText" />
					</div>
					<!--one button-->
					<div class="buttonShadow formButton rad1">
						<div class="buttonBox rad1">
							<div class="buttonContent">
								<a href="" class="button"><s:message code="restorePasswordConfirmation.page.homePageButton" /></a>
							</div>
						</div>
					</div>
					<!--end one button-->
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
		</div>
	</div>
	<!-- end  of one columns content -->
	<div class="clr verticalSpace"></div>
</div>