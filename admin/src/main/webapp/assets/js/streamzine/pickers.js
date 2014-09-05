if(Pickers == undefined) {
    var Pickers = {};

    var GenericDialog = function(dialogId, saveHandler, showHandler) {
        //
        // Model
        //
        // the value selected
        var _value;
        // optional means that dialog does not ask before cancel (if dialog asks before cancel - that means it is not optional and must be picked up anyway)
        var _optional = false;
        // the value which is the copy of the original value which could be return back if:
        // a) dialog is optional,
        // b) dialog was cancelled
        var _originalValue;
        var okayClicked = true;

        //
        // Dialog Binding
        $('div#' + dialogId + ' div.modal-footer button[class="btn"][data-dismiss="modal"]').click(function() {
            okayClicked = false;
        });

        // 1) on show
        $('#' + dialogId).on('show.bs.modal', function (e) {
            $('#' + dialogId + ' div[data-content="modal-dialog-body"]').empty();

            var needToPrevent = _onShow();

            if(needToPrevent) {
                e.preventDefault();
            } else {
                $('#' + dialogId).removeClass('sz-dialog-init');
            }
        });
        // 2) on hide
        $('#' + dialogId).on('hide.bs.modal', function (e) {
            var needToPrevent = _onHide();

            if(needToPrevent) {
                e.preventDefault();
            } else {
                $('#' + dialogId).addClass('sz-dialog-init');
            }
        });
        // 3) on save
        $('#' + dialogId + ' .modal-footer .btn-primary').on('click.own', function(e) {
            e.preventDefault();

            _onSave();
        });

        //
        // Internals
        //
        function _onShow() {
           // _value = null;
            if(showHandler) {
                showHandler();
            }
            return false;
        }

        function _onHide() {
            // additional check for the optional if cancels
            if(!okayClicked && _optional) {
                saveHandler(_originalValue);
                return false;
            }

            if(_value) {
                if(confirm('Selected value will be lost. Proceed?')) {
                    _value = null;
                    return false;
                } else {
                    return true;
                }
            }

            return false;
        }

        function _onSave() {
            okayClicked = true;

            if(_value || okayClicked && _optional) {
                saveHandler(_value);
                _value = null;
                $('#' + dialogId).modal('hide');
            } else {
                alert('Nothing was selected. Please select the value');
            }
        }

        //
        // API
        //
        this.show = function() {
            $('#' + dialogId).modal('show').draggable({handle: ".modal-header"});
        }

        // one time function
        this.markAsOptional = function() {
            _optional = true;
            return this;
        }

        this.setValue = function(val){
            _value = val;
        }

        this.getValue = function(){
            return _value;
        }

        this.originalValue = function(originalValue) {
            _originalValue = originalValue;
            return this;
        }

        this.setTitle = function(title) {
            $('#' + dialogId + ' .modal-title').html(title);
        }
    }

    var SearchDataDialog = function(data) {
        var dialogId = data.dialogId;
        var saveHandler = data.saveHandler;
        var rowTemplate = data.rowTemplate;
        var restoreIdCallback = data.restoreIdCallback;
        var endpointUrl = data.endpointUrl;
        var preprocessData = (data.preprocessData) ? data.preprocessData : function(d){return d;};
        var params = (data.params) ? data.params : {};
        var paramsCallback = data.paramsCallback;
        var init = (data.init) ? data.init : function(){};
        var selectable = !!data.selectable;
        var initDataProcessor = data.initDataProcessor;
        var showHandler = (data.showHandler) ? data.showHandler : function(){};
        var ref = this;
        //
        // Construction
        //
        var _genericDialog = new GenericDialog(dialogId, saveHandler, function() {
            _emptyGrid();
            _emptySearchInput();
            showHandler(ref);
        });
        //
        // Binding
        // 1) search
        $('#' + dialogId + ' .s_btn').click(_search);

        getInputElement().keydown(function(e) {
            if(13 == e.keyCode) {
                _search();
            }
        });

        // 2) possible selectable
        if(selectable) {
            $('#' + dialogId + ' ul.table_up').selectable({
                selected: function( event, ui ) {
                    var id = ui.selected.id;
                    _genericDialog.setValue(restoreIdCallback(id));
                }
            });
        }

        // 3) init
        init(ref);

        //
        // Internals
        //
        function _search() {
            if(selectable) {
                _genericDialog.setValue(null);
            }

            var searchText = getInputElement().val();

            _emptyGrid();
            _showLoadingBar();

            var parameters = {};
            parameters.q = searchText;
            // copy props of params
            for(var p in params) {
                if(params.hasOwnProperty(p)) {
                    parameters[p] = params[p];
                }
            }

            // also call the callback if defined:
            if(paramsCallback) {
                var fromCallback = paramsCallback(ref);
                // copy from callback exec
                for(var p in fromCallback) {
                    if(fromCallback.hasOwnProperty(p)) {
                        parameters[p] = fromCallback[p];
                    }
                }
            }

            $.ajax({
                url : endpointUrl,
                dataType: 'json',
                contentType: 'application/json',
                type: "GET",
                data : parameters,
                success : function(data, textStatus) {
                    _hideLoadingBar();

                    _emptyGrid();

                    _fillGrid(preprocessData(data));
                },
                error: function() {
                    alert('Failed to search. Try again later');

                    _hideLoadingBar();
                }
            });
        }

        function _fillGrid(array) {
            var html = '';

            for(var i=0; i < array.length; i++) {
                var row = Template.render(rowTemplate, array[i]);
                html += row;
            }

            $($('#' + dialogId + ' ul.table_up').get(0)).html(html);
        }

        function _emptyGrid() {
            $($('#' + dialogId + ' ul.table_up').get(0)).empty();
        }

        function _hideLoadingBar() {
            $('#' + dialogId + ' .loading-container').hide();
        }

        function _showLoadingBar() {
            $('#' + dialogId + ' .loading-container').show();
        }

        function _emptySearchInput() {
            getInputElement().val('');
        }

        function getInputElement() {
            return $('#' + dialogId + ' input[name="q"]');
        }

        //
        // API
        //
        this.show = function(initValue) {
            var d = initValue;

            if(d) {
                if(initDataProcessor) {
                    d = initDataProcessor(d);
                }
                _genericDialog.setValue(d);
            }
            _genericDialog.show();
        }

        this.search = function() {
            _search();
        }

        this.setValue = function(v) {
            _genericDialog.setValue(v);
        }

        this.getValue = function() {
            return _genericDialog.getValue();
        }
    }

    var ExternalAdPicker = function(dialogId, saveHandler) {
        var opener = {
            IN_APP: 'In-app',
            BROWSER: 'In Browser'
        };

        var initialBlock;
        //
        // Construction
        //
        var _genericDialog = new GenericDialog(dialogId, saveHandler, function() {
            _clear();

            var select = $('# ' + dialogId + ' select').empty();
            for (var key in opener) {
                select.append($("<option/>", {
                    value: key,
                    text: opener[key]
                }))
            };

            // init the dialog with the value
            var splitData = initialBlock.value.split('#')
            _getInput().val(splitData[0]);
            _getOpener().val(splitData[1]);
            if(initialBlock.value) {
              _check();
            }
            syncUrl(_getInput().val());
            syncOpener();
        });
        _genericDialog.setTitle('External ad picker');
        _getOpener().change(function(){
            syncOpener();
        });

        //
        // Binding
        // 1) search
        $('#' + dialogId + ' .s_btn').click(function() {
            _check();
        });
        //
        // Internals
        //
        function _getInput() {
            return $('#' + dialogId + ' input[name="q"]');
        }

        function syncOpener(){
            _updateModel('opener', _getOpener().val());

        }

        function syncUrl(src){
            _updateModel('url', src);

        }

        function _getOpener() {
            return $('#opener');
        }

        function _updateModel(prop, value) {
            var existing = _genericDialog.getValue();
            if(!existing) {
                _genericDialog.setValue({});
            }
            _genericDialog.getValue()[prop] = value;
        }
        function _getModalDialogHeader() {
            return $('#' + dialogId + ' div[data-header="modal-dialog-header"]');
        }

        function _getModalDialogBody() {
            return $('#' + dialogId + ' div[data-content="modal-dialog-body"]');
        }

        function _clear() {
            _getInput().val('')
            _getModalDialogHeader().empty();
            _getModalDialogBody().empty();
        }

        function _check() {
            var src = _getInput().val();
            var header = _getModalDialogHeader().empty();
            var content = _getModalDialogBody().empty();

            // ... but not checked yet...
            header.removeClass('streamzine-url-picker-checked').html("Checking: " + src + "...");
            syncUrl(src);
            syncOpener();

            if(!(src.indexOf('http://') >= 0 || src.indexOf('https://') >= 0)) {
                src = 'http://' + src;
            }

            $(Template.render('<iframe src="{src}" class="streamzine-url-picker-response-frame" />', {src:src}))
                .load(function() {
                    $('#' + dialogId + ' input[name="q"]').val(src)
                    syncUrl(src);
                    syncOpener();
                    header.empty().addClass('streamzine-url-picker-checked').html("Checked: " + src);
                })
                .appendTo(content);
        }

        //
        // API
        //
        this.show = function(block) {
            initialBlock = block;
            _genericDialog.show();
        }
    }

    var InternalAdPicker = function(dialogId, saveHandler) {
        //
        // Construction
        //
        var _genericDialog = new GenericDialog(dialogId, saveHandler, beforeShow);
        var _pagesTemplate = '<li class="streamzine-app-url-picker-item" id="app_url_{value}">{value}</li>';
        var _actionsTemplate = '<li class="streamzine-app-url-picker-item" id="action_{value}">{value}</li>';

        //
        // Binding
        // 1) app pages
        _getGrid(0).selectable({
            selected: function( event, ui ) {
                _getActionCheckBox().removeAttr('disabled');
                _onSelect(ui, 'app_url_', 'url');
            }
        });
        // 2) actions
        _getGrid(1).selectable({
            selected: function( event, ui ) {
                _onSelect(ui, 'action_', 'action');
            }
        }).selectable('disable');;

        // 3) checkbox
        _getActionCheckBox().click(
            function(e) {
                var checked = !!$(e.target).attr('checked');
                if(checked) {
                    _getGrid(1)
                        .removeClass('streamzine-app-page-picker-checked-action')
                        .selectable('enable');
                } else {
                    _getGrid(1)
                        .addClass('streamzine-app-page-picker-checked-action')
                        .selectable('disable');
                    _updateModel('action', null);
                }
            }
        );

        //
        // Internals
        //
        function _onSelect(ui, prefix, prop) {
            var id = ui.selected.id;
            var startIndex = prefix.length;
            var endIndex = id.length;
            var restoredId = id.substring(startIndex, endIndex);

            _updateModel(prop, restoredId);
        }

        function _updateModel(prop, value) {
            var existing = _genericDialog.getValue();
            if(!existing) {
                _genericDialog.setValue({});
            }
            _genericDialog.getValue()[prop] = value;
        }

        function _getActionCheckBox() {
            return $('#' + dialogId + ' input[type="checkbox"]');
        }

        function _enableActionCheckBox() {
            _getActionCheckBox().removeAttr('disabled');
        }

        function _enableAndCheckActionCheckBox() {
            _getActionCheckBox().removeAttr('disabled').attr('checked', 'checked');
        }

        function _disableActionCheckBox() {
            _getActionCheckBox().removeAttr('checked').attr('disabled', 'disabled');
        }

        function beforeShow() {
            _disableActionCheckBox();
            _getGrid(1)
                .addClass('streamzine-app-page-picker-checked-action')
                .selectable('disable');

            _loadGrid(0, '../pages/list', 'pages', _pagesTemplate, 'app_url_');
            _loadGrid(1, '../actions/list', 'actions', _actionsTemplate, 'action_');
        }

        function _loadGrid(index, endpoint, prop, template, idPrefix) {
            _showLoadingBar(index);

            $.ajax({
                url : endpoint,
                dataType: 'json',
                contentType: 'application/json',
                type: "GET",
                success : function(data, textStatus) {
                    var urlIndex    = index==0;
                    var actionIndex = index==1;

                    _hideLoadingBar(index);

                    _emptyGrid(index);

                    _fillGrid(index, data[prop], template);

                    // 1) set the initial value
                    if(urlIndex && _genericDialog.getValue() && _genericDialog.getValue().url) {
                        $('#' + dialogId + ' li#' + idPrefix + _genericDialog.getValue().url).addClass('ui-selected');
                        _enableActionCheckBox();
                    }
                    // 2) set the initial action
                    if(actionIndex && _genericDialog.getValue() && _genericDialog.getValue().action) {
                        $('#' + dialogId + ' li#' + idPrefix + _genericDialog.getValue().action).addClass('ui-selected');
                        _getGrid(1).removeClass('streamzine-app-page-picker-checked-action').selectable('enable');
                        _enableAndCheckActionCheckBox();
                    }
                },
                error: function() {
                    alert('Failed to get data. Try again later');
                    _hideLoadingBar(index);
                }
            });
        }

        function _hideLoadingBar(index) {
            $($('#' + dialogId + ' .loading-container')[index]).hide();
        }

        function _showLoadingBar(index) {
            $($('#' + dialogId + ' .loading-container')[index]).show();
        }

        function _emptyGrid(index) {
            _getGrid(index).empty();
        }

        function _getGrid(index) {
            return $($('#' + dialogId + ' .table')[index]);
        }

        function _fillGrid(index, array, template) {
            var html = '';

            for(var i=0; i < array.length; i++) {
                var row = Template.render(template, {value: array[i]});
                html += row;
            }

            _getGrid(index).html(html);
        }

        //
        // API
        //
        this.show = function(block) {
            if(block.value) {
                _genericDialog.setValue({
                    url: block.value.split('#')[0],
                    action: block.value.split('#')[1]
                });
            }

            _genericDialog.show();
        }
    }

    //
    // Factory Methods:
    //
    Pickers.createUserPicker = function(dialogId, saveHandler) {
        var _rowTemplate =
            '<li id="user_picker_{userName}">'           +
            '    <div class="sz-title-email">'           +
            '        <p>{userName}</p>'                  +
            '    </div>'                                 +
            '</li>';

        return new SearchDataDialog({
            dialogId : dialogId,
            saveHandler: saveHandler,
            rowTemplate: _rowTemplate,
            endpointUrl: "../user/list",
            selectable: true,
            restoreIdCallback: function(id) {
                var startIndex = 'user_picker_'.length;
                var endIndex = id.length;
                return id.substring(startIndex, endIndex);
            },
            preprocessData: function(a) {
                return a['USER_DTO_LIST'];
            },
            paramsCallback: function(owner) {
                return {ids:Streamzine.Model.getUsers().join('#')};
            }
        });
    }

    Pickers.createMediaTracksPicker = function(id, dialogId, mediaImgBaseUrl, saveHandler) {
        var _rowTemplate =
            '<li id="media_picker_{id}">'              +
            '    <div>                    '              +
            '        <img alt="Media picture" src="{url}" width="50" />' +
            '    </div>'                                 +
            '    <div class="sz-tracks-dialog-trackId">' +
            '        {trackId}        '                  +
            '    </div>'                                 +
            '    <div class="sz-tracks-dialog-title">'   +
            '     {title}                  '             +
            '    </div>                    '             +
            '    <div class="sz-tracks-dialog-info">'    +
            '     {artistDto.name}         '             +
            '    </div>                    '             +
            '</li>';

        var owner;
        var _dataCopy = [];

        function cutId(value, prefix) {
            var startIndex = prefix.length;
            var endIndex = value.length;
            return value.substring(startIndex, endIndex);
        }

        function restoreIdCallback(id) {
            return cutId(id, 'media_picker_');
        }

        function _restoreMediaDtoFromIsrc(id) {
            return _dataCopy[_findIndex(_dataCopy, id)];
        }

        function _findIndex(where, id) {
            for(var j=0; j < where.length; j++) {
                if(where[j].id == id) {
                    return j;
                }
            }
            return -1;
        }

        function _getSelectedExceptByIndexOrNull(where, index) {
            var copy = [];
            for(var i=0; i < where.length; i++) {
                if(i != index) {
                    copy.push(where[i]);
                }
            }
            if(copy.length == 0) {
                return null;
            } else {
                return copy;
            }
        }

        function generateSelectedId(id) {
            return 'selected_channels_' + id;
        }

        function createSelectedItem(owner, id) {
            var theIndex = _findIndex(owner.getValue(), id);
            var newId = generateSelectedId(id);

            return $('<div></div>')
                .append($('<a href="javascript:;">Delete</a>')
                    .click(function() {
                        owner.setValue(_getSelectedExceptByIndexOrNull(owner.getValue(), theIndex));

                        $('#' + newId).off('click').remove();

                        owner.search();
                    })
            );
        }

        function onReceive(event, ui) {
            if(!owner.getValue()) {
                owner.setValue([]);
            }
            var i = $(ui.item);
            var dragged = restoreIdCallback(i.attr('id'));

            i.attr('id', generateSelectedId(dragged));

            var existingIndex = _findIndex(owner.getValue(), dragged);
            if(existingIndex < 0) {
                // add id to selected ones
                owner.getValue().push(_restoreMediaDtoFromIsrc(dragged));
                i.append(createSelectedItem(owner, dragged));
            }
        }

        function getSelectedGrid() {
            return $($('#' + dialogId + ' ul.table_up').get(1));
        }

        function extractActualIds() {
            var ids = [];
            var selectedIdPrefix = 'selected_channels_';
            var liElements = $('#mediaTracksPickerId li[id^=' + selectedIdPrefix + ']');
            for(var i=0; i < liElements.length; i++) {
                var id = $(liElements[i]).attr('id');
                var restoredId = cutId(id, selectedIdPrefix);
                ids.push(restoredId);
            }
            return ids;
        }

        function reCalcIndexes(existing) {
            var actualIds = extractActualIds();
            var resultValues = [];
            for(var i=0; i<actualIds.length; i++) {
                var id = actualIds[i];
                resultValues.push(existing[_findIndex(existing, id)]);
            }
            return resultValues;
        }

        function clearButtonHandler() {
            var selected = owner.getValue() != null ? owner.getValue() : [];

            if(selected.length > 0) {
                if(confirm("Selected values will be deleted. Continue?")) {
                    for(var j=0; j < selected.length; j++) {
                        var sid = selected[j].id;
                        $('#' + generateSelectedId(sid)).remove();
                    }

                    owner.setValue(null);
                    owner.search();
                }
            }
        }

        return new SearchDataDialog({
            dialogId : dialogId,
            initDataProcessor: function(d) {
                for(var i=0; i<d.length; i++) {
                    d[i].url = mediaImgBaseUrl + d[i].fileName;
                }
                return d;
            },
            saveHandler: function(v) {
                var r = reCalcIndexes(v);
                saveHandler(r);
            },
            rowTemplate: _rowTemplate,
            endpointUrl: "../media/list",
            params: {id: id},
            selectable: false,
            restoreIdCallback: restoreIdCallback,
            showHandler: function(owner) {
                var grid = getSelectedGrid().empty();

                if(owner.getValue()) {
                    var items = owner.getValue();
                    for(var j=0; j < items.length; j++) {
                        var item = items[j];
                        $(Template.render(_rowTemplate, item))
                            .append(createSelectedItem(owner, item.id))
                            .attr('id', generateSelectedId(item.id))
                            .appendTo(grid);
                    }
                }

                var dialog = $('#' + dialogId);
                if(!dialog.hasClass('sz-tracks-dialog')) {
                    dialog.addClass('sz-tracks-dialog');
                }
            },
            paramsCallback: function(owner) {
                if(owner.getValue()) {
                    var ids = [];
                    $.each(owner.getValue(), function(i, o) {
                        ids.push(o.id);
                    });
                    return {ids:ids.join('#')};
                } else {
                    return '';
                }
            },
            preprocessData: function(a) {
                _dataCopy = [];

                var data = a['CHART_ITEM_DTO_LIST'];

                for(var i=0; i<data.length; i++) {
                    data[i].url = mediaImgBaseUrl + data[i].fileName;
                    _dataCopy[i] = data[i];
                }

                return _dataCopy;
            },
            init: function(ref) {
                $('div#' + dialogId + ' button.btn-warning').click(clearButtonHandler);

                owner = ref;

                // dragged items grid
                getSelectedGrid().sortable({
                    receive: onReceive
                });
                // from grid
                $($('#' + dialogId + ' ul.table_up').get(0)).sortable({
                    connectWith: getSelectedGrid()
                });
            }
        });
    }

    Pickers.createMediaTrackPicker = function(id, dialogId, mediaImgBaseUrl, saveHandler) {
        var _rowTemplate =
            '<li id="media_picker_{id}">'              +
            '    <div class="cover_media">'              +
            '        <img alt="Media picture" src="{fileName}" width="50" />' +
            '    </div>'                                 +
            '    <div class="trackId_media">'            +
            '        <p>{trackId}</p>       '            +
            '    </div>'                                 +
            '    <div class="title_media"> '             +
            '        {title}               '             +
            '    </div>                    '             +
            '    <div class="artist_media">'             +
            '        {artistDto.name}      '             +
            '    </div>                    '             +
            '</li>';

        var reference = this;

        return new SearchDataDialog({
            dialogId : dialogId,
            saveHandler: saveHandler,
            rowTemplate: _rowTemplate,
            endpointUrl: "../media/list",
            params: {id: id},
            selectable: true,
            restoreIdCallback: function(id) {
                var startIndex = 'media_picker_'.length;
                var endIndex = id.length;
                var sid = id.substring(startIndex, endIndex);
                // search by id
                for(var i=0; i<reference.gridData.length;i++) {
                    if(reference.gridData[i].id == sid) {
                        return reference.gridData[i];
                    }
                }
            },

            preprocessData: function(a) {
                var data = a['CHART_ITEM_DTO_LIST'];
                var output = [];
                for(var i=0; i<data.length; i++) {
                    output[i] = data[i];
                    output[i].fileName = mediaImgBaseUrl + data[i].fileName;
                }
                reference.gridData = output;
                return output;
            }
        });
    }

    Pickers.createExternalAdPicker = function(dialogId, saveHandler) {
        return new ExternalAdPicker(dialogId, saveHandler);
    }

    Pickers.createInternalAdPicker = function(dialogId, saveHandler) {
        return new InternalAdPicker(dialogId, saveHandler);
    }


    //
    //
    // Media Type Picker
    //
    //
    Pickers.createMediaPlaylistTypePicker = function(id, dialogId, playListUrl, saveHandler) {
        return new function() {
            var _genericDialog = new GenericDialog(dialogId, saveHandler, beforeShow);
            var _data;
            //
            // Construction
            //
            var _rowTemplate = '<li class="streamzine-playlist-picker-item" id="media_playlist_{chartType}"><img src="{imageFileUrl}" height="50px"/> {name} <span style="float: right;">{tracksCount}</span> </li>';
            // select
            $('#' + dialogId + ' .table').selectable({
                selected: function( event, ui ) {
                    var id = ui.selected.id;
                    var startIndex = 'media_playlist_'.length;
                    var endIndex = id.length;
                    var restoredId = id.substring(startIndex, endIndex);
                    // restore dto from id: _data
                    for(var j=0; j<_data.length;j++) {
                        var selected = _data[j];
                        if(selected.chartType == restoredId) {
                            selected.chartType = {
                                '$type': 'ChartType',
                                '$name': selected.chartType
                            };
                            _genericDialog.setValue(selected);
                            break;
                        }
                    }
                }
            });

            //
            // Internals
            //
            function _restoreData(input, restoredId) {
                for(var j=0; j<input.length;j++) {
                    if(input[j].chartType == restoredId) {
                        return input[j];
                    }
                }
            }

            function inrichData(data) {
                var d = [];

                for(var i=0; i < data.length; i++) {
                    var e = data[i];
                    d.push({
                        chartType: e.chartType,
                        imageFileUrl: (e.imageFileName) ? Streamzine.Presenter.Editor.imagesBaseUrl + '/' + e.imageFileName : '',
                        imageFileName: e.imageFileName,
                        name: e.name,
                        tracksCount: e.tracksCount
                    });
                }

                return d;
            }

            function beforeShow() {
                _showLoadingBar();
                _getGrid().empty();

                $.ajax({
                    url : playListUrl,
                    dataType: 'json',
                    contentType: 'application/json',
                    type: "GET",
                    data: {id: id},
                    success : function(data, textStatus) {
                        _hideLoadingBar();

                        _emptyGrid();

                        // update server side data copy
                        _data = inrichData(data['CHART_DTO_LIST']);

                        _fillGrid(_data);
                    },
                    error: function() {
                        _getGrid().empty().append(_errorContent());

                        _hideLoadingBar();
                    }
                });
            }

            function _errorContent() {
                var link = $('<a href="javascript:;">Try again later</a>').on('click', beforeShow);
                var content = $('<div class="sz-playlist-picker-error-message">Failed to get data. </div>').append(link);
                return content;
            }

            function _hideLoadingBar() {
                $('#mediaPlaylistsContainerId .loading-container').hide();
            }

            function _showLoadingBar() {
                $('#mediaPlaylistsContainerId .loading-container').show();
            }

            function _getGrid() {
                return $('#mediaPlaylistsContainerId .table');
            }

            function _emptyGrid() {
                _getGrid().empty();
            }

            //
            // Handlers
            //
            function _fillGrid(array) {
                var html = '';

                for(var i=0; i < array.length; i++) {
                    var row = Template.render(_rowTemplate, array[i]);
                    html += row;
                }

                $('#mediaPlaylistsContainerId .table').html(html);

                if(_genericDialog.getValue()) {
                    $('#' + dialogId + ' li#media_playlist_' + _genericDialog.getValue().chartType.$name).addClass('ui-selected');
                }
            }

            //
            // API
            //
            this.show = function(block) {
                if(block.data) {
                    _genericDialog.setValue(block.data);
                }
                _genericDialog.show();
            }
        };
    }



    //
    //
    // Badge Picker
    //
    //
    Pickers.createBadgePicker = function(dialogId, formId, holderId, okHandler, badgesGetAll, badgesUpdateName, badgesDelete) {
        return new function() {
            var _genericDialog = new GenericDialog(dialogId, okHandler, beforeShow).markAsOptional();

            var _rowTemplate = '<li class="streamzine-playlist-picker-item" data-id="{alias}" data-name="{fileName}"><div><img src="{src}" height="50px"/></div></li>';
            var _renTemplate = '<a href="javascript:;" data-id="{alias}">{alias}</a>';
            var _inPlaceEditorTextTemplate = '<input type="text" maxlength="128" value="{alias}"/>';
            var _inPlaceEditorDoneTemplate = '<a href="javascript:;">Done</a>';
            var _delTemplate = '<div style="float:right;"><a href="javascript:;" data-id="{alias}">Delete</a></div>';
            //
            // Construction
            //
            init();

            //
            // Internal functions
            //
            function init() {
                $('#' + formId).submit(function(event) {
                    createFrame();

                    var form = event.target;
                    form.target = _getFrame().attr('name');

                    // display 'Loading ...'
                    var body = $(document.getElementById(_getFrame().attr('name')).contentWindow.document.body);
                    body.html('Loading...');
                });

                $('#' + formId + ' input[type="file"]').change(function() {
                    $('#' + formId).submit();
                });

                $('#' + dialogId + ' ul.table').selectable({
                    selected: function( event, ui ) {
                        var alias = $(ui.selected).attr('data-name');
                        _genericDialog.setValue(alias);
                    }
                });

                $('div#' + dialogId + ' button.btn-warning').click(unAssignBadgeHandler);
            }

            function unAssignBadgeHandler() {
                if(_genericDialog.getValue()) {
                    _genericDialog.setValue(null);
                    loadNames();
                }
            }

            function _getFrame() {
                return $($('#' + holderId + ' > iframe')[0]);
            }

            function onFrameLoad() {
                var doc = document.getElementById(_getFrame().attr('name')).contentWindow.document;
                var count = doc.getElementsByTagName('img').length;
                // found one image in the internal frame
                if(count == 1) {
                    loadNames();
                    $('#' + formId).trigger('reset');
                    _getFrame().remove();
                }
            }

            function beforeShow() {
                $('#' + formId).trigger('reset');

                loadNames();
            }

            function createFrame() {
                var name = formId + "_" + new Date().getTime();
                $('#' + holderId)
                    .empty()
                    .append(
                    $(Template.render('<iframe id="{name}" name="{name}" class="streamzine-badge-picker-response-frame" />', {name: name})).load(onFrameLoad)
                );
            }

            function getGrid() {
                return $('#' + dialogId + ' ul');
            }

            function fillGrid(data) {
                var array = data['badges'];
                for(var i=0; i < array.length; i++) {
                    var a = array[i];

                    var row = $(Template.render(_rowTemplate, {
                        src: Streamzine.Presenter.Editor.imagesBaseUrl + a.fileName,
                        fileName: a.fileName,
                        alias: a.alias
                    }));

                    getGrid().append(
                        fillRow(row, a.alias)
                    );
                }
            }

            function fillRow(row, alias) {
                var deleteLink = $(Template.render(_delTemplate, {alias: alias}));
                deleteLink.click(deleteItem);

                var nameLink = $(Template.render(_renTemplate, {alias: alias}));
                nameLink.click(renameItem);

                return row.append($('<div></div>').append(nameLink)).append(deleteLink);
            }

            function request(url, data, success) {
                $.ajax({
                    url : url,
                    dataType: 'json',
                    contentType: 'application/json',
                    type: "GET",
                    data: data || {},
                    success : function(d, t) {
                        success(d, t);
                    },
                    error: function() {
                        alert('Server error');
                    }
                });
            }

            function loadNames() {
                _showLoadingBar();
                getGrid().empty();

                request(badgesGetAll, null, function(data, textStatus) {
                    _hideLoadingBar();
                    fillGrid(data);
                    highlightBadge();
                });
            }

            function highlightBadge() {
                var theValue = _genericDialog.getValue();
                if(theValue) {
                    $('#' + dialogId + ' li[data-name="' + theValue + '"]').addClass('ui-selected');
                }
            }

            function _hideLoadingBar() {
                $('#' + dialogId + ' .loading-container').hide();
            }

            function _showLoadingBar() {
                $('#' + dialogId + ' .loading-container').show();
            }

            function deleteItem(ui) {
                var name = $(ui.target).attr('data-id');
                if(confirm('The badge with name ' + name + ' will be deleted. Proceed?')) {
                    _showLoadingBar();
                    getGrid().empty();

                    request(badgesDelete, {name: name}, function(data) {
                        _hideLoadingBar();
                        fillGrid(data);
                    });
                }
            }

            function renameItem(ui) {
                var link = $(ui.target);
                var id = link.attr('data-id');
                var placeToPut = link.parent();
                link.remove();

                var wrapper = $('<div></div>');
                var text = $(Template.render(_inPlaceEditorTextTemplate, {alias: id}));
                var done = $(Template.render(_inPlaceEditorDoneTemplate, {alias: id}));
                wrapper.append(text).append(done);
                placeToPut.append(wrapper);

                done.click(function() {
                    var newName = text.val();

                    if(newName != id) {
                        _showLoadingBar();
                        request(badgesUpdateName, {oldName: id, newName: newName}, function(data) {
                            _hideLoadingBar();
                            if(data['notUniqueName']) {
                                alert('The name you provided ' + newName + ' is not unique. Please choose another one');
                            } else {
                                getGrid().empty();
                                fillGrid(data);
                            }
                        });
                    } else {
                        var nameLink = $(Template.render(_renTemplate, {alias: id}));
                        nameLink.click(renameItem);
                        wrapper.empty().append(nameLink);
                    }
                });
            }

            //
            // API
            //
            this.show = function(block) {
                _genericDialog.originalValue(block.badgeUrl);
                _genericDialog.setValue(block.badgeUrl);

                _genericDialog.show();
            }
        };
    }

    //
    //
    // Image Picker
    //
    //
    Pickers.createImagePicker = function(dialogId, formId, holderId, saveId, okHandler) {
        return new function() {
            //
            // Construction
            //
            // on show event: needs to clean everything up
            $('#' + dialogId).on('show.bs.modal', function (e) {
                var needToPrevent = _onShow();

                if(needToPrevent) {
                    e.preventDefault();
                }
            });
            // on hide event: try to ask to save the changes
            $('#' + dialogId).on('hide.bs.modal', function (e) {
                var needToPrevent = _onHide();

                if(needToPrevent) {
                    e.preventDefault();
                }
            });
            // on save event:
            // cleaning prev loaded content
            $('#' + saveId).on('click.own', function(e) {
                e.preventDefault();

                _onSave();
            });

            $('#' + formId).submit(function(event) {
                var form = event.target;
                form.target = _getFrame().attr('name');

                // display 'Loading ...'
                var body = $(document.getElementById(_getFrame().attr('name')).contentWindow.document.body);
                body.html('Loading...');
            });

            $('#' + formId + ' input[type="file"]').change(function() {
                // alert($('#' + formId + ' input[type="file"]').val());
                $('#' + formId).submit();
            });

            //
            // Variables
            //
            var _href;
            var _afterSave;

            //
            // internals
            //
            function _getFrame() {
                return $($('#' + holderId + ' > iframe')[0]);
            }

            function _onSave() {
                var doc = document.getElementById(_getFrame().attr('name')).contentWindow.document;

                _href = $(doc.getElementById('postedImageUrlId')).attr('href');
                _fileNameId = $(doc.getElementById('fileNameId')).val();

                if(_href) {
                    okHandler(_fileNameId);
                    _href = null;
                    _fileNameId = null;
                    _afterSave = true;
                    $('#' + dialogId).modal('hide');
                } else {
                    alert('Nothing was selected. Please select the image');
                }
            }

            function _onHide() {
                if(_afterSave) {
                    return false;
                }

                var href = document.getElementById(_getFrame().attr('name')).contentWindow.document.getElementById('postedImageUrlId');

                if(href) {
                    if(confirm('Selected value will be lost. Proceed?')) {
                        _href = null;
                        return false;
                    } else {
                        return true;
                    }
                } else {
                    // it could be the situation when loading is in progress but the user cancels the image picker dialog
                    // so just move the iframe to body and hide it

                    _getFrame()
                         .css({display:'block'})
                         .appendTo($(document.body));

                    return false;
                }
            }

            function _onShow() {
                _href = null;
                _afterSave = false;

                // cleaning prev loaded content: can not clear iframe according to the security restrictions - so clean up the wrapper DIV
                var name = formId + "_" + new Date().getTime();
                $('#' + holderId)
                    .empty()
                    .html('<iframe id="'+name+'" name="'+name+'" class="streamzine-image-picker-response-frame" />');

                // reseting the upload form
                $('#' + formId).trigger('reset');

                return false;
            }

            //
            // API
            //
            this.show = function(id) {
                $('#' + dialogId)
                .modal('show')
                .draggable({handle: ".modal-header"});
            }
        };
    }
}

