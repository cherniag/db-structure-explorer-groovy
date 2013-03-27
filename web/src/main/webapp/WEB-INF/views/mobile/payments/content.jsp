<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div class="header">
    <a href="payments.html" class="button-small button-left"><s:message code='m.page.main.menu.back' /></a>
	<span class="logo"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo.png"/></span>
    <a href="account.html" class="button-small button-right"><s:message code='m.page.main.menu.close' /></a>
</div>
<div class="container">

            <div class="content">
                <h1><s:message code="pays.page.h1.options" /></h1>
                <p><s:message code="pays.page.h1.options.note.o2" /></p>
            </div>

</div>
