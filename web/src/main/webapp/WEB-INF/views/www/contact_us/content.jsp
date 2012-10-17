<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div class="contentContainer">
	<div class="verticalSpace"></div>
	<div class="content rel oneWideColumn">
		<!-- top part of content -->
		<h1 class="azHeader"><s:message code="page.contactUs.header.h1" /><span><s:message code="page.contactUs.header.description" /></span></h1>
		<!-- tabs  -->
		<%@ include file="/WEB-INF/views/www/menu.jsp"%>
		<div class="widerContainer boxWithBorder lessBottomPad">
			<div class="wholePart">
				<div class="details">
					<h2><s:message code="contactUs.page.h2" /></h2>
					<p><s:message code="contactUs.page.form.description" /></p>
		
					<form:form method="post" modelAttribute="contactUsDto">
						<div class="oneInputLine">
							<div class="nameCell"><s:message code="contactUs.page.form.name" /></div>
							<div class="valueCell">
								<form:input path="name" />
								<form:errors path="name" cssClass="errorSign" />
							</div>
						</div>
						<div class="clr"></div>
						
						<div class="oneInputLine">
							<div class="nameCell"><s:message code="contactUs.page.form.email" /></div>
							<div class="valueCell">
								<form:input path="email" />
								<form:errors path="email" cssClass="errorSign" />
							</div>
						</div>
						<div class="clr"></div>
						
						<div class="oneInputLine">
							<div class="nameCell"><s:message code="contactUs.page.form.subject" /></div>
							<div class="valueCell">
								<form:textarea path="subject" />
							</div>
							<form:errors path="subject" cssClass="errorSign" />
						</div>
						<div class="clr"></div>
		
						<s:hasBindErrors name="contactUsDto">
							<div class="errorBoxContainer contactBoxError">
								<div class="errorBox rad7">
									<form:errors path="*" />
								</div>
							</div>
							<div class="clr"></div>
						</s:hasBindErrors>
						
						<div class="buttons rel">
							<div class="buttonShadow shortBut rad4 abs">
								<div class="buttonBox rad4">
									<div class="buttonContent">
										<input type="reset" class="button" value="<s:message code='contactUs.page.form.btn.clear' />">
									</div>
								</div>
							</div>
							
							<div class="buttonShadow rad4 abs">
								<div class="buttonBox rad4">
									<div class="buttonContent">
										<input id="submitContactUs" type="submit" class="button" value="<s:message code='contactUs.page.form.btn.submit' />">
									</div>
								</div>
							</div>
						</div>
					</form:form>
		
					<div class="clr"></div>
				</div>
			</div>
		</div>
	</div>

	<c:if test="${null!=sentStatus}">
		<c:choose>
			<c:when test="${sentStatus}">
				<!--popup with contact form - confirmatiopn-->
					<div id="contact_1"
						class="pageWindow simplePopup">
						<div class="pageWindowContent simpleTextPopup">
							<div class="innerBox">
								<h2>
									<s:message code='contactUs.page.dialog.success.title' />
								</h2>
								<p>
									<s:message code='contactUs.page.dialog.success.body' />
								</p>
								<a href="" class="popupButton rad3"><s:message
										code='contactUs.page.dialog.success.button' />
								</a>
							</div>
						</div>
					</div>
					<script type="text/javascript">
					showModalDialog("#contact_1");
					</script>
			</c:when>
		</c:choose>
	</c:if>

	<!--end of main account content area-->
	<!-- end  of two columns content -->
	<div class="clr verticalSpaceMiddle"></div>
</div>