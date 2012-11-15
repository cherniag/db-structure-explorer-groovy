<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div class="contentContainer">
	<div class="verticalSpace"></div>
	<div class="content rel oneWideColumn">
		<!-- top part of content -->
		<h1 class="azHeader"><s:message code="page.faq.header.h1" /><span><s:message code="page.faq.header.description" /> </span></h1>
		<!-- tabs  -->
		<%@ include file="/WEB-INF/views/www/menu.jsp"%>
		
		<div class="widerContainer boxWithBorder lessBottomPad">
			<div class="wholePart">
				<div class="details">
					<h2><s:message code="faq.page.h2" /></h2>
					<s:message code="faq.page.description" />
					<div class="faqBox"><s:message code="faq.page.faqBox.content" /></div>
					<div class="clr"></div>
				</div>
			</div>
		</div>
	</div>
	<script type="text/javascript">
		$(".oneQuestion div").hide();
		$(".oneQuestion h2").click(function(e){ $(this).next().toggle("slow", null) });
	</script>
	<!--end of main account content area-->
	<!-- end  of two columns content -->
	<div class="clr verticalSpaceMiddle"></div>
</div>