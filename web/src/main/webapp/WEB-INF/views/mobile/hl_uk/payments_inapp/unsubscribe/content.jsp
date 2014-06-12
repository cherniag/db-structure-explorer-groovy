<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<div class="header pie-pp">
    <a href="javascript:;" onclick="closeForm()" class="close-pp"><s:message code='pay.paypal.form.close' /></a>
    <span class="logo-pp"><s:message code='page.account.title' /></span>
</div>

<div class="container-pp-mq">
    <script>
        function _submitForm() {
            document.getElementById('unsubscribeFormId').submit();
        }
     </script>
		<c:choose>
			<c:when test="${result == null||result == 'fail'}">
                <div class="header-message-pp">
                    <div style="float:left;width:60%;">
                        <span class="pay-pp" style="vertical-align: middle;"><s:message code='unsub.page.header' /></span>
                    </div>
                </div>
                <div class="body-message-pp"><s:message code='unsub.page.description' /></div>
                <a href="javascript:;" onclick="_submitForm()" class="button-uns"><span class="button-text-uns"><s:message code='unsub.page.form.submit' /></span></a>
                <a href="javascript:;" onclick="returnToApp()" class="button-uns"><span class="button-text-uns"><s:message code='unsub.page.form.btn.cancel' /></span></a>

				<form:form modelAttribute="unsubscribeDto" method="post" id="unsubscribeFormId">

					<s:hasBindErrors name="unsubscribeDto">
							<div class="note" id="note">
								<form:errors path="reason" />
							</div>
					</s:hasBindErrors>
				</form:form>
			</c:when>
			<c:otherwise>
                <div class="header-message-pp">
                    <div style="float:left;width:60%;">
                        <span class="pay-pp" style="vertical-align: middle;"><s:message code='unsub.page.sucess.header' /></span>
                    </div>
                </div>
                <div class="body-message-pp"><s:message code='unsub.page.description.unsubscribed' /></div>
                <a href="javascript:;" onclick="returnToApp()" class="button-pp"><span class="button-text-pp"><s:message code='m.pay.paypal.dialog.successful.button.inapp' /></span></a>
			</c:otherwise>
		</c:choose>
	</div>
<div class="container-pp-mq-footer">
    <img class="logo-hl" style="vertical-align: middle;" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo_footer.png" />
</div>