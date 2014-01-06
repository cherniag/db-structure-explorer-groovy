$(document).ready(function() {
    $('.selectpicker').selectpicker();
});

var Tracks = {};

function onShownTrackDetails(detailsEl) {
    if(Tracks.details){
        Tracks.details.hide();
    }

    var popover = detailsEl.data("popover");
    Tracks.details = popover;

    var id = detailsEl.find("div:first").text();

    find(id, function(track, trackId){
        var popoverContent = popover.tip();
        var trackFilesTable = popoverContent.find(".trackFiles");
        var trackTerritoriesTable = popoverContent.find(".trackTerritories");

        onFetchTrackFiles(track, track.files, trackFilesTable);
        onFetchTrackTerritories(track.territories, trackTerritoriesTable);
    }, true, true);
};

function onHiddenTrackDetails(detailsEl){
    Tracks.details = null;
}

function onFetchTrackFiles(track, data, view){
    var template = view.find(".title");
    for(var i= 0; i<data.length; i++){
        var row = template.clone();
        row.attr("class","content");
        var columns = row.children();
        $(columns[0]).text(data[i].type);
        var path = data[i].filename;

        $(columns[1]).text("");
        if(data[i].type == 'VIDEO' || data[i].type == 'DOWNLOAD'){
            $("<a>").attr("href", filesUrl+"?id="+track.mediaFileName).text(path).appendTo($(columns[1]));
        } else if(data[i].type == 'IMAGE'){
            $("<a>").attr("href", filesUrl+"?id="+track.coverFileName).text(path).appendTo($(columns[1]));
        } else {
            $(columns[1]).text(path);
        }


        row.appendTo(view);
    }

    view.find(".ui-state-disabled").detach();
}

function onFetchTrackTerritories(data, view){
    var template = view.find(".title");
    for(var i= 0; i<data.length; i++){
        var row = template.clone();
        row.attr("class","content");
        var columns = row.children();
        $(columns[0]).text(data[i].code ? data[i].code : "");
        $(columns[1]).text(data[i].distributor ? data[i].distributor : "");
        $(columns[2]).text(data[i].publisher ? data[i].publisher : "");
        $(columns[3]).text(data[i].label ? data[i].label : "");
        $(columns[4]).text(data[i].currency ? data[i].currency : "");
        $(columns[5]).text(data[i].price ? data[i].price : "");
        $(columns[6]).text(data[i].priceCode ? data[i].priceCode : "");
        $(columns[7]).text(data[i].startDate ? data[i].startDate : "");
        $(columns[8]).text(data[i].reportingId ? data[i].reportingId : "");
        $(columns[9]).text(data[i].deleted ? data[i].deleted : "false");

        row.appendTo(view);
    }

    view.find(".ui-state-disabled").detach();
}

function showEncodeDialog(trackId) {
    var id = $("#idDiv_" + trackId).text();
    var isrc = $("#isrcDiv_" + trackId).text();
    var licensed = $("#licensedDiv_" + trackId).text();
    var resolution = $("#resolutionDiv_" + trackId).text();
    var isHighRate = resolution == "RATE_96" ? true : false;

    var encodeForm = $("form#encode-form");
    encodeForm.find("input[name='id']").val(id);
    encodeForm.find("input[name='isrc']").val(isrc);
    encodeForm.find("input[name='licensed']").attr('checked', licensed);
    encodeForm.find("input[name='isHighRate']").attr('checked', isHighRate);

    $("#encode-dialog").modal("show");
};

function showPullDialog(trackId) {
    var id = $("#idDiv_" + trackId).text();
    var isrc = $("#isrcDiv_" + trackId).text();
    var title = $("#publishTitleDiv_" + trackId).text();
    title = title.length > 50 ? title.substring(0, 49).replace('&', ' ') : title.replace('&', ' ');
    var artist = $("#publishArtistDiv_" + trackId).text();
    artist = artist.length > 40 ? artist.substring(0, 39).replace('&', ' ') : artist.replace('&', ' ');
    var info = $("#infoDiv_" + trackId).text();
    var iTunesUrl = $("#iTunesUrlDiv_" + trackId).text();
    var amazonUrl = $("#amazonUrlDiv_" + trackId).text();
    var areArtistUrls = $("#areArtistUrlsDiv_" + trackId).text();

    var pullForm = $("form#pull-form");
    pullForm.find("input[name='id']").val(id);
    pullForm.find("input[name='isrc']").val(isrc);
    pullForm.find("input[name='title']").val(title);
    pullForm.find("input[name='artist']").val(artist);
    pullForm.find("input[name='info']").val(info);
    pullForm.find("input[name='itunesUrl']").val(iTunesUrl);
    pullForm.find("input[name='areArtistUrls']").attr('checked', (areArtistUrls == "true"));
    var itunesLink = pullForm.find("a[name='itunesLink']");
    itunesLink.text(iTunesUrl);
    itunesLink.attr('href', iTunesUrl);
    pullForm.find("input[name='amazonUrl']").val(amazonUrl);
    var itunesLink = pullForm.find("a[name='amazonLink']");
    itunesLink.text(amazonUrl);
    itunesLink.attr('href', amazonUrl);

    $("#pull-dialog").modal("show");
};

