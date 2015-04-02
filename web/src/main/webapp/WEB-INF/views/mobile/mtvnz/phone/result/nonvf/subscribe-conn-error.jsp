<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

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
            <s:message code='error.connection.problem.header1' />
        </span>
        <br/>
        <span class="header header2">
            <s:message code='error.connection.problem.header2' />
            <br/>
        </span>
        <span class="header2">
            <br/>
        </span>
    </div>

    <div class="message">
        <s:message code='error.connection.problem.body' />
    </div>

    <a class="go-premium-button go-premium-button-device go-premium-button-target go-premium-body-cancel" onclick="returnToApp();">
        <span><s:message code='button.back.to.the.app.title' /></span>
    </a>
</div>