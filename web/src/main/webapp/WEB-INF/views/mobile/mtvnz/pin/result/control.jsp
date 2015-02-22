<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<script src="${requestScope.assetsPathWithoutCommunity}scripts/utils.js"></script>

<div id="pinCodeId" class="pin-code">
    <input class="pin-code-digit" type="text" maxlength="1" style="left: 0%;" />
    <input class="pin-code-digit" type="text" maxlength="1" style="left: 25%;" />
    <input class="pin-code-digit" type="text" maxlength="1" style="left: 50%;" />
    <input class="pin-code-digit" type="text" maxlength="1" style="left: 75%;" />
</div>

<script>
    $(document).ready(function() {
        var pinControl = new PinCodeControl("pinCodeId");
        enterPin = function() {
            window.location = "pin/result?pin=" + pinControl.getValue();
        };
    });
</script>

<c:if test="${not empty showEnterButton}">
    <a class="go-premium-button subscribe-button-device go-premium-button-target go-premium-body-ok" onclick="enterPin()">
        <span>Enter</span>
    </a>
</c:if>

<c:if test="${not empty showResendCodeButton}">
    <a class="go-premium-button subscribe-button-device go-premium-button-target go-premium-body-cancel" onclick="enterPin()">
        <span>Resend Code</span>
    </a>
</c:if>

<c:if test="${not empty showBackToTheAppCodeButton}">
    <a class="go-premium-button subscribe-button-device go-premium-button-target go-premium-body-ok" onclick="enterPin()">
        <span>Back to The app</span>
    </a>
</c:if>

<c:if test="${not empty error}">
    ${error}
</c:if>
