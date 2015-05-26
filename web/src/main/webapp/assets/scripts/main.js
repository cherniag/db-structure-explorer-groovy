function showPopup(id) {
	document.getElementById(id).style.display = 'block';
}
function hidePopup(id) {
	document.getElementById(id).style.display = 'none';
}
function closeForm(){
	window.location = "/web/closeApp.html";
}

function returnToApp(){
	window.location = "/web/exitFromApp.html";
}

function feedback(email) {
	window.location = "/web/feedback.html?email=" + email;
}

function goTo(uri){
	window.location = uri;
}

function submitForm(id) {
	document.getElementById(id).submit();
}

var simpleDialogOptions={opacity:100, overlayClose:true};
var modalDialogOptions={opacity:100, escClose:false, close:false};

function showAsDialog(elem, onShow) {
	if(null != onShow)
		simpleDialogOptions.onShow = onShow;
	$(elem).modal(simpleDialogOptions);
}

function showModalDialog(obj) {
	if ($(obj).length==0)
		$.modal(content, modalDialogOptions);
	else
		$(obj).modal(modalDialogOptions);
}

function applyDefaultText() {
	$(".defaultInputText").focus(function(srcc) {
		if ($(this).val() == $(this)[0].title) {
	        $(this).removeClass("defaultInputTextActive");
	        $(this).val("");
	    }
	});

	$(".defaultInputText").blur(function() {
		if ($(this).val() == "") {
			$(this).addClass("defaultInputTextActive");
			$(this).val($(this)[0].title);
		}
	});
	$(".defaultInputText").blur();
}

var COOKIE_ALERT_NAME="cookie_alert_message";

function setCookie (name, value, expires, path, domain, secure) {
    document.cookie = name + "=" + escape(value) +
    ((expires) ? "; expires=" + expires : "") +
    ((path) ? "; path=" + path : "") +
    ((domain) ? "; domain=" + domain : "") +
    ((secure) ? "; secure" : "");
}

function getCookie(name) {
    var cookie = " " + document.cookie;
    var search = " " + name + "=";
    var setStr = null;
    var offset = 0;
    var end = 0;
    if (cookie.length > 0) {
        offset = cookie.indexOf(search);
        if (offset != -1) {
            offset += search.length;
            end = cookie.indexOf(";", offset)
            if (end == -1) {
                end = cookie.length;
            }
            setStr = unescape(cookie.substring(offset, end));
        }
    }
    return(setStr);
}


function onStart() {
	applyDefaultText();
	
	var liCount = $("div.tabs ul li").size();
	if (liCount == 2) {
		$('.tabs ul li').css('width','49%');
	}
	if (liCount == 4) {
		$('.tabs ul li').css('width','24%');
	}
	if (liCount == 5) {
		$('.tabs ul li').css('width','19%');
	}
	
	
	if (getCookie(COOKIE_ALERT_NAME) == null) {
		$("#cookeiAlertMessage").show('slow');
	} else {
		$("#cookeiAlertMessage").hide();
	}
	
	$("#cookeiAlertMessageCloseBtn").click(function() {
		$("#cookeiAlertMessage").hide('slow');
		setCookie (COOKIE_ALERT_NAME, "true", "Mon, 06-Jan-2020 00:00:00 GMT", "/");
	});
	
	if (window.PIE) {
   		$('.pie').each(function() {
        	PIE.attach(this);
    	});
	}
	
	$('.alert-button-close').click(function() {
    	$(this).parent().hide();
	});
}

$(document).ready(function() {
	onStart();
});