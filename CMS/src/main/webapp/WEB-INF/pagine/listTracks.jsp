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
<form:form commandName="searchCommand" method="POST">
	<table>
		<tr>
			<td>Title:<br>
			<form:input path="title" /></td>
		</tr>
		<tr>
			<td>Artist:<br>
			<form:input path="artist" /></td>
		</tr>
		<tr>
			<td>ISRC:<br>
			<form:input path="ISRC" /></td>
		</tr>
		<tr>
			<td>Label/Distrobutor:<br>
			<form:input path="label" /></td>
		</tr>
		<tr>
			<td>Ingestor:<br>
			<form:input path="ingestor" /></td>
		</tr>
		<tr>
			<td>Ingested from (DD/MM/YY):<br>
			<form:input path="ingestFrom" /></td>
		</tr>
		<tr>
			<td>Ingested to (DD/MM/YY):<br>
			<form:input path="ingestTo" /></td>
		</tr>
		<tr>
			<td><input type="submit"
				value="Search" /></td>
		</tr>
	</table>
</form:form>
</div>

<table>
	<tr>
		<th>ProductId</th>
		<th>Artist</th>
		<th>Title</th>
		<th>Sub Title</th>
		<th>ISRC</th>
		<th>Ingestion date</th>
		<th>Update date</th>
		<th>Publish date</th>
		<th>Territories</th>
		<th nowrap></th>
	</tr>
	<c:forEach items="${ tracks}" var="track" varStatus="s">
		<tr
			class="<c:choose>
				<c:when test="${ s.index%2==0}">
					even
				</c:when>
				<c:otherwise>
					odd
				</c:otherwise>
				</c:choose>
				">
			<td><c:out value="${ track.productId}" /></td>
			<td><c:out value="${ track.artist}" /></td>
			<td><c:out value="${ track.title}" /></td>
			<td><c:out value="${ track.subTitle}" /></td>
			<td><c:out value="${ track.ISRC}" /></td>
			<td><fmt:formatDate type="date" value="${track.ingestionDate}" /></td>
			<td><fmt:formatDate type="date"
				value="${track.ingestionUpdateDate}" /></td>
			<td><fmt:formatDate type="date" value="${track.publishDate}" /></td>
			<td>
				<c:forEach items="${ track.territories}" var="territory" varStatus="st">
				<c:out value="${territory.code }"/>
				</c:forEach>
			</td>

			<td><A href="<c:url value="/publish.erm?id=${ track.id}"/>">Publish</A></td>
			<td><A href="<c:url value="/details.erm?id=${ track.id}"/>">Details</A></td>
			<td><A href="<c:url value="/myform.erm?id=${ track.id}"/>"><fmt:message
				key="label.edit" /></A></td>
			<td nowrap></td>
			<td><A
				href="javascript:confirmDelete('<c:url value="/delete.erm?id=${ track.id}"/>')"><fmt:message
				key="label.delete" /></A></td>
		</tr>
	</c:forEach>
	<tr>
		<td colspan="10"><%@include file="../include/footer.jsp"%>
		</td>
	</tr>

</table>
</body>
</html>
