<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="optionPrice" value="${paymentPolicy.subcost}" />
<c:set var="numWeeks" value="${paymentPolicy.subweeks}" />


<div class="header pie-pp" id="header">
    <a href="javascript:;" onclick="closeForm()" class="close-pp"><s:message code='pay.paypal.form.close' /></a>
    <span class="logo-pp"><s:message code='page.account.title' /></span>
</div>

<div class="container-pp-mq">
    <script>
        function _submitForm() {
            window.location = "${pageContext.request.contextPath}/payments_inapp/unsubscribe.html";
        }
    </script>
    <div class="header-message-pp">
        <div style="float:left;width:60%;">
            <span class="pay-pp" style="vertical-align: middle;"><s:message code='unsub.page.start.header' /></span>
            <img class="paypal-logo-pp" style="vertical-align: middle;" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/img_paypal_logo.png" />
        </div>
    </div>

    <div class="body-message-pp"><s:message code='unsub.page.start.description.start' />
        &pound;
            <fmt:formatNumber pattern="0.00" value="${optionPrice}" />
            <s:message code='pays.page.header.txt.business_2.weeks' arguments="${numWeeks}"/>
            <s:message code='unsub.page.start.description.finish' />
    </div>
    <a href="javascript:;" onclick="_submitForm()" class="button-uns"><span class="button-text-uns"><s:message code='unsub.page.start.button.title' /></span></a>

</div>
<div class="container-pp-mq-footer">
    <img class="logo-hl" style="vertical-align: middle;" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo_footer.png" />
</div>