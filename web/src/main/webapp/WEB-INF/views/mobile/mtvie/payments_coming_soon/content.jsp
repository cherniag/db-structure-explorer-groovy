<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%--
  ~ Copyright 2015 Musicqubed.com. All Rights Reserved.
  --%>

<div class="holding_page_main_container">
    <div class="holding_page_content_container">
        <div class="holding_page_close_button_small">
            <button onClick="returnToApp()"></button>
        </div>
        <div class="holding_page_logo"></div>
        <div class="holding_page_img"></div>
        <div class="holding_page_relax">
            <s:message code="holding.page.relax.message" />
        </div>
        <div class="holding_page_enjoy">
            <s:message code="holding.page.enjoy.message" />
        </div>
        <div class="holding_page_close_button_big">
            <button onClick="returnToApp()"><s:message code="holding.page.close.message" /></button>
        </div>
    </div>
</div>
