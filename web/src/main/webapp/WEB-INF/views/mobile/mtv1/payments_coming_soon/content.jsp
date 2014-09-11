<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<div class="holding_page_main_container">
    <div class="holding_page_content_container">
        <div class="holding_page_close_button_small">
            <button title="${pageContext.request.contextPath}/account.html" onClick="location.href=this.title"></button>
        </div>
        <div class="holding_page_logo" >
            <img style="vertical-align: middle;" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo_holding.png" />
        </div>
        <div class="holding_page_img">
            <img style="vertical-align: middle;" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/img_holding.png" />
        </div>
        <div class="holding_page_relax">
            <s:message code="holding.page.relax.message" />
        </div>
        <div class="holding_page_enjoy">
            <s:message code="holding.page.enjoy.message" />
        </div>
        <div class="holding_page_close_button_big">
            <button title="${pageContext.request.contextPath}/account.html" onClick="location.href=this.title"><s:message code="holding.page.close.message" /></button>
        </div>
    </div>
</div>

