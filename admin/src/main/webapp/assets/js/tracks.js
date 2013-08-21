var Tracks = {};

function onShownTrackDetails(detailsEl) {
    if(Tracks.details){
        Tracks.details.hide();
    }

    var popover = detailsEl.data("popover");
    Tracks.details = popover;

    var id = detailsEl.find("div:first").text();
    var params = {"trackIds[0]":id,"withFiles":true,"withTerritories":true};

    $.ajax({
        url: findUrl,
        type: "get",
        data: params,
        success: function (data) {
            var track = data.PAGE_LIST_DTO.list[0];
            var popoverContent = popover.tip();
            var trackFilesTable = popoverContent.find(".trackFiles");
            var trackTerritoriesTable = popoverContent.find(".trackTerritories");

            onFetchTrackFiles(track, track.files, trackFilesTable);
            onFetchTrackTerritories(track.territories, trackTerritoriesTable);
        }
    });
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
    $('.busy').show();
}

function onEncodeSuccess(data, trackId){
    var pulltrackButtonId = "#pulltrackButton_" + trackId;
    var encodetrackButtonId = "#encodetrackButton_" + trackId;
    var publishTitleDivId = "#publishTitleDiv_" + trackId;
    var publishArtistDivId = "#publishArtistDiv_" + trackId;
    var iTunesDivId = "#iTunesUrlDiv_" + trackId;
    var infoDivId = "#infoDiv_" + trackId;

    $(encodetrackButtonId+".btn-primary").button('retry');
    $(pulltrackButtonId+".btn-primary").removeAttr("disabled");

    $(publishTitleDivId).text(data.TRACK_DTO.title);
    $(publishArtistDivId).text(data.TRACK_DTO.artist);
    $(iTunesDivId).text(data.TRACK_DTO.itunesUrl);
    $(infoDivId).text(data.TRACK_DTO.publishDate);

    $('.busy').hide();
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
    $('.busy').hide();
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
    $('.busy').show();
}

function onPullSuccess(data, trackId){
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

    var publishDate = new Date(data.TRACK_DTO.publishDate);
    $(publishDateDivId).text(dateFormat(publishDate, $dateformat));
    $(publishTitleDivId).text(data.TRACK_DTO.title);
    $(publishArtistDivId).text(data.TRACK_DTO.artist);
    $(iTunesDivId).text(data.TRACK_DTO.itunesUrl);
    $(amazonDivId).text(data.TRACK_DTO.amazonUrl);
    $(areArtistUrls).text(data.TRACK_DTO.areArtistUrls);
    $(infoDivId).text(data.TRACK_DTO.info);

    $('.busy').hide();
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

    $('.busy').hide();
}