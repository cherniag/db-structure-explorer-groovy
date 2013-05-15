$(function() {
	//--------------Chart Details Bar----------------//	
	$('#chartUpdateBar.nav-pills li.active').each(showChartUpdateTab);
	
	$('#chartUpdateBar li').click(showChartUpdateTab);	

	$('#chartDetailsForm').each(onCreateChartEditForm);	
});	

function showChartUpdateTab(i){
	var tabName = $(this).attr('name');	
	$(this).parent().children().each(function(){
		$(this).attr('class','');
	});
	$(this).attr('class','active');
	
	var hiddableEls = "#chartDetailsForm, #chart-edit-form, #mediaSearchContainer, #basket, #chartItemsTable .newsListWide";
	var itemListEls = "#mediaSearchContainer, #basket, #chartItemsTable .newsListWide";
	var detailsEls = "#chartDetailsForm, #chart-edit-form";
	
	var showEls = tabName == 'itemList' ? itemListEls : detailsEls;
	
	$(hiddableEls).hide();
	$(showEls).show();
};

function onCreateChartEditForm() {
	
	var id = chart.id;
	var name = chart.name;
	var subtitle = chart.subtitle;
	var position = chart.position;
	var description = chart.description;
	var imageTitle = chart.imageTitle;
	var imageFileName = chart.imageFileName;
	var imageFileUrl = filesURL+imageFileName;
	var chartDetailId = chart.chartDetailId;
	
	var chartEditForm = $("form#chart-edit-form");
	chartEditForm.find("input[name='id']").val(id);
	chartEditForm.find("input[name='name']").val(name);
	chartEditForm.find("input[name='subtitle']").val(subtitle);
	chartEditForm.find("input[name='position']").val(position);
	chartEditForm.find("input[name='imageTitle']").val(imageTitle);
	chartEditForm.find("input[name='description']").val(description);
	chartEditForm.find("input[name='imageFileName']").val(imageFileName);
	chartEditForm.find("input[name='chartDetailId']").val(chartDetailId);
	chartEditForm.find(".addedImage").attr('src', imageFileUrl);	
};

function onSaveChart() {
	var chartEditForm = $("form#chart-edit-form");
	chartEditForm.submit();
};	