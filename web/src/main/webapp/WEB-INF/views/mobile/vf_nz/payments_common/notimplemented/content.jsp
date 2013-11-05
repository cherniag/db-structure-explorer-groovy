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
.notimplementedTitle {
	color: #e60000;
	font-family: vodafoneRegular, Helvetica, Arial, sans-serif;
	font-size: 25px;
	line-height: 120%;
	text-align: center;
}

.notimplementedText {
	color: #333;
	font-family: vodafoneLight, Helvetica, Arial, sans-serif;
	font-size: 15px;
	line-height: 140%;
	text-align: center;
}
</style>

<div class="notimplementedTitle">
	You are on a trial<br />
	and have full access to<br />
	Vodafone Music.
</div>
<div style="text-align: center;">
	<img src="${requestScope.assetsPathAccordingToCommunity}imgs/icon_holding_page.png" width="47px" height="44px" align="bottom" />
</div>

<div class="notimplementedText" style="text-align: center;">We want you to get the most out of your free access  so there's no need to upgrade yet.<br />
Don't worry, we'll let you know when it's time.</div>

<div>
	<input class="button-turquoise pie" title="/account.html" type="button" onClick="location.href=this.title" value="Close" />
</div>

