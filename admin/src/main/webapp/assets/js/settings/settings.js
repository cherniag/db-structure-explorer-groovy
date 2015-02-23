if(Settings == undefined) {
    var Settings = {};
    Settings.Controller = new function() {
        var self = this;
        var model, copy;
        var opts;
        var progress = false;

        //
        // Main point
        //
        this.init = function(options) {
            if(progress) return;
            progress = true;

            opts = options;

            $.ajax({
                url : options.getUrl,
                dataType: 'json',
                contentType: 'application/json',
                type: "GET",
                // fill the filter value for some versions of Chrome browser to exclude '__proto__' property
                // data : JSON.stringify(modelToSend, ["id", "timestamp", "userNames", "blocks", "contentType", "coverUrl", "badgeId", "included", "key", "value", "position", "shapeType", "subTitle", "title", "vip", "expanded", "player"]),
                success : function(data, textStatus, status) {
                    if(data == null) {
                        alert('Settings for this community is not supported');
                        progress = false;
                        return;
                    }

                    // init model
                    model = data;
                    copy = JSON.parse(JSON.stringify(data));

                    // rendering
                    render(options.renderTo);

                    for(var i = 0; i < model.behaviorConfigTypes.length ; i++){
                        var configType = model.behaviorConfigTypes[i];

                        $('#switchConfigTypeBlock').append('<button class="btn" type="submit" id="switchTo' + configType + '"></button>');
                        var switchButton = $('#switchTo' + configType);
                        switchButton.text(configType);

                        if(configType == model.behaviorConfigType){
                            switchButton.addClass("btn-success");
                        } else {
                            switchButton.addClass("btn-default");
                            switchButton.click(getSwitchToConfigTypeHandler(switchButton, configType));
                        }
                    }

                    $('#saveId').click(save);

                    // editing
                    Editors.assign(Settings.Controller.model(), options.renderTo);

                    progress = false;
                },
                error: function(qXHR) {
                    progress = false;
                    alert('Failed to update: please refresh the page and try again');
                }
            });

            return self;
        };

        this.model = function() {
            return model;
        };

        //
        // Internals
        //

        function getSwitchToConfigTypeHandler(button, behaviorConfigType){
            return function(){ switchToConfigType(button, behaviorConfigType); }
        }

        function switchToConfigType(button, behaviorConfigType) {
            button.text('Please wait...');

            if(progress) return;
            progress = true;

            $.ajax({
                url : opts.switchUrl,
                dataType: 'json',
                data: JSON.stringify(behaviorConfigType),
                contentType: 'application/json',
                type: "POST",
                success : function(data, textStatus, status) {
                    alert('Switched successfully. Page will be reloaded');
                    progress = false;
                    location.reload(true)
                },
                error: function(qXHR) {
                    progress = false;
                    alert('Failed to switch: please refresh the page and try again');
                }
            });

        }

        function save() {
            if(progress) return;

            progress = true;

            $.ajax({
                url : opts.saveUrl,
                dataType: 'json',
                data: JSON.stringify(model),
                contentType: 'application/json',
                type: "POST",
                success : function(data, textStatus, status) {
                    alert('Saved successfully. Page will be reloaded');
                    progress = false;
                    location.reload(true)
                },
                error: function(qXHR) {
                    progress = false;
                    alert('Failed to update: please refresh the page and try again');
                }
            });
        }

        //
        // Set of render methods
        //
        function render(rootSettingsContentId) {
            rootSettingsContentId.empty().html(
                Template.renderDom(
                    'rootTemplateId',
                    {
                        data: model,
                        view: {
                            playlistTypeSettings: playlistTypeSettings(model.playlistTypeSettings),
                            playlistSettings: playlistSettings(model.playlistSettings),
                            favouriteSettings: favouriteSettings(model),
                            adSettings: adSettings(model)
                        }
                    }
                )
            );
        }

        function favouriteSettings(model) {
            var favourites = model.favourites;
            var html = '';
            for(var favourite in favourites) {
                if(favourites.hasOwnProperty(favourite)) {
                    html += Template.renderDom(
                        'favouriteSettingsId',
                        {
                            userStatusTitle: model.i18n[favourite],
                            userStatus: favourite,
                            contentBehaviorType: favourites.favourite
                        }
                    );
                }
            }
            return html;
        }

        function adSettings(model) {
            var ads = model.ads;
            var html = '';
            for(var ad in ads) {
                if(ads.hasOwnProperty(ad)) {
                    html += Template.renderDom(
                        'adSettingsId',
                        {
                            userStatusTitle: model.i18n[ad],
                            userStatus: ad,
                            contentBehaviorType: ads.ad
                        }
                    );
                }
            }
            return html;
        }

        function playlistTypeSettings(playlistTypeSettings) {
            var model = Settings.Controller.model();
            var html = '';
            for(var playlistTypeSetting in playlistTypeSettings) {
                if(playlistTypeSettings.hasOwnProperty(playlistTypeSetting)) {
                    var playlistType = playlistTypeSetting;
                    var data = playlistTypeSettings[playlistType];
                    var maxTracks = data.maxTracks;
                    var skipTracks = data.skipTracks;

                    html += Template.renderDom(
                        'playlistTypeSettingsId',
                        {
                            typeTitle: model.i18n[playlistType],
                            type: playlistType,
                            offline: data.offline,
                            playTrackSeconds:  (data.metaInfo.tracksPlayDurationSupported)?Template.renderDom('playTrackSecondsId', {
                                type: playlistType, playTrackSeconds: data.playTrackSeconds
                            }):'',
                            maxTracks: (data.metaInfo.tracksInfoSupported)?Template.renderDom('playlistMaxTrackSettingsId', {
                                type: playlistType, number: (maxTracks) ? maxTracks.number : undefined
                            }):'',
                            skipTracks: (data.metaInfo.tracksInfoSupported)?Template.renderDom('playlistSkipTrackSettingsId', {
                                type: playlistType, number: (skipTracks) ? skipTracks.number : undefined
                            }):''
                        }
                    );
                }
            }
            return html;
        }

        function playlistSettings(playlistSettings) {
            var model = Settings.Controller.model();
            var html = '';
            for(var playlistSetting in playlistSettings) {
                if(playlistSettings.hasOwnProperty(playlistSetting)) {
                    var chartId = playlistSetting;
                    var info = playlistSettings[chartId];

                    html += Template.renderDom(
                        'playlistSettingsId',
                        {
                            id: chartId,
                            title: model.playlistInfo[chartId].name + ' / ' + model.playlistInfo[chartId].subtitle,
                            src: opts.imageURL + model.playlistInfo[chartId].imageFileName,
                            playlistTypeSettings: statusToInfoMapping(chartId, info)
                        }
                    );
                }
            }
            return html;
        }

        function statusToInfoMapping(id, infos) {
            var model = Settings.Controller.model();
            var html = '';


            for(var info in infos) {
                if(infos.hasOwnProperty(info)) {
                    var status = info;

                    html += Template.renderDom(
                        'playlistInfoId',
                        {
                            id: id,
                            statusTitle: model.i18n[status],
                            status: status,
                            typeTitle: model.i18n[infos[info].chartBehaviorType],
                            type: infos[info].chartBehaviorType,
                            info: Template.renderDom(
                                'playlistInfoValueId',
                                {
                                    id: id,
                                    status: status,
                                    typeTitle: model.i18n[infos[info].chartBehaviorType],
                                    type: infos[info].chartBehaviorType,
                                    locked: status.locked,
                                    page: status.page,
                                    action: status.action
                                }
                            )
                        }
                    );
                }
            }
            return html;
        }
    };
}

