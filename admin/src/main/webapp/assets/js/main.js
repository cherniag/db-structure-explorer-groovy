//-----------------jQuery extention-------------------//
$.fn.descendantOf = function(element) {
    element = element[0];
    var current = this;
   // var body    = document.body;
    while (current && current != element && current != document.body) {
        current = $(current).parent()[0];
    }
    if (typeof(current) == "undefined" || typeof(current) == "null") {
        return false;
    } else if (current == element) {
        return true;
    } else if (current == document.body) {
        return false;
    }
};
//---------------------------------------------------//
$(function() {
    //------------------Popover Dialogs----------------//

    var popoverSelector = $(".popover-selector").popover({
        title : function(){
            var popoverTitle = $(this).find(".popover-title");
            return popoverTitle.html();
        },
        content : function(){
           var popoverContent = $(this).find(".popover-content");
           return popoverContent.html();
        },
        html : true
    }).click(function(e){

            var el = $(e.target)

            if (el.is('input')) {return};
            

            $(".popover-selector").each(function (){
              $(this).css({ marginBottom: 10 }); 
            });
           
            $(this).popover('toggle');
            
        });

    popoverSelector.each(function () {
        var $this = $(this);
        $this.on("shown", function(){
             window[$this.attr("onShownPopover")]($this);
             var popover$ = $(this).parent().find('.popover');
             height = popover$.height() || 0;
             height = height > 0 ? height + 25 : 0;

            $(this).css({ marginBottom: 10 + height});
        });
        $this.on("hidden", function(){
            window[$this.attr("onHiddenPopover")]($this);
            $(this).css({ marginBottom: 10 });
        });
    });
    //------------------Play Video and Audio-----------//
    var player = null;

    $(document).ready(function(){       
      // debugger; 

      if($.browser && ($.browser.mozilla || $.browser.opera)) {
        
        $.each( $('video'), function(idx, item){
          var item$ = $(item);
          // debugger;
          // item$.after('<div class="video" id="sfw-object-video-'+idx+'"></div>');
          // var s = new SWFObject(/*'@{/assets/utils/hdplayer.swf}'*/ '/jadmin/assets/utils/hdplayer.swf','player','320','240','9');
          // s.addParam('allowfullscreen','true');
          // s.addParam('allowscriptaccess','always');
          // s.addParam('wmode','transparent');
          var src = item$.children('source').attr('src');
          item$.after('<a class="swf-video" href="'+src+'" style="display:block;width:320px;height:240px;" id="sfw-object-video-'+idx+'"></a>');
          // s.addVariable('file', src);
          // s.write('sfw-object-video-' + idx);
          flowplayer('sfw-object-video-' + idx, /*'@{/assets/utils/flowplayer-3.2.18.swf}'*/ '/jadmin/assets/utils/flowplayer-3.2.18.swf', {
            clip:  {
                  autoPlay: false,
                  autoBuffering: true
              }
            });
        });
        
        $('video').remove();
      }
    });

    $(".play").mouseover(function(){
        var that = $(this);
        var parent = $(this).parent();
        var modalEl = parent.find(".modal");

        if(!modalEl.is(':visible')){
               modalEl.click(stopBubble);
               modalEl.on("shown", stopBubble);
               setAbsPlayerPosition(modalEl, parent.offset());
               modalEl.modal({backdrop:false});

               modalEl.mouseout(function(e){
                   hideIfNotPlay(that, modalEl, e);
               });
               that.mouseout(function(e){
                   hideIfNotPlay(that, modalEl, e);
               });

               var videoEl = modalEl.find("video, audio, .swf-video");
               var closeEl = modalEl.find(".close");
               closeEl.hide();
               closeEl.click(function(){
                   stop();
               });
               videoEl[0].addEventListener('ended', function(){
                   stop();
               });
               videoEl[0].addEventListener('play', function(){
                   if(player == null || modalEl[0] !== player[0]){
                       stop();

                       player = modalEl;
                       closeEl.show();
                   }
               });
        }
    });

    var hideIfNotPlay = function(playArea, playEl, e){

        var visibleNow = playEl.is(':visible');
        var containedPlayArea = playArea.containsPoint(e) || playEl.containsPoint(e);
        var notPlayer = player == null || player[0] !== playEl[0];

        if($.browser && ($.browser.mozilla || $.browser.opera)){
          var id = $(playEl).find('.swf-video').attr('id');
          if (id){
            notPlayer = !$f(id).isPlaying();
          }
        }

        if(visibleNow && !containedPlayArea && notPlayer){
            playEl.modal("hide");
        }
    }

    var stop = function(){
        if(player){
            player.modal("hide");
            var videoEl = player.find("video, audio");
            videoEl[0].pause();
            videoEl[0].load();
            player = null;
        }
    }

    $(window).scroll(function () {
        if(player){
            setAbsPlayerPosition(player, player.parent().offset());
        }
    });

    var setAbsPlayerPosition = function (el, offset) {
            // debugger;
            // var audio = el.find("audio");
            // var pos = offset;
            // pos.top = pos.top - $(window).scrollTop();
            // pos.left = pos.left - $(window).scrollLeft();
            // var offX = 280;
            // var offY = audio ? 234+el.parent().height()-audio.height() : 234 ;
            // el.css("top",pos.top+offY);
            // el.css("left",pos.left+offX);
    }

    //-----------------Select All Checkbox------------//
    var checkAllInput = $("input#selectAll[type=checkbox]");
    var otherCheckboxes = $("input[id!='selectAll'][type='checkbox']");
    if(checkAllInput){
        var updateCheckAll = function() {
            var allchecked = null;
            otherCheckboxes.each(function(i) {
                var checked = $(this).attr("checked");
                checked = checked ? true : false;
                if(allchecked == null)
                    allchecked = checked;
                else if(allchecked != checked){
                    allchecked = null;
                    return false;
                }
            });

            if(allchecked != null){
                checkAllInput.attr("checked",allchecked);
            }
        }

        checkAllInput.change(function(){
            var checked = $(this).attr("checked");
            otherCheckboxes.each(function(i) {
                $(this).attr("checked", checked ? true : false);
            });
        });
        otherCheckboxes.change(updateCheckAll);
        updateCheckAll();
    }
	//-----------------Toggle button-----------------//
	$('.toggleButton').toggleButtons({
		transitionspeed: "500%"
	});
	//-----------------Date picker-------------------//
	$('input[id$=Datepicker]').datepicker({
		dateFormat: "yy-mm-dd",
	    constrainInput: false
	});
	//-----------------Sortable list-----------------//
	$('ul[id*=Sortable]').sortable({ 
		connectWith: "ul[id*=Sortable]",
		items: "li:not(.ui-state-disabled, [editing='true'])",
		dropOnEmpty: true,
		helper: 'clone',
		appendTo: 'body',
		zIndex: 10000
	});
	$("#sortable").sortable();
	$("#sortable").disableSelection();
	$('#updatePositionsButton').click(function() {
		var form = $('#updatePositionsForm');

		$("#sortable").children().each(function(i) {
			var position = $(this).find("#position").val();
			var new_position = i+1;
			if (position != new_position) {
				var id = $(this).find("#ID").val();
				$('<input>').attr({
					type : 'hidden',
					name : 'positionMap[' + id + ']',
					value : new_position
				}).appendTo(form);
			}
		});

		form.submit();
	});
	//-----------------Pagination-----------------//
	var gotoPage = function(page, form) {
		$('<input>').attr({
			type : 'hidden',
			name : 'page.page',
			value : page
		}).appendTo(form);

		form.submit();
	}; 
	$('#searchByPageButton').click(function() {
		var form = $(this).parents('form:first');
		gotoPage(1, form);
	});
	$("#paginationBar a").each(function(i) {
		var button = $(this);
		var form = $('#searchByPageButton').parents('form:first');
		var page = button.attr('name');
		var status = button.parent().attr('class');
		if(status != 'active'){
			button.click(function() {
				gotoPage(page, form);
			});
		}
	});
	//----------------------------------------------//
	$('button[type="submit"].s_btn').attr('disabled','disabled');
	$('input[type="text"]#q').keyup(function(){
	    if($('input[type="text"]#q').val() == ""){
	        $('button[type="submit"].s_btn').attr('disabled','disabled');
	    }
	    else{
	        $('button[type="submit"].s_btn').removeAttr('disabled');
	    }
	});
	
	$('button.u_btn').attr('disabled','disabled');
    $('input.u').change(function(){
           if($(this).val != ''){
              $('button.u_btn').removeAttr('disabled');
           }
	});
    
    $('button.m_btn').attr('disabled','disabled');
    $('input.m').change(function(){
           if($(this).val != ''){
              $('button.m_btn').removeAttr('disabled');
           }
	});
	
    $('.element').tooltip('hide');
	
    $("input.quantity").keypress(function (e)  
	{ 
	  if( e.which!=8 && e.which!=0 && (e.which<48 || e.which>57))
	  {
		$(".errmsg").html("Digits Only").show().fadeOut("slow"); 
	    return false;
      }	
	});
});

var Main = function() {
	return {
		getURLParameter : function(name) {
			return decodeURI((RegExp(name + '=' + '(.+?)(&|$)').exec(location.search) || [ , null ])[1]);
		},
		getBaseURL : function(pathToken) {
			return location.href.substring(0, location.href.indexOf(pathToken));
		},
		getSubURL : function(pathToken) {
			return location.href.substring(location.href.lastIndexOf(pathToken)+pathToken.length);
		}
	};
};

var jAdmin = new Main();

/*
 * Date Format 1.2.3
 * (c) 2007-2009 Steven Levithan <stevenlevithan.com>
 * MIT license
 *
 * Includes enhancements by Scott Trenda <scott.trenda.net>
 * and Kris Kowal <cixar.com/~kris.kowal/>
 *
 * Accepts a date, a mask, or a date and a mask.
 * Returns a formatted version of the given date.
 * The date defaults to the current date/time.
 * The mask defaults to dateFormat.masks.default.
 */

var dateFormat = function () {
	var	token = /d{1,4}|m{1,4}|yy(?:yy)?|([HhMsTt])\1?|[LloSZ]|"[^"]*"|'[^']*'/g,
		timezone = /\b(?:[PMCEA][SDP]T|(?:Pacific|Mountain|Central|Eastern|Atlantic) (?:Standard|Daylight|Prevailing) Time|(?:GMT|UTC)(?:[-+]\d{4})?)\b/g,
		timezoneClip = /[^-+\dA-Z]/g,
		pad = function (val, len) {
			val = String(val);
			len = len || 2;
			while (val.length < len) val = "0" + val;
			return val;
		};

	// Regexes and supporting functions are cached through closure
	return function (date, mask, utc) {
		var dF = dateFormat;

		// You can't provide utc if you skip other args (use the "UTC:" mask prefix)
		if (arguments.length == 1 && Object.prototype.toString.call(date) == "[object String]" && !/\d/.test(date)) {
			mask = date;
			date = undefined;
		}

		// Passing date through Date applies Date.parse, if necessary
		date = date ? new Date(date) : new Date;
		if (isNaN(date)) throw SyntaxError("invalid date");

		mask = String(dF.masks[mask] || mask || dF.masks["default"]);

		// Allow setting the utc argument via the mask
		if (mask.slice(0, 4) == "UTC:") {
			mask = mask.slice(4);
			utc = true;
		}

		var	_ = utc ? "getUTC" : "get",
			d = date[_ + "Date"](),
			D = date[_ + "Day"](),
			m = date[_ + "Month"](),
			y = date[_ + "FullYear"](),
			H = date[_ + "Hours"](),
			M = date[_ + "Minutes"](),
			s = date[_ + "Seconds"](),
			L = date[_ + "Milliseconds"](),
			o = utc ? 0 : date.getTimezoneOffset(),
			flags = {
				d:    d,
				dd:   pad(d),
				ddd:  dF.i18n.dayNames[D],
				dddd: dF.i18n.dayNames[D + 7],
				M:    m + 1,
				MM:   pad(m + 1),
				MMM:  dF.i18n.monthNames[m],
				MMMM: dF.i18n.monthNames[m + 12],
				yy:   String(y).slice(2),
				yyyy: y,
				h:    H % 12 || 12,
				hh:   pad(H % 12 || 12),
				H:    H,
				HH:   pad(H),
				m:    M,
				mm:   pad(M),
				s:    s,
				ss:   pad(s),
				l:    pad(L, 3),
				L:    pad(L > 99 ? Math.round(L / 10) : L),
				t:    H < 12 ? "a"  : "p",
				tt:   H < 12 ? "am" : "pm",
				T:    H < 12 ? "A"  : "P",
				TT:   H < 12 ? "AM" : "PM",
				Z:    utc ? "UTC" : (String(date).match(timezone) || [""]).pop().replace(timezoneClip, ""),
				o:    (o > 0 ? "-" : "+") + pad(Math.floor(Math.abs(o) / 60) * 100 + Math.abs(o) % 60, 4),
				S:    ["th", "st", "nd", "rd"][d % 10 > 3 ? 0 : (d % 100 - d % 10 != 10) * d % 10]
			};

		return mask.replace(token, function ($0) {
			return $0 in flags ? flags[$0] : $0.slice(1, $0.length - 1);
		});
	};
}();

