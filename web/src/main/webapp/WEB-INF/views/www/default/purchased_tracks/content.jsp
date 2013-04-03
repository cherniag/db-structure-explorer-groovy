<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div class="contentContainer">
	<div class="verticalSpace"></div>
	<div class="content rel oneWideColumn">
		<!-- top part of content -->
		<h1 class="azHeader">
			<s:message code='purchasedTracks.azHeader' />
		</h1>
		<!-- tabs  -->
		<%@ include file="/WEB-INF/views/www/default/menu.jsp"%>
		<div class="widerContainer boxWithBorder lessBottomPad">
			<div class="wholePart">
				<div class="details noBg">
					<s:message code='purchasedTracks.details' />
					<!--table with history of transactions-->
					<table class="table addSpace">
						<thead>
							<tr>
								<th><s:message code='purchasedTracks.table.header.tracks' /></th>
								<th><s:message code='purchasedTracks.table.header.artist' /></th>
								<th class="width120 alignCenter"><s:message code='purchasedTracks.table.header.purchased' /></th>
								<th class="width120 alignCenter"><s:message code='purchasedTracks.table.header.downloadToPc' /></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="purchasedTrackDto" items="${purchasedTrackDtoList}" varStatus="rowCounter">

								<form:form name="downloadTrackForm" method="POST" commandName="purchasedTrackDto">

									<c:choose>
										<c:when test="${!purchasedTrackDto.isDownloadedOriginal}">
											<tr id="track_${purchasedTrackDto.mediaId}" class="current">
										</c:when>
										<c:otherwise>
											<tr>
										</c:otherwise>
									</c:choose>

									<td>${purchasedTrackDto.trackName}</td>
									<td>${purchasedTrackDto.artistName}</td>
									<td><fmt:formatDate value="${purchasedTrackDto.purchasedDate}" pattern="dd/MM/yyyy" /></td>
									<td><c:choose>
											<c:when test="${!purchasedTrackDto.isDownloadedOriginal}">
												<a target="_self" onclick="onDownload(${purchasedTrackDto.mediaId}); return false;" href="purchased_tracks/${fn:replace(purchasedTrackDto.artistName, '/', '-')}-${purchasedTrackDto.trackName}.mp3?mediaId=${purchasedTrackDto.mediaId}&mediaIsrc=${purchasedTrackDto.mediaIsrc}"> 
													<img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/icons/table_button.png" name="<s:message code='purchasedTracks.button.alt.downloaded' />"/>
												</a>
											</c:when>
											<c:otherwise>
												<img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/icons/table_button_disabled.png" alt="<s:message code='purchasedTracks.button.alt.downloaded' />" />
											</c:otherwise>
										</c:choose></td>
									</tr>
								</form:form>
							</c:forEach>

						</tbody>

					</table>
					<!--end table with history of transactions-->

				</div>
			</div>
		</div>
	</div>
	<!--end of main account content area-->
	<!-- end  of two columns content -->
	<div class="clr verticalSpaceMiddle"></div>
</div>

<script type="text/javascript">
	function onDownload(mediaId, self) {		
		var trackTR = $("#track_" + mediaId);
		var trackDownloadLink = trackTR.find("a");
		
		window.open(trackDownloadLink.attr("href"), '_self');
		
		if(trackTR.attr("class") == "current")
		{
			trackTR.attr("class","");
			
			setTimeout(function () {
				var trackDownloadButton = trackDownloadLink.find("img");
				var trackDownloadTD = trackDownloadButton.parent().parent();
				var src = trackDownloadButton.attr("src");
				
				trackDownloadButton = trackDownloadButton.clone();
				trackDownloadButton.attr("src", src.replace(".png", "_disabled.png")+'?'+Math.random());
				trackDownloadButton.css("display", "block-inline");
				trackDownloadButton.attr("alt", trackDownloadButton.attr("name"));
				
				trackDownloadButton.detach().appendTo(trackDownloadTD);
				trackDownloadLink.remove();
			}, 500);
		}
	};
</script>