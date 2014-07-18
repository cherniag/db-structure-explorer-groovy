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
    <%-- <span class="logo"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo.png" alt="" /></span> --%>
    <span class="logo videoOptInLogo"><s:message code="videfreetrial.page.logo" /></span>
    <%-- <a href="${returnUrl}" class="button-small button-right-abs"><s:message code='m.page.main.menu.close' /></a> --%>
</div>

<div class="container">
<div class="content centered" style="background-color: inherit; color: #fff; padding: 4px;">
	<div class="videoOptInTitle">
		<s:message code="videfreetrial.page.header" />
	</div>
	
	<div><img width="107px" height="108px"	src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/ic_video_opt.png" /></div>
	
	<div class="frRoman S15" style="margin-top: 7px;">
		<s:message code="videfreetrial.page.subheader" />
	</div>
	
	<hr class="videoOptHR" />
	
	<div class="frLight S13" style="margin-bottom: 16px">
		<s:message code="videfreetrial.page.text1" />
	</div>
	
	<div class="frLight S13">
		<s:message code="videfreetrial.page.text1_2" />
	</div>
	
	<hr class="videoOptHR" />
	
	<div class="frLight S11">
		<s:message code="videfreetrial.page.text2" />
		<div class="videoOptInTAndC"><a href="terms.html"><s:message code="videfreetrial.page.text_tc" /></div>
	</div>
	
	<div class="rel" >
		<a class="button-turquoise pie" href="javascript: submitUserConfirmation()" ><s:message code='videfreetrial.page.button.upgrade' /></a>
		<div style="display: none">
		<form action="${pageContext.request.contextPath}/videotrial.html" method="post" name="userConfirmationForm" id="userConfirmationForm">
			<input type="hidden" name="return_url" value="${returnUrl}" />
		</form></div>
	</div>
	
	<div class="rel" >
		<a class="button-blue pie" href="${returnUrl}" ><s:message code='videfreetrial.page.button.notnow' /></a>
	</div>
</div>
</div>