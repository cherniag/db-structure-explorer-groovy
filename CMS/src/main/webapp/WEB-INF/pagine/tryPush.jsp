<%@include file="../include/taglibs.jsp"%>
<%@include file="../include/header.jsp"%>

<title>Try Push Messages</title>

</head>
<body>
<form:form commandName="data" method="post">
	<table border=1>
		<tr>
			<td>Key:</td><td colspan=3> <c:out value="${ data.key}" /></td>
		</tr>
		
		<tr>
			<td>IMEI or MAC</td>
			<td>
				<form:textarea path="uid" rows="1" cols="20"/></td>
			</td>
			<td>
			      IMEI_XXXXXXXXX or MAC_XXXXXXXXXX
			</td>
		</tr>


	</table>
	<input type="submit" value="Send" />
</form:form>
</body>
</html>
