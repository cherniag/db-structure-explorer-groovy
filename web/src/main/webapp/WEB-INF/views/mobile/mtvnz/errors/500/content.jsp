<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>

<div class="page-container">
    <img src="${requestScope.assetsPathAccordingToCommunity}imgs/icon_close_72.png" class="go-premium-button-target go-premium-button-close" onclick="returnToApp();" />

    <div class="center-container">
        <img src="${requestScope.assetsPathAccordingToCommunity}imgs/icon_error.png" class="error-logo" />
    </div>

    <div class="center-container">
        <span class="header1">
            <br/>
        </span>
        <span class="header header1">
            <s:message code="errors.500.title" />
        </span>
        <br/>
        <span class="header1">
            <br/>
        </span>
    </div>

    <div class="message">
        <s:message code="errors.500.description" />
    </div>

    <a class="go-premium-button go-premium-button-device go-premium-button-target go-premium-body-cancel" onclick="returnToApp();">
        <span><s:message code='button.back.to.the.app.title' /></span>
    </a>
</div>

