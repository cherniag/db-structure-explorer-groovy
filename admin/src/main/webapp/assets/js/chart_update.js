$(function() {
	//--------------Chart Details Bar----------------//
	var showChartUpdateTab = function(){
		var tabName = $(this).attr('name');
		
		var hiddableEls = "#chartDetailsForm, #mediaSearchContainer, #basket, #chartItemsTable";
		var itemListEls = "#mediaSearchContainer, #basket, #chartItemsTable";
		var detailsEls = "#chartDetailsForm";
		
		var showEls = tabName == 'item_list' ? itemListEls : detailsEls;
		
		$(hiddableEls).hide();
		$(showEls).show();	
	};
	
	$('#chartUpdateBar li.active').each(showChartUpdateTab);
	
	$('#chartUpdateBar li').click(showChartUpdateTab);
	
	$('#chartDetailsForm').show(onShowChartEditForm);
});	

function onShowChartEditForm() {
	
	var id = chart.id;
	var name = chart.name;
	var subtitle = chart.subtitle;
	var imageFileName = chart.imageFileName;
	var imageFileUrl = filesURL+imageFileName;
	
	var chartEditForm = $("form#chart-edit-form");
	chartEditForm.find("input[name='id']").val(id);
	chartEditForm.find("input[name='name']").val(name);
	chartEditForm.find("input[name='subtitle']").val(subtitle);
	chartEditForm.find("input[name='imageFileName']").val(imageFileName);
	chartEditForm.find(".addedImage").attr('src', imageFileUrl);	
};

function onSaveChart() {
	var chartEditForm = $("form#chart-edit-form");
	chartEditForm.submit();
};	