<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div>
    <s:message code='error.not.vf.body' />
</div>

<div>
    <s:message code='error.not.vf.paypal.body1' />
</div>

<div>
    <s:message code='error.not.vf.paypal.body2.${payPalPaymentPolicy.durationUnit}' arguments="${payPalPaymentPolicy.subCost}" />
</div>

<a class="go-premium-button go-premium-button-device go-premium-body-button-below go-premium-button-target go-premium-body-ok" href="payments_inapp.html">
    <span><s:message code='button.pay.paypal.title' /></span>
</a>

<a class="go-premium-button go-premium-button-device go-premium-button-target go-premium-body-cancel" onclick="returnToApp();">
    <span><s:message code='button.back.to.the.app.title' /></span>
</a>
