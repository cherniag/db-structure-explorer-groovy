<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<div class="page-container">
    <div class="vf-nav-header">
    </div>
    <div class="center-container">
        <div class="message">
            <s:message code="enter.phone.reassigned" />
        </div>
    </div>

    <a class="go-premium-button go-premium-button-device go-premium-button-target go-premium-body-cancel" onclick="returnToApp();">
        <span><s:message code='button.cancel.title' /></span>
    </a>
</div>

