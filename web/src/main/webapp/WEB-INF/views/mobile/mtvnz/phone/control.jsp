<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>

<div class="root-container">
    <script>
        var enterPhoneNumber = function() {
            window.location = "phone/${endpoint}?phone=" + $('#phone').val();
        }
    </script>

    <div>
        <s:message code="enter.phone.header" />
    </div>

    <input type="text" id="phone" class="mobile-input" placeholder="<s:message code="enter.phone.hint" />" />

    <c:if test="${not empty error}">
        <div class="phone-error-msg">
                ${error}
        </div>
    </c:if>

    <div>
        <s:message code="enter.phone.footer" />
    </div>

    <a class="go-premium-button go-premium-button-device go-premium-button-target go-premium-body-ok" onclick="enterPhoneNumber();">
        <span><s:message code="button.go.title"/></span>
    </a>

</div>

