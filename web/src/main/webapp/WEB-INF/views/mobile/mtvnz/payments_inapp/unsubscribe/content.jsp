<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div class="page-container">
    <img src="${requestScope.assetsPathAccordingToCommunity}imgs/icon_close_72.png" class="go-premium-button-target go-premium-button-close" onclick="returnToApp();" />

    <div class="center-container premium-logo-container">
        <img src="${requestScope.assetsPathAccordingToCommunity}imgs/notification_premium.png" class="premium-logo" />
    </div>

    <div class="center-container">
        <span class="header2">
            <br/>
        </span>
        <span class="header header2">
            <s:message code='unsubscribe.step2.header' />
        </span>
        <span class="header2">
            <br/><br/>
        </span>
    </div>

    <div class="message">
        <s:message code='unsubscribe.step2.body' />
    </div>

    <a class="go-premium-button go-premium-button-device go-premium-button-target go-premium-body-cancel" onclick="submitForm('unsubscribeFormId')">
        <span><s:message code='button.yes.title' /></span>
    </a>
    <a class="go-premium-button go-premium-button-device go-premium-button-target go-premium-body-ok" onclick="returnToApp();">
        <span><s:message code='button.cancel.title' /></span>
    </a>
    <form:form modelAttribute="unsubscribeDto" method="post" id="unsubscribeFormId" action="payments_inapp/unsubscribeAndRedirect.html">
        <s:hasBindErrors name="unsubscribeDto">
            <div class="note" id="note">
                <form:errors path="reason" />
            </div>
        </s:hasBindErrors>
    </form:form>
</div>
