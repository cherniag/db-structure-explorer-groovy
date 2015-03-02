<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div class="root-container">
    <div class="center-container">
        <img src="${requestScope.assetsPathAccordingToCommunity}imgs/icon_error.png" class="error-logo" />
    </div>

    <div class="center-container">
        <span class="header1">
            <br/>
        </span>
        <span class="header header1">
            <s:message code='error.not.vf.header1' />
        </span>
        <span class="header1">
            <br/><br/>
        </span>
    </div>

    <div class="message">
        <s:message code='error.not.vf.body' />
        <br/><br/>
    </div>

    <div class="message">
        <s:message code='error.not.vf.itunes.body1' />
        <br/><br/>
    </div>

    <div class="message">
        <s:message code='error.not.vf.itunes.body2.${payPalPaymentPolicy.durationUnit}' arguments="${payPalPaymentPolicy.subCost}" />
        <br/><br/>
    </div>

    <a class="go-premium-button go-premium-button-device go-premium-body-button-below go-premium-button-target go-premium-body-ok" href="payments_inapp.html">
        <span><s:message code='button.pay.itunes.title' /></span>
    </a>

    <a class="go-premium-button go-premium-button-device go-premium-button-target go-premium-body-cancel" onclick="returnToApp();">
        <span><s:message code='button.back.to.the.app.title' /></span>
    </a>
</div>