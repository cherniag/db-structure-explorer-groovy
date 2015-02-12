<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>




<div class="subscription_root_container">
    Enter
    <input type="text" id="phone" />

    <script>
        var enterPhoneNumber = function() {
            window.location = "phone/result?phone=" + $('#phone').val();
        }
    </script>


    <a class="go-premium-button subscribe-button-device go-premium-button-target go-premium-body-ok" onclick="enterPhoneNumber()">
        <span>Go</span>
    </a>

</div>

