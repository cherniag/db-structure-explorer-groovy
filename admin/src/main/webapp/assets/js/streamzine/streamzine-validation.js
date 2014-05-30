if(Streamzine.errorsHandler == undefined) {
    Streamzine.ErrorWrapper = function(field) {
        var _f = field;

        this.getField = function() {
            return _f;
        }

        // parsing like 'blocks[0].property':
        this.getName = function() {
            return _f.substring(_f.indexOf('.') + 1);
        }

        this.getPosition = function() {
            return parseInt(_f.substring(_f.indexOf('[') + 1, _f.indexOf(']'))) + 1;
        }
    }

    Streamzine.errorsHandler = new function() {
        var _errors;

        function _draw(errors) {
            var _view = $('#errorsEditor');
            _view.empty();

            for(var i=0; i<errors.length; i++) {
                if(errors[i]) {
                    var rawText =
                        Template.render(
                            '<div><span class="streamzine-error-title">{message}</span> <a href="javascript:;" onclick="Events.fire(\'EDIT_ERROR\',\'{key}\')">Edit</a> | <a href="javascript:;" onclick="Events.fire(\'DELETE_ERROR\',\'{key}\')">Delete</a> </div>',
                            errors[i]
                        );
                    $(rawText).appendTo(_view);
                }
            }
        }

        function _find(key) {
            for(var i=0; i<_errors.length; i++) {
                if(_errors[i] && _errors[i].key == key) {
                    return i;
                }
            }

            throw new Error('Not found by field: ' + key + ', available: ' + JSON.stringify(_errors));
        }

        this.handle = function(data, status) {
            _errors = data;
            _draw(_errors);
        }

        this.onEdit = function(field) {
            var wrapper = new Streamzine.ErrorWrapper(field);

            var blocks = Streamzine.Presenter.getModelToSend(Streamzine.Model, true).blocks;
            var block;
            for(var j=0; j<blocks.length; j++) {
                if(blocks[j].position == wrapper.getPosition()) {
                    block = blocks[j];
                    break;
                }
            }

            Events.fire('BLOCK_EDITED', block.id);
        }

        this.onDelete = function(field) {
            var found = _find(field);

            delete _errors[found];

            _draw(_errors);
        }
    };


}

