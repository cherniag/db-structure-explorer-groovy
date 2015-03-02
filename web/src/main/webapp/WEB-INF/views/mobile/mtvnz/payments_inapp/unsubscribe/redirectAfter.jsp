<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div class="root-container">
    <div class="center-container">
        <img src="${requestScope.assetsPathAccordingToCommunity}imgs/notification_premium.png" class="premium-logo" />
    </div>

    <div class="center-container">
        <span class="header2">
            <br/>
        </span>
        <span class="header header2">
            <s:message code='unsubscribe.step3.header' />
        </span>
        <span class="header2">
            <br/><br/>
        </span>
    </div>

    <div class="message">
        <s:message code='unsubscribe.step3.body' />
    </div>

    <a class="go-premium-button go-premium-button-device go-premium-button-target go-premium-body-cancel" onclick="returnToApp();">
        <span><s:message code='button.back.to.the.app.title' /></span>
    </a>
    <s:message code="feedback.email" var="feedbackEmail" />
    <a class="go-premium-button go-premium-button-device go-premium-button-target go-premium-body-ok" onclick="feedback('${feedbackEmail}')">
        <span><s:message code='button.feedback.title' /></span>
    </a>
</div>

