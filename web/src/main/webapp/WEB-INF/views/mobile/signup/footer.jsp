<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<div class="footer">
	<div class="bottomLinks">
		<a href="faq.html"><s:message code="page.header.link.faq" /></a>	
		<a href="terms.html"><s:message code="footer.link.terms" /></a><br/><br/>
		<img width="80%" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/bgs/brought.png" alt="" />
	</div>
	<div class="copyright" style="padding-bottom: 10px;">
		<s:message code="m.footer.copyright" />
		
	</div>
</div>