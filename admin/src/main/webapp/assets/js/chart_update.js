$(function() {
	//--------------Chart Details Bar----------------//	
	$('#chartDetailsForm input[name="hasErrors"][value="true"]').each(function(){
		showChartUpdateTab.call($('#chartUpdateBar li[name="details"]')[0]);
	});
	
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
	var position = chart.getPosition();
	var description = chart.description;
	var imageTitle = chart.imageTitle;
	var imageFileName = chart.imageFileName;
	var imageFileUrl = filesURL+imageFileName;
	var chartDetailId = chart.chartDetailId;
	var chartType = chart.chartType;
	
	var chartEditForm = $("form#chart-edit-form");
	chartEditForm.find("input[name='id']").val(id);
	chartEditForm.find("input[name='name']").val(name);
	chartEditForm.find("input[name='subtitle']").val(subtitle);
	chartEditForm.find("input[name='position']").val(position);
	chartEditForm.find("input[name='imageTitle']").val(imageTitle);
	chartEditForm.find("input[name='description']").val(description);
	chartEditForm.find("input[name='imageFileName']").val(imageFileName);
	chartEditForm.find("input[name='chartDetailId']").val(chartDetailId);
	chartEditForm.find("input[name='chartType']").val(chartType);
	chartEditForm.find(".addedImage").attr('src', imageFileUrl);	
};

function getPosition(){
	if(chart.position)
		return chart.position;

	if(chart.type = 'FOURTH_CHART')
		return 0;
	
	var max = 0;
	for(i in chartList){
		var pos = chartList[i].position;
		if(pos != null && pos > max)
			max = pos;
	}
	
	return max+1;
}

function onSaveChart() {
	var chartEditForm = $("form#chart-edit-form");
	chartEditForm.submit();
};	