// Some common format strings
dateFormat.masks = {
	"default":      "ddd MMM dd yyyy HH:mm:ss",
	shortDate:      "m/d/yy",
	mediumDate:     "MMM d, yyyy",
	longDate:       "MMMM d, yyyy",
	fullDate:       "dddd, MMMM d, yyyy",
	shortTime:      "h:mm TT",
	mediumTime:     "h:mm:ss TT",
	longTime:       "h:mm:ss TT Z",
	isoDate:        "yyyy-MM-dd",
	isoTime:        "HH:mm:ss",
	isoDateTime:    "yyyy-MM-dd'T'HH:mm:ss",
	isoUtcDateTime: "UTC:yyyy-MM-dd'T'HH:mm:ss'Z'"
};

// Internationalization strings
dateFormat.i18n = {
	dayNames: [
		"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat",
		"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
	],
	monthNames: [
		"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
		"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"
	]
};

// For convenience...
Date.prototype.format = function (mask, utc) {
	return dateFormat(this, mask, utc);
};

String.prototype.endsWith = function(suffix) {
    return this.indexOf(suffix, this.length - suffix.length) !== -1;
};

//Shop Popup

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

// selection and deselection text
function selectText(el) {
	deselectText();
	if (document.selection) {
		var range = document.body.createTextRange();
		range.moveToElementText(el);
		range.select();
	}
	else if (window.getSelection) {
		var range = document.createRange();
		range.selectNode(el);
		window.getSelection().addRange(range);
	}
}

