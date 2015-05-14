$(function() {
	//--------------Chart Details Bar----------------//	
	$('#chartDetailsForm input[name="hasErrors"][value="true"]').each(function(){
		showChartUpdateTab.call($('#chartUpdateBar li[name="details"]')[0]);
	});
	
	$('#chartUpdateBar.nav-pills li.active').each(showChartUpdateTab);
	
	$('#chartUpdateBar li').click(showChartUpdateTab);

	$('#chartDetailsForm').each(onCreateChartEditForm);

    // Lock all tracks
    checkLockAllTracksState();
    initLockTrackCallBacks();

    $("#lockAllTracks").data('options', {onChange : function(element, active, e){
            $("#chartItemsSortable").find("> li").each(function (index) {
                $(this).find("div[class~=locked_chartItem]").toggleButtons('setState', active, false);
});
        }
    });
});

// assign onChange lock switcher handler on every chart item in the list
function initLockTrackCallBacks(){
    $("#chartItemsSortable").find("> li").each( function (index) {
        var toggleLocked = $(this).find("div[class~=locked_chartItem]");
        toggleLocked.data('options', {onChange : lockTrackCallBack});
    });
}

function lockTrackCallBack(element, active, e){
    var $lockAllTracks = $("#lockAllTracks");
    var lockAllTracksStatus = $lockAllTracks.toggleButtons('status');
    if(!active && lockAllTracksStatus){
        $lockAllTracks.toggleButtons('setState', false, true);
    }
    if(active && !lockAllTracksStatus){
        checkLockAllTracksState();
    }
}

function checkLockAllTracksState(){
    var $lockAllTracks = $("#lockAllTracks");
    var allTrackAreLocked = false;
    $("#chartItemsSortable").find("> li").each( function (index) {
        var toggleLocked = $(this).find("div[class~=locked_chartItem]");
        allTrackAreLocked = toggleLocked.toggleButtons('status');
        if(!allTrackAreLocked){
            return false;
        }

    });
    if(allTrackAreLocked) {
        $lockAllTracks.toggleButtons('setState', true, true);
    } else if ($lockAllTracks.toggleButtons('status')){
        $lockAllTracks.toggleButtons('setState', false, true);
    }
}

function lockIfAllTracksAreLocked(item){
    var areTracksLocked = $("#lockAllTracks").toggleButtons('status');
    var toggleLocked = $(item).find("div[class~=locked_chartItem]");
    if(areTracksLocked){
        toggleLocked.toggleButtons('setState', true, true);
    }
    // assign onChange lock switcher handler on just added chart item
    toggleLocked.data('options', {onChange : lockTrackCallBack});
}

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
	var position = getPosition();
	var description = chart.description;
	var imageTitle = chart.imageTitle;
	var imageFileName = chart.imageFileName;
	var imageFileUrl = filesURL + imageFileName;
	var chartDetailId = chart.chartDetailId;
	var defaultChart = chart.defaultChart;
	var chartType = chart.chartType;
    var fileNameAlias = chart.fileNameAlias;
    var badgeFileUrl = null;
    var badgeId = null;
    var fileName = null;

    if(fileNameAlias){
        badgeFileUrl = filesURL + chart.fileNameAlias.fileName;
        badgeId = fileNameAlias.id;
        fileName= fileNameAlias.fileName;
    }

	var chartEditForm = $("form#chart-edit-form");
	chartEditForm.find("input[name='id']").val(id);
	chartEditForm.find("input[name='name']").val(name);
	chartEditForm.find("input[name='subtitle']").val(subtitle);
	chartEditForm.find("input[name='badgeId']").val(badgeId);
	chartEditForm.find("input[name='position']").val(position);
	chartEditForm.find("input[name='imageTitle']").val(imageTitle);
	chartEditForm.find("input[name='description']").val(description);
	chartEditForm.find("input[name='imageFileName']").val(imageFileName);
	chartEditForm.find("input[name='chartDetailId']").val(chartDetailId);
	chartEditForm.find("input[name='defaultChart']").val(defaultChart);
	chartEditForm.find("input[name='chartType']").val(chartType);
	chartEditForm.find(".addedImage").attr('src', imageFileUrl);	
	chartEditForm.find(".badgeImage").attr('src', badgeFileUrl);
	chartEditForm.find(".badgeImage").attr('title', fileName);
};

function getPosition(){
	if(chart.position)
		return chart.position;

	if(chart.chartType == 'FOURTH_CHART')
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

var currId = -1;
var iTunesSpan = null;

function openDialog(buttonItem, mediaId){
    currId = mediaId;
    iTunesSpan = $(buttonItem).parent().parent().children('.iTunesSpan');
    var currentValue = iTunesSpan.text().trim();
    $("#iTunesUrl").val(currentValue);
    $('#change_dialog').modal();
}

function closeDialog(){
    $("#change_dialog").modal('hide');
}
function changeBtn(){
    var newValue = $("#iTunesUrl").val();
    iTunesSpan.text(newValue);
    closeDialog();
}
function cancelBtn(){
    currId = -1;
    closeDialog();
}

function validateITunesUrls(){
    var objects = chartItemsFromUl("#chartItemsSortable");
    $( "#chartItemsSortable > li" ).each(function(index) {
        $(this).removeClass("invalidITunesLink");
    });
    $.ajax({
        url : "/jadmin/validateITunesLinks.json",
        type : "post",
        contentType: "application/json; charset=utf-8",
        data: objects,
        dataType: "json",
        error: function(xhr) {
            if (xhr.status == 400) {
                var errors = jQuery.parseJSON(xhr.responseText);
                var positions = jQuery.parseJSON(errors[0].message);
                $("#chartItemsSortable > li").each(function (index) {
                    var position = $(this).children('.position_chartItem').text().trim();
                    var li = $(this);
                    jQuery.each(positions, function (i, value) {
                        if (value == position) {
                            li.addClass("invalidITunesLink");
                        }
                    });
                });
            }

            else {
                alert("Error during validation");
            }
        }
    });


}