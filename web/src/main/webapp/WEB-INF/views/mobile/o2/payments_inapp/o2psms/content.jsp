<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div class="header pie">
    <a href="payments_inapp.html" class="button-small button-left"><s:message code='m.page.main.menu.back' /></a>
    <span class="logo" style="padding-right: 49px;"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo.png" alt="" /></span>
</div>

<c:choose>
    <c:when test="${suweeks == 1}">
        <c:set var="paymentPolicyOptionNo" value="3" />
        <c:set var="imageWeeks" value="3" />
    </c:when>
    <c:when test="${suweeks == 2}">
        <c:set var="paymentPolicyOptionNo" value="2" />
        <c:set var="imageWeeks" value="2" />
    </c:when>
    <c:when test="${suweeks == 3}">
        <c:set var="paymentPolicyOptionNo" value="4" />
        <c:set var="imageWeeks" value="1" />
    </c:when>
    <c:when test="${suweeks == 5}">
        <c:set var="paymentPolicyOptionNo" value="1" />
        <c:set var="imageWeeks" value="1" />
    </c:when>
    <c:otherwise>
        <c:set var="paymentPolicyOptionNo" value="0" />
    </c:otherwise>
</c:choose>

<div class="container">
    <img style="width:100%;" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/img_header_payment.png" />
    <div class="subscription-container">
        <a class="subscription-selector option-${paymentPolicyOptionNo}" disabled="true">
            <img style="width:66px; height:66px;" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/ic_option_${imageWeeks}.png" />
            <div class="rel" style="padding-top:8px;margin-bottom: 12px;">
                <span class="title"><s:message code='pays.select.payby.o2psms.option${paymentPolicyOptionNo}.title' /></span><br />
                <span class="price">&#163;<fmt:formatNumber pattern="0.00" value="${subcost}" /></span> <s:message code='pays.select.payby.o2psms.option${paymentPolicyOptionNo}.weeks' />
            </div>
        </a>
        
        <div class="rel" style="margin:12px 6px; padding:6px 0; border-top: 1px solid #a0a0a0">
            <input class="button-grey no-margin left pie" title="${pageContext.request.contextPath}/payments_inapp.html" type="button" onClick="location.href=this.title" value="<s:message code="pays.page.options.note.o2psms.cansel.button"/>" />
            <input class="button-turquoise no-margin right pie" title="${pageContext.request.contextPath}/payments_inapp/o2psms_confirm.html?paymentPolicyId=${paymentPolicyId}" type="button" onClick="location.href=this.title" value="<s:message code="pays.page.options.note.o2psms.ok.button"/>" />
            <div style="clear: both;"></div>
        </div>
        
    </div>

    <div class="terms rel">
        By tapping on subscribe you will be accepting our<br />
        <a href="${pageContext.request.contextPath}/terms.html">Terms & Conditions</a>
    </div>

    <div class="rel" style="text-align: center; margin-top: 15px;">
        <img width="79" height="12" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/label_secure_payment.png"/>
    </div>
    
</div>
