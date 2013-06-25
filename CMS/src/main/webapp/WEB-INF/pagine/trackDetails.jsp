<%@include file="../include/taglibs.jsp"%>
<%@include file="../include/header.jsp"%>

<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.1/jquery.min.js"></script>
<script type="text/javascript" src="http://malsup.github.com/jquery.media.js"></script>

<script type="text/javascript">
    $(function() {
        $('a.media').media( { width: 300, height: 20 } );
    });
</script>
<title>Track details</title>
</head>
<body>
<table>
	<tr class="even">
		<td >Track title</td>
		<td><c:out value="${ track.title}" /></td>
	</tr>
	<tr class="even">
		<td >Track subtitle</td>
		<td><c:out value="${ track.subTitle}" /></td>
	</tr>
	<tr class="odd">
		<td>Artist</td>
		<td><c:out value="${ track.artist}" /></td>
	</tr>
	<tr class="even">
		<td>Album</td>
		<td><c:out value="${ track.album}" /></td>
	</tr>
	<tr class="odd">
		<td>Year</td>
		<td><c:out value="${ track.year}" /></td>
	</tr>
	<tr class="even">
		<td>Genre</td>
		<td><c:out value="${ track.genre}" /></td>
	</tr>
	<tr class="odd">
		<td>Copyright</td>
		<td><c:out value="${ track.copyright}" /></td>
	</tr>
	<tr class="even">
		<td>Track ISRC</td>
		<td><c:out value="${ track.ISRC}" /></td>
	</tr>
	<tr class="odd">
		<td>Product Code</td>
		<td><c:out value="${ track.productCode}" /></td>
	</tr>
	<tr class="even">
		<td>Product ID</td>
		<td><c:out value="${ track.productId}" /></td>
	</tr>
	<tr class="odd">
		<td>Ingestion Process</td>
		<td><c:out value="${ track.ingestor}" /></td>
	</tr>
</table>

<table>
	<tr>
		<th>Type</th>
		<th>Path</th>
	</tr>
	<c:forEach items="${ track.files}" var="asset" varStatus="s">
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
			<td><c:choose>
				<c:when test="${ asset.type eq 'DOWNLOAD'}">
				<a href="<c:url value="/music"/>?ISRC=<c:out value="${ track.ISRC}" />&productCode=<c:out value="${ track.productCode}"/>&ingestor=<c:out value="${ track.ingestor}" />">
					<c:out value="${ asset.type}" />
				</a>				
				</c:when>
				<c:when test="${ asset.type eq 'IMAGE'}">
				<a href="<c:url value="/image"/>?ISRC=<c:out value="${ track.ISRC}" />&productCode=<c:out value="${ track.productCode}"/>&ingestor=<c:out value="${ track.ingestor}" />">
					<c:out value="${ asset.type}" />
				</a>				
				</c:when>
				<c:otherwise>
					<c:out value="${ asset.type}" />
				</c:otherwise>
				</c:choose>
			</td>
			<td><c:out value="${ asset.path}" /></td>

		</tr>
	</c:forEach>
</table>
<table>
	<tr>
		<th>Country</th>
		<th>Distributor</th>
		<th>Publisher</th>
		<th>Label</th>
		<th>Currency</th>
		<th>Price</th>
		<th>Price Code</th>
		<th>Start Date</th>
		<th>Reporting ID</th>
		<th>Deleted</th>
	</tr>
	<c:forEach items="${ track.territories}" var="territory" varStatus="s">
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
			<td><c:out value="${ territory.code}" /></td>
			<td><c:out value="${ territory.distributor}" /></td>
			<td><c:out value="${ territory.publisher}" /></td>
			<td><c:out value="${ territory.label}" /></td>
			<td><c:out value="${ territory.currency}" /></td>
			<td><c:out value="${ territory.price}" /></td>
			<td><c:out value="${ territory.priceCode}" /></td>
			<td><c:out value="${ territory.startDate}" /></td>
			<td><c:out value="${ territory.reportingId}" /></td>
			<td><c:out value="${ territory.deleted}" /></td>

		</tr>
	</c:forEach>
</table>
<img width="200" src="<c:url value="/image"/>?ISRC=<c:out value="${ track.ISRC}" />&productCode=<c:out value="${ track.productCode}" />&ingestor=<c:out value="${ track.ingestor}" />">

   <br/>
<a class="media" href="<c:url value="/music"/>?ISRC=<c:out value="${ track.ISRC}" />&productCode=<c:out value="${ track.productCode}"/>&ingestor=<c:out value="${ track.ingestor}" />&audio.mp3">AU File</a> 
<table>
	<tr>
		<td colspan="5"><%@include file="../include/footer.jsp"%>
		</td>
	</tr>
	</table>
</body>
</html>
