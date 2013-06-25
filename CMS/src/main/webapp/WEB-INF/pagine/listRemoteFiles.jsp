<%@include file="../include/taglibs.jsp"%>
<%@include file="../include/header.jsp"%>

<title>Track list</title>
<script language='javascript'>
            function confirmDelete(url)    {
                if (confirm("<fmt:message key="label.person.confirmDelete" />"))  {
                    document.location=url;
                }
            }
            
	</script>
</head>
<body>

<form:form commandName="data" method="post" >
	<table>
	<tr>
		<th>Transfert</th>
		<th>Name</th>
	</tr>
	<c:forEach items="${ data.files}" var="file" varStatus="status">
		<tr class="<c:choose>
				<c:when test="${ s.index%2==0}">
					even
				</c:when>
				<c:otherwise>
					odd
				</c:otherwise>
				</c:choose>
				">
			<td><form:checkbox path="files[${status.index}].selected" id="${ status.index}"/></td>
			<td><c:out value="${ file.name}" /></td>
		</tr>
	</c:forEach>



		<tr>
			<td><input type="submit" value="Transfert"></td>
		</tr>
		<tr>
			<td><%@include file="../include/footer.jsp"%>
			</td>
		</tr>

	</table>
</form:form>
</body>
</html>
