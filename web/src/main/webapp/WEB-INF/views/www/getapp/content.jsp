<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div class="contentContainer">
	<div class="verticalSpace"></div>
	<!-- top part of content -->
	<div class="content rel oneWideColumn">
		<h1 class="azHeader"><s:message code="page.getapp.header.h1" /><span><s:message code="page.getapp.header.description" /></span></h1>
		<%@ include file="/WEB-INF/views/www/menu.jsp"%>
		
		<div class="widerContainer boxWithBorder">
			<h2><s:message code="getapp.page.description" /></h2>
			<div class="appBoxes">
				<div class="floatLeft appBoxesContainer rel">
					<h2><s:message code="getapp.page.h2" /></h2>
					<form:form modelAttribute="getPhone" method="post">
						<s:message code='getapp.form.phone.title' var="phoneTitle" />
						<form:input path="phone" cssClass="textInputTel defaultInputText" title="${phoneTitle}" />
						<form:errors path="phone" cssClass="errorSign" />
						<p><s:message code='getapp.form.description' /></p>
						<div class="clr"></div>
						
						<s:hasBindErrors name="getPhone">
							<div class="errorBox rad7">
								<form:errors path="*" />
							</div>
							<div class="clr"></div>
						</s:hasBindErrors>
						
						<div class="buttonShadow floatLeft rad1" >
							<div class="buttonBox rad1">
								<div class="buttonContent rad1">
									<input class="button" value="<s:message code='getapp.form.submit' />" type="submit" />
								</div>
							</div>
						</div>
						<c:if test="${sentStatus == true}">
							<div id="popupSuccessfulGetApp" class="pageWindow simplePopup">
								<div class="pageWindowContent simpleTextPopup">
									<div class="innerBox">
										<h2><s:message code='getapp.form.submit.successful.title' /></h2>
										<p><s:message code='getapp.form.submit.successful.body' /></p>
										<a href="" class="popupButton rad3"><s:message code='getapp.form.submit.successful.button' /></a>
									</div>
								</div>
							</div>
							<script type="text/javascript">showModalDialog("#popupSuccessfulGetApp");</script>
						</c:if>
					</form:form>
				</div>
				<div class="floatLeft phonesBox">
					<h2><s:message code='getapp.download.header' /></h2>
					<a href="<s:message code='page.available.on.store.link' />" target="_blank" class="iphoneImgLink"><s:message code='getapp.available.on' /> <span><s:message code='getapp.available.on.store' /></span></a>
					<a href="<s:message code='page.available.on.market.link' />" target="_blank" class="androidImgLink"><s:message code='getapp.available.on' /> <span><s:message code='getapp.available.on.market' /></span></a>
					<a href="<s:message code='page.available.on.bbmarket.link' />" target="_blank" class="blackberryLink"><s:message code='getapp.available.on' /> <span><s:message code='getapp.available.on.bbmarket' /></span></a>
				</div>
			</div>
		</div>				
	</div>
	<div class="clr verticalSpace"></div>
</div>
		