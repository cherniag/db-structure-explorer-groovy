<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<script src="${requestScope.assetsPathWithoutCommunity}scripts/utils.js"></script>

Not valid, try again

Enter
<div class="pin-code"
     digitsCount="4"
     name="pin"
     error="Invalid code. Please try again."
></div>

<script>
    var enterPin = function() {
        window.location = "pin/result?pin=" + $('#pin').val();
    };
    $(document).ready(PinCodeControl.init);
</script>

<c:if test="${not empty error}">
    ${error}
</c:if>


<a class="go-premium-button subscribe-button-device go-premium-button-target go-premium-body-ok" onclick="enterPin()">
    <span>Go</span>
</a>




