<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div class="header">
    <div class="gradient_border">&#160;</div>
    <span class="logo"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo.png" alt="" /></span>
    <div class="buttonBox">
        <span class="arrow">&nbsp;</span>
        <a href="payments.html" class="button3"><s:message code='m.page.main.menu.back' /></a>
    </div>
</div>
<div class="container">
    <div class="content">
        <h1>Subscription</h1>
        <p><s:message code="pays.page.options.note.o2psms"/></p>

        <div class="rel">
            <input class="button-turquoise" type="submit" value="Yes" />
            <input class="button-grey" title="payments.html" type="button" onClick="location.href=this.title" value="Cansel" />
        </div>
    </div>
</div>