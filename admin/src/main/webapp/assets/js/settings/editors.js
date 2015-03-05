if(Editors == undefined) {
    var EditorsHelper = new function() {
        this.getObject = function(model, path) {
            var keys = path.split('.');
            var value = model;
            for(var i=0; i<keys.length; i++) {
                if(value === undefined || value == null) {
                    return value;
                }
                value = value[keys[i]];
            }
            return value;
        };
        this.setObject = function(model, path, newValue) {
            var keys = path.split('.');
            var value = model;
            for(var i=0; i<keys.length; i++) {
                if(i==keys.length-1) {
                    value[keys[i]] = newValue;
                    return;
                }
                if(value === undefined || value == null) {
                    return;
                }
                value = value[keys[i]];
            }
        };
    };

    //
    // Editors
    //
    var TemplateEditor = function(model, dataAttributePairs, concreteEditorMixin) {
        var self = this;

        this.render = function() {
            var modelBranch = EditorsHelper.getObject(model, path());

            var link = $('<a href="javascript:;"></a>');
            link.click(function() {
                var editContent = self.edit();
                var parent = link.parent();
                parent.append(editContent);
                link.remove();
            });
            link.html(concreteEditorMixin.display(modelBranch));
            return link;
        };

        this.edit = function() {
            var modelBranch = EditorsHelper.getObject(model, path());

            var settingsEditorContent = $('<div class="settings-editor"></div>');
            var okLink = $('<a href="javascript:;">Ok</a>').click(function(){ok(settingsEditorContent.parent())});
            var cancelLink = $('<a href="javascript:;">Cancel</a>').click(function(){cancel(settingsEditorContent.parent())});
            var header = $('<div class="settings-editor-cancel"></div>').append(okLink).append($('<span> | </span>')).append(cancelLink);
            var body = $('<div></div>');

            settingsEditorContent.append(header).append(body);

            concreteEditorMixin.view(modelBranch, body).appendTo(body);

            return settingsEditorContent;
        };

        this.getValue = function() {
            return concreteEditorMixin.getValue();
        };

        function ok(parent) {
            var v = self.getValue();
            EditorsHelper.setObject(model, path(), v);
            parent.empty().append(self.render());
        }

        function cancel(parent) {
            parent.empty().append(self.render());
        }

        function path() {
            return dataAttributePairs['data-path'];
        }
    };

    //
    // Boolean
    //
    var BooleanEditor = function(model, dataAttributePairs) {
        var handler;
        var selectedValue = false;
        var template = new TemplateEditor(model, dataAttributePairs, {
            display: function(modelBranch){
                return (modelBranch) ? 'Yes' : 'No';
            },
            view: function(modelBranch) {
                var checkbox =
                    $('<span>New value</span><input type="checkbox"/>')
                        .click(function(evt) {
                            selectedValue = !!$(evt.target).attr('checked');
                        });
                if(modelBranch) {
                    checkbox.attr('checked', 'checked');
                } else {
                    checkbox.removeAttr('checked');
                }
                return checkbox;
            },
            getValue: function() {
                return selectedValue;
            }
        });

        //
        // API
        //
        this.render = function() {
            return template.render();
        };
    };

    //
    // Text
    //
    var TextEditor = function(model, dataAttributePairs, dataTransformer) {
        var currentTextView;
        var dt = (dataTransformer)?dataTransformer:function(v){return v;};
        var template = new TemplateEditor(model, dataAttributePairs, {
            display: function(modelBranch){
                return modelBranch;
            },
            view: function(modelBranch) {
                currentTextView = $('<input type="text"/>').val(modelBranch);
                return currentTextView;
            },
            getValue: function() {
                return dt(currentTextView.val());
            }
        });

        //
        // API
        //
        this.render = function() {
            return template.render();
        }
    };

    //
    // Digit
    //
    var DigitEditor = function(model, dataAttributePairs) {
        var template = new TextEditor(model, dataAttributePairs, function(v) {
            return isNaN(v) ? 0 : parseInt(v);
        });

        //
        // API
        //
        this.render = function() {
            return template.render();
        }
    };

    //
    // Dictionary Optional
    //
    var DictionaryEditor = function(model, dataAttributePairs) {
        var selectedValue;

        var template = new TemplateEditor(model, dataAttributePairs, {
            display: function(modelBranch) {
                if(modelBranch) {
                    return modelBranch;
                } else {
                    return "...";
                }
            },
            view: function(modelBranch, body) {
                selectedValue = modelBranch;
                return getBodyView(body);
            },
            getValue: function() {
                return selectedValue;
            }
        });

        //
        // Internals
        //
        function rememberSelectedAndReRender(tagValue, body) {
            var unselect = selectedValue==tagValue;
            if(unselect) {
                selectedValue = undefined;
            } else {
                selectedValue = tagValue;
            }
            body.empty().append(getBodyView(body));
        }

        function getBodyView(body) {
            var view = $('<div class="editor-dictionary"></div>');
            const dd = model[dataAttributePairs['data-dictionary-path']];

            for(var i=0; i<dd.length;i++) {
                var tagValue = dd[i];
                var tagElement = $('<span class="editor-tag"></span>').text(tagValue);
                assignClickHandler(tagElement, body, tagValue);
                tagElement.addClass((selectedValue==tagValue) ? 'editor-tag-selected' : 'editor-tag');
                tagElement.appendTo(view);
            }
            return view;
        }

        function assignClickHandler(tagElement, body, tagValue) {
            tagElement.click(function(){rememberSelectedAndReRender(tagValue, body)});
        }

        //
        // API
        //
        this.render = function() {
            return template.render();
        }
    };

    //
    // Dictionary Strict
    //
    var DictionaryStrictEditor = function(model, dataAttributePairs) {
        var selectedValue;

        var template = new TemplateEditor(model, dataAttributePairs, {
            display: function(modelBranch) {
                return modelBranch;
            },
            view: function(modelBranch, body) {
                selectedValue = modelBranch;
                return getBodyView(body);
            },
            getValue: function() {
                return selectedValue;
            }
        });

        //
        // Internals
        //
        function rememberSelectedAndReRender(tagValue, body) {
            selectedValue = tagValue;
            body.empty().append(getBodyView(body));
        }

        function getBodyView(body) {
            var view = $('<div class="editor-dictionary"></div>');
            const dd = model[dataAttributePairs['data-dictionary-path']];

            for(var i=0; i<dd.length;i++) {
                var tagValue = dd[i];
                var tagElement = $('<span class="editor-tag"></span>').text(tagValue);
                assignClickHandler(tagElement, body, tagValue);
                tagElement.addClass((selectedValue==tagValue) ? 'editor-tag-selected' : 'editor-tag');
                tagElement.appendTo(view);
            }
            return view;
        }

        function assignClickHandler(tagElement, body, tagValue) {
            tagElement.click(function(){rememberSelectedAndReRender(tagValue, body)});
        }

        //
        // API
        //
        this.render = function() {
            return template.render();
        }
    };

    //
    // Duration
    //
    var DurationEditor = function(model, dataAttributePairs) {
        var selectedValue;
        var textView;

        var template = new TemplateEditor(model, dataAttributePairs, {
            display: function(modelBranch) {
                if(modelBranch) {
                    return modelBranch.amount + ' ' + ( (modelBranch.durationUnit) ? modelBranch.durationUnit : '' );
                } else {
                    return '...';
                }
            },
            view: function(modelBranch, body) {
                selectedValue = {
                    amount: (modelBranch.amount) ? modelBranch.amount : 0,
                    durationUnit: (modelBranch.durationUnit) ? modelBranch.durationUnit : ''
                };
                return getBodyView(body);
            },
            getValue: function() {
                selectedValue.amount = ( isNaN(textView.val()) ? 0 : parseInt(textView.val()) );
                return selectedValue;
            }
        });

        //
        // Internals
        //
        function getBodyView(body) {
            var view = $('<div class="editor-dictionary"></div>');
            textView = $('<input type="text" />').val(selectedValue.amount);
            view.append(textView).append('<br/>');

            for(var i=0; i<model['periods'].length;i++) {
                var tagValue = model['periods'][i];
                var tagElement = $('<span></span>').text(tagValue);
                assignClickHandler(tagElement, body, tagValue);
                tagElement.addClass((selectedValue.durationUnit==tagValue) ? 'editor-tag-selected' : 'editor-tag');
                tagElement.appendTo(view);
            }
            return view;
        }

        function assignClickHandler(tagElement, body, tagValue) {
            var unselect = selectedValue.durationUnit==tagValue;
            tagElement.click(function () {
                selectedValue.amount = ( isNaN(textView.val()) ? 0 : parseInt(textView.val()) );
                if(unselect) {
                    selectedValue.durationUnit=undefined;
                } else {
                    selectedValue.durationUnit=tagValue;
                }
                body.empty().append(getBodyView(body));
            });
        }

        //
        // API
        //
        this.render = function() {
            return template.render();
        }
    };

    //
    // Editors Factory
    //
    var Editors = new function() {
        var editors = {};

        construct();

        //
        // api
        //
        this.add = function(key, editor) {
            doAdd(key, editor);
        };

        this.assign = function(model, root) {
            var assigners = root.find('[data-editor]');
            for(var i=0; i < assigners.length; i++) {
                var target = $(assigners.get(i));

                var editorType = target.attr('data-editor');
                if(!editors[editorType]) {
                    continue;
                }

                var allData = extractAllData(target);

                var editor = new editors[editorType](model, allData);
                target.empty().append(editor.render());
            }
        };

        //
        // internals
        //
        function extractAllData(element) {
            var extractedAllData = {};
            $.each($(element).get(0).attributes, function(i, attributeName) {
                if(attributeName.name.indexOf("data-") == 0) {
                    extractedAllData[attributeName.name] = attributeName.value;
                }
            });
            return extractedAllData;
        }

        function construct() {
            doAdd('boolean', BooleanEditor);
            doAdd('text', TextEditor);
            doAdd('digit', DigitEditor);
            doAdd('duration', DurationEditor);
            doAdd('dictionary', DictionaryEditor);
            doAdd('dictionaryStrict', DictionaryStrictEditor);
        }

        function doAdd(key, editor) {
            if(editors[key]) {
                throw new Error('Duplicated for ' + key);
            }
            editors[key] = editor;
        }
    };
}

