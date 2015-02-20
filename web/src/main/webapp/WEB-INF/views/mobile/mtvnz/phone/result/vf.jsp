<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<script src="${requestScope.assetsPathWithoutCommunity}scripts/utils.js"></script>

<div class="subscription_root_container">
    Please confirm for ${phone}.

    Enter
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

    <a class="go-premium-button subscribe-button-device go-premium-button-target go-premium-body-ok" onclick="enterPin()">
        <span>Go</span>
    </a>
</div>
