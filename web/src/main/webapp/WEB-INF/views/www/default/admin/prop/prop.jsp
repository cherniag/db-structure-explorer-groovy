<html xmlns="http://www.w3.org/1999/xhtml">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<head lang="en">
    <meta charset="UTF-8"/>
    <title>Props</title>
</head>
<body>
<div>
    <a href="signout">Sign Out</a>
    <h1>Actual properties:</h1>
    <c:if test="${not empty locale}">
        <strong>Locale: </strong><label>${locale}</label>
    </c:if>
    <br/>
    <c:if test="${not empty properties}">
    <table class="table">
        <thead>
        <tr>
            <th>#</th>
            <th>Key</th>
            <th>Value</th>
        </tr>
        </thead>
        <tbody>
        <form id="propEditForm" action="props">
            <c:set var="count" value="1"/>
            <c:forEach var="prop" items="${properties}">
                <tr>
                    <td>${count}</td>
                    <c:set var="count" value="${count+1}"/>
                    <td>${prop.key}</td>
                    <td>${prop.value}</td>
                </tr>
            </c:forEach>
        </form>
        </tbody>
    </table>
    </c:if>
</div>
</body>
</html>