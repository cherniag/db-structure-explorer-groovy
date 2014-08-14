$(function() {
    $('#messageType').change(function() {
        $('div.controls input[name=filterDtos]').removeAttr('disabled');
        $('div.controls input[type=radio]').removeAttr('disabled');

        var value = $("#messageType :selected").val();
        if (value=='RICH_POPUP') {
            $("#frequence2").attr('disabled', false);
            $("#actionType").attr('disabled', false);
            $("#actionButtonText").attr('disabled', false);
            tinyMCE.init({
                mode : "textareas",
                theme : "advanced",
                plugins :"lists, searchreplace, wordcount",
                // Theme options
                theme_advanced_buttons1 : "bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull,formatselect,|,bullist,numlist,|,code,|,forecolor,backcolor"
            });
        } else if(value=='LIMITED_BANNER' || value=='SUBSCRIBED_BANNER' || value=='FREE_TRIAL_BANNER') {
            $('div.controls input[name=filterDtos]').attr('disabled', true);
            $('div.controls input[type=radio]').attr('disabled', true);
            $("#actionType").attr('disabled', false);
            $("#actionButtonText").attr('disabled', true);
            $("#headline").attr('disabled', true);
            tinymce.execCommand('mceRemoveControl', true, 'editableTextArea');
        } else {
            $("#frequence2").attr('disabled', true);
            $("#action").attr('disabled', true);
            $("#actionType").attr('disabled', true);
            $("#actionButtonText").attr('disabled', true);
            $("#editableTextArea_tbl").detach();
            $("#editableTextArea").show();
        }
    }).change();
    $('#actionType').change(onActionType).change();
});

var onActionType = function() {
    var value = $("#actionType :selected").val();
    if (value=='A_SPECIFIC_NEWS_STORY'||value=='A_SPECIFIC_TRACK'||value=='EXTERNAL_URL'||value=='MOBILE_WEB_PORTAL'){
        $("#action").attr('disabled', false);
    }else{
        $("#action").attr('disabled', true);
    }
};