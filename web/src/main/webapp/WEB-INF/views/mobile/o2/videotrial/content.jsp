<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<script type="text/javascript">
function submitUserConfirmation() {
	var frm = $("#userConfirmationForm");
	frm.submit();
}
</script>

<div class="header pie">
	<div class="gradient_border">&#160;</div>
    <span class="logo"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo.png" alt="" /></span>
    <a href="${returnUrl}" class="button-small button-right"><s:message code='m.page.main.menu.close' /></a>
</div>

<div class="container">
<div class="content">
	<h1>
		<p class="centered"><s:message code="videfreetrial.page.header" /></p>
	</h1>
	<p class="centered" style="margin-bottom: 20px">
		<s:message code="videfreetrial.page.subheader" />
	</p>
	
	<p class="centered" style="margin-bottom: 20px">
		<s:message code="videfreetrial.page.text1" />
	</p>
	
	<p class="centered" style="margin-bottom: 20px">
		<s:message code="videfreetrial.page.text2" /><br />
		<a href="terms.html"><s:message code="videfreetrial.page.text_tc" /></a>
	</p>
	
	<div class="rel" >
		<a class="button-turquoise pie" href="javascript: submitUserConfirmation()" ><s:message code='videfreetrial.page.button.upgrade' /></a>
		<div style="display: none">
		<form action="${pageContext.request.contextPath}/videotrial.html" method="post" name="userConfirmationForm" id="userConfirmationForm">
			<input type="hidden" name="return_url" value="${returnUrl}" />
		</form></div>
	</div>
	
	<div class="rel" >
		<a class="button-grey pie" href="${returnUrl}" ><s:message code='videfreetrial.page.button.notnow' /></a>
	</div>
</div>
</div>