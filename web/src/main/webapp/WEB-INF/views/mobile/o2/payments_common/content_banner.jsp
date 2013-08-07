<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>

<div class="container" style="background-color: inherit;margin-top: 20px; margin-bottom: 17px;">
	<div style="font-family: frutigerRoman,Helvetica,Arial,sans-serif; font-size: 13px; color: #66ccff; margin-bottom: 6px;">Free Trial</div>
	<div style="font-family: frutigerLight,Helvetica,Arial,sans-serif; font-size: 15px; color: #ffffff">
		<img style="width: 8px; height: 15px; margin-right: 9px" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/ic_phone_account.png" />
		${mobilePhoneNumber}
	</div>
</div>