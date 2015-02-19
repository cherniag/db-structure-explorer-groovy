var Browser = {
		isiPhone5 : function (){
			return this.isiPhone() && navigator.userAgent.match(/OS 6(_\d)+ like Mac OS X/i) != null;
		},
		isiPhone4 : function (){
			return this.isiPhone() && navigator.userAgent.match(/OS 5(_\d)+ like Mac OS X/i) != null;
		},
		isiPhone3 : function (){
			return this.isiPhone() && navigator.userAgent.match(/OS 4(_\d)+ like Mac OS X/i) != null;
		},
		isiPhone : function(){
			return navigator.userAgent.match(/iPhone/i) != null;
		}
};

var i18n = {

};

var Strings = {
    cut: function (str, length) {
        if (str.length > length)
            return str.substring(0, length - 3) + '...';
        return str;
    },
    cutLn: function(str, rows, columns){
        var result = '';
        var cuted = Strings.cut(str, rows*columns);
        for (var c in cuted){
            if(c % columns == 0)
                result += '\n';
            result += cuted[c];
        }
        return result;
    }
};

var Templates = {
    names:[],
    mainName: null,
    loaded: 0,
    notStarted: true,

    pick: function (name, callback) {
        var T = Templates;
            $.ajax({
            url: T.templatesPath + name + ".htm",
                success: function (data) {
                T[name] = _.template(data);
                T.loaded++;
                if(T.notStarted && (T.loaded == T.names.length || T.mainName == name)){
                    T.notStarted = false;
                    callback();
                }
            }
        });
                },
    load: function (names, mainName, callback) {
        var T = Templates;
        T.names = names;
        T.mainName = mainName;
        for(var i in T.names)
            T.pick(T.names[i], callback);
    }
}

function PinCodeControl(controlId){
    this.controlId = controlId;

    $('#'+this.controlId).find('input[type=text]').each(function(){
        $(this)
            .on('focus', function(){
                var prev = $(this).prev('input[type=text]');
                if(prev && prev.length && !prev.val() && !$(this).val()){
                    prev[0].focus();
                }
            })
            .on('keyup', function(e){
                var next = $(this).next('input[type=text]');
                var prev = $(this).prev('input[type=text]');

                if(e.keyCode==46 && next.length && !(prev.length && prev.val())) {
                    next[0].focus();
                    if(next.val().length) next[0].setSelectionRange(0, 0);
                    return;
                }

                if($(this).val() && next.length && !next.val()){
                    next[0].focus();
                    if(next.val().length) next[0].setSelectionRange(1, 1);
                } else if (!$(this).val() && !next.val() && prev.length) {
                    prev[0].focus();
                    if(prev.val().length) prev[0].setSelectionRange(1, 1);
                }
            });
    });
}

PinCodeControl.prototype.getValue = function() {
    var val = "";
    $('#'+this.controlId).find('input[type=text]').each(function(){
        val += $(this).val();
    });
    return val;
};

function createCookie(name, value, days) {
	var expires = "";
	if (days) {
		var date = new Date();
		date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
		expires = "; expires=" + date.toGMTString();
	}
	
	document.cookie = name + "=" + value + expires + "; path=/";
}

function readCookie(name) {
	var nameEQ = name + "=";
	var ca = document.cookie.split(';');
	for ( var i = 0; i < ca.length; i++) {
		var c = ca[i];
		while (c.charAt(0) == ' ')
			c = c.substring(1, c.length);
		if (c.indexOf(nameEQ) == 0)
			return c.substring(nameEQ.length, c.length);
	}
	return null;
}
