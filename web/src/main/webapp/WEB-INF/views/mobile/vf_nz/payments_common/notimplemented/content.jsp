<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<style type="text/css">
body {
	background-color: #fff;
}
.container {
	background-color: #fff;
	padding-bottom: 0px;
}
</style>
<div style="margin: 0 13px;">
<div class="notimplementedTitle">
	You are on a trial<br />
	and have full access to<br />
	Vodafone Music.
</div>
<div style="text-align: center; margin: 17px 0 22px;">
	<img src="${requestScope.assetsPathAccordingToCommunity}imgs/icon_holding_page.png" width="47px" height="44px" align="bottom" />
</div>

<div class="notimplementedText" style="text-align: center;">We want you to get the most out of your free access  so there's no need to upgrade yet.<br />
Don't worry, we'll let you know when it's time.</div>

<div>
	<input class="button-turquoise pie" title="/account.html" type="button" onClick="location.href=this.title" value="Close" />
</div>
</div>