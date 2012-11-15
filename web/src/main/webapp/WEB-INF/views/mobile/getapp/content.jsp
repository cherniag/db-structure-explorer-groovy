<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<div class="header">
<div class="gradient_border">&#160;</div>
	<a href="" class="logo"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo.png" alt="" /></a>	
	<div class="buttonBox">
		<span class="arrow">&nbsp;</span>
		<a href="account.html" class="button buttonTop"><s:message code="page.main.menu.my.account" /></a>			
	</div>		
</div>
<div class="container">		
	<div class="content getAppContent">
		<h1><s:message code="getapp.page.h2" /></h1>
		<p><s:message code="getapp.form.description" /></p>
		
		<form:form modelAttribute="getPhone" method="post">
			<div class="oneField">										
				<form:input path="phone" alt="+44(0)" />
				<s:hasBindErrors name="getPhone">
					<div class="note" id="note">
						<form:errors path="phone" />
					</div>
				</s:hasBindErrors>										
			</div>
			<div class="clr"></div>
			<!--button-->
			<div class="contentButton formButton rad10">
				<input class="button" value="<s:message code='getapp.form.submit' />" type="submit" />
			</div>
			
			<c:if test="${sentStatus == true}">
				<div class="note successNote" id="note">
					<span><s:message code='getapp.form.submit.successful.body' /></span>
				</div>
			</c:if>	
		</form:form>
		
		<div class="orBox">
			<img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/bgs/or.png" alt="" />
		</div>				
		<div class="getAppBottom rel">
			<h1><s:message code='getapp.download.header' /></h1>
			<div class="linksToStores">
				<a href="<s:message code='page.available.on.store.link' />" class="appStore"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/apple.png" alt="<s:message code='getapp.available.on.store' />" /></a>
				<a href="<s:message code='page.available.on.market.link' />" class="androidMarket"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/android.png" alt="<s:message code='getapp.available.on.market' />" /></a>
				<a href="<s:message code='page.available.on.bbmarket.link' />" class="bbMarket"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/blackberry.png" alt="<s:message code='getapp.available.on.bbmarket' />" /></a>
			</div>
		</div>
	</div>	
</div>
