<%--
<a class="go-premium-button go-premium-button-device go-premium-button-target go-premium-body-ok" onclick="returnToApp();">
    <span><s:message code='button.get.listening.title' /></span>
</a>
--%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<div class="go-premium-success">
    <div class="go-premium-success-salutation">
        <span class="header1">
            <br/>
        </span>
        <span class="header header1">
            <s:message code='premium.salutation.line1' arguments="${customerName}"/>
        </span>
        <br/>
        <span class="header header2">
            <s:message code='premium.salutation.line2' />
            <br/>
            <s:message code='premium.salutation.line3' />
        </span>
        <span class="header2">
            <br/>
        </span>
    </div>

    <div class="go-premium-first-half"></div>

    <div class="go-premium-success-avatar-container">
        <img src="${requestScope.assetsPathAccordingToCommunity}imgs/notification_premium.png" class="premium-logo" />
    </div>
    <div class="go-premium-success-ad">
        <s:message code="subscription.end.ad1" />
        <br/>
        <s:message code="subscription.end.ad2" />
        <br/><br/>
        <a class="go-premium-button go-premium-button-device go-premium-button-target go-premium-body-cancel" onclick="returnToApp();">
            <span><s:message code='button.get.listening.title' /></span>
        </a>
    </div>
    <div class="go-premium-second-half"></div>
</div>
