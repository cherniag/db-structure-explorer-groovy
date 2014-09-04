if(Badges == undefined) {
    var Badges = {};

    var GenericDialog = function(dialogId, saveHandler, showHandler, beforeSave) {
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

        var self = this;

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
            if(beforeSave) {
                beforeSave();
                return;
            }

            okayClicked = true;

            if(_value || okayClicked && _optional) {
                saveHandler(_value);
                self.hide();
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

        this.hide = function() {
            _value = null;
            $('#' + dialogId).modal('hide');
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

    var AddResolutionDialog = function(dialogId, endpointUrl) {
        var _genericDialog = new GenericDialog(dialogId, function(){}, _init, _beforeSave);
        _genericDialog.setTitle('Add Resolution');

        function _getDeviceTypes() {
            return $('#' + dialogId + ' select');
        }

        function _getWidth() {
            return $('#' + dialogId + ' input[name=width]');
        }

        function _getHeight() {
            return $('#' + dialogId + ' input[name=height]');
        }

        function _getError() {
            return $('#' + dialogId + ' div[class=hasError]');
        }

        function _getAllInputs() {
            return $('#' + dialogId + ' input');
        }

        function _getFeedback() {
            return $('#' + dialogId + ' .badges-feedback');
        }

        function _init() {
            // device type
            var deviceTypes = $('#' + dialogId + ' select').empty().removeClass('hasErrorInput');
            for(var i=0; i < Badges.deviceTypes.length; i++) {
                var deviceType = Badges.deviceTypes[i];
                deviceTypes.append($("<option></option>").attr("value", deviceType).text(deviceType));
            }
            // resolution
            _getAllInputs().removeClass('hasErrorInput');
            _getHeight().val('');
            _getWidth().val('');
            // error
            _getError().empty();
        }

        function _beforeSave() {
            var deviceType = _getDeviceTypes().val();
            var width = _getWidth().val();
            var height = _getHeight().val();

            _genericDialog.setValue({
                deviceType: deviceType,
                width: width,
                height: height
            });

            _getFeedback().text("Please wait...");
            _getError().empty();
            _getAllInputs().removeClass('hasErrorInput');
            $.ajax({
                url : endpointUrl,
                dataType: 'json',
                contentType: 'application/json',
                type: "POST",
                data : JSON.stringify(_genericDialog.getValue()),
                success : function(data, textStatus) {
                    _getFeedback().text("Refreshing window...");
                    _getError().empty();
                    window.location.reload();
                },
                error: function(response) {
                    _getFeedback().text("");
                    _getError().empty();
                    _getAllInputs().removeClass('hasErrorInput');

                    if(response.status == 500) {
                        alert('Some unhandled problem occurred. Please contact developers');
                        return;
                    }

                    if(response.status == 400) {
                        try {
                            var errors = JSON.parse(response.responseText);

                            for(var i=0; i < errors.length; i++) {
                                var e = errors[i];

                                // this is global error: duplicate for existing resolution
                                if(!e.key) {
                                    _getError().text(e.message);
                                    return;
                                }

                                //
                                if(e.key) {
                                    $('#' + dialogId + ' input[name=' + e.key + ']').addClass('hasErrorInput');
                                    _getError().append(
                                        $('<span></span>').text(e.message).append($('<br/>'))
                                    );
                                }
                            }
                        } catch(err) {
                            if(err instanceof SyntaxError) {
                                _getError().text('Not valid data');
                            } else {
                                alert('Some unhandled problem occurred. Please contact developers');
                            }
                        }
                    }
                }
            });
        }


        //
        // API
        //
        this.show = function() {
            _genericDialog.show();
        }
    };

    var AddBadgeDialog = function(dialogId, endpointUrl) {
        var _genericDialog = new GenericDialog(dialogId, function(){}, _init, _beforeSave);

        function _getFrame() {
            return $($('#' + dialogId + ' iframe')[0]);
        }

        function _getFeedback() {
            return $('#' + dialogId + ' .badges-feedback');
        }

        function _getError() {
            return $('#' + dialogId + ' div[class=hasError]');
        }

        function _getAllInputs() {
            return $('#' + dialogId + ' input');
        }

        function _init() {
            $('#' + dialogId + ' form input[name=title]').removeClass('hasErrorInput');

            var name = dialogId + "_" + new Date().getTime();
            $('#imageUploadResponseHolderId')
                .empty()
                .html('<iframe id="'+name+'" name="'+name+'" style="border:none;width:100%;min-height:270px;" />');

            // resetting the upload form
            $('#' + dialogId + ' form').trigger('reset').submit(function(event) {
                var form = event.target;
                form.target = _getFrame().attr('name');

                // display 'Loading ...'
                var body = $(document.getElementById(_getFrame().attr('name')).contentWindow.document.body);
                body.html('Loading...');
            });

            $('#' + dialogId + ' form input[type="file"]').change(function() {
                $('#' + dialogId + ' form').submit();
            });
        }

        function _beforeSave() {
            var doc = document.getElementById(_getFrame().attr('name')).contentWindow.document;

            var file = $(doc.getElementById('fileNameId')).val();
            var width = $(doc.getElementById('width')).val();
            var height = $(doc.getElementById('height')).val();
            var title = $('#' + dialogId + ' form input[name=title]').val();

            _genericDialog.setValue({
                file: file,
                title: title,
                width: width,
                height: height
            });

            _getFeedback().text("Please wait...");
            _getError().empty();
            _getAllInputs().removeClass('hasErrorInput');
            $.ajax({
                url: endpointUrl,
                dataType: 'json',
                contentType: 'application/json',
                type: "POST",
                data: JSON.stringify(_genericDialog.getValue()),
                success: function (data, textStatus) {
                    _getError().empty();
                    _getFeedback().text("Refreshing window...");
                    window.location.reload();
                },
                error: function (response) {
                    _getFeedback().text("");
                    _getError().empty();

                    if (response.status == 500) {
                        alert('Some unhandled problem occurred. Please contact developers');
                        return;
                    }

                    if (response.status == 400) {
                        try {
                            var errors = JSON.parse(response.responseText);

                            for (var i = 0; i < errors.length; i++) {
                                var e = errors[i];
                                $('#' + dialogId + ' form input[name=' + e.key + ']').addClass('hasErrorInput');
                                _getError().append(
                                    $('<span></span>').text(e.message).append($('<br/>'))
                                );
                            }
                        } catch (err) {
                            // invalid parameters
                            alert('Some unhandled problem occurred. Please contact developers')
                        }
                    }
                }
            });
        }

        //
        // API
        //
        this.show = function() {
            _genericDialog.show();
        }
    };

    var AddDimensionDialog = function(dialogId, endpointUrl) {
        var _genericDialog = new GenericDialog(dialogId, function(){}, _init, _beforeSave);

        var aliasId;
        var resolutionId;

        function _getWidth() {
            return $('#' + dialogId + ' input[name=width]');
        }

        function _getHeight() {
            return $('#' + dialogId + ' input[name=height]');
        }

        function _getAllInputs() {
            return $('#' + dialogId + ' input');
        }

        function _init() {
            _getHeight().val('').removeClass('hasErrorInput');
            _getWidth().val('').removeClass('hasErrorInput');
            _getError().empty();
            _getAllInputs().removeClass('hasErrorInput');
        }

        function _getError() {
            return $('#' + dialogId + ' div[class=hasError]');
        }

        function _getFeedback() {
            return $('#' + dialogId + ' .badges-feedback');
        }

        function _beforeSave() {
            var h = _getHeight().val();
            var w = _getWidth().val();

            _genericDialog.setValue({
                width: w,
                height: h,
                alias: aliasId,
                resolution: resolutionId
            });

            _getFeedback().text("Please wait...");
            _getError().empty();
            _getAllInputs().removeClass('hasErrorInput');
            $.ajax({
                url: endpointUrl,
                dataType: 'json',
                contentType: 'application/json',
                type: "POST",
                data: JSON.stringify(_genericDialog.getValue()),
                success: function (data, textStatus) {
                    _getFeedback().text("Refreshing window...");
                    window.location.reload();
                    window.location.reload();
                },
                error: function (response) {
                    _getFeedback().text("");
                    _getError().empty();
                    _getAllInputs().removeClass('hasErrorInput');

                    if (response.status == 500) {
                        alert('Some unhandled problem occurred. Please contact developers');
                        return;
                    }

                    if (response.status == 400) {
                        try {
                            var errors = JSON.parse(response.responseText);

                            for (var i = 0; i < errors.length; i++) {
                                var e = errors[i];
                                $('#' + dialogId + ' input[name=' + e.key + ']').addClass('hasErrorInput');
                                _getError().append(
                                    $('<span></span>').text(e.message).append($('<br/>'))
                                );
                            }
                        } catch (err) {
                            // invalid parameters
                            alert('Some unhandled problem occurred. Please contact developers')
                        }
                    }
                }
            });
        }

        //
        // API
        //
        this.show = function(a, r) {
            aliasId = a;
            resolutionId = r;

            _genericDialog.show();
        }
    };
}

