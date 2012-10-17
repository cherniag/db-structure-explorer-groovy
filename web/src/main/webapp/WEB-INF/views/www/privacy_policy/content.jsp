<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<div class="contentContainer">
	<div class="verticalSpace"></div>
		<!-- top part of content -->
		<sec:authorize access="authenticated">
			<div class="content rel oneWideColumn">	
			<h1 class="azHeader"><s:message code="page.privacy_policy.header.h1" /><span><s:message code="page.privacy_policy.header.description" /> </span></h1>
			<%@ include file="/WEB-INF/views/www/menu.jsp"%>
		
		
		<div class="widerContainer boxWithBorder lessBottomPad">
			<div class="wholePart">
				<div class="details">
					<h1><s:message code="privacy_policy.page.h1" /></h1>
					<p><s:message code="privacy_policy.page.note" /></p>
					<div class="clr"></div>
				</div>
			</div>
		</div>
		</sec:authorize>
		<sec:authorize access="anonymous">
		<div class="contentNotAuth rel oneWideColumnNotAuth">
			<div class="widerContainerNotAuth">
				<div class="detailsNotAuth">
					<h1><s:message code="privacy_policy.page.h1" /></h1>
					<p><s:message code="privacy_policy.page.note" /></p>
				</div>
			</div>
		</div>
		</sec:authorize>
	</div>
	
	<!--end of main account content area-->
	<!-- end  of two columns content -->
	
</div>