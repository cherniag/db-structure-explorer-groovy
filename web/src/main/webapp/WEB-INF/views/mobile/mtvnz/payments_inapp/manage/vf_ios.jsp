<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div class="root-container">
    unsubscribe for iTunes ...
    <a class="go-premium-button go-premium-button-device go-premium-button-target go-premium-body-ok" href="phone/reassign.html">
        <span><s:message code='button.phone.change' /></span>
    </a>

    <a class="go-premium-button go-premium-button-device go-premium-button-target go-premium-body-cancel" href="payments_inapp/unsubscribeConfirmation.html">
        <span><s:message code='button.unsubscribe.title' /></span>
    </a>

    <a class="go-premium-button go-premium-button-device go-premium-button-target go-premium-body-cancel" onclick="returnToApp();">
        <span><s:message code='button.back.to.the.app.title' /></span>
    </a>
</div>

