<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div class="root-container">
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
