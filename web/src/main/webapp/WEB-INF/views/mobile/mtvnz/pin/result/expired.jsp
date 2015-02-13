<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

Expired ...

Enter
<input type="text" id="pin" />

<script>
    var enterPin = function() {
        window.location = "pin/result?pin=" + $('#pin').val();
    }
</script>

<c:if test="${not empty error}">
    ${error}
</c:if>


<a class="go-premium-button subscribe-button-device go-premium-button-target go-premium-body-ok" onclick="enterPin()">
    <span>Go</span>
</a>



