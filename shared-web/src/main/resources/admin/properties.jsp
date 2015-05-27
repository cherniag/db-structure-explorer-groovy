<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="mobi.nowtechnologies.common.util.LocaleUtils" %>
<%@ page import="java.util.Locale" %>
<%@ page import="org.springframework.beans.support.ArgumentConvertingMethodInvoker" %>
<%@ page import="org.springframework.context.support.ReloadableResourceBundleMessageSource" %>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
    String requestedURI = request.getRequestURI();
    String bundleName = requestedURI.substring(requestedURI.lastIndexOf('/') + 1, requestedURI.length());
    String community = request.getParameter("community");
    String language = request.getParameter("language");
    String country = request.getParameter("country");
    Locale locale = LocaleUtils.buildLocale(community, language, country);
    Object properties = null;

    if ("application.properties".equals(bundleName) && context.containsBean("propertyPlaceholderConfigurer")) {
        properties = getValue(context.getBean("propertyPlaceholderConfigurer"), "mergeProperties");
    }

    if ("services.properties".equals(bundleName) && context.containsBean("serviceReloadableResourceBundleMessageSource")) {
        Object propertiesHolder = getValue(context.getBean("serviceReloadableResourceBundleMessageSource"), "getMergedProperties", locale);
        properties = getValue(propertiesHolder, "getProperties");
    }

    if ("messages.properties".equals(bundleName) && context.containsBean("messageSource")) {
        Object messageSource = context.getBean("messageSource");
        if(messageSource instanceof ReloadableResourceBundleMessageSource){
            Object propertiesHolder = getValue(messageSource, "getMergedProperties", locale);
            properties = getValue(propertiesHolder, "getProperties");
        }
    }

    request.setAttribute("locale", locale.toString());
    request.setAttribute("bundleName", bundleName);
    request.setAttribute("properties", properties);
%>

<%!
    public Object getValue(Object targetObject, String targetMethod, Object... arguments) throws Exception {
        ArgumentConvertingMethodInvoker invoker = new ArgumentConvertingMethodInvoker();
        invoker.setTargetObject(targetObject);
        invoker.setTargetMethod(targetMethod);
        invoker.setArguments(arguments);
        invoker.prepare();
        return invoker.invoke();
    }
%>

<html>
<head>
    <title>Properties</title>
    <style>
        body { padding: 20px; }
        .p-table { border: #C7E0F1 1px solid;  border-collapse: collapse;  cursor: default; }
        .p-table td { border-left: #C7E0F1 1px solid;  padding: 8px; }
        .p-table thead tr { background: #C7E0F1;  font-size: 20px; }
        .p-table tbody tr:nth-child(even) { background: #ECF4FF; }
        .p-table tbody tr:nth-child(odd) { background: #FFFFFF; }
        .p-table tbody td:last-child { cursor: pointer; }
        .p-table tbody td:last-child:hover { outline: #65cbea 2px solid; }
        .p-table textarea { width: 100%; }
    </style>
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>

    <script type="text/javascript">
        $(document).ready(function () {
            $('.p-table tbody td:last-child').click(propertyValueHolderOnClick);
        });

        function propertyValueHolderOnClick() {
            var cell = $(this);
            if (cell.attr('mode') == 'html') {
                cell.attr('mode', 'input');
                cell.html('<textarea>' + cell.html() + '</textarea>');
                var textarea = cell.find('textarea'), oldText = textarea.text();
                textarea.height(textarea[0].scrollHeight);
                textarea.on('blur', function () {
                    propertyValueHolderOnBlur(cell, textarea, oldText);
                });
                textarea.focus();
            }
        }

        function propertyValueHolderOnBlur(cell, textarea, oldText) {
            cell.attr('mode', 'html');
            if (oldText != textarea.val() && confirm("Change property value?")) {
                cell.html(textarea.val());
                changePropertyValue(cell.parent().find('.key').html(), textarea.val(), '${bundleName}', '${locale}');
            } else {
                cell.html(oldText);
            }
        }

        function changePropertyValue(key, value, bundleName, locale) {
            alert('Do change property [' + key + ']\nSet value [' + value + ']\nBundle [' + bundleName + ']\nLocale ['+locale+']');
        }
    </script>

</head>
<body>
<c:choose>
    <c:when test="${not empty properties}">
        Bundle: ${bundleName}<br/>
        <c:if test="${not empty locale}">Community: ${locale}<br/></c:if>
        <table class="p-table">
            <thead><tr>     <th>#</th>     <th>Key</th>     <th>Value</th>     </tr></thead>
            <tbody>
            <c:forEach var="prop" items="${properties}" varStatus="loop">
                <tr>
                    <td align="center">${loop.index+1}</td>
                    <td class="key">${prop.key}</td>
                    <td class="value" mode="html">${prop.value}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:when>
    <c:otherwise>
        Properties bean for <b><c:out value="${bundleName}"/></b> not found.
    </c:otherwise>
</c:choose>
</body>
</html>
