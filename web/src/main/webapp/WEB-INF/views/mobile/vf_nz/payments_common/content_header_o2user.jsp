<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<div class="paymentscontainer">
	<div class="tracksHeader S16 vfR">
		<s:message code='pays.page.header.txt.o2consumer_1' /> <span style="font-size: 12px"><s:message code='pays.page.header.txt.o2consumer_2' /></span>
	</div>
</div>

<c:if test="${paymentsPage.userCanGetVideo eq true}">
	<%-- the two lines are only for video enabled users - remove the check if we need them to all o2 users --%>
	<hr class="o2Userhr" />
	<div class="paymentscontainer frL11">
		<div class="paymentscontainer"><s:message code='pays.page.header.txt' /></div>
	</div>
</c:if>
	
<div class="paymentscontainer">
	<c:if test="${paymentsPage.userCanGetVideo eq true}">
		<jsp:include page="../payments_common/content_videooption.jsp" />
	</c:if>
</div>

<hr class="o2Userhr" />

<div class="paymentscontainer">
	<div class="o2UserHeader2">
		<div class="frR15"><s:message code='pays.page.header.txt.o2consumer.billing_1' /></div>
		<div class="frL11"><s:message code='pays.page.header.txt.o2consumer.billing_2' /></div>
	</div>
</div>