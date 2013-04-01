<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<div class="header">
<div class="gradient_border">&#160;</div>
    <a href="payments.html" class="button-small button-left"><s:message code='m.page.main.menu.back' /></a>
    <span class="logo"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo.png" /></span>
    <a href="payments_inapp.html" class="button-small button-right"><s:message code='m.page.main.menu.close' /></a>
</div>
<div class="container">
	<div class="content">
	<c:choose>
		<c:when test="${null!=result && result=='successful'}">
			<h1><s:message code='pay.psms.verify.dialog.success.title' /></h1>
			<p><s:message code='pay.psms.verify.dialog.success.body.inapp' /></p>
			<div class="addSmallSpace"></div>
			<!--button -->
			<div class="contentButton formButton rad5 rel">
				<input class="button accounts" title="account.html" type="button" onClick="location.href=this.title" value="<s:message code='m.pay.psms.verify.dialog.success.button.inapp' />" />
				<span class="rightButtonArrow">
					&nbsp;
				</span>
			</div>
		</c:when>
		<c:otherwise>
			<h1><s:message code="pay.psms.form.title" /></h1>
			<p><s:message code="pay.psms.verify.description" /></p>
			<div class="addSmallSpace"></div>
			<form:form modelAttribute="verifyDto" method="post">
				<div class="oneField">
					<label class="shortLabel"><s:message code="pay.psms.verify.form.pin" /></label>
					<form:input path="pin" class="width100"/>
					<c:if test="${null!=result && result!='successful'}">
						<div class="note" id="note">
							<span><s:message code="pay.psms.verify.error" /></span>
						</div>
					</c:if>
				</div>
				
				<div class="clr"></div>
				
				<div class="contentButton formButton rad5 rel">
					<input class="button-turquoise" type="submit" value="<s:message code='pay.psms.verify.submit' />" />
					<span class="rightButtonArrow">
					&nbsp;
					</span>
				</div>
				
			</form:form>
			
			<hr class="addSpace2" />
			<div class="attention"></div>
			<p><s:message code="pay.psms.verify.resend.description" /></p>
			<form:form modelAttribute="pSmsDto" method="post">
				<form:hidden path="phone"/>
				<form:hidden path="operator"/>
				
				<div class="contentButton formButton rad5 rel">
					<input id="resendSms" class="button-turquoise" type="button" value="<s:message code='pay.psms.verify.resend' />">
					<span class="button-arrow"/>
				</div>
				
				<div id="ajaxLoading" style="display:none;">
					<div class="oneField">
						<img alt="" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />/imgs/ajax-loader.gif" />
					</div>
				</div>
			</form:form>
			<script type="text/javascript">
				$("#resendSms").click(function() {
					$("#resendInfoBlock").hide();
					$("#ajaxLoading").show();
					$.ajax({
						url:"payments/pin.html",
						type:"post",
						data: $("form#pSmsDto").serialize()
					}).done(function(e) {
						$("#ajaxLoading").hide();
						$("#resendInfoBlock").show();
					});
				});
			</script>
		</c:otherwise>
	</c:choose>
	</div>
</div>