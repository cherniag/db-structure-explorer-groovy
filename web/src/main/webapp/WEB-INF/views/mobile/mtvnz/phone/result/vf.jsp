<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<script src="${requestScope.assetsPathWithoutCommunity}scripts/utils.js"></script>

<div class="subscription_root_container">
    Please confirm for ${phone}.

    Enter
    <div class="pin-code"
         digitsCount="4"
         name="pin"
         <%--value="5653"--%>
         <%--error="You have reached the daily pin codes limit.<br/>Please try again tomorrow."--%>
    ></div>

    <script>
        var enterPin = function() {
            window.location = "pin/result?pin=" + $('#pin').val();
        };
        $(document).ready(PinCodeControl.init);
    </script>

    <a class="go-premium-button subscribe-button-device go-premium-button-target go-premium-body-ok" onclick="enterPin()">
        <span>Go</span>
    </a>
</div>
