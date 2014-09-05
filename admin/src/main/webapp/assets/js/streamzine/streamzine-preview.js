/*

REFACTOR IT !!!

 */

if(StreamzinePreview == undefined) {
    function _removeViewClasses(id) {
        $('#' + id).removeAttr('class');
    }

    var _decideRenderer = function(editor, view, block) {
        var type = (block.shapeType.$name) ? block.shapeType.$name : block.shapeType;
        var contentType = (block.contentType.$name) ? block.contentType.$name : block.contentType;
        var subType  = block.key;

        // editor is defined if there is in single block preview
        if(editor) {
            editor.empty().show();
        }
        if(view) {
            view.empty();
        }

        var slimBannerRenderer =  {
            render: function() {
                var coverUrl = block.coverUrl;
                if(!coverUrl) {
                    alert('No cover url assigned. Please assign before preview');
                    return;
                }
                var cover = $('<img />')
                    .attr('src', Streamzine.Presenter.Editor.imagesBaseUrl + '/' + coverUrl)
                    .attr('width', 640)
                    .attr('height', 84)
                    .css({position: 'absolute'})
                    .appendTo(view);
            }
        };

        var found = {
            SLIM_BANNER: {
                MUSIC: {
                    MANUAL_COMPILATION: slimBannerRenderer,
                    PLAYLIST: slimBannerRenderer,
                    TRACK: slimBannerRenderer
                },
                PROMOTIONAL: {
                    INTERNAL_AD: slimBannerRenderer,
                    EXTERNAL_AD: slimBannerRenderer
                },
                NEWS: {
                    STORY: slimBannerRenderer,
                    LIST: slimBannerRenderer
                }
            },
            NARROW: {
                MUSIC: {
                    PLAYLIST: {
                        render: function() {
                            //
                            // Validation
                            //
                            var coverUrl = block.coverUrl;
                            if(!coverUrl) {
                                alert('No cover url assigned. Please assign before preview');
                                return;
                            }

                            //
                            // View
                            //
                            // cover
                            var cover = $('<img />')
                                .attr('src', Streamzine.Presenter.Editor.imagesBaseUrl + '/' + coverUrl)
                                .attr('width', 318)
                                .attr('height', 460)
                                .css({position: 'absolute'})
                                .appendTo(view);

                            if(block.badgeFileNameAlias) {
                                // badge
                                $('<img />')
                                    .attr('src', Streamzine.Presenter.Editor.imagesBaseUrl + '/' + block.badgeFileNameAlias.fileName)
                                    .css({position: 'absolute', top: 210, left: 4})
                                    .appendTo(view);
                            }

                            var number = $('<div class="sz-preview-title-number">17</div>')
                                .css({ position: "absolute", left: 20, top: 14}).appendTo(view);
                            var tracks = $('<div class="sz-preview-title-tracks">tracks</div>')
                                .css({ position: "absolute", left: 20, top: (14 + 46 + 4)}).appendTo(view);
                            var title = $('<div class="sz-preview-title-title-narrow"></div>')
                                .css({ position: "absolute", left: 20, top: 364}).appendTo(view).html(block.title);

                            // editor is defined if there is in single block preview
                            if(editor) {
                                //
                                // Editor
                                //
                                var textAreaTitle = $('<textarea maxlength="255" />')
                                    .val(block.title)
                                    .keyup(function () {
                                        title.html($(this).val());
                                    })
                                    .blur(function () {
                                        title.html($(this).val());
                                    })
                                    .appendTo(editor);

                                //
                                // Buttons
                                //
                                $('#streamzinePreviewSaveId').on('click.own', function(e) {
                                    e.preventDefault();
                                    block.title = textAreaTitle.val();
                                    Events.fire('BLOCK_CHANGED', block);
                                    $('#streamzinePreviewSaveId').off('click.own');
                                    $('#previewModalDialogId').modal('hide');
                                });
                                $('#streamzinePreviewCancelId').on('click.own', function(e) {
                                    e.preventDefault();
                                    if(block.title != textAreaTitle.val() || block.subTitle != textAreaSubTitle.val()) {
                                        if(confirm('Save changes before quit?')) {
                                            block.title = textAreaTitle.val();
                                            Events.fire('BLOCK_CHANGED', block);
                                            $('#streamzinePreviewCancelId').off('click.own');
                                        }
                                    }
                                    $('#streamzinePreviewCancelId').off('click.own');
                                });
                            }
                        }
                    },
                    TRACK: {
                        render: function() {
                            //
                            // Validation
                            //
                            var coverUrl = block.coverUrl;
                            if(!coverUrl) {
                                alert('No cover url assigned. Please assign before preview');
                                return;
                            }

                            //
                            // View
                            //
                            // cover
                            var cover = $('<img />')
                                .attr('src', Streamzine.Presenter.Editor.imagesBaseUrl + '/' + coverUrl)
                                .attr('width', 318)
                                .attr('height', 460)
                                .css({position: 'absolute'})
                                .appendTo(view);

                            if(block.badgeFileNameAlias) {
                                // badge
                                $('<img />')
                                    .attr('src', Streamzine.Presenter.Editor.imagesBaseUrl + '/' + block.badgeFileNameAlias.fileName)
                                    .css({position: 'absolute', top: 264, left: 0})
                                    .appendTo(view);
                            }

                            var number = $('<div class="sz-preview-title-number">17</div>')
                                .css({ position: "absolute", left: 20, top: 14}).appendTo(view);
                            var tracks = $('<div class="sz-preview-title-tracks">tracks</div>')
                                .css({ position: "absolute", left: 20, top: (14 + 46 + 4)}).appendTo(view);
                            var title = $('<div class="sz-preview-title-title-narrow"></div>')
                                .css({ position: "absolute", left: 20, top: 364}).appendTo(view).html(block.title);

                            // editor is defined if there is in single block preview
                            if(editor) {
                                //
                                // Editor
                                //
                                var textAreaTitle = $('<textarea maxlength="255" />')
                                    .val(block.title)
                                    .keyup(function () {
                                        title.html($(this).val());
                                    })
                                    .blur(function () {
                                        title.html($(this).val());
                                    })
                                    .appendTo(editor);

                                //
                                // Buttons
                                //
                                $('#streamzinePreviewSaveId').on('click.own', function(e) {
                                    e.preventDefault();
                                    block.title = textAreaTitle.val();
                                    Events.fire('BLOCK_CHANGED', block);
                                    $('#streamzinePreviewSaveId').off('click.own');
                                    $('#previewModalDialogId').modal('hide');
                                });
                                $('#streamzinePreviewCancelId').on('click.own', function(e) {
                                    e.preventDefault();
                                    if(block.title != textAreaTitle.val() || block.subTitle != textAreaSubTitle.val()) {
                                        if(confirm('Save changes before quit?')) {
                                            block.title = textAreaTitle.val();
                                            Events.fire('BLOCK_CHANGED', block);
                                            $('#streamzinePreviewCancelId').off('click.own');
                                        }
                                    }
                                    $('#streamzinePreviewCancelId').off('click.own');
                                });
                            }
                        }
                    }
                }
            },
            WIDE: {
                MUSIC: {
                    PLAYLIST: {
                        render: function() {
                            //
                            // Validation
                            //
                            var coverUrl = block.coverUrl;
                            if(!coverUrl) {
                                alert('No cover url assigned. Please assign before preview');
                                return;
                            }

                            //
                            // View
                            //
                            // cover
                            var cover = $('<img />')
                                .attr('src', Streamzine.Presenter.Editor.imagesBaseUrl + '/' + coverUrl)
                                .attr('width', 640)
                                .attr('height', 580)
                                .css({position: 'absolute'})
                                .appendTo(view);

                            if(block.badgeFileNameAlias) {
                                // badge
                                $('<img />')
                                    .attr('src', Streamzine.Presenter.Editor.imagesBaseUrl + '/' + block.badgeFileNameAlias.fileName)
                                    .css({position: 'absolute', top: 320, left: 4})
                                    .appendTo(view);
                            }

                            var heylist = $('<div class="sz-preview-title-heylist">heylist</div>')
                                .css({ position: "relative", top: 22, lineHeight:'44px', fontSize: '44px', textAlign: 'center' }).appendTo(view);
                            $('<div></div>')
                                .css({position: "absolute", top: 70, left: 268, width: 52, height: 4, backgroundColor: '#fff'})
                                .appendTo(view);
                            $('<div></div>')
                                .css({position: "absolute", top: 70, left: (268+52), width: 52, height: 4, backgroundColor: '#fff', opacity:'0.5'})
                                .appendTo(view);

                            // menu icon
                            $('<img src="../../assets/img/streamzine/buttons/ic_menu_normal.png" width="50" height="32" />')
                                .css({ position: "absolute", left: 12, top: 36 }).appendTo(view);

                            var number = $('<div class="sz-preview-title-number">17</div>')
                                .css({ position: "absolute", left: 20, top: 146}).appendTo(view);
                            var tracks = $('<div class="sz-preview-title-tracks">tracks</div>')
                                .css({ position: "absolute", left: 20, top: (146 + 46 + 4)}).appendTo(view);
                            var title = $('<div class="sz-preview-title-title"></div>')
                                .css({ position: "absolute", left: 20, top: 460}).appendTo(view).html(block.title);
                            var subTitle = $('<div class="sz-preview-title-sub-title"></div>')
                                .css({ position: "absolute", left: 20, top: (460 + 36 + 14)}).appendTo(view).html(block.subTitle);
                            // editor is defined if there is in single block preview
                            if(editor) {
                                //
                                // Editor
                                //
                                var textAreaTitle = $('<textarea maxlength="255" />')
                                    .val(block.title)
                                    .keyup(function () {
                                        title.html($(this).val());
                                    })
                                    .blur(function () {
                                        title.html($(this).val());
                                    })
                                    .appendTo(editor);
                                var textAreaSubTitle = $('<textarea maxlength="255" />')
                                    .val(block.subTitle)
                                    .keyup(function () { subTitle.html($(this).val()); })
                                    .blur(function () { subTitle.html($(this).val()); })
                                    .appendTo(editor);

                                //
                                // Buttons
                                //
                                $('#streamzinePreviewSaveId').on('click.own', function(e) {
                                    e.preventDefault();
                                    block.title = textAreaTitle.val();
                                    block.subTitle = textAreaSubTitle.val();
                                    Events.fire('BLOCK_CHANGED', block);

                                    $('#streamzinePreviewSaveId').off('click.own');
                                    $('#streamzinePreviewCancelId').off('click.own');

                                    $('#previewModalDialogId').modal('hide');
                                });
                                $('#streamzinePreviewCancelId').on('click.own', function(e) {
                                    e.preventDefault();
                                    if(block.title != textAreaTitle.val() || block.subTitle != textAreaSubTitle.val()) {
                                        if(confirm('Save changes before quit?')) {
                                            block.title = textAreaTitle.val();
                                            block.subTitle = textAreaSubTitle.val();
                                            Events.fire('BLOCK_CHANGED', block);
                                        }
                                    }
                                    $('#streamzinePreviewSaveId').off('click.own');
                                    $('#streamzinePreviewCancelId').off('click.own');
                                });
                            }
                        }
                    },
                    TRACK: {
                        render: function() {
                            //
                            // Validation
                            //
                            var coverUrl = block.coverUrl;
                            if(!coverUrl) {
                                alert('No cover url assigned. Please assign before preview');
                                return;
                            }

                            //
                            // View
                            //
                            // cover
                            var cover = $('<img />')
                                .attr('src', Streamzine.Presenter.Editor.imagesBaseUrl + '/' + coverUrl)
                                .attr('width', 640)
                                .attr('height', 580)
                                .css({position: 'absolute'})
                                .appendTo(view);

                            if(block.badgeFileNameAlias) {
                                // badge
                                $('<img />')
                                    .attr('src', Streamzine.Presenter.Editor.imagesBaseUrl + '/' + block.badgeFileNameAlias.fileName)
                                    .css({position: 'absolute', top: 380, left: 0})
                                    .appendTo(view);
                            }

                            var heylist = $('<div class="sz-preview-title-heylist">heylist</div>')
                                .css({ position: "relative", top: 22, lineHeight:'44px', fontSize: '44px', textAlign: 'center' }).appendTo(view);
                            $('<div></div>')
                                .css({position: "absolute", top: 70, left: 268, width: 52, height: 4, backgroundColor: '#fff'})
                                .appendTo(view);
                            $('<div></div>')
                                .css({position: "absolute", top: 70, left: (268+52), width: 52, height: 4, backgroundColor: '#fff', opacity:'0.5'})
                                .appendTo(view);

                            // menu icon
                            $('<img src="../../assets/img/streamzine/buttons/ic_menu_normal.png" width="50" height="32" />')
                                .css({ position: "absolute", left: 12, top: 36 }).appendTo(view);

                            var number = $('<div class="sz-preview-title-number">17</div>')
                                .css({ position: "absolute", left: 20, top: 146}).appendTo(view);
                            var tracks = $('<div class="sz-preview-title-tracks">tracks</div>')
                                .css({ position: "absolute", left: 20, top: (146 + 46 + 4)}).appendTo(view);
                            var title = $('<div class="sz-preview-title-title"></div>')
                                .css({ position: "absolute", left: 20, top: 460}).appendTo(view).html(block.title);
                            var subTitle = $('<div class="sz-preview-title-sub-title"></div>')
                                .css({ position: "absolute", left: 20, top: (460 + 36 + 14)}).appendTo(view).html(block.subTitle);
                            // editor is defined if there is in single block preview
                            if(editor) {
                                //
                                // Editor
                                //
                                var textAreaTitle = $('<textarea maxlength="255" />')
                                    .val(block.title)
                                    .keyup(function () {
                                        title.html($(this).val());
                                    })
                                    .blur(function () {
                                        title.html($(this).val());
                                    })
                                    .appendTo(editor);
                                var textAreaSubTitle = $('<textarea maxlength="255" />')
                                    .val(block.subTitle)
                                    .keyup(function () { subTitle.html($(this).val()); })
                                    .blur(function () { subTitle.html($(this).val()); })
                                    .appendTo(editor);

                                //
                                // Buttons
                                //
                                $('#streamzinePreviewSaveId').on('click.own', function(e) {
                                    e.preventDefault();
                                    block.title = textAreaTitle.val();
                                    block.subTitle = textAreaSubTitle.val();
                                    Events.fire('BLOCK_CHANGED', block);

                                    $('#streamzinePreviewSaveId').off('click.own');
                                    $('#streamzinePreviewCancelId').off('click.own');

                                    $('#previewModalDialogId').modal('hide');
                                });
                                $('#streamzinePreviewCancelId').on('click.own', function(e) {
                                    e.preventDefault();
                                    if(block.title != textAreaTitle.val() || block.subTitle != textAreaSubTitle.val()) {
                                        if(confirm('Save changes before quit?')) {
                                            block.title = textAreaTitle.val();
                                            block.subTitle = textAreaSubTitle.val();
                                            Events.fire('BLOCK_CHANGED', block);
                                        }
                                    }
                                    $('#streamzinePreviewSaveId').off('click.own');
                                    $('#streamzinePreviewCancelId').off('click.own');
                                });
                            }
                        }
                    }
                }
            }
        }[type];

        if(!found) {
            return null;
        }

        found = found[contentType];
        if(!found) {
            return null;
        }

        found = found[subType];
        if(!found) {
            return null;
        }

        return found;
    };

    //
    // Controller
    //
    var StreamzinePreview = {};
    StreamzinePreview.Presenter = {};

    StreamzinePreview.Presenter.previewBlockUI = function(id) {
        Events.fire('BLOCK_EDITED', id);

        //
        // Model
        //
        var block = Streamzine.Model.findBlockById(id);
        var type = block.shapeType.$name;

        //
        // Dialog
        //
        // the whole modal
        var modal = $('#previewModalDialogId');

        _removeViewClasses('previewModalDialogId');
        _removeViewClasses('previewModalDialogViewId');
        _removeViewClasses("previewModalDialogCommonId");
        _removeViewClasses("previewModalDialogEditorId");

        var view =   $('#previewModalDialogViewId');
        var editor = $('#previewModalDialogEditorId');

        modal.addClass("modal fade streamzine-modal streamzine-modal-preview-" + type.toLowerCase());
        editor.addClass("streamzine-modal-property-editor streamzine-modal-property-editor-" + type.toLowerCase());
        $('#previewModalDialogCommonId').addClass("streamzine-modal-common-editor-" + type.toLowerCase());

        var renderer = _decideRenderer(editor, view, block);
        if(renderer == null) {
            alert('Preview for ' + block.shapeType.$name + ' of ' + block.contentType.$name + ' not implemented');
            return;
        }
        renderer.render();

        modal.modal('show').css({
            'margin-top': function () {
                return '-300px';
            }
        });
    }

    StreamzinePreview.Presenter.previewUpdate = function() {
        var withReCalcPositions = Streamzine.Presenter.getModelToSend(Streamzine.Model, true);
        var blocks = withReCalcPositions.blocks;

        // to do not perform when no blocks
        if(blocks.length == 0) {
            alert('No blocks to preview');
            return;
        }

        // to do not perform when no included blocks
        var foundSupported = [];
        for(var i=0; i < blocks.length; i++) {
            var block = blocks[i];
            if(_decideRenderer(null, null, block)) {
                foundSupported.push(block);
            }
        }
        if(foundSupported.length == 0) {
            alert('No blocks to preview');
            return;
        }

        var modal = $('#previewModalDialogId');

        _removeViewClasses('previewModalDialogId');
        _removeViewClasses('previewModalDialogViewId');
        _removeViewClasses("previewModalDialogCommonId");
        _removeViewClasses("previewModalDialogEditorId");

        var view =   $('#previewModalDialogViewId');
        var editor = $('#previewModalDialogEditorId');

        modal.addClass("modal fade streamzine-modal streamzine-modal-preview-update");
        editor.addClass("streamzine-modal-property-editor streamzine-modal-property-editor-update");
        $('#previewModalDialogCommonId').addClass("streamzine-modal-common-editor-update");

        modal.modal('show').css({
            'margin-top': function () {
                return '-300px';
            }
        });

        editor.empty().hide();
        view.empty();

        // start rendering
        // sort according to positions
        blocks.sort(function(f, s) {
            return parseInt(f.position) - parseInt(s.position);
        });
        var firstNarrowBlockDetected = false;
        for(var i = 0; i < blocks.length; i++) {
            var block = blocks[i];

            if(!_decideRenderer(null, null, block)) {
                continue;
            }

            var viewForTheBlock = $('<div></div>')
                .attr('class', 'streamzine-modal-common-editor-' + block.shapeType.toLowerCase())
                .css({position: 'relative'})
                .appendTo(view);

            if(block.shapeType == 'NARROW') {
                if(firstNarrowBlockDetected) {
                    viewForTheBlock.css({marginLeft: '318px'});
                } else {
                    viewForTheBlock.css('float', 'left');
                }
                firstNarrowBlockDetected = !firstNarrowBlockDetected;
            }

            var renderer = _decideRenderer(null, viewForTheBlock, block);
            renderer.render();
        }
    }
}

