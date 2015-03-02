<%--
<a class="go-premium-button go-premium-button-device go-premium-button-target go-premium-body-ok" onclick="returnToApp();">
    <span><s:message code='button.get.listening.title' /></span>
</a>
--%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="go-premium-success">
    <div class="go-premium-first-half"></div>
    <img src="${requestScope.assetsPathAccordingToCommunity}imgs/icon_error.png" class="success-logo" />
    <div class="go-premium-second-half"></div>
</div>