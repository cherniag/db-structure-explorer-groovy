if(Streamzine.Presenter.Editor == undefined) {
    Streamzine.Presenter.Editor = new function() {
        var _generator = 0;
        var _currentInEdit;

        //
        // Widget Property editor
        //
        var Widget = function(template, data) {
            var _id = 'widget_id_' + _generator++;

            this.getId = function() {
                return _id;
            }

            this.render = function(o) {
                var notVisibleBadge = true;
                var rules = Streamzine.Presenter.Editor.badgeMappingRules;
                if(rules[o.shapeType.$name]) {
                    var subTypes = rules[o.shapeType.$name][o.contentType.$name];
                    if(subTypes) {
                        for(var i=0; i < subTypes.length; i++) {
                            if(subTypes[i] == o.key) {
                                notVisibleBadge = false;
                                break;
                            }
                        }
                    }
                }

                $('div.streamzinePropertiesEditor > div')
                    .empty()
                    .html(Template.render(template, {
                        id: _id,
                        notVisibleBadge: (notVisibleBadge) ? 'sz-not-visible' : '',
                        notVisibleTitle: (Streamzine.Presenter.Editor.titlesMappingRules[o.shapeType.$name].title) ? '' : 'sz-not-visible',
                        notVisibleSubTitle: (Streamzine.Presenter.Editor.titlesMappingRules[o.shapeType.$name].subTitle) ? '' : 'sz-not-visible'
                    }));
            }

            this.update = function(o) {
                var r = o;
                if(data && data.preProcess) {
                    r = data.preProcess.call(this, o);
                }

                for(var m in r) {
                    if(r.hasOwnProperty(m)) {
                        var e = $('#' + _id + '_' + m);

                        if(e.length > 0) {
                            if(e.get(0).tagName == 'INPUT') {
                                if(e.attr('type').toLowerCase() == "checkbox") {
                                    if(r[m]) {
                                        e.attr('checked', 'checked');
                                    } else {
                                        e.removeAttr('checked')
                                    }
                                } else {
                                    e.val(r[m]);
                                }
                                continue;
                            }
                            if(e.get(0).tagName == 'IMG') {
                                var thisIsCover = e.attr('id').indexOf('_coverUrl') > 0;
                                if(r[m] && r[m] != Streamzine.Presenter.Editor.imagesBaseUrl) {
                                    e.attr('src', r[m]);
                                    e.removeAttr('alt').removeAttr('class').addClass( ((thisIsCover)?'sz-cover-url-editor':'sz-badge-url-editor') );
                                } else {
                                    if(m == 'coverUrl') {
                                        e.attr('alt', 'No image');
                                    } else {
                                        e.attr('alt', 'No badge');
                                    }
                                    e.removeAttr('class').addClass( ((thisIsCover)?'sz-no-cover-url-editor':'sz-no-badge-url-editor') );
                                }
                                continue;
                            }
                            e.html(r[m]);
                        }
                    }
                }
            }

            this.clearErrors = function() {
                var fieldsHolder = Streamzine.Model.createEmptyBlock();

                for(var f in fieldsHolder) {
                    if(fieldsHolder.hasOwnProperty(f)) {
                        $('#' + _id + '_' + f).removeClass('streamzine-error-field');
                    }
                }

                // in case of internal ad
                $('#' + _id + '_valueAction').removeClass('streamzine-error-field');
                $('#' + _id + '_valuePage').removeClass('streamzine-error-field');

                // in case of external ad
                $('#' + _id + '_valueLink').removeClass('streamzine-error-field');
                $('#' + _id + '_valueOpener').removeClass('streamzine-error-field');

            }

            this.showError = function(name, block) {
                this.clearErrors();
                $('#' + _id + '_' + name).addClass('streamzine-error-field');

                if(name == 'value' && block.contentType.$name == 'PROMOTIONAL' && block.key == 'INTERNAL_AD') {
                    $('#' + _id + '_valueAction').addClass('streamzine-error-field');
                    $('#' + _id + '_valuePage').addClass('streamzine-error-field');
                }
            }
        }

        var _widgets = {
            MUSIC: {
                TRACK: new Widget(
                    '<input id="{id}_vip" type="checkbox" title="VIP" onclick="Streamzine.Presenter.Editor.onChange(\'vip\', !!$(this).attr(\'checked\'))" />VIP' +
                    '<div><a href="javascript:;" id="{id}_valuePicker" onclick="Events.fire(\'VALUE_PICKING\', \'value\')">Track Search</a></div>' +
                    '<div id="{id}_value" class="sz-editor-value"></div>' +
                    '<input placeholder="Title" maxlength="255" type="text" class="{notVisibleTitle}" id="{id}_title" onkeydown="Streamzine.Presenter.Editor.onChange(\'title\', this.value)" onkeyup="Streamzine.Presenter.Editor.onChange(\'title\', this.value)" onblur="Streamzine.Presenter.Editor.onChange(\'title\', this.value)" />' +
                    '<input placeholder="Subtitle" maxlength="255" type="text" class="{notVisibleSubTitle}" id="{id}_subTitle" onkeydown="Streamzine.Presenter.Editor.onChange(\'subTitle\', this.value)" onkeyup="Streamzine.Presenter.Editor.onChange(\'subTitle\', this.value)" onblur="Streamzine.Presenter.Editor.onChange(\'subTitle\', this.value)"  />' +
                    '<div class="sz-badge-url-editor-wrapper {notVisibleBadge}">' +
                    '<img id="{id}_badgeId" class="sz-no-badge-url-editor" onclick="Events.fire(\'VALUE_PICKING\', \'badgeFileNameAlias\')"/>' +
                    '</div>' +
                    '<div class="sz-cover-url-editor-wrapper">' +
                    '<img id="{id}_coverUrl" class="sz-no-cover-url-editor" onclick="Events.fire(\'VALUE_PICKING\', \'coverUrl\')"/>' +
                    '</div>', {
                        preProcess: function(incoming) {
                            var o = {
                                value: incoming.value,
                                title: incoming.title,
                                subTitle: incoming.subTitle,
                                value: '',
                                badgeId: Streamzine.Presenter.Editor.imagesBaseUrl + ( (incoming.badgeFileNameAlias) ? ('/' + incoming.badgeFileNameAlias.fileName) : '' ),
                                coverUrl: Streamzine.Presenter.Editor.imagesBaseUrl + ( (incoming.coverUrl) ? ('/' + incoming.badgeFileNameAlias.fileName) : '' ),
                                vip: incoming.vip
                            };
                            if(incoming.data && incoming.data.artistDto) {
                                o.value = incoming.title + ' - ';
                                o.value+=(incoming.data.artistDto.name) ? incoming.data.artistDto.name: '';
                            } else {
                                o.value = '';
                            }
                            return o;
                        }
                    })
                ,
                PLAYLIST: new Widget(
                    '<input id="{id}_vip" type="checkbox" title="VIP" onclick="Streamzine.Presenter.Editor.onChange(\'vip\', !!$(this).attr(\'checked\'))" />VIP' +
                    '<div><a href="javascript:;" id="{id}_valuePicker" onclick="Events.fire(\'VALUE_PICKING\', \'value\')">Select Playlist</a></div>' +
                    '<div id="{id}_value" class="sz-editor-value"></div>' +
                    '<input placeholder="Title" maxlength="255" type="text" class="{notVisibleTitle}" id="{id}_title" onkeydown="Streamzine.Presenter.Editor.onChange(\'title\', this.value)" onkeyup="Streamzine.Presenter.Editor.onChange(\'title\', this.value)" onblur="Streamzine.Presenter.Editor.onChange(\'title\', this.value)" />' +
                    '<input placeholder="Subtitle" maxlength="255" type="text" class="{notVisibleSubTitle}" id="{id}_subTitle" onkeydown="Streamzine.Presenter.Editor.onChange(\'subTitle\', this.value)" onkeyup="Streamzine.Presenter.Editor.onChange(\'subTitle\', this.value)" onblur="Streamzine.Presenter.Editor.onChange(\'subTitle\', this.value)"  />' +
                    '<div class="sz-badge-url-editor-wrapper {notVisibleBadge}">' +
                    '<img id="{id}_badgeId" class="sz-no-badge-url-editor" onclick="Events.fire(\'VALUE_PICKING\', \'badgeId\')"/>' +
                    '</div>' +
                    '<div class="sz-cover-url-editor-wrapper">' +
                    '<img id="{id}_coverUrl" class="sz-no-cover-url-editor" onclick="Events.fire(\'VALUE_PICKING\', \'coverUrl\')"/>' +
                    '</div>', {
                        preProcess: function(incoming) {
                            var amount = (incoming.data && incoming.data.tracksCount) ? incoming.data.tracksCount : 0;
                            return {
                                value: incoming.value,
                                title: incoming.title,
                                subTitle: incoming.subTitle,
                                value: '' + amount + (  (amount == 1)?' Track':' Tracks'  ),
                                badgeId: Streamzine.Presenter.Editor.imagesBaseUrl + ( (incoming.badgeFileNameAlias) ? ('/' + incoming.badgeFileNameAlias.fileName) : '' ),
                                coverUrl: Streamzine.Presenter.Editor.imagesBaseUrl + ( (incoming.coverUrl) ? ('/' + incoming.badgeFileNameAlias.fileName) : '' ),
                                vip: incoming.vip
                            }
                        }
                    })
                ,
                MANUAL_COMPILATION: new Widget(
                    '<div><a href="javascript:;" id="{id}_valuePicker" onclick="Events.fire(\'VALUE_PICKING\', \'value\')">Search Tracks</a></div>' +
                    '<div id="{id}_value" class="sz-editor-value"></div>' +
                    '<input placeholder="Title" maxlength="255" type="text" class="{notVisibleTitle}" id="{id}_title" onkeydown="Streamzine.Presenter.Editor.onChange(\'title\', this.value)" onkeyup="Streamzine.Presenter.Editor.onChange(\'title\', this.value)" onblur="Streamzine.Presenter.Editor.onChange(\'title\', this.value)" />' +
                    '<input placeholder="Subtitle" maxlength="255" type="text" class="{notVisibleSubTitle}" id="{id}_subTitle" onkeydown="Streamzine.Presenter.Editor.onChange(\'subTitle\', this.value)" onkeyup="Streamzine.Presenter.Editor.onChange(\'subTitle\', this.value)" onblur="Streamzine.Presenter.Editor.onChange(\'subTitle\', this.value)"  />' +
                    '<div class="sz-badge-url-editor-wrapper {notVisibleBadge}">' +
                    '<img id="{id}_badgeId" class="sz-no-badge-url-editor" onclick="Events.fire(\'VALUE_PICKING\', \'badgeId\')"/>' +
                    '</div>' +'<div class="sz-cover-url-editor-wrapper">' +
                    '<img id="{id}_coverUrl" class="sz-no-cover-url-editor" onclick="Events.fire(\'VALUE_PICKING\', \'coverUrl\')"/>' +
                    '</div>', {
                        preProcess: function(incoming) {
                            var amount = (incoming.data && incoming.data.length) ? incoming.data.length : 0;
                            return {
                                value: incoming.value,
                                title: incoming.title,
                                subTitle: incoming.subTitle,
                                value: '' + amount + (  (amount == 1)?' Track':' Tracks'  ),
                                coverUrl: Streamzine.Presenter.Editor.imagesBaseUrl + ( (incoming.coverUrl) ? ('/' + incoming.coverUrl) : '' ),
                                badgeId: Streamzine.Presenter.Editor.imagesBaseUrl + ( (incoming.badgeFileNameAlias) ? ('/' + incoming.badgeFileNameAlias.fileName) : '' )
                            }
                        }
                    }
                )
            },
            NEWS: {
                STORY: new Widget(
                    '<div><a href="javascript:;" id="{id}_valuePicker" onclick="Events.fire(\'VALUE_PICKING\', \'value\')">Select news to publish</a></div>' +
                    '<input type="text" id="{id}_value" readonly="readonly" />' +
                    '<input placeholder="Title" maxlength="255" type="text" class="{notVisibleTitle}" id="{id}_title" onkeydown="Streamzine.Presenter.Editor.onChange(\'title\', this.value)" onkeyup="Streamzine.Presenter.Editor.onChange(\'title\', this.value)" onblur="Streamzine.Presenter.Editor.onChange(\'title\', this.value)" />' +
                    '<input placeholder="Subtitle" maxlength="255" type="text" class="{notVisibleSubTitle}" id="{id}_subTitle" onkeydown="Streamzine.Presenter.Editor.onChange(\'subTitle\', this.value)" onkeyup="Streamzine.Presenter.Editor.onChange(\'subTitle\', this.value)" onblur="Streamzine.Presenter.Editor.onChange(\'subTitle\', this.value)"  />' +
                    '<div class="sz-badge-url-editor-wrapper {notVisibleBadge}">' +
                    '<img id="{id}_badgeId" class="sz-no-badge-url-editor" onclick="Events.fire(\'VALUE_PICKING\', \'badgeId\')"/>' +
                    '</div>' +
                    '<div class="sz-cover-url-editor-wrapper">' +
                    '<img id="{id}_coverUrl" class="sz-no-cover-url-editor"  onclick="Events.fire(\'VALUE_PICKING\', \'coverUrl\')"/>' +
                    '</div>', {
                        preProcess: function(incoming) {
                            return {
                                value: incoming.value,
                                title: incoming.title,
                                subTitle: incoming.subTitle,
                                value: incoming.value,
                                coverUrl: Streamzine.Presenter.Editor.imagesBaseUrl + ( (incoming.coverUrl) ? ('/' + incoming.coverUrl) : '' ),
                                badgeId: Streamzine.Presenter.Editor.imagesBaseUrl + ( (incoming.badgeFileNameAlias) ? ('/' + incoming.badgeFileNameAlias.fileName) : '' )
                            }
                        }
                    })
                ,
                LIST: new Widget(
                    '<div id="{id}_value" class="sz-editor-value"></div>' +
                    '<input placeholder="Title" maxlength="255" type="text" class="{notVisibleTitle}" id="{id}_title" onkeydown="Streamzine.Presenter.Editor.onChange(\'title\', this.value)" onkeyup="Streamzine.Presenter.Editor.onChange(\'title\', this.value)" onblur="Streamzine.Presenter.Editor.onChange(\'title\', this.value)" />' +
                    '<input placeholder="Subtitle" maxlength="255" type="text" class="{notVisibleSubTitle}" id="{id}_subTitle" onkeydown="Streamzine.Presenter.Editor.onChange(\'subTitle\', this.value)" onkeyup="Streamzine.Presenter.Editor.onChange(\'subTitle\', this.value)" onblur="Streamzine.Presenter.Editor.onChange(\'subTitle\', this.value)"  />' +
                    '<div class="sz-badge-url-editor-wrapper {notVisibleBadge}">' +
                    '<img id="{id}_badgeId" class="sz-no-badge-url-editor" onclick="Events.fire(\'VALUE_PICKING\', \'badgeId\')"/>' +
                    '</div>' +
                    '<div class="sz-cover-url-editor-wrapper">' +
                    '<img id="{id}_coverUrl" class="sz-no-cover-url-editor" onclick="Events.fire(\'VALUE_PICKING\', \'coverUrl\')"/>' +
                    '</div>', {
                          preProcess: function(incoming) {
                            // update from block
                              var date = new Date(incoming.value);

                              var m = (date.getMonth() + 1);
                              m = (m < 10) ? ('0' + m) : m;
                              var d = (date.getDate() < 10) ? ('0' + date.getDate()) : date.getDate();
                              var y = date.getUTCFullYear();
                            return {
                                title: incoming.title,
                                subTitle: incoming.subTitle,
                                value: m + '/' + d + '/' + y,
                                coverUrl: Streamzine.Presenter.Editor.imagesBaseUrl + ( (incoming.coverUrl) ? ('/' + incoming.coverUrl) : '' ),
                                badgeId: Streamzine.Presenter.Editor.imagesBaseUrl + ( (incoming.badgeFileNameAlias) ? ('/' + incoming.badgeFileNameAlias.fileName) : '' )
                            };
                        }
                    }
                )
            },
            PROMOTIONAL: {
                EXTERNAL_AD: new Widget(
                    '<div><a href="javascript:;" id="{id}_valuePicker" onclick="Events.fire(\'VALUE_PICKING\', \'value\')">Select External Link</a></div>' +
                        '<div>Link</div>' +
                        '<div id="{id}_valueLink" class="sz-editor-value"></div>' +
                        '<div>Open</div>' +
                        '<div id="{id}_valueOpener" class="sz-editor-value"></div>' +
                        '<input placeholder="Title" maxlength="255" type="text" class="{notVisibleTitle}" id="{id}_title" onkeydown="Streamzine.Presenter.Editor.onChange(\'title\', this.value)" onkeyup="Streamzine.Presenter.Editor.onChange(\'title\', this.value)" onblur="Streamzine.Presenter.Editor.onChange(\'title\', this.value)" />' +
                    '<input placeholder="Subtitle" maxlength="255" type="text" class="{notVisibleSubTitle}" id="{id}_subTitle" onkeydown="Streamzine.Presenter.Editor.onChange(\'subTitle\', this.value)" onkeyup="Streamzine.Presenter.Editor.onChange(\'subTitle\', this.value)" onblur="Streamzine.Presenter.Editor.onChange(\'subTitle\', this.value)"  />' +
                    '<div class="sz-badge-url-editor-wrapper {notVisibleBadge}">' +
                    '<img id="{id}_badgeId" class="sz-no-badge-url-editor" onclick="Events.fire(\'VALUE_PICKING\', \'badgeId\')"/>' +
                    '</div>' +
                    '<div class="sz-cover-url-editor-wrapper">' +
                    '<img  id="{id}_coverUrl" class="sz-no-cover-url-editor" onclick="Events.fire(\'VALUE_PICKING\', \'coverUrl\')" />' +
                    '</div>', {
                        preProcess: function(incoming) {
                            var delimIndex = incoming.value.indexOf('#');
                            return {

                                valueLink: (delimIndex < 0) ? incoming.value : incoming.value.substring(0, delimIndex),
                                valueOpener: (delimIndex > 0) ? opener[incoming.value.substr(delimIndex + 1)] : '',
                                title: incoming.title,
                                subTitle: incoming.subTitle,
                                coverUrl: Streamzine.Presenter.Editor.imagesBaseUrl + ( (incoming.coverUrl) ? ('/' + incoming.coverUrl) : '' ),
                                badgeId: Streamzine.Presenter.Editor.imagesBaseUrl + ( (incoming.badgeFileNameAlias) ? ('/' + incoming.badgeFileNameAlias.fileName) : '' )
                            }
                        }
                    })
                ,
                INTERNAL_AD: new Widget(
                    '<div><a href="javascript:;" id="{id}_valuePicker" onclick="Events.fire(\'VALUE_PICKING\', \'value\')">Select Internal Link</a></div>' +
                    '<div>Navigation Page</div>' +
                    '<div id="{id}_valuePage" class="sz-editor-value"></div>' +
                    '<div>Action</div>' +
                    '<div id="{id}_valueAction" class="sz-editor-value"></div>' +
                    '<input placeholder="Title" maxlength="255" type="text" class="{notVisibleTitle}" id="{id}_title" onkeydown="Streamzine.Presenter.Editor.onChange(\'title\', this.value)" onkeyup="Streamzine.Presenter.Editor.onChange(\'title\', this.value)" onblur="Streamzine.Presenter.Editor.onChange(\'title\', this.value)" />' +
                    '<input placeholder="Subtitle" maxlength="255" type="text" class="{notVisibleSubTitle}" id="{id}_subTitle" onkeydown="Streamzine.Presenter.Editor.onChange(\'subTitle\', this.value)" onkeyup="Streamzine.Presenter.Editor.onChange(\'subTitle\', this.value)" onblur="Streamzine.Presenter.Editor.onChange(\'subTitle\', this.value)"  />' +
                    '<div class="sz-badge-url-editor-wrapper {notVisibleBadge}">' +
                    '<img id="{id}_badgeId" class="sz-no-badge-url-editor" onclick="Events.fire(\'VALUE_PICKING\', \'badgeId\')"/>' +
                    '</div>' +
                    '<div class="sz-cover-url-editor-wrapper">' +
                    '<img id="{id}_coverUrl" class="sz-no-cover-url-editor" onclick="Events.fire(\'VALUE_PICKING\', \'coverUrl\')" />' +
                    '</div>', {
                        preProcess: function(incoming) {
                            // update from block
                            if(incoming.value) {
                                var delimIndex = incoming.value.indexOf('#');

                                return {
                                    value: incoming.value,
                                    title: incoming.title,
                                    subTitle: incoming.subTitle,
                                    valuePage: (delimIndex < 0) ? incoming.value : incoming.value.substring(0, delimIndex),
                                    valueAction: (delimIndex > 0) ? incoming.value.substr(delimIndex + 1) : '',
                                    coverUrl: Streamzine.Presenter.Editor.imagesBaseUrl + ( (incoming.coverUrl) ? ('/' + incoming.coverUrl) : '' ),
                                    badgeId: Streamzine.Presenter.Editor.imagesBaseUrl + ( (incoming.badgeFileNameAlias) ? ('/' + incoming.badgeFileNameAlias.fileName) : '' )
                                }
                            } else {
                                return {
                                    value: incoming.value,
                                    title: incoming.title,
                                    subTitle: incoming.subTitle,
                                    valuePage: '',
                                    valueAction: '',
                                    badgeId: Streamzine.Presenter.Editor.imagesBaseUrl + ( (incoming.badgeFileNameAlias) ? ('/' + incoming.badgeFileNameAlias.fileName) : '' ),
                                    coverUrl: Streamzine.Presenter.Editor.imagesBaseUrl + ( (incoming.coverUrl) ? ('/' + incoming.coverUrl) : '' )
                                }
                            }
                        }
                    })
            }
        };

        var editorRef = this;

        function resetContentTypeComboboxes() {
            // update the combos
            $('select[id=contentTypeId]').attr('selected', '0').attr('disabled', 'disabled');
            $('select[id=subTypeId]').attr('disabled', 'disabled').empty();
        }

        function clearCurrentError() {
            var currentBlock = Streamzine.Model.getCurrentBlock();
            var obj = _widgets[currentBlock.contentType.$name][currentBlock.key];
            obj.clearErrors();
        }

        //
        // API
        //
        this.widgetTypeChanged = function() {
            var currentBlock = Streamzine.Model.getCurrentBlock();

            var obj = _widgets[currentBlock.contentType.$name][currentBlock.key];
            obj.render(currentBlock);
            obj.update(currentBlock);
        }

        this.blockRemoved = function(ids) {
            var message = (ids.length > 1) ? "These blocks will be deleted. Proceed?" : "This block will be deleted. Proceed?";

            if(confirm(message)) {
                for(var i=0; i<ids.length; i++) {
                    if(Streamzine.Model.currentInEditId == ids[i]) {
                        Streamzine.Model.currentInEditId = null;

                        $('div.streamzinePropertiesEditor > div').empty();
                        resetContentTypeComboboxes();
                    }
                }
            } else {
                return {stopEvent: true};
            }
        }

        this.onChange = function(f, v) {
            Events.fire('VALUE_TYPED', {field: f, value: v});
            clearCurrentError();
        }

        this.updateBlockPropertiesUIFromObject = function(block) {
            //
            // Update Properties UI
            //
            this.removeErrorMarkers();

            var key = block.key;
            var contentType = block.contentType.$name;

            _widgets[contentType][key].update(block);

            //
            // enable and select proper value for combos
            //
            initContentTypes(block);
            $('select[id=contentTypeId]').val(contentType);

            initSubTypes(contentType);
            $('select[id=subTypeId]').val(key);
        }

        this.editBlockPropertiesUI = function(blockId) {
            Streamzine.Model.currentInEditId = blockId;
            var block = Streamzine.Model.findBlockById(blockId);

            this.widgetTypeChanged();
            this.updateBlockPropertiesUIFromObject(block);

            return false;
        }

        this.markWithError = function(field) {
            _currentInEdit = field;

            var wrapper = new Streamzine.ErrorWrapper(field);

            // find normalized model:
            var modelToSend = Streamzine.Presenter.getModelToSend(Streamzine.Model, true);
            // walk thought the positions and find needed:
            for(var i=0; i<modelToSend.blocks.length;i++) {
                var b = modelToSend.blocks[i];
                if(b.position == wrapper.getPosition()) {
                    var found = Streamzine.Model.findBlockById(b.id);
                    var obj = _widgets[found.contentType.$name][found.key];
                    obj.showError(wrapper.getName(), found);
                    break;
                }
            }
        }

        this.removeErrorMarkers = function(field) {
            if(_currentInEdit == field) {
                _currentInEdit = null;

                clearCurrentError();
            }
        }

        this.pickingTheValue = function(field) {
            var block = Streamzine.Model.getCurrentBlock();

            if(!block) {
                alert('Please start to edit block before picking the value');
                return;
            }

            if(field == 'coverUrl') {
                this.imagePicker.show(block);
                return;
            }

            if(field == 'badgeId') {
                this.badgePicker.show(block);
                return;
            }

            if(field == 'value') {
                if(block.contentType.$name == 'MUSIC') {
                    if(block.key == 'TRACK') {
                        this.mediaTrackPicker.show();
                    }

                    if(block.key == 'PLAYLIST') {
                        this.mediaTypePicker.show(block);
                    }

                    if(block.key == 'MANUAL_COMPILATION') {
                        this.mediaTracksPicker.show(block.data);
                    }
                }

                if(block.contentType.$name == 'PROMOTIONAL') {
                    if(block.key == 'EXTERNAL_AD') {
                        this.externalAdPicker.show(block);
                    }

                    if(block.key == 'INTERNAL_AD') {
                        this.internalAdPicker.show(block);
                    }
                }
            }
        }


        this.ifPresentIncludedBlock = function() {
            var withReCalcPositions = Streamzine.Presenter.getModelToSend(Streamzine.Model, true);

            if(withReCalcPositions.hasIncluded()) {
                return;
            }

            alert('No blocks to filter');
            return {stopEvent: true};
        }

        this.pickingTheUser = function(userName) {
            editorRef.userPicker.show();
        }


        //
        //
        // Initialization
        //
        //
        this.init = function(imagesBaseUrl, tracksBaseUrl, playListUrl, id, updateTmstp, badgesGetAll, badgesUpdateName, badgeMappingRules, titlesMappingRules) {
            this.id = id;
            this.imagesBaseUrl = imagesBaseUrl;
            this.badgeMappingRules = badgeMappingRules;
            this.titlesMappingRules = titlesMappingRules;

            initPickers(id, imagesBaseUrl, tracksBaseUrl, playListUrl, badgesGetAll, badgesUpdateName);

            initPropertiesEditors(updateTmstp);

            Streamzine.Presenter.renderUsers();
        }
        //
        // Init helpers
        //

        function initPropertiesEditors(updateTmstp) {
            // update the combos
            resetContentTypeComboboxes();

            $('#contentTypeId').change(function(e) {
                initSubTypes($(e.target).val());

                changeSubType(updateTmstp);
            });

            $('#subTypeId').change(function() {
                changeSubType(updateTmstp);
            });
        }

        function changeSubType(updateTmstp) {
            var contentType = $('select[id=contentTypeId]').val();
            var key = $('select[id=subTypeId]').val();

            var currentBlock = Streamzine.Model.getCurrentBlock();
            currentBlock.contentType.$name = contentType;
            currentBlock.key = key;
            currentBlock.value = (key == 'LIST') ? updateTmstp : "";
            currentBlock.title = "";
            currentBlock.subTitle = "";
            currentBlock.coverUrl = "";
            currentBlock.badgeId = null;
            currentBlock.vip = false;
            currentBlock.data = null;

            Events.fire('WIDGET_TYPE_CHANGED');
        }

        function initContentTypes(block) {
            var shapeType = block.shapeType.$name;

            var contentTypes = Streamzine.Presenter.Mapping.getContentTypes(shapeType);

            var subTypeCombobox = $('select[id=contentTypeId]').empty().removeAttr('disabled');
            for(var i=0; i < contentTypes.length; i++) {
                var title = Streamzine.Presenter.Mapping.getContentTypeTitle(shapeType, contentTypes[i]);
                subTypeCombobox.append( $('<option value="' + contentTypes[i] + '">' + title + '</option>'));
            }
        }

        function initSubTypes(contentType) {
            // get content type from parameters because it can come from combobox
            var block = Streamzine.Model.getCurrentBlock();
            var shapeType = block.shapeType.$name;
            var keys = Streamzine.Presenter.Mapping.getKeys(shapeType, contentType);

            var subTypeCombobox = $('select[id=subTypeId]').empty().removeAttr('disabled');
            for(var key in keys) {
                if(keys.hasOwnProperty(key)) {
                    subTypeCombobox.append( $('<option value="' + key + '">' + keys[key] + '</option>'));
                }
            }
        }

        function fireValuePickedEvent(f) {
            return function(v){
                Events.fire('VALUE_PICKED', {
                    field: (f) ? f : 'value',
                    value: v
                });
            }
        }

        function initPickers(id, imagesBaseUrl, tracksBaseUrl, playListUrl, badgesGetAll, badgesUpdateName) {
            editorRef.userPicker = Pickers.createUserPicker('userPickerId', function(userName){
                Events.fire('USER_PICKED', [true, userName]);
            });

            editorRef.imagePicker = Pickers.createImagePicker('rackspaceImagePickerId', 'rackspaceImagePickerFormId', 'imageUploadResponseHolderId', 'rackspaceImagePickerSaveId', fireValuePickedEvent('coverUrl'));
            editorRef.badgePicker = Pickers.createBadgePicker('badgePickerId', fireValuePickedEvent('badgeFileNameAlias'), badgesGetAll, badgesUpdateName);

            editorRef.mediaTrackPicker = Pickers.createMediaTrackPicker(id, 'mediaTrackPickerId', tracksBaseUrl, function(mediaDto) {
                Streamzine.Model.getCurrentBlock().data = mediaDto;
                fireValuePickedEvent('value')(mediaDto.id);
                fireValuePickedEvent('title')(mediaDto.title);
                fireValuePickedEvent('subTitle')(mediaDto.artistDto.name);
                //fireValuePickedEvent('coverUrl')(mediaDto.fileName);
            });

            editorRef.mediaTracksPicker = Pickers.createMediaTracksPicker(id, 'mediaTracksPickerId', tracksBaseUrl, function(v) {
                Streamzine.Model.getCurrentBlock().data = v;

                var ids = [];
                $.each(v, function(i, o) {
                    ids.push(o.id);
                });
                fireValuePickedEvent('value')(ids.join('#'));
            });

            editorRef.mediaTypePicker = Pickers.createMediaPlaylistTypePicker(id, 'mediaPlaylistPickerId', playListUrl, function(playlistDto) {
                Streamzine.Model.getCurrentBlock().data = {
                    'chartType': {
                        '$type': 'ChartType',
                        '$name': playlistDto.chartType.$name
                    },
                    'imageFileUrl': playlistDto.imageFileUrl,
                    'name': playlistDto.name,
                    'subtitle': playlistDto.subtitle,
                    'tracksCount': playlistDto.tracksCount
                }
                fireValuePickedEvent('coverUrl')(playlistDto.imageFileName);
                fireValuePickedEvent('value')(playlistDto.chartType.$name);
                fireValuePickedEvent('title')(playlistDto.name);
                fireValuePickedEvent('subTitle')(playlistDto.subtitle);
            });

            editorRef.externalAdPicker = Pickers.createExternalAdPicker('externalAdPicker', fireValuePickedEvent());

            editorRef.internalAdPicker = Pickers.createInternalAdPicker('internalAdPicker', fireValuePickedEvent());
        }
    };

}

