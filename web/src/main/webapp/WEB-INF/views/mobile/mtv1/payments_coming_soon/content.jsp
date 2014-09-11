<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<div class="coming_soon_container">
    <div class="content_container">
        <a href="${pageContext.request.contextPath}/account.html" class="close_button_small"></a>
        <div class="logo_holding" >
            <img style="vertical-align: middle;" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo_holding.png" />
        </div>
        <div class="img_holding">
            <img style="vertical-align: middle;" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/img_holding.png" />
        </div>
        <div class="relax">
            <s:message code="relax.message" />
        </div>
        <div class="enjoy">
            <s:message code="enjoy.message" />
        </div>
        <div class="close_button_big">
            <button title="${pageContext.request.contextPath}/account.html" onClick="location.href=this.title"><s:message code="close.message" /></button>
        </div>
    </div>
</div>
