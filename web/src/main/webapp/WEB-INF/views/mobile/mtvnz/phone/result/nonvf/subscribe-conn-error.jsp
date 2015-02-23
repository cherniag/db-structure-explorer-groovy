<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<h2>
    <s:message code='error.connection.problem.header1' />
</h2>
<h3>
    <s:message code='error.connection.problem.header2' />
</h3>

<div>
    <s:message code='error.connection.problem.body' />
</div>

<a class="go-premium-button go-premium-button-device go-premium-button-target go-premium-body-cancel" onclick="returnToApp();">
    <span><s:message code='button.back.to.the.app.title' /></span>
</a>
