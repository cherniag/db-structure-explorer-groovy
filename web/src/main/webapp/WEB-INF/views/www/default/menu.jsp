<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<%
	String current = " current";

	String firstItemCurrentStyle = "firstItem";
	String secondItemCurrentStyle = "secondItem";
	String thirdItemCurrentStyle = "thirdItem";
	String forthItemCurrentStyle = "forthItem";
	String lastItemCurrentStyle = "lastItem";
	String uri = (String) request.getAttribute("javax.servlet.forward.request_uri");

	if (uri.contains("account.html"))
		secondItemCurrentStyle += current;
	else if (uri.contains("payments_inapp.html"))
		thirdItemCurrentStyle += current;
	else if (uri.contains("one_click_subscription_successful.html"))
		thirdItemCurrentStyle += current;

	request.setAttribute("firstItemCurrentStyle", firstItemCurrentStyle);
	request.setAttribute("secondItemCurrentStyle", secondItemCurrentStyle);
	request.setAttribute("thirdItemCurrentStyle", thirdItemCurrentStyle);
	request.setAttribute("forthItemCurrentStyle", forthItemCurrentStyle);
	request.setAttribute("lastItemCurrentStyle", lastItemCurrentStyle);
%>

<div class="widerContainer">
	<div class="tabs">
		<ul>
			<s:message code='page.main.menu.get.app' var="page_main_menu_get_app" />
			<c:if test="${not empty account_page_rightPart_submit}">
				<li class="${firstItemCurrentStyle}">
					<a href="getapp.html"><s:message code='page.main.menu.get.app' />
					</a>
				</li>
			</c:if>
			<li class="${secondItemCurrentStyle}">
				<a href="account.html"><s:message code='page.main.menu.my.account' />
				</a>
			</li>
			<li class="${thirdItemCurrentStyle}">
				<a href="payments_inapp.html"><s:message
						code='page.main.menu.manage.payment' /> </a>
			</li>
		</ul>
	</div>
</div>