function onEncode() {
    var encodeForm = $("form#encode-form");
    var resolution = encodeForm.find("input[name='isHighRate']").attr('checked') ? "RATE_96" : "RATE_48";
    var trackId = encodeForm.find("input[name='id']").val();
    encodeForm.find("input[name='resolution']").val(resolution);

    onEncodeStart(trackId);

    $.ajax({
        url: encodeUrl,
        type: "post",
        data: encodeForm.serialize(),
        success: function (data) {
            onEncodeSuccess(data, trackId);
        }
    }).fail(function (data, x, e) {
          onEncodeFail(data, x, e, trackId);
    });
};

function onEncodeStart(trackId){
    var pulltrackButtonId = "#pulltrackButton_" + trackId;
    var encodetrackButtonId = "#encodetrackButton_" + trackId;

    $(encodetrackButtonId+".btn-primary").button("loading");
    $(pulltrackButtonId+".btn-primary").attr("disabled", true);
}

function find(trackId, callback, withFiles, withTerritories){
    var params = {"trackIds[0]":trackId,"withFiles":withFiles,"withTerritories":withTerritories};

    $.ajax({
        url: findUrl,
        type: "get",
        data: params,
        success: function (data) {
            var track = data.PAGE_LIST_DTO.list[0];

            callback(track, trackId);
        }
    });
}

function onEncodeSuccess(data, trackId){
    var status = data.status;

    if(status == "ENCODED"){
        var pulltrackButtonId = "#pulltrackButton_" + trackId;
        var encodetrackButtonId = "#encodetrackButton_" + trackId;
        var publishTitleDivId = "#publishTitleDiv_" + trackId;
        var publishArtistDivId = "#publishArtistDiv_" + trackId;
        var iTunesDivId = "#iTunesUrlDiv_" + trackId;
        var infoDivId = "#infoDiv_" + trackId;

        $(encodetrackButtonId+".btn-primary").button('retry');
        $(pulltrackButtonId+".btn-primary").removeAttr("disabled");

        $(publishTitleDivId).text(data.publishTitle);
        $(publishArtistDivId).text(data.publishArtist);
        $(iTunesDivId).text(data.itunesUrl);
        $(infoDivId).text(data.info);
    } else if(status == "ENCODING"){
        setTimeout(function(){
           find(trackId, onEncodeSuccess, false, false);
        }, encodeCheckDelay);
    } else {
        data.responseText = '{"internal_error" : "'+ defaultEncodeErrorMsg +'"}';

        onEncodeFail(data, null, null, trackId);
    }
}

function onEncodeFail(data, x, e, trackId) {
    var encodetrackButtonId = "#encodetrackButton_" + trackId;

    try {
        var error = $.parseJSON(data.responseText);
        if (error.external_error)
            alert(error.external_error);
        else if (error.internal_error)
            alert(error.internal_error);
    } catch (ex) {
        if (e != null && e != '')
            alert(e);
    }
    $(encodetrackButtonId+".btn-primary").button('original');
}

function onPull() {
    var pullForm = $("form#pull-form");
    var trackId = pullForm.find("input[name='id']").val();

    onPullStart(trackId);

    $.ajax({
        url: pullUrl,
        type: "post",
        data: pullForm.serialize(),
        success: function (data) {
            onPullSuccess(data,trackId);
        }
    }).fail(function(data, x, e) {
        onPullFail(data,x,e,trackId);
    });
};

function onPullStart(trackId){
    var pulltrackButtonId = "#pulltrackButton_" + trackId;
    var encodetrackButtonId = "#encodetrackButton_" + trackId;

    $(encodetrackButtonId+".btn-primary").attr("disabled", true);
    $(pulltrackButtonId+".btn-primary").button("loading");
}

function onPullSuccess(data, trackId){
    var status = data.status;

    if(status == "PUBLISHED"){
        var pulltrackButtonId = "#pulltrackButton_" + trackId;
        var encodetrackButtonId = "#encodetrackButton_" + trackId;
        var publishDateDivId = "#publishDateDiv_" + trackId;
        var publishTitleDivId = "#publishTitleDiv_" + trackId;
        var publishArtistDivId = "#publishArtistDiv_" + trackId;
        var iTunesDivId = "#iTunesUrlDiv_" + trackId;
        var amazonDivId = "#amazonUrlDiv_" + trackId;
        var infoDivId = "#infoDiv_" + trackId;
        var areArtistUrls = "#areArtistUrlsDiv_" + trackId;

        $(encodetrackButtonId+".btn-primary").removeAttr("disabled");
        $(pulltrackButtonId+".btn-primary").button("original");

        var publishDate = new Date(data.publishDate);
        $(publishDateDivId).text(dateFormat(publishDate, $dateformat));
        $(publishTitleDivId).text(data.publishTitle);
        $(publishArtistDivId).text(data.publishArtist);
        $(iTunesDivId).text(data.itunesUrl);
        $(amazonDivId).text(data.amazonUrl);
        $(areArtistUrls).text(data.areArtistUrls);
        $(infoDivId).text(data.info);
    } else if(status == "PUBLISHING"){
        setTimeout(function(){
            find(trackId, onPullSuccess, false, false);
        }, pullCheckDelay);
    } else {
        data.responseText = '{"internal_error" : "'+ defaultPullErrorMsg +'"}';

        onPullFail(data, null, null, trackId);
    }
}

