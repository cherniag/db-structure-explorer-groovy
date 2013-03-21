<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div class="header">
    <a href="payments.html" class="button-small button-left"><s:message code='m.page.main.menu.back' /></a>
    <span class="logo"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo.png"/></span>
    <a href="account.html" class="button-small button-right"><s:message code='m.page.main.menu.close' /></a>
</div>
<div class="container">
    <c:set var="accountBannerON"> <s:message code="pays.page.note.account.on"/> </c:set>
    <c:if test="${accountBannerON eq 'true'}">
        <c:choose>
            <c:when test="${trialExpiredOrLimited}">
                <div class="pane-red rel">
                    <img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/icon_banner_alert.png"/>
                    <span class="alert-text">${paymentAccountNotes}</span>
                </div>
            </c:when>
            <c:otherwise>
                <div class="banner-pane">
                    <c:if test="${not empty paymentAccountBanner}">
                        <img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}'/>${paymentAccountBanner}" align="middle"/>
                    </c:if>
                    <span>${paymentAccountNotes}</span>
                </div>
            </c:otherwise>
        </c:choose>

    </c:if>
    <div class="content">

        <c:if test="${paymentDetailsByPaymentDto!=null&&paymentDetailsByPaymentDto.activated==false}">
            <h1><s:message code="pays.subscription.header" /></h1>
            <p>
                <c:set var="paymentType">
                    <s:message code="${paymentDetailsByPaymentDto.paymentType}"/>
                </c:set>
                <c:set var="currencyISO">
                    <s:message code="${paymentDetailsByPaymentDto.paymentPolicyDto.currencyISO}"/>
                </c:set>
                <s:message code="pays.subscription.description" arguments='${paymentType},${paymentDetailsByPaymentDto.paymentPolicyDto.subweeks},${currencyISO},${paymentDetailsByPaymentDto.paymentPolicyDto.subcost}'/></p>
            <div class="contentButton formButton rad5 rel" >
                <form action="payments/paymentDetails/${paymentDetailsByPaymentDto.paymentDetailsId}" method="post">
                    <input class="button-small" type="submit" value="<s:message code='pays.activate.submit' />" />
                    <span class="button-arrow"/>
                </form>
            </div>
            <hr />
        </c:if>

        <h1><s:message code="pays.page.h1.options" /></h1>
        <p>${paymentPoliciesNote}</p>
        <hr />
        <div class="setOfButtons">
            <c:forEach var="paymentPolicy" items="${paymentPolicies}">
                <c:if test="${paymentPolicy.paymentType == 'creditCard'}">
                    <c:set var="method_name" value="creditcard" />
                    <s:message code='pays.select.payby.creditcard' var="payment_label" />
                </c:if>
                <c:if test="${paymentPolicy.paymentType == 'PAY_PAL'}">
                    <c:set var="method_name" value="paypal" />
                    <s:message code='pays.select.payby.paypal' var="payment_label" />
                </c:if>
                <c:if test="${paymentPolicy.paymentType == 'PSMS'}">
                    <c:set var="method_name" value="psms" />
                    <s:message code='pays.select.payby.psms' var="payment_label" />
                </c:if>
                <c:if test="${paymentPolicy.paymentType == 'o2Psms'}">
                    <c:set var="method_name" value="o2psms" />
                    <c:set var="payment_label" value="${paymentPolicy.subcost}&#163; for ${paymentPolicy.subweeks} week"/>
                </c:if>
                <c:if test="${paymentPolicy.paymentType == 'iTunesSubscription'}">
                    <c:set var="method_name" value="iTunesSubscription" />
                    <s:message code='pays.select.iTunesSubscription' var="payment_label" />
                </c:if>

                <div class="rel">
                    <c:choose>
                        <c:when test="${nonIOSDevice && paymentPolicy.paymentType == 'iTunesSubscription'}">

                        </c:when>
                        <c:when test="${paymentPolicy.paymentType == 'o2Psms'                        
                        && paymentDetails != null
                        && activePolicy != null
                        && paymentDetails.activated
                        && activePolicy.subcost == paymentPolicy.subcost
                        && activePolicy.subweeks == paymentPolicy.subweeks }">
                            <input class="button-disabled" disabled="true" title="payments/${method_name}.html?paymentPolicyId==${paymentPolicy.id}" type="button" onClick="location.href=this.title" value="<s:message code="${payment_label}" />" />
                            <span class="button-on"/>
                        </c:when>
                        <c:when test="${paymentPolicy.paymentType == 'o2Psms'}">
                            <input class="button-turquoise" title="payments/${method_name}.html?paymentPolicyId=${paymentPolicy.id}" type="button" onClick="location.href=this.title" value="<s:message code="${payment_label}" />" />
                            <span class="button-off"/>
                        </c:when>
                        <c:otherwise>
                            <c:if test="${paymentPolicy.paymentType != 'iTunesSubscription'}">
                                <input class="button-turquoise" title="payments/${method_name}.html?paymentPolicyId=${paymentPolicy.id}" type="button" onClick="location.href=this.title" value="<s:message code="${payment_label}" />" />
                                <span class="button-arrow"/>
                            </c:if>
                        </c:otherwise>
                    </c:choose>
                </div>

            </c:forEach>
        </div>
        <c:if test="${(paymentDetails!=null) && (true==paymentDetails.activated)}">
            <div class="rel" >
                <div class="cross-text"><span>  <s:message code="pays.deactivate.header" />  </span>  </div>
                <input class="button-grey" title="payments/unsubscribe.html" type="button" onClick="location.href=this.title" value="<s:message code='pays.deactivate.submit' />" />
            </div>
        </c:if>

    </div>
</div>
