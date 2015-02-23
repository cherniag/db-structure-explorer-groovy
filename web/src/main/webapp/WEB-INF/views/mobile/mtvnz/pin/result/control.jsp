<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<script src="${requestScope.assetsPathWithoutCommunity}scripts/utils.js"></script>

<div>
    <s:message code='enter.pin.header' arguments='${phone}'/>
</div>

<div>
    <s:message code='enter.pin.footer'/>
</div>

<div id="pinCodeId">
    <input class="pin-code-digit pin-code-digit-1" type="text" maxlength="1" />
    <input class="pin-code-digit pin-code-digit-2" type="text" maxlength="1" />
    <input class="pin-code-digit pin-code-digit-3" type="text" maxlength="1" />
    <input class="pin-code-digit pin-code-digit-4" type="text" maxlength="1"  />
</div>

<script>
    $(document).ready(function() {
        var pinControl = new PinCodeControl("pinCodeId");
        enterPin = function() {
            window.location = "pin/result?pin=" + pinControl.getValue();
        };
    });

    function resendCode() {
        alert('Resend code');
    }

</script>

<c:if test="${not empty showEnterButton}">
    <a class="go-premium-button go-premium-button-device go-premium-button-target go-premium-body-ok" onclick="enterPin()">
        <span>
            <s:message code='button.enter.title'/>
        </span>
    </a>
</c:if>

<c:if test="${not empty showResendCodeButton}">
    <a class="go-premium-button go-premium-button-device go-premium-button-target go-premium-body-ok" onclick="resendCode()">
        <span>
            <s:message code='button.resend.code.title'/>
        </span>
    </a>
</c:if>

<c:if test="${not empty showBackToTheAppCodeButton}">
    <a class="go-premium-button go-premium-button-device go-premium-button-target go-premium-body-cancel" onclick="returnToApp();">
        <span><s:message code='button.back.to.the.app.title' /></span>
    </a>
</c:if>

<c:if test="${not empty error}">
    ${error}
</c:if>
