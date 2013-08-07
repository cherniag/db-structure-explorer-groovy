<%--

This file is not in a js file to avoid the extra http call. When we'll merge js files, we should move this in a separate js file

 --%>
<script type="text/javascript">

var videoCheckbox = null;

$(document).ready( function(){
	videoCheckbox = new CheckboxElement();
	videoCheckbox.init();
	videoCheckbox.updateOptions();
} );

var CheckboxElement = (function(){
	
	var checkboxSelected = false;
	var checkboxId = "videoCheckbox";
	var initialized = false;
	var checkboxElem = null;
	
	updateOptions = function(){
		if ( !initialized ) return;
		
		var paymentButtons = $("div.rel");
		
		paymentButtons.each(function(){
			var attrib = $(this).attr("data-hasvideo");
			if ( typeof attrib === 'undefined' || attrib === false ) {
				return;
			}
			var videoAttr = (attrib == "1");
			
			if ( videoAttr == checkboxSelected ) {
				$(this).show();
			} else {
				$(this).hide();
			}
		});
	}
	
	init = function() {
		checkboxElem = $("#" + checkboxId);
		if ( checkboxElem.length === 0 ) {
			return;
		}
		
		checkboxSelected = checkboxElem.hasClass("button-on");
		
		initialized = true;
		
		updateOptions();
	}
	
	switchState = function() {
		if ( !initialized ) return;
		
		checkboxSelected = !checkboxSelected;
		
		if ( checkboxSelected === true ) {
			checkboxElem.removeClass("button-off");
			checkboxElem.addClass("button-on");
		} else {
			checkboxElem.removeClass("button-on");
			checkboxElem.addClass("button-off");
		}
		
		updateOptions();
	}
	
	return {
		init: init,
		updateOptions: updateOptions,
		switchState: switchState
	};
});
</script>