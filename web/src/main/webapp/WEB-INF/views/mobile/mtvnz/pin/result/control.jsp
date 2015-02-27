<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<script src="${requestScope.assetsPathWithoutCommunity}scripts/utils.js"></script>

<c:if test="${reassigned}">
    <c:set var="key" value="reassigned" />
</c:if>

<div class="message">
    <s:message code='enter.pin.header' arguments='${phone}'/>
</div>

<br/>
<br/>

<div class="message">
    <s:message code='enter.pin.footer'/>
</div>

<br/>

<div id="pinCodeId" class="pin-code-wrapper">
    <input class="pin-code-digit pin-code-digit-1" type="text" maxlength="1" />
    <input class="pin-code-digit pin-code-digit-2" type="text" maxlength="1" />
    <input class="pin-code-digit pin-code-digit-3" type="text" maxlength="1" />
    <input class="pin-code-digit pin-code-digit-4" type="text" maxlength="1" />
</div>

<div class="message error">
    <c:if test="${check == false}">
        <s:message code="enter.pin.error.notValid" />
    </c:if>
    <c:if test="${not empty maxAttemptsReached}">
        <s:message code="enter.pin.error.maxAttempts" />
    </c:if>
</div>

<script>
    $(document).ready(function() {
        var pinControl = new PinCodeControl("pinCodeId");
        enterPin = function() {
            window.location = "pin/result?pin=" + pinControl.getValue() + "&phone=" + ${phone} + "&key=${key}";
        };
    });

    function resendCode() {
        window.location = "pin/resend?phone=" + ${phone};
    }

</script>

<c:choose>
    <c:when test="${empty maxAttemptsReached}">
        <a class="go-premium-button go-premium-button-device go-premium-button-target go-premium-body-ok" onclick="enterPin()">
            <span>
                <s:message code='button.enter.title'/>
            </span>
        </a>
        <a class="go-premium-button go-premium-button-device go-premium-button-target go-premium-body-ok" onclick="resendCode()">
            <span>
                <s:message code='button.resend.code.title'/>
            </span>
        </a>
    </c:when>
    <c:otherwise>
        <a class="go-premium-button go-premium-button-device go-premium-button-target go-premium-body-cancel" onclick="returnToApp();">
            <span><s:message code='button.back.to.the.app.title' /></span>
        </a>
    </c:otherwise>
</c:choose>

