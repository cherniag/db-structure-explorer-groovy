<%@include file="../include/taglibs.jsp"%>
<%@include file="../include/header.jsp"%>

<title>Edit Push Messages</title>

</head>
<body>
<form:form commandName="data" method="post">
	<table>
		<tr>
			<td>Key:</td><td colspan=3> <c:out value="${ data.editData.key}" /></td>
		</tr>
		<tr>
			<td/>
			<td>Ticker</td>
			<td>
				<form:textarea path="editData.ticker" rows="1" cols="100"/></td>
			</td>
		</tr>
		<tr>
			<td/>
			<td>Title</td>
			<td>
				<form:textarea path="editData.title" rows="1" cols="100"/></td>
			</td>
		</tr>
		<tr>
			<td/>
			<td>Body</td>
			<td>
				<form:textarea path="editData.body" rows="3" cols="100"/></td>
			</td>
		</tr>


	</table>
	<input type="submit" value="<fmt:message key="save.changes" />" />
</form:form>
</body>
</html>
