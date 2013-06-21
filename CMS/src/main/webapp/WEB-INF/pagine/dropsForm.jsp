<%@include file="../include/taglibs.jsp"%>
<%@include file="../include/header.jsp"%>

<title>Ingest Tracks</title>

<SCRIPT LANGUAGE="JavaScript">
<!-- 
var size=<c:out value="${fn:length(data.dropdata.drops)}" />;
function check(value)
{
	for (i = 0; i < size; i++){
			var name='dropdata.drops['+i+'].selected';
			document.getElementsByName(name)[0].checked = value ;
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

<form:form commandName="data" method="post">
<br/>
<input type="button" name="CheckAll" value="Check All" onClick="checkAll()">
<input type="button" name="UnCheckAll" value="Uncheck All" onClick="uncheckAll()">
<br/>

<input type="submit" value="Next" name="_target1">

	<table>
	<tr>
		<th>Ingest</th>
		<th>Ingestor</th>
		<th>Drop name</th>
		<th>Drop date</th>
	</tr>
	<c:forEach items="${ data.dropdata.drops}" var="drop" varStatus="status">
		<tr class="<c:choose>
				<c:when test="${ s.index%2==0}">
					even
				</c:when>
				<c:otherwise>
					odd
				</c:otherwise>
				</c:choose>
				">
				
			<td><form:checkbox path="dropdata.drops[${status.index}].selected" id="${drop.name}"/></td>
			<td><c:out value="${ drop.ingestor}" /></td>
			<td><c:out value="${ drop.drop.name}" /></td>
			<td><c:out value="${ drop.drop.date}" /></td>
		</tr>
	</c:forEach>

</table>

<br/>
<input type="button" name="CheckAll" value="Check All" onClick="checkAll()">
<input type="button" name="UnCheckAll" value="Uncheck All" onClick="uncheckAll()">
<br/>


<input type="submit" value="Next" name="_target1">
<%@include file="../include/footer.jsp"%>
</form:form>
</body>
</html>
