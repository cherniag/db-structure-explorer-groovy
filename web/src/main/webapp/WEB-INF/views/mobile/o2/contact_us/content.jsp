<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
	
<div class="header pie">
<div class="gradient_border">&#160;</div>
	<a href="" class="logo"><img
			src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo.png"
			alt="" /> </a>
	<div class="buttonBox">
		<span class="arrow">&nbsp;</span>
		<a href="account.html" class="button buttonTop"><s:message
				code="page.main.menu.my.account" /> </a>
	</div>
</div>
<form:form method="post" cssClass="contact"	modelAttribute="contactUsDto">
<div class="container">
	<div class="content">
		<h1>
			<s:message code="contactUs.page.h2" />
		</h1>
		<p>
			<s:message code="contactUs.page.form.description" />
		</p>
			<div class="oneField">
				<label>
					<s:message code="contactUs.page.form.name" />
				</label>
				<form:input path="name" />
				<s:hasBindErrors name="contactUsDto">
					<div class="note" id="note">
						<form:errors path="name" />
					</div>
				</s:hasBindErrors>
			</div>
			<div class="oneField">
				<label>
					<s:message code="contactUs.page.form.email" />
				</label>
				<form:input path="email" />
			</div>
			<div class="oneField">
				<label>
					<s:message code="contactUs.page.form.subject" />
				</label>
				<form:textarea path="subject" />
				<s:hasBindErrors name="contactUsDto">
					<div class="note" id="note">
						<form:errors path="subject" />
					</div>
				</s:hasBindErrors>
			</div>
			<!--button -->
			<div class="contentButton formButton rad5">
				<input type="submit" class="button"
					value="<s:message code='contactUs.page.form.btn.submit' />">
			</div>
			<c:if test="${sentStatus == true}">
				<div class="margin successNote" id="note" >
					<span><s:message code='getapp.form.submit.successful' /> </span>
				</div>
			</c:if>
	</div>
</div>

</form:form>