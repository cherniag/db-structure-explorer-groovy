<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:choose>
	<%--we have 2 cases:
	(1) user is 4G and opted-in (we display the video options)
	(2) user is 4g and not opted-in (we display a "link" for the user to opt-in) --%>
	<c:when test="${userIsOptedInToVideo eq true}">

		<div class="rel tapArea" style="margin-top: 20px">
			<a class="subscription-selector" href="javascript: void(0)"	onclick="videoCheckbox.switchState()" type="button"	style="height: 105px; padding: 12px 6px 6px 10px">
				<div>
					<img style="width: 34px; height: 32px;"	src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/ic_video.png" />
					<div class="rel" style="padding-top: 1px;">
						<div class="frR15">
							<div style="margin-bottom: 7px">
								<s:message code='pays.page.note.account.videotitle' />
							</div>
							<div style="color: #3399cc;">
								<s:message code='pays.page.note.account.videoprice' />
							</div>
						</div>
					</div>
					<div style="clear: both">&nbsp;</div>
				</div>
				<div class="frL11" style="line-height: 16px;margin-right: 70px;">
					For all the best music videos straight to your phone everyday. Only	with O2. Only on 4G.</div>
					
					<c:set var="buttonClass" value="button-off" />
					<c:if test="${(paymentDetails!=null) && (true==paymentDetails.activated) && (paymentDetails.paymentPolicy.videoAndAudio4GSubscription==true)}">
						<c:set var="buttonClass" value="button-on" />
						<%-- Activate the video checkbox if the user has a video subscription --%>
					</c:if>
					<c:if test="${(paymentDetails==null) || (false==paymentDetails.activated)}">
						<c:set var="buttonClass" value="button-on" />
						<%--Activate the video checkbox if the user has no subscription --%>
					</c:if>
					
					<span class="${buttonClass}" id="videoCheckbox" style="top:35px;"></span>
			</a>
		</div>
	</c:when>
	<c:when test="${userIsOptedInToVideo eq false}">
		<div class="rel tapArea" style="margin-top: 20px">
			<div class="subscription-selector"
				style="height: 135px; padding: 12px 6px 6px 10px">
				<div>
					<img style="width: 34px; height: 32px;" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/ic_video.png" />
					<div class="rel" style="padding-top: 1px;">
						<div  class="frB15">
							<s:message code='pays.page.header.txt.o2consumer.video_1' />
						</div>
					</div>
					<div style="clear: both">&nbsp;</div>
				</div>
				<div class="frL11" style="line-height: 16px;margin-right: 20px; margin-bottom: 10px">
					<s:message code='pays.page.header.txt.o2consumer.video_2' />
				</div>
				<input class="button-turquoise no-margin pie"
					title="${pageContext.request.contextPath}/videotrial.html?return_url=<%=request.getParameter("callingPage")%>.html"
					type="button" onClick="location.href=this.title"
					value="<s:message code='pays.page.header.txt.o2consumer.video.button' />" />
			</div>
		</div>
	</c:when>
</c:choose>