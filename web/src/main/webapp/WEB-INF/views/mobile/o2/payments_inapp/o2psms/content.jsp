<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div class="header pie">
    <a href="payments_inapp.html" class="button-small button-left"><s:message code='m.page.main.menu.back' /></a>
    <span class="logo"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo.png" alt="" /></span>
</div>
<div class="container">
    <div class="content">
        <h1><s:message code="pays.page.options.note.o2psms.title"/></h1>
        <p class="centered"><s:message code="pays.page.options.note.o2psms" arguments="${subcost}, ${suweeks}"/></p>
        <div class="rel" style="margin-top: 20px">
            <input class="button-turquoise pie" title="${pageContext.request.contextPath}/payments_inapp/o2psms_confirm.html?paymentPolicyId=${paymentPolicyId}" type="button" onClick="location.href=this.title" value="<s:message code="pays.page.options.note.o2psms.ok.button"/>" />
            <input class="button-grey pie" title="${pageContext.request.contextPath}/payments_inapp.html" type="button" onClick="location.href=this.title" value="<s:message code="pays.page.options.note.o2psms.cansel.button"/>" />
        </div>
    </div>
</div>