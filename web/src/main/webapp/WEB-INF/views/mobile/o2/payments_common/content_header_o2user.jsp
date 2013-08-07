<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div style="padding-top: 16px; padding-left: 10px;">
	<div class="frR15">
		<s:message code='pays.page.header.txt.o2consumer_1' /> <span style="font-size: 12px"><s:message code='pays.page.header.txt.o2consumer_2' /></span>
	</div>
</div>

<c:if test="${userCanGetVideo eq true}">
	<jsp:include page="../payments_common/content_videooption.jsp" />
</c:if>

<div style="padding-left: 10px; padding-bottom: 20px">
	<hr style="color: #ddd; margin: 10px 0 10px 0" />

	<div class="frR15" style="line-height: 16px"><s:message code='pays.page.header.txt.o2consumer.billing_1' /></div>
	<div class="frL11" style="margin-top: 7px;"><s:message code='pays.page.header.txt.o2consumer.billing_2' /></div>
</div>