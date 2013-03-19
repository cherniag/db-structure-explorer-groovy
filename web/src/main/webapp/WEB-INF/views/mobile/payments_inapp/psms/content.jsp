<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<div class="header">
<div class="gradient_border">&#160;</div>
	<a href="" class="logo"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo_inapp.png" alt="" /></a>	
	<div class="buttonBox">
		<span class="arrow">&nbsp;</span>
		<a href="payments_inapp.html" class="button-small"><s:message code='m.page.main.menu.back' /></a>
	</div>				
</div>
<div class="container">		
	<div class="content">
		<h1><s:message code="pay.psms.form.title" /></h1>
		<p><s:message code="pay.psms.form.description" /></p>
		<div class="addSmallSpace"></div>
		<form:form modelAttribute="pSmsDto" method="post">
			<div class="oneField">
				<label><s:message code="pay.psms.form.mobile.number" /></label>
				<form:input path="phone" />
				<s:hasBindErrors name="pSmsDto" >
					<div class="note" id="note">
						<form:errors path="phone" />
					</div>
				</s:hasBindErrors>
			</div>
			<div class="oneField">
				<label><s:message code="pay.psms.form.operator" /></label>
				<form:select path="operator" cssClass="month">
					<option value=""><s:message code="pay.psms.form.operator.select" /></option>
					<form:options items="${operators}" itemLabel="name" itemValue="id" />
				</form:select>
				<s:hasBindErrors name="pSmsDto" >
					<div class="note" id="note">
						<form:errors path="operator" />
					</div>
				</s:hasBindErrors>
			</div>
			<div class="contentButton formButton rad5 rel">
				<input class="button" type="submit" value="<s:message code='pay.psms.form.submit' />" />
				<span class="rightButtonArrow">
					&nbsp;
				</span>
			</div>

			
			<c:choose>
				<c:when test="${result=='fail'}">
					<div class="note" id="note">
						<c:choose>
							<c:when test="${not empty external_error}">
								<span>${external_error}</span>
							</c:when>
							<c:otherwise>
								<span>${internal_error}</span>
							</c:otherwise>
						</c:choose>
					</div>
				</c:when>
			</c:choose>
		</form:form>
	</div>
</div>