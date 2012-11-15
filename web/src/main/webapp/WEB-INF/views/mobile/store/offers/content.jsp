<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<div class="offers_container">
<div class="header">
<div class="gradient_border">&#160;</div>
	<a href="" class="logoWebStore"><img
			src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo_left.png"
			alt="" width="55" height="53" /><span><s:message code="offers.page.title" /></span></a> 
	<div class="buttonBox">
		<span class="arrow">&nbsp;</span>
		<input class="button accounts" class="buttonTop" title="account.html" type="button" onClick="location.href=this.title" value="<s:message code='m.page.main.menu.back' />" />
	</div>	
</div>
	<c:forEach varStatus="contentOfferDtoRowCounter"  var="contentOfferDto" items="${contentOfferDtoList}">
			<div class="packBlock clear">
				<div class="albumIcon">
					<img src="${filesURL}/${contentOfferDto.coverFileName}" width="80" height="80" alt="" />
				</div>
				<div class="albmPrice">
					<span class="align"><a href="offers/${contentOfferDto.id}">
					<s:message code="transaction_history.historyTable.amount.formater" var="amount_formater"/> 
					<fmt:formatNumber type="currency"  currencySymbol="&pound;" pattern="${amount_formater}" value="${contentOfferDto.price}">
					</fmt:formatNumber>&#160;&#160;&#160;<span><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/arrow_pink.png" alt="" /></span></a></span></div>
				<div class="albmTitle">
					<span>${contentOfferDto.title}</span>
					<span class="albmBody">${contentOfferDto.description}</span>
				</div>
				<div class="clear"></div>
			</div>
			
	</c:forEach>
</div>
