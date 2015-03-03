<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>

<div class="root-container">
    <script>
        var enterPhoneNumber = function() {
            window.location = "phone/${endpoint}?phone=" + $('#phone').val();
        };
        $(document).ready(function() {
            $("#phone").keydown(function (e) {
                // Allow: backspace, delete, tab, escape, enter and .
                if ($.inArray(e.keyCode, [46, 8, 9, 27, 13, 110, 190]) !== -1 ||
                            // Allow: Ctrl+A
                        (e.keyCode == 65 && e.ctrlKey === true) ||
                            // Allow: home, end, left, right, down, up
                        (e.keyCode >= 35 && e.keyCode <= 40)) {
                    // let it happen, don't do anything
                    return;
                }
                // Ensure that it is a number and stop the keypress
                if ((e.shiftKey || (e.keyCode < 48 || e.keyCode > 57)) && (e.keyCode < 96 || e.keyCode > 105)) {
                    e.preventDefault();
                }
            });
        });
    </script>

    <div class="message">
        <s:message code="enter.phone.header" />
    </div>

    <div class="mobile-input-wrapper">
        <input type="text"
               id="phone"
               class="mobile-input <c:if test="${not empty error}">mobile-input-error</c:if>" placeholder="<s:message code="enter.phone.hint" />"
               maxlength="10"
               value="${phone}"
        />
    </div>

    <c:if test="${not empty error}">
        <div class="message error">
                ${error}
        </div>
    </c:if>

    <br />
    <div class="message message-terms-and-conditions">
        <s:message code="enter.phone.footer" />
    </div>

    <a class="go-premium-button go-premium-button-device go-premium-button-target go-premium-body-ok" onclick="enterPhoneNumber();">
        <span><s:message code="button.go.title"/></span>
    </a>

</div>

