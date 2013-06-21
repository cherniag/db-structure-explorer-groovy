<%@include file="../include/taglibs.jsp"%>
<%@include file="../include/header.jsp"%>

<title>Publish track</title>

</head>
<body>
<form:form commandName="data" method="post">
	<table>
		<tr>
			<td>Title:</td><td> <c:out value="${ data.track.title}" /></td>
		</tr>
		<tr>
			<td>Published Title: </td><td>
				<form:textarea path="publishTitle" rows="1" cols="100"/></td>
			<td><form:errors path="track.title" cssClass="error" /></td>
		</tr>
		<tr>
			<td>ISRC: </td><td><c:out value="${ data.track.ISRC}" /></td>
		</tr>
		<tr>
			<td>Artist: </td><td><c:out value="${ data.artist.name}" /></td>
		</tr>
		<tr>
			<td>Published Artist: </td><td>
				<form:textarea path="publishArtist" rows="1" cols="100"/></td>
			<td><form:errors path="artist.name" cssClass="error" /></td>
		</tr>
		<tr>
			<td>Artist info:</td><td>
			<form:textarea path="artist.info" rows="4" cols="100"/></td>
			<td><form:errors path="artist.info" cssClass="error" /></td>
		</tr>
		<tr>
			<td>iTunes link:</td>
			<td><a href="<c:out value="${data.iTunesUrl}"/>" /><c:out value="${data.iTunesUrl}"/></td>
		</tr>		
		<tr>
			<td></td>
			<td><form:textarea path="editiTunesUrl" rows="4" cols="100"/></td></td>
		</tr>		
		<tr>
		<td><form:checkbox path="highRate"/> 96 kbps</td>
		</tr>
		<tr>
		<td><form:checkbox path="track.licensed"/> Licensed</td>
		</tr>
		<tr>
			<td>
<c:choose>
				<c:when test="${data.territories==null}">
				<h1>No valid territory for this track: cannot be published</h1>
				</c:when>
				<c:otherwise>

		<input type="submit"
				value="Publish" />
				</c:otherwise>
				</c:choose>
				</td>
		</tr>
		<tr>
			<td><%@include file="../include/footer.jsp"%>
			</td>
		</tr>

	</table>
</form:form>

</body>
</html>
