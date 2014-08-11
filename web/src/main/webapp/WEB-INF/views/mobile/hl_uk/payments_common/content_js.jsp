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
	var initialized = false;
	var checkboxElemOn = null;
	var checkBoxElemOff = null;
	
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
		checkboxElemOn = $("#videoCheckboxOn");;
		checkBoxElemOff = $("#videoCheckboxOff");;
		if ( checkboxElemOn.length === 0 || checkBoxElemOff.length === 0 ) {
			return;
		}
		
		checkboxSelected = checkboxElemOn.is(":visible");
		
		initialized = true;
		
		updateOptions();
	}
	
	switchState = function() {
		if ( !initialized ) return;
		
		checkboxSelected = !checkboxSelected;
		
		if ( checkboxSelected === true ) {
			checkBoxElemOff.hide();
			checkboxElemOn.show();
			/* checkboxElem.removeClass("button-off");
			checkboxElem.addClass("button-on"); */
		} else {
			/* checkboxElem.removeClass("button-on");
			checkboxElem.addClass("button-off"); */
			checkBoxElemOff.show();
			checkboxElemOn.hide();
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