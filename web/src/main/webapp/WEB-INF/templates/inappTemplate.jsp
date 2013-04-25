<%@ page session="false" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<!DOCTYPE html>

<html xmlns="http://www.w3.org/1999/xhtml" >

<%
	// Fix for IE.
	// Bug is from here http://stackoverflow.com/questions/6541721/ie-doesnt-support-relative-paths-in-the-base-element-when-referencing-css-files
	String requestURL = request.getRequestURL().toString();
	String requestURI = request.getRequestURI();
	String baseURL = requestURL.substring(0, requestURL.indexOf(requestURI));
	String contextPath = request.getContextPath();
	
	String urlContext = baseURL + contextPath + "/";
%>
<head>
	<base href="<%= urlContext %>">
	<meta http-equiv="content-type" content="text/html; charset=utf-8" />
	<meta http-equiv="Cache-Control" content="no-store, no-cache, must-revalidate" />
	<meta http-equiv="Pragma" content="no-cache" />
	<c:choose>
		<c:when test="${requestScope.isMobileRequest}">
    		<meta name="viewport" content="width=device-width, target-densitydpi=160, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
			<meta name="MobileOptimized" content="640"/>
			<link rel="stylesheet" type="text/css" href="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />css/bootstrap.css" />
			<link rel="stylesheet" type="text/css" href="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />css/mobile.css" />
		</c:when>
		<c:otherwise>
			<link rel="stylesheet" type="text/css" href="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />css/960.css" /> 
			<link rel="stylesheet" type="text/css" href="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />css/text.css" />
			<link rel="stylesheet" type="text/css" href="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />css/reset.css" />
			<link rel="stylesheet" type="text/css" href="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />css/site.css" media="screen, projection" />
			<link rel="stylesheet" type="text/css" href="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />css/simpledialog.css" />
			<sec:authorize access="authenticated">
				<link rel="stylesheet" type="text/css" href="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />css/account.css" media="screen, projection" />
			</sec:authorize>
			
		</c:otherwise>
	</c:choose>
	
	<!--[if IE]>
		<script type="text/javascript" src="assets/scripts/PIE.js"></script>
	<![endif]-->
	<script src="assets/scripts/jquery-1.7.2.min.js" type="text/javascript"></script>
	<script src="assets/scripts/jquery.simplemodal.1.4.2.min.js" type="text/javascript"></script>
	<script src="assets/scripts/main.js" type="text/javascript"></script>
	
	<script type="text/javascript">$(document).ready(function(){onStart();});</script>
	<tiles:insertAttribute name="headSection" />
</head>
<body>
	<s:message code="page.google.analytics.include" />
	<div class="page">
		
		<div class="cookie_conteiner">
			<div class="left">&#160;</div>
			<div class="right">&#160;</div>
			<c:choose>
				<c:when test="${requestScope.isMobileRequest}">
					<div class="center" id="cookeiAlertMessage"><span><s:message code='m.page.cookie.alert.message' /></span><span id="cookeiAlertMessageCloseBtn" class="cookies_style_close"><s:message code='page.cookie.alert.message.close' /></span></div>
				</c:when>
				<c:otherwise>
					<div class="center" id="cookeiAlertMessage"><span><s:message code='page.cookie.alert.message' /></span><span id="cookeiAlertMessageCloseBtn" class="cookies_style_close"><s:message code='page.cookie.alert.message.close' /></span></div>
				</c:otherwise>
			</c:choose>
		</div>
		
		<tiles:insertAttribute name="header" />
		<tiles:insertAttribute name="content" />
		<tiles:insertAttribute name="footer" />
	</div>
	<tiles:insertAttribute name="footSection" />
</body>
</html>