<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:choose>
	<%--we have 2 cases:
	(1) user is 4G and opted-in (we display the video options)
	(2) user is 4g and not opted-in (we display a "link" for the user to opt-in) --%>
	<c:when test="${userIsOptedInToVideo eq true}">

		<div class="rel tapArea videoOption">
			<a class="subscription-selector" href="javascript: void(0)"	onclick="videoCheckbox.switchState()" type="button">
				<div class="clr">
					<img width="34px" height="32px" style="margin-top: 2px"	src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/ic_video.png" />
					<div class="rel frR15">
						<div style="margin-bottom: 3px">
							<s:message code='pays.page.note.account.videotitle' />
						</div>
						<div style="color: #3399cc;">
							<s:message code='pays.page.note.account.videoprice' />
						</div>
					</div>
				</div>
				<div class="frL11 videoInfo">
					<s:message code='pays.page.note.account.videoinfo' />
				</div>
					
				<c:set var="buttonClassOnStyle" value="display: none" />
				<c:set var="buttonClassOffStyle" value="display: block" />
				<c:if test="${(paymentDetails!=null) && (true==paymentDetails.activated) && (paymentDetails.paymentPolicy.videoAndAudio4GSubscription==true)}">
					<%-- <c:set var="buttonClass" value="button-on" /> --%>
					<%-- Activate the video checkbox if the user has a video subscription --%>
					<c:set var="buttonClassOnStyle" value="display: block" />
					<c:set var="buttonClassOffStyle" value="display: none" />
				</c:if>
				<c:if test="${(paymentDetails==null) || (false==paymentDetails.activated)}">
					<%-- <c:set var="buttonClass" value="button-on" /> --%>
					<%--Activate the video checkbox if the user has no subscription --%>
					<c:set var="buttonClassOnStyle" value="display: block" />
					<c:set var="buttonClassOffStyle" value="display: none" />
				</c:if>
				
				<span class="button-on" id="videoCheckboxOn" style="top:35px; ${buttonClassOnStyle}"></span>
				<span class="button-off" id="videoCheckboxOff" style="top:35px; ${buttonClassOffStyle}"></span>
			</a>
		</div>
	</c:when>
	
	
	<c:when test="${userIsOptedInToVideo eq false}">
		<div class="rel tapArea" style="margin-top: 10px">
			<div class="subscription-selector videoNotOptedIn">
				<div class="clr videoNotOptedInHeader">
					<img width="34px" height="32px" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/ic_video.png" />
					<div class="rel frB15"><s:message code='pays.page.header.txt.o2consumer.video_1' /></div>
				</div>
				<div class="frL11 videoNotOptedInHeaderSmall"><s:message code='pays.page.header.txt.o2consumer.video_2' /></div>
				<input class="button-turquoise no-margin pie"
					title="${pageContext.request.contextPath}/videotrial.html?return_url=<%=request.getParameter("callingPage")%>.html"
					type="button" onClick="location.href=this.title"
					value="<s:message code='pays.page.header.txt.o2consumer.video.button' />" />
			</div>
		</div>
	</c:when>
</c:choose>