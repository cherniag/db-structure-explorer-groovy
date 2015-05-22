<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div class="go-premium-container go-premium-container-device">
    <div class="go-premium-button-target go-premium-button-close go-premium-button-close-device" onclick="returnToApp();">
    </div>
    <div class="go-premium-unsubscribe-banner go-premium-unsubscribe-banner-device error-banner">
    </div>
    <span class="go-premium-body-title go-premium-body-title-device error-title">
        <s:message code="errors.404.title" />
    </span>
    <div class="go-premium-text-message-wrapper go-premium-text-message-wrapper-device error-description">
        <span class="go-premium-text-message go-premium-text-message-device">
            <s:message code="errors.404.description" />
        </span>
        <br/>
    </div>
    <a class="go-premium-button go-premium-button-device go-premium-body-button-below go-premium-button-target go-premium-body-cancel" onclick="returnToApp();">
        <span><s:message code='button.back.to.the.app.title' /></span>
    </a>
</div>
