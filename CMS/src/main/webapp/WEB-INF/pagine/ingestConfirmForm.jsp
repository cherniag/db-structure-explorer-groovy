<%@include file="../include/taglibs.jsp"%>
<%@include file="../include/header.jsp"%>

<title>Ingest Tracks</title>

</head>
<body>
<form:form commandName="data" method="post" >

The Following tracks will not be ingested and content be deleted from the drop
	<table>
	<tr>
		<th>Product id</th>
		<th>Action</th>
		<th>Artist</th>
		<th>Title</th>
		<th>ISRC</th>
		<th>Existing</th>
		<th nowrap> </th>
	</tr>
	<c:forEach items="${ data.dropdata.drops}" var="drop" varStatus="dropstatus">
	<c:forEach items="${ drop.ingestdata.data}" var="track" varStatus="status">
	
			<c:if test="${track.ingest == false}">
		<tr class="<c:choose>
				<c:when test="${ s.index%2==0}">
					even
				</c:when>
				<c:otherwise>
					odd
				</c:otherwise>
				</c:choose>
				">
				
			<td><c:out value="${ track.productCode}" /></td>
			<td><c:out value="${ track.type}" /></td>
			<td><c:out value="${ track.artist}" /></td>
			<td><c:out value="${ track.title}" /></td>
			<td><c:out value="${ track.ISRC}" /></td>
			<td><c:out value="${ track.exists}" /></td>
		</tr>
			</c:if>
	</c:forEach>
	</c:forEach>

</table>


		<tr>
			<td><input type="submit" value="Ingest" name="_finish"></td>
		</tr>
		<tr>
			<td><%@include file="../include/footer.jsp"%>
			</td>
		</tr>

	</table>
</form:form>
</body>
</html>