function onPullFail(data, x, e, trackId) {
    var pulltrackButtonId = "#pulltrackButton_" + trackId;
    var encodetrackButtonId = "#encodetrackButton_" + trackId;

    try {
        var error = $.parseJSON(data.responseText);
        if (error.external_error)
            alert(error.external_error);
        else if (error.internal_error)
            alert(error.internal_error);
    } catch (ex) {
        if (e != null && e != '')
            alert(e);
    }
    $(encodetrackButtonId+".btn-primary").removeAttr("disabled");
    $(pulltrackButtonId+".btn-primary").button("original");
}

function preEncodeAll(){

    var fl = false;
    var labels = $(".for-encode");
    for (var i = 0; i < labels.size(); i++) {
        var label = $(labels[i]);
        if (label.find('[name=isEncoded]').attr("checked") == "checked") {
            fl=true;
            break;
        }
    }

    if (fl==false){
        alert("No tracks have been selected");
        return;
    }

    clearErrList();
    $("#header-label").text("Encode");
    var dialog=$("#encode-finished");
    $("#btnEncodeAllOk").click(toEncode);
    dialog.find("div.controls").show();
    dialog.modal("show");
}

function toEncode(){
    var tracks =[];
    var labels = $(".for-encode");
    var resolution = $("#encode-finished").find("input[name='isHighRate']").attr('checked') ? "RATE_96" : "RATE_48";
    var license = $("#encode-finished").find("input[name='licensed']").attr('checked') ? "on" : "off";

    for (var i = 0; i < labels.size(); i++) {
        var label = $(labels[i]);
        if (label.find('[name=isEncoded]').attr("checked") == "checked") {
            var data = {
                id: label.find('[name=encId]').val(),
                isrc: label.find('[name=encIsrc]').val(),
                resolution: resolution,
                license: license
            };
            onEncodeStart(label.find('[name=encId]').val());
            tracks.push(data);
        }
    }

    var jsobj = null;

    jsobj = {TRACK_DTO:tracks};
    $.ajax({
        url: "/jadmin/tracks/encode2.json",
        type: "post",
        dataType: "json",
        data: jsobj,
        success: function (data) {
            clearErrList();
            var obj = $("#errList");

            //pure win !
            if ((data.success.length!=0) && (data.fail.length==0)){
                $("#header-label").text("Encoding successful");
                obj.append("<div>All tracks have been encoded</div>");
            } else {
            //with fails
                $("#header-label").text("Track(s) encoding failed");
                for (i=0; i<data.fail.length; i++){
                    obj.append("<div>"+"id="+data.fail[i].id+" isrc="+data.fail[i].isrc+"</div>");
                }
            }

            //set fields and buttons
            if (data.success.length != 0) {
                for (var i = 0; i < data.success.length; i++) {
                    var trackId = data.success[i].id;

                    var pulltrackButtonId = "#pulltrackButton_" + trackId;
                    var encodetrackButtonId = "#encodetrackButton_" + trackId;
                    var publishTitleDivId = "#publishTitleDiv_" + trackId;
                    var publishArtistDivId = "#publishArtistDiv_" + trackId;
                    var iTunesDivId = "#iTunesUrlDiv_" + trackId;
                    var infoDivId = "#infoDiv_" + trackId;

                    $(encodetrackButtonId + ".btn-primary").button('retry');
                    $(pulltrackButtonId + ".btn-primary").removeAttr("disabled");

                    $(publishTitleDivId).text(data.success[i].publishTitle);
                    $(publishArtistDivId).text(data.success[i].publishArtist);
                    $(iTunesDivId).text(data.success[i].itunesUrl);
                    $(infoDivId).text(data.success[i].info);
                }
            }

            if (data.fail.length!=0){
                for (var i = 0; i < data.fail.length; i++) {
                    var encodeTrackButtonId = "#encodetrackButton_" + data.fail[i].id;
                    $(encodeTrackButtonId+".btn-primary").button('original');
                }
            }

            var dialog=$("#encode-finished");
            $("#btnEncodeAllOk").off("click");
            dialog.find("div.controls").hide();
            dialog.modal("show");
        }
    }).fail(function (data) {
            var error = $.parseJSON(data.responseText);
            if (error != null) {
                if (error.external_error)
                    alert(error.external_error);
                else if (error.internal_error)
                    alert(error.internal_error);
            }
        });
}

function checkAll(){
    var labels = $(".for-encode");
    var etalon = $('[name=allEnc]');

    var fl = etalon.attr("checked") == "checked";
    for (var i = 0; i < labels.size(); i++) {
      var  label = $(labels[i]);
        label.find('[name=isEncoded]').attr("checked", fl);
    }
}

function clearErrList(){
   var obj = $("#errList");
   obj.find('div').remove();

}