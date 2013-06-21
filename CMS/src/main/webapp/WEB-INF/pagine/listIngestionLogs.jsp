<%@include file="../include/taglibs.jsp"%>
<%@include file="../include/header.jsp"%>

<title>Ingestion Logs</title>
	<script type="text/javascript">
function toggle_visibility(tbid,lnkid) {
if (document.getElementsByTagName) {
  var tables = document.getElementsByTagName('table');
  for (var i = 0; i < tables.length; i++) {
   if (tables[i].id == tbid){
     var trs = tables[i].getElementsByTagName('tr');
     for (var j = 0; j < trs.length; j+=1) {
       if(trs[j].style.display == 'none')
          trs[j].style.display = '';
       else
          trs[j].style.display = 'none';
    }
   }
  }
 }
   var x = document.getElementById(lnkid);
   if (x.innerHTML == '[+]')
      x.innerHTML = '[-]';
   else
      x.innerHTML = '[+]';
}</script>

</head>
<body>
<table width=100% >
	<tr>
		<th></th>
		<th>Ingestor</th>
		<th>Date</th>
		<th>Status</th>
		<th>Drop Name</th>
		<th>Comment</th>
	</tr>
	<c:forEach items="${ data}" var="data" varStatus="s">
		<tr class="<c:choose>
				<c:when test="${ s.index%2==0}">
					even
				</c:when>
				<c:otherwise>
					odd
				</c:otherwise>
				</c:choose>
				">
			<td width=5%>
					<a style="TEXT-DECORATION: none;" href="javascript:toggle_visibility('tbl<c:out value="${ s.index}" />','lnk<c:out value="${ s.index}" />');">
            			<div align="right" id="lnk<c:out value="${ s.index}" />" name="lnk<c:out value="${ s.index}" />">[+]</div>
            		</a>
            </td>
			
			<td><c:out value="${ data.ingestor}" /></td>
			<td><fmt:formatDate value="${data.ingestionDate}" pattern="dd/MM/yyyy hh:mm:ss" /></td>
			<td><c:out value="${ data.status}" /></td>
			<td><c:out value="${ data.dropName}" /></td>
			<td><c:out value="${ data.message}" /></td>
		</tr>
		<tr>
		<td/>
		<td colspan=5>
			<table id="tbl<c:out value="${ s.index}" />" name="tbl<c:out value="${ s.index}" />">
				<tr style="display:none;">
					<th>ISRC</th>
					<th>Artist</th>
					<th>Title</th>
					<th>Update</th>
				</tr>
				<c:forEach items="${ data.content}" var="track" varStatus="st">
						<tr style="display:none;" class="<c:choose>
				<c:when test="${ st.index%2==0}">
					even
				</c:when>
				<c:otherwise>
					odd
				</c:otherwise>
				</c:choose>
				">
							<td><c:out value="${ track.ISRC}" /></td>
							<td><c:out value="${ track.artist}" /></td>
							<td><c:out value="${ track.title}" /></td>
							<td><c:out value="${ track.updated}" /></td>
						</tr>
				</c:forEach>
				
			
			</table>
		</td>
		</tr>
	</c:forEach>
	<tr>
		<td colspan="5"><%@include file="../include/footer.jsp"%>
		</td>
	</tr>

</table>
</body>
</html>
