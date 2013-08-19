function createToken(userToken, theDate){
	var salt = "8z54YKmns9Qz";
	var tkn = hex_md5(salt + userToken + salt + theDate + salt);
	return tkn;
}
function setAuthValues(){
	tsValue=new Date().format('yyyy-mm-dd HH:MM')+"+0000";//2013-07-30 13:54+0000  "yyyy-MM-dd HH:mmZ", Locale.US
	
	$("input[name='USER_NAME']").val($("#p_userName").val());
	token=$("#p_userToken").val();

	var generatedToken=createToken(token,tsValue);
	$("input[name='USER_TOKEN']").val(generatedToken);
    $("input[name='TIMESTAMP']").val(tsValue);
}	

$(document).ready(function() {
	defaultPrefix=".json";
	//defaultPrefix="";

	//defaultServer = "http://kiwi.dev.now-technologies.mobi/transport/service/Mqid/o2/3.9/";
	defaultServer = "http://rage.musicqubed.com/transport/service/Mqid/o2/4.0/";
	//defaultServer = "http://localhost:8080/transport/service/Mqid/o2/3.9/"; 
	var serverUrl=$("#serverUrlId").attr("value",defaultServer);	
});
