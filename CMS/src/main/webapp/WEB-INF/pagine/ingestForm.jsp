<%@include file="../include/taglibs.jsp"%>
<%@include file="../include/header.jsp"%>

<title>Ingest Tracks</title>

<SCRIPT LANGUAGE="JavaScript">
<!-- 
var drops=[
	<c:forEach items="${ data.dropdata.drops}" var="drop" varStatus="dropstatus">
		<c:out value="${fn:length(drop.ingestdata.data)}" />,
	</c:forEach>
0
];
function check(value)
{
	for (i = 0; i < drops.length; i++){
		for (j=0;j<drops[i];j++) {
			var name='dropdata.drops['+i+'].ingestdata.data['+j+'].ingest';
			document.getElementsByName(name)[0].checked = value ;
		}
	}
}

function checkAll()
{
	check(true);
} 
function uncheckAll(field)
{
	check(false);
}
-->
</script>


</head>
<body>

<form:form commandName="data" method="post" >
<br/>
<input type="button" name="CheckAll" value="Check All" onClick="checkAll()">
<input type="button" name="UnCheckAll" value="Uncheck All" onClick="uncheckAll()">
<br/>
<input type="submit" value="Next" name="_target2">

	<table>

	<tr>
		<th>Keep</th>
		<th>Product id</th>
		<th>Action</th>
		<th>Artist</th>
		<th>Title</th>
		<th>ISRC</th>
		<th>Existing</th>
		<th nowrap> </th>
	</tr>
	<c:forEach items="${ data.dropdata.drops}" var="drop" varStatus="dropstatus">
		<c:if test="${drop.selected}" >
			<tr>
				<td colspan=5>Drop: <c:out value="${drop.name}" /></td>
			</tr>
			<c:forEach items="${ drop.ingestdata.data}" var="track" varStatus="status">
				<tr class="<c:choose>
						<c:when test="${ status.index%2==0}">
							even
						</c:when>
						<c:otherwise>
							odd
						</c:otherwise>
						</c:choose>
						">
					<td><form:checkbox path="dropdata.drops[${dropstatus.index}].ingestdata.data[${status.index}].ingest" id="${ track.ISRC}" /></td>
					<td><c:out value="${ track.productCode}" /></td>
					<td><c:out value="${ track.type}" /></td>
					<td><c:out value="${ track.artist}" /></td>
					<td><c:out value="${ track.title}" /></td>
					<td><c:out value="${ track.ISRC}" /></td>
					<td><c:out value="${ track.exists}" /></td>
				</tr>
			</c:forEach>
		</c:if>
	</c:forEach>
	

</table>
<br/>
<input type="button" name="CheckAll" value="Check All" onClick="checkAll()">
<input type="button" name="UnCheckAll" value="Uncheck All" onClick="uncheckAll()">
<br/>


<input type="submit" value="Next" name="_target2">
<%@include file="../include/footer.jsp"%>
</form:form>
</body>
</html>