function deselectText() {
	if (document.selection) document.selection.empty(); 
	else if (window.getSelection)
            window.getSelection().removeAllRanges();
}

jQuery.fn['bounds'] = function () {
    var bounds = {
        left: Number.POSITIVE_INFINITY,
        top: Number.POSITIVE_INFINITY,
        right: Number.NEGATIVE_INFINITY,
        bottom: Number.NEGATIVE_INFINITY,
        width: Number.NaN,
        height: Number.NaN
    };

    this.each(function (i,el) {
        var elQ = $(el);
        var off = elQ.offset();
        off.right = off.left + $(elQ).width();
        off.bottom = off.top + $(elQ).height();

        if (off.left < bounds.left)
            bounds.left = off.left;

        if (off.top < bounds.top)
            bounds.top = off.top;

        if (off.right > bounds.right)
            bounds.right = off.right;

        if (off.bottom > bounds.bottom)
            bounds.bottom = off.bottom;
    });

    bounds.width = bounds.right - bounds.left;
    bounds.height = bounds.bottom - bounds.top;
    return bounds;
}

jQuery.fn['containsPoint'] = function (p) {

    var bounds = $(this).bounds();
    var contains = bounds.left < p.pageX && bounds.top < p.pageY && bounds.right > p.pageX && bounds.bottom > p.pageY ;
    return contains;
}

var stopBubble = function(e){
    if(e.stopPropagation){
        e.stopPropagation();
    } else {
        window.event.cancelBubble = true;
    }
}