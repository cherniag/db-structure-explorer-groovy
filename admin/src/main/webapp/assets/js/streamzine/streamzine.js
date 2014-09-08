if(Streamzine == undefined) {
    var Streamzine = {};

    Streamzine.canEdit = true;

    Streamzine.Model;
    Streamzine.ModelCopy;

    Streamzine.Presenter = {};

    //
    // User Management
    //
    Streamzine.Presenter.selectedUser = function(dataArray) {

        if(dataArray[0]){
            Streamzine.Model.addUser(dataArray[1]);
        }else{
            Streamzine.Model.removeUser(dataArray[1]);
        }
        Streamzine.Presenter.renderUsers();
    }

    Streamzine.Presenter.renderUsers = function() {
        var userNames = Streamzine.Model.getUsers().sort();

        var innerHtml = "";
        if(userNames) {
            for (var i = 0; i < userNames.length; i++){
                innerHtml+= Template.render('<div><span>{userName} | </span> <a href="javascript:;" onclick="return Events.fire(\'USER_PICKED\', [false,\'{userName}\']);">Delete</a></div>', {userName: userNames[i]})
            }
        }
        $('#selectedUserId').empty().html(Template.render(innerHtml));
    }

    Streamzine.Presenter.valueTyped = function(data) {
        var currentBlock = Streamzine.Model.getCurrentBlock();

        var value;
        if(currentBlock.key == 'INTERNAL_AD' && data.field == 'value') {
            value = (data.value.action) ? data.value.url + '#' + data.value.action : data.value.url;
        } else {
            value = data.value;
        }
        Streamzine.Model.updateCurrentBlock(data.field, value);
    }

    Streamzine.Presenter.valuePicked = function(data) {
        Streamzine.Presenter.valueTyped(data);

        var block = Streamzine.Model.getCurrentBlock();
        Events.fire('BLOCK_CHANGED', block);
    }

    //
    //
    // Manage updates
    //
    //
    Streamzine.Presenter.initModel = function(model, modelCopy, canEdit) {
        Streamzine.Model = new UpdateModel(model);
        Streamzine.ModelCopy = new UpdateModel(modelCopy);

        Streamzine.canEdit = canEdit;

        // create current in edit block
        Streamzine.Model.currentInEditId = null;
    }

    Streamzine.Presenter.deleteUpdate = function(formId, id, dateString) {
        if(Streamzine.canEdit) {
            if(confirm('Are you sure to delete streamzine update for ' + dateString + "?")) {
                if(Streamzine.Presenter.detectedChanges()) {
                    if(confirm('All changes will be lost. Proceed?')) {
                        $('#'+formId).submit();
                    }
                } else {
                    $('#'+formId).submit();
                }
            }
        }
    }

    Streamzine.Presenter.onEditUpdateSubmit = function(id, formId) {
        if(Streamzine.Presenter.detectedChanges()) {
            if(confirm('All changes will be lost. Proceed?')) {
                document.getElementById(formId).submit();
            }
        } else {
            document.getElementById(formId).submit();
        }
    }

    Streamzine.Presenter.addUpdate = function(time, selectedPublishDateObject) {
        var form = $('#createStreamzineUpdateForm');
        var url = Streamzine.Presenter.ContextPath + '/streamzine/add/' + selectedPublishDateObject + '_' + time + ':00';
        form.attr('action', url);
    }

    Streamzine.Presenter.onAddUpdateSubmit = function() {
        if(Streamzine.Presenter.detectedChanges()) {
            if(confirm('All changes will be lost. Proceed?')) {
                document.getElementById('createStreamzineUpdateForm').submit();
            }
        } else {
            document.getElementById('createStreamzineUpdateForm').submit();
        }
    }

    Streamzine.Presenter.selectPublishDate = function(date) {
        if(Streamzine.Presenter.detectedChanges()) {
            if(confirm('All changes will be lost. Proceed?')) {
                location.href = Streamzine.Presenter.ContextPath + '/streamzine?selectedPublishDate=' + date;
            }
        } else {
            location.href = Streamzine.Presenter.ContextPath + '/streamzine?selectedPublishDate=' + date;
        }
    }

    //
    //
    // Manage blocks
    //
    //
    Streamzine.Presenter.handleEmptyBlocksUI = function() {
        var items = $('#sortableGrid').data().sortable.items;

        if(items.length == 2) {
            $('#firstBlockId').removeClass('streamzine_placeholder_true');
            $('#firstBlockId').addClass('streamzine_placeholder_false');
        }

        if(items.length == 1) {
            $('#firstBlockId').removeClass('streamzine_placeholder_false');
            $('#firstBlockId').addClass('streamzine_placeholder_true');
        }
    }

    Streamzine.Presenter.swapTiles = function(firstId, secondId) {
        var secondBlockInNarrowTile = Streamzine.Model.isSecondBlockInNarrowTile(firstId);


        if(secondBlockInNarrowTile) {
            $('ul#sortableGrid div#' + secondId).attr('id', firstId);
        } else {
            $('ul#sortableGrid div#' + firstId).attr('id', secondId);
        }

        Streamzine.Model.swap(firstId, secondId);
        Streamzine.Presenter.Tile.updateDraggableBlockById(firstId);
        Streamzine.Presenter.Tile.updateDraggableBlockById(secondId);
    }

    Streamzine.Presenter.removeBlockUI = function(ids) {
        var grid = $('#sortableGrid');

        for(var i=0; i<ids.length;i++) {
            var blockId = ids[i];

            // walk through the ids (for narrow it could be two items)
            var items = grid.data().sortable.items;

            // remove from UI
            for(var j = 0; j < items.length; j++) {
                var foundElement = items[j].item.attr('id') == blockId;
                if(foundElement) {
                    items[j].item.remove();
                    grid.sortable("refresh");
                    break;
                }
            }

            // change the model
            Streamzine.Model.deleteBlockById(blockId);
        }

        // notify removed
        Streamzine.Presenter.handleEmptyBlocksUI();
    }

    Streamzine.Presenter.includeBlockUI = function(checked, blockId, blockId2) {
        var block = Streamzine.Model.findBlockById(blockId);
        block.included = checked;
        $('#' + blockId + '_include').attr('checked', checked);

        if(blockId2) {
            // handle for narrow: if one is included, the second one also should be included/excluded
            var next = Streamzine.Model.findBlockById(blockId2);
            next.included = checked;
            $('#' + blockId2 + '_include').attr('checked', checked);
        }
    }

    Streamzine.Presenter.addBlockUI = function(i) {
        var shapeType = i.attr('data-shapeType');

        i.removeClass('streamzine_shape');
        i.removeClass('streamzine_shape_' + shapeType);

        // depends on what type shape type we have: special case for narrow
        if(shapeType == 'NARROW') {
            var generatedId = 'block_item_' + new Date().getTime();

            var firstGeneratedId = generatedId + "_1";
            var secondGeneratedId = generatedId + "_2";

            var first = Streamzine.Model.createEmptyBlock(firstGeneratedId, shapeType);
            var second = Streamzine.Model.createEmptyBlock(secondGeneratedId, shapeType);
            Streamzine.Model.addBlock(first);
            Streamzine.Model.addBlock(second);

            i.attr('id', firstGeneratedId);
            i.html(Streamzine.Presenter.Tile.createNarrowTile(first, second));


            Events.fire('BLOCK_EDITED', first.id);

        } else {
            var generatedId = 'block_item_' + new Date().getTime();
            i.attr('id', generatedId);

            var newBlock = Streamzine.Model.createEmptyBlock(generatedId, shapeType);
            Streamzine.Model.addBlock(newBlock);

            i.html(Streamzine.Presenter.Tile.createTile(newBlock));

            Events.fire('BLOCK_EDITED', newBlock.id);
        }

        // notify new added
        Streamzine.Presenter.handleEmptyBlocksUI();
    }

    Streamzine.Presenter.detectedChanges = function() {
        if(!Streamzine.canEdit) {
            return false;
        }

        // means no changes done - not in edit form
        if(!Streamzine.Model) {
            return false;
        }

        var currentModel = Streamzine.Presenter.getModelToSend(Streamzine.Model, true);
        var modelCopy = Streamzine.Presenter.getModelToSend(Streamzine.ModelCopy, false);

        return !currentModel.equalTo(modelCopy);
    }

    //
    //
    // Cancel and Save button handlers
    //
    //
    //
    // Cancel
    //
    Streamzine.Presenter.cancelBlocksChanges = function() {
        if(Streamzine.Presenter.detectedChanges()) {
            if(confirm('All changes will be lost. Proceed?')) {
                location.reload();
            }
        } else {
            location.reload();
        }
    }
    //
    // Save
    //
    Streamzine.Presenter.getModelToSend = function(model, updatePositions) {
        // try to refresh the sortable UI to make indexes stay on proper positions
        $('#sortableGrid').sortable("refresh");
        $('#sortableGrid').sortable("refreshPositions");

        // jQuery sortable does not work properly with item ids...
        // so let's walk through the all DIVs

        if(updatePositions) {
            // extract the ids of UI items
            var ids = $.map(
                $('#sortableGrid > div'),
                function(e, i) {
                    return $(e).attr('id');
                }
            );
            return model.normalize(updatePositions, ids);
        } else {
            return model.normalize(updatePositions);
        }

    }

    Streamzine.Presenter.saveBlocksChanges = function() {
        var modelToSend = Streamzine.Presenter.getModelToSend(Streamzine.Model, true);

        // update model
        $.ajax({
            url : "../update",
            dataType: 'json',
            contentType: 'application/json',
            type: "POST",
            // fill the filter value for some versions of Chrome browser to exclude '__proto__' property
            data : JSON.stringify(modelToSend, ["id", "timestamp", "userNames", "blocks", "contentType", "coverUrl", "badgeId", "included", "key", "value", "position", "shapeType", "subTitle", "title", "vip", "expanded"]),
            success : function(data, textStatus, status) {
                       alert('Your changes has been successfully saved. This page will be refreshed');
                       window.location.reload();
            },
            error: function(qXHR) {
                if (qXHR.status = 400) {
                    var responseText = qXHR.responseText;
                    var errorsObject = JSON.parse(responseText);
                    Streamzine.errorsHandler.handle(errorsObject, status);
                }
                else
                {
                    alert('Failed to update: pls refresh the page and try again');
                }
            }
        });
    }

    //
    //
    // Init handlers
    //
    //
    Streamzine.Presenter.beforeShowDateStyles = function(selectedPublishDateObject, date, updatePublishDates) {
        var selectedPublishDate = new Date(selectedPublishDateObject.time);
        if (selectedPublishDate.getDate() == date.getDate() && selectedPublishDate.getMonth() == date.getMonth() && selectedPublishDate.getFullYear() == date.getFullYear()) {
            return [true,'sz-timepicker-now', ''];
        }
        for (var i=0; i< updatePublishDates.length; i++) {
            if (updatePublishDates[i].getDate() == date.getDate() && updatePublishDates[i].getMonth() == date.getMonth() && updatePublishDates[i].getFullYear() == date.getFullYear()) {
                return [true,'highlight', ''];
            }
        }
        return [true, '',''];
    }

    Streamzine.Presenter.init = function(selectedPublishDate, selectedPublishDateString, updatePublishDates) {
        var updateDates = [];
        for (var i=0; i < updatePublishDates.length; i++) {
            var date = new Date();
            date.setYear(updatePublishDates[i].year + 1900);
            date.setMonth(updatePublishDates[i].month);
            date.setDate(updatePublishDates[i].date);
            updateDates.push(date);
        }
        //
        // datepicker calendar
        //
        $("#datepicker").datepicker({
            dateFormat : 'yy-mm-dd',
            beforeShowDay: function (date) {
                return Streamzine.Presenter.beforeShowDateStyles(selectedPublishDate, date, updateDates);
            },
            onSelect: function(dateText) {
                Streamzine.Presenter.selectPublishDate(dateText);
            }
        });
        $("#datepicker").datepicker("setDate", selectedPublishDateString);

        //
        // timepicker
        //
        $('#timepicker_customrange').timepicker({
            minutes: { interval: 15 },
            showCloseButton: true,
            rows: 3,
            showPeriodLabels: true,
            minuteText: 'Min',
            onSelect: function(newTime, inst) {
                Streamzine.Presenter.addUpdate(newTime, selectedPublishDateString);
            }
        });
    }

    Streamzine.Presenter.initEvents = function(canEdit) {
        Events
            .stop( function(){return !canEdit;} )
            .map({
                WIDGET_TYPE_CHANGED: [{
                    ctx: Streamzine.Presenter.Editor,
                    callback: Streamzine.Presenter.Editor.widgetTypeChanged
                }, {
                    ctx: Streamzine.Presenter.Tile,
                    callback: Streamzine.Presenter.Tile.updateDraggableBlock
                }],
                USER_PICKED: {
                    ctx: Streamzine.Presenter,
                    callback: Streamzine.Presenter.selectedUser
                },
                VALUE_PICKED: {
                    ctx: Streamzine.Presenter,
                    callback: Streamzine.Presenter.valuePicked
                },
                VALUE_TYPED: [{
                    ctx: Streamzine.Presenter,
                    callback: Streamzine.Presenter.valueTyped
                }, {
                    ctx: Streamzine.Presenter.Tile,
                    callback: Streamzine.Presenter.Tile.updateDraggableBlock
                }],
                USER_PICKING: [{
                    ctx: Streamzine.Presenter.Editor,
                    callback: Streamzine.Presenter.Editor.ifPresentIncludedBlock
                }, {
                    ctx: Streamzine.Presenter.Editor,
                    callback: Streamzine.Presenter.Editor.pickingTheUser
                }],
                VALUE_PICKING: {
                    ctx: Streamzine.Presenter.Editor,
                    callback: Streamzine.Presenter.Editor.pickingTheValue
                },
                BLOCK_CHANGED: [{
                    ctx: Streamzine.Presenter.Editor,
                    callback: Streamzine.Presenter.Editor.updateBlockPropertiesUIFromObject
                }, {
                    ctx: Streamzine.Presenter.Tile,
                    callback: Streamzine.Presenter.Tile.updateDraggableBlock
                }, {
                    ctx: Streamzine.Presenter.Editor,
                    callback: Streamzine.Presenter.Editor.widgetTypeChanged
                }],
                BLOCK_EDITED: [{
                    ctx: Streamzine.Presenter.Tile,
                    callback: Streamzine.Presenter.Tile.editBlockPropertiesUI
                }, {
                    ctx: Streamzine.Presenter.Editor,
                    callback: Streamzine.Presenter.Editor.editBlockPropertiesUI
                }],
                EDIT_ERROR: [{
                    ctx: Streamzine.errorsHandler,
                    callback: Streamzine.errorsHandler.onEdit
                }, {
                    ctx: Streamzine.Presenter.Editor,
                    callback: Streamzine.Presenter.Editor.markWithError
                }],
                DELETE_ERROR: [{
                    ctx: Streamzine.errorsHandler,
                    callback: Streamzine.errorsHandler.onDelete
                }, {
                    ctx: Streamzine.Presenter.Editor,
                    callback: Streamzine.Presenter.Editor.removeErrorMarkers
                }],
                BLOCK_REMOVED: [{
                    ctx: Streamzine.Presenter.Editor,
                    callback: Streamzine.Presenter.Editor.blockRemoved
                }, {
                    ctx: Streamzine.Presenter,
                    callback: Streamzine.Presenter.removeBlockUI
                }],
                BLOCK_PREVIWED: [{
                    ctx: StreamzinePreview.Presenter,
                    callback: StreamzinePreview.Presenter.previewBlockUI
                }],
                SAVE: [{
                    ctx: StreamzinePreview.Presenter,
                    callback: Streamzine.Presenter.saveBlocksChanges
                }],
                CANCEL: [{
                    ctx: StreamzinePreview.Presenter,
                    callback: Streamzine.Presenter.cancelBlocksChanges
                }]
            });
    }

    Streamzine.Presenter.initInEditMode = function(selectedPublishDate, selectedPublishDateString) {
        //
        // init blocks grid
        //
        $("#sortableGrid").sortable({
            stop: function(event, ui) {
                // true when dragged outside
                var i = $(ui.item);
                if(i.hasClass('streamzine_shape')) {
                    Streamzine.Presenter.addBlockUI(i);
                }
            },
            cancel: ".ui-state-disabled"
        });

        //
        // blocks constructor
        //
        $("#blocksEditorId").sortable({
            connectWith: '#sortableGrid',
            helper: 'clone',
            cancel: ".ui-state-disabled"
        });

        if(Streamzine.canEdit) {
            $("#wideRangeId, #narrowShapeId, #slimShapeId").draggable({
                helper: 'clone',
                connectToSortable: '#sortableGrid'
            });
        }
    }

}

