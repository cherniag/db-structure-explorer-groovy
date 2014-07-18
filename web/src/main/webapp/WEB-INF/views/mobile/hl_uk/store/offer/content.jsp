<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<div class="offers_container">
<div class="header pie">
<div class="gradient_border">&#160;</div>
	<span class="logoWebStore"><img
			src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo_left.png"
			alt="" width="55" height="53" /><span><s:message code="offers.page.title" /></span></span> 
	<div class="buttonBox">
		<span class="arrow">&nbsp;</span>
		<input class="button accounts" class="buttonTop" title="javascript: history.go(-1)" type="button" onClick="location.href=this.title" value="<s:message code='m.page.main.menu.back' />" />
	</div>	
</div>
	
<div class="offer_block">	
<div class="packBlockDetails">
	<div class="albumIconDetails">
		<img src="${filesURL}/${contentOfferDto.coverFileName}" alt="" />
	</div>
	<div class="albmTitleDetails">
		${contentOfferDto.title}
		<div class="albmBodyDetails">
		${contentOfferDto.description}
		</div>
		<div class="rel">
			<div class="albumPriceDetails">
				<s:message code="transaction_history.historyTable.amount.formater" var="amount_formater"/> 
				<fmt:formatNumber type="currency"  currencySymbol="&pound;" pattern="${amount_formater}" value="${contentOfferDto.price}"></fmt:formatNumber>
			</div>
			<div class="albumButton">
			<a href="offers/${contentOfferDto.id}/payments.html"><s:message
					code='offers.page.link.buyTrack' /></a>
			</div>
		</div>
	</div>
</div>
<div class="clear"></div>
<c:forEach varStatus="rowCounter" var="contentOfferItemDto"
	items="${contentOfferDto.contentOfferItemDtos}">
	<div class="itemsDetails">
		<div class="itemsImage">
			<img src="${coverStorePath}/${contentOfferItemDto.coverFileName}" alt="" width="54" height="54"/>
		</div>
		<div class="itemCount">${rowCounter.count}
			<p class="itemsArtistName">
				<span>${contentOfferItemDto.authorName}</span>
				<span class="itemsTrackName">${contentOfferItemDto.title}</span>
			</p>
		</div>
	</div>
	<div class="clear"></div>
</c:forEach>
</div>

</div>