<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:choose>
    <c:when test="${suweeks == 1}">
        <c:set var="paymentPolicyOptionNo" value="2" />
        <c:set var="imageWeeks" value="2" />
    </c:when>
    <c:when test="${suweeks == 4}">
        <c:set var="paymentPolicyOptionNo" value="1" />
        <c:set var="imageWeeks" value="1" />
    </c:when>
    <c:otherwise>
        <c:set var="paymentPolicyOptionNo" value="0" />
    </c:otherwise>
</c:choose>

<div class="container" style="margin-top: 10px;">
    <img style="width:100%;display: block" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/img_header_payment.png" />
    <div class="maincontainer" style="padding-top: 5px;">
    	
    	<div class="rel tapArea" style="margin:0px 6px 0;">
        <a class="subscription-selector option-${paymentPolicyOptionNo}" disabled="true" style="height: 46px;">
            <img style="width:52px; height:52px;" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/ic_option_${imageWeeks}.png" />
            <div class="rel networkUserPaymentOption" style="padding-top:8px;margin-bottom: 12px;">
                <span class="title"><s:message code='pays.select.payby.networkuserpsms.option${paymentPolicyOptionNo}.title' /></span><br />
                <span class="price">$<fmt:formatNumber pattern="0.00" value="${subcost}" /></span> <s:message code='pays.select.payby.networkuserpsms.option${paymentPolicyOptionNo}.weeks' />
            </div>
        </a>
        </div>
        
        <div class="rel" style="margin: 10px 6px 10px; padding:6px 0;">
            <input class="button-white no-margin left pie" title="${pageContext.request.contextPath}/<%=request.getParameter("callingPage")%>.html" type="button" onClick="location.href=this.title" value="<s:message code="pays.page.options.note.oppsms.cansel.button"/>" />
            <input class="button-turquoise no-margin right pie" title="${pageContext.request.contextPath}/<%=request.getParameter("callingPage")%>/oppsms_confirm.html?paymentPolicyId=${paymentPolicyId}" type="button" onClick="location.href=this.title" value="<s:message code="pays.page.options.note.oppsms.ok.button"/>" />
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
