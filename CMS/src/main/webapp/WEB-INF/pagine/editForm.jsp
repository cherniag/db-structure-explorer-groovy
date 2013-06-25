<%@include file="../include/taglibs.jsp" %>
<%@include file="../include/header.jsp" %>

<title>Edit track</title>

</head>
<body>
<form:form commandName="track" method="post">
	<table>
		<tr>
			<td>Title:<br>
			<form:input path="title" /></td>
			<td><form:errors path="title" cssClass="error" /></td>
		</tr>
		<tr>
			<td>Artist:<br>
			<form:input path="artist" /></td>
			<td><form:errors path="artist" cssClass="error" /></td>
		</tr>
		<tr>
			<td><input type="submit" value="<fmt:message key="save.changes" />" /></td>
		</tr>
                <tr>
                        <td>
                        <%@include file="../include/footer.jsp" %>
                        </td>
		</tr>

	</table>
</form:form>
</body>
</html>
