<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>


<c:forEach var="paymentPolicy" items="${paymentPolicies}">
    <c:if test="${paymentPolicy.paymentType == 'PSMS'}">
        <c:set var="method_name" value="psms" />
    </c:if>
    <c:if test="${paymentPolicy.paymentType == 'o2Psms'}">
        <c:set var="method_name" value="o2psms" />
    </c:if>
    
	<c:choose>
	    <c:when test="${paymentPolicy.subweeks == 1}">
	        <c:set var="paymentPolicyOptionNo" value="3" />
	        <c:set var="imageWeeks" value="3" />
	    </c:when>
	    <c:when test="${paymentPolicy.subweeks == 2}">
	        <c:set var="paymentPolicyOptionNo" value="2" />
	        <c:set var="imageWeeks" value="2" />
	    </c:when>
	    <c:when test="${paymentPolicy.subweeks == 3}">
	        <c:set var="paymentPolicyOptionNo" value="4" />
	        <c:set var="imageWeeks" value="1" />
	    </c:when>
	    <c:when test="${paymentPolicy.subweeks == 5}">
	        <c:set var="paymentPolicyOptionNo" value="1" />
	        <c:set var="imageWeeks" value="1" />
	    </c:when>
	    <c:otherwise>
	        <c:set var="paymentPolicyOptionNo" value="0" />
	    </c:otherwise>
	</c:choose>
	
	<div class="rel tapArea" data-hasvideo="${paymentPolicy.videoAndAudio4GSubscription ? '1' : '0'}" id="paymentOption${paymentPolicy.id}">
		
	   	<c:set var="disabledAttrib" />
	   	<c:set var="buttonStyle" value="button-off" />
	   	<c:set var="hrefValue">href="${pageContext.request.contextPath}/<%=request.getParameter("callingPage")%>/${method_name}.html?paymentPolicyId=${paymentPolicy.id}"</c:set>
	   	<c:if test="${paymentDetails != null && activePolicy != null && paymentDetails.activated && activePolicy.subcost == paymentPolicy.subcost && activePolicy.subweeks == paymentPolicy.subweeks}">
	   		<c:set var="disabledAttrib">disabled="true"</c:set>
	   		<c:set var="buttonStyle" value="button-on" />
	   		<c:set var="hrefValue" value="" />
	   	</c:if>
	   	
	    <a class="subscription-selector option-${paymentPolicyOptionNo}" type="button" ${disabledAttrib} ${hrefValue} style="height: 46px;">
			<img width="51px" height="51px" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/ic_option_${imageWeeks}.png" />
	        <div class="rel o2UserPaymentOption">
	            <div class="title"><s:message code='pays.select.payby.o2psms.option${paymentPolicyOptionNo}.title' /></div>
	            <span class="price">&#163;<fmt:formatNumber pattern="0.00" value="${paymentPolicy.subcost}" /></span> <s:message code='pays.select.payby.o2psms.option${paymentPolicyOptionNo}.weeks' />
	        </div>
	        <span class="${buttonStyle}"></span>
		</a>
	</div>
	    
</c:forEach>
<div style="height: 5px">&nbsp;</div><%--clearfix --%>