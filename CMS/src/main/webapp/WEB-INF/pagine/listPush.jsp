<%@include file="../include/taglibs.jsp"%>
<%@include file="../include/header.jsp"%>

<title>List Push Messages</title>

</head>
<body>
<form:form commandName="data" method="post">
	<table border=1>
		<c:forEach items="${ data.messageData}" var="message" varStatus="s">
		<tr>
			<td><b>Key:</b></td><td colspan=3> <b><c:out value="${ message.key}" /></b></td>
		</tr>
		<tr>
			<td/>
			<td colspan=3>
			<A href="<c:url value="/editPush.erm?id=${ message.key}"/>">Edit</A>
			<A href="<c:url value="/tryPush.erm?id=${ message.key}"/>">Try it</A>
			</td>
		</tr>				
		
		<tr>
			<td/>
			<td>Ticker</td>
			<td>
				<c:out value="${ message.ticker}" /></td>
			</td>
		</tr>
		<tr>
			<td/>
			<td>Title</td>
			<td>
				<c:out value="${ message.title}" /></td>
			</td>
		</tr>
		<tr>
			<td/>
			<td>Body</td>
			<td>
				<c:out value="${ message.body}" /></td>
			</td>
		</tr>
		<tr>
			<td/>
		</tr>
		
		</c:forEach>
		
		<c:forEach items="${ data.weeklyData}" var="message" varStatus="s">
		<tr>
			<td>Key:</td><td colspan=3> <c:out value="${ message.key}" /></td>
		</tr>
		<tr>
			<td/>
			<td colspan=3>
			<A href="<c:url value="/editPush.erm?id=${ message.key}"/>">Edit</A>
			<A href="<c:url value="/tryPush.erm?id=${ message.key}"/>">Try it</A>
			</td>
		</tr>				
		
		<tr>
			<td/>
			<td>Ticker</td>
			<td>
				<c:out value="${ message.ticker}" /></td>
			</td>
		</tr>
		<tr>
			<td/>
			<td>Title</td>
			<td>
				<c:out value="${ message.title}" /></td>
			</td>
		</tr>
		<tr>
			<td/>
			<td>Body</td>
			<td>
				<c:out value="${ message.body}" /></td>
			</td>
		</tr>
		<tr>
			<td/>
		</tr>
				
		</c:forEach>
		


	</table>
</form:form>
</body>
</html>
