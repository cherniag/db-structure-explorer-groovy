<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<div class="contentContainer">
	<div class="verticalSpace"></div>
	<!-- top part of content -->
	<div class="content rel oneWideColumn">	
		<!-- top part of content -->
		<h1 class="azHeader"><s:message code="page.psms.header.h1" /><span><s:message code="page.psms.header.description" /></span></h1>
		<!-- tabs  -->
		<%@ include file="/WEB-INF/views/www/default/menu.jsp"%>
		
		<div class="widerContainer boxWithBorder lessBottomPad">
			<div class="wholePart">
				<div class="details smsBox">
					<h2><s:message code="pay.psms.form.title" /></h2>
					<form:form modelAttribute="pSmsDto" method="post">
						<input type="hidden" name="paymentPolicyId" value="${paymentPolicy.id}"/>
						<div class="payDetails">
							<p><s:message code="pay.psms.form.description" /></p>
							<div class="oneLine">
								<div class="nameCell"><s:message code="pay.psms.form.mobile.number" /></div>
								<div class="valueCell">
									<form:input path="phone" />
									<form:errors path="phone" cssClass="errorSign" />
								</div>
							</div>
							<div class="clr"></div>
							<div class="oneLine">
								<div class="nameCell"><s:message code="pay.psms.form.operator" /></div>
								<div class="valueCell">
									<form:select path="operator" cssClass="month">
										<option value=""><s:message code="pay.psms.form.operator.select" /></option>
										<form:options items="${operators}" itemLabel="name" itemValue="id" />
									</form:select>
									<form:errors path="operator" cssClass="errorSign" />
								</div>			
							</div>						
							<div class="clr"></div>
							
							<s:hasBindErrors name="pSmsDto" >
								<div class="errorBoxContainer">
									<div class="errorBox rad7">
										<form:errors path="*" />
									</div>
								</div>
							</s:hasBindErrors>
							
							<div class="buttonShadow rad4">
								<div class="buttonBox rad4">
									<div class="buttonContent">
										<input class="button" type="submit" value="<s:message code='pay.psms.form.submit' />" />
									</div>
								</div>
							</div>	
							<!--end of one button-->	
							<div class="clr"></div>
						</div>
					</form:form>
				</div>
				<c:if test="${result eq 'fail'}">
					<div class="errorBox rad7" style="display: block;">
						<c:choose>
							<c:when test="${not empty external_error}">
								${external_error}
							</c:when>
							<c:otherwise>
								${internal_error}
							</c:otherwise>
						</c:choose>
					</div>
				</c:if>
			</div>
		</div>
	</div>
</div>