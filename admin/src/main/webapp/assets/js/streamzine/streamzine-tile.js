if(Streamzine.Presenter.Tile == undefined) {
    Streamzine.Presenter.Tile = new function() {
        var inProgressToToggle = {};

        var _transformers = {
            _DEFAULT: function(input) {
                var cloned = Streamzine.Model.cloneBlock(input);
                cloned.coverUrl = (input.coverUrl) ? (Streamzine.Presenter.Editor.imagesBaseUrl + '/' + input.coverUrl) : '';
                cloned.badgeUrl = (input.badgeUrl) ? (Streamzine.Presenter.Editor.imagesBaseUrl + '/' + input.badgeUrl) : '';
                return cloned;
            },

            NEWS: {
                LIST: function(input) {
                    var cloned = Streamzine.Model.cloneBlock(input);

                    var date = new Date(cloned.value);
                    var m = (date.getMonth() + 1);
                    m = (m < 10) ? ('0' + m) : m;
                    var d = (date.getDate() < 10) ? ('0' + date.getDate()) : date.getDate();
                    cloned.value = m + '/' + d + '/' +date.getUTCFullYear();
                    cloned.coverUrl = (input.coverUrl) ? (Streamzine.Presenter.Editor.imagesBaseUrl + '/' + input.coverUrl) : '';
                    return cloned;
                }
            },
            MUSIC: {
                MANUAL_COMPILATION: function(input) {
                    var cloned = Streamzine.Model.cloneBlock(input);
                    var amount = (  (cloned.data && cloned.data.length) ? cloned.data.length : 0  );
                    cloned.value = (amount == 1) ? '1 Track' : (amount + ' Tracks');
                    cloned.coverUrl = (input.coverUrl) ? (Streamzine.Presenter.Editor.imagesBaseUrl + '/' + input.coverUrl) : '';
                    return cloned;
                },
                TRACK: function(input) {
                    var cloned = Streamzine.Model.cloneBlock(input);
                    if(cloned.data && cloned.data.artistDto) {
                        cloned.value = ((cloned.data.title)?cloned.data.title:'') + ' - ';
                        cloned.value+=(cloned.data.artistDto.name) ? cloned.data.artistDto.name: '';
                    } else {
                        cloned.value = '';
                    }
                    cloned.coverUrl = (input.coverUrl) ? (Streamzine.Presenter.Editor.imagesBaseUrl + '/' + input.coverUrl) : '';
                    cloned.badgeUrl = (input.badgeUrl) ? (Streamzine.Presenter.Editor.imagesBaseUrl + '/' + input.badgeUrl) : '';
                    return cloned;
                },
                PLAYLIST: function(input) {
                    var cloned = Streamzine.Model.cloneBlock(input);
                    cloned.value = (input.data) ? input.data.name : '';
                    cloned.coverUrl = (input.coverUrl) ? (Streamzine.Presenter.Editor.imagesBaseUrl + '/' + input.coverUrl) : '';
                    cloned.badgeUrl = (input.badgeUrl) ? (Streamzine.Presenter.Editor.imagesBaseUrl + '/' + input.badgeUrl) : '';
                    return cloned;
                }
            },

            detect: function(input) {
                var byType = this[input.contentType.$name];
                if(byType) {
                    var byKey = byType[input.key];
                    if(byKey) {
                        return byKey;
                    }
                }
                return this['_DEFAULT'];
            }
        };

        var _narrowTemplate =
            '<div class="streamzine_block_common streamzine_block_{first.shapeType.$name}" style="{expandStyle}">' +
            '<div>' +
            '<div style="float: right;"><a href="javascript:;" onclick="Streamzine.Presenter.Tile.toggle(\'{first.id}\', this)">{toggle}</a></div>' +
            '<div><span>&nbsp;</span><span>{shortDescription}</span></div>' +
            '</div>' +
            '<div id="{first.id}_body">' +
            '<div style="text-align: center;"><a href="javascript:;" onclick="Streamzine.Presenter.swapTiles(\'{first.id}\', \'{second.id}\')">Swap</a></div>' +
            '<div>' +
            '<div class="streamzine_block_common streamzine_block_NARROW_first {inFirstEdit}" id="{first.id}_value_holder">' +
            '    <div>' +
            '    <input type="checkbox" onclick="Streamzine.Presenter.includeBlockUI(this.checked, \'{first.id}\', \'{second.id}\')" title="Included" id="{first.id}_include" {includedFirst}/>' +
            '    <a href="javascript:;" onclick="Events.fire(\'BLOCK_EDITED\', \'{first.id}\')" class="streamzineEditPropertiesBlock">Edit</a> |' +
            '    <a href="javascript:;" onclick="Events.fire(\'BLOCK_REMOVED\', [\'{first.id}\', \'{second.id}\']);" class="streamzineRemoveBlock" href=""">Remove</a> |' +
            '    <a href="javascript:;" onclick="Events.fire(\'BLOCK_PREVIWED\', \'{first.id}\')" class="streamzineRemoveBlock" href=""">Preview</a>' +
            '    <span class="sz-vip">{firstVip}</span>' +
            '    </div>' +
            '    <div class="streamzine-block-preview-icon">{firstImage}</div>     ' +
            '    <div class="streamzine-block-preview-info">                       ' +
            '    <div class="streamzine-preview-placeholder">{firstKey}</div>      ' +
            '    <div class="streamzine-preview-placeholder sz-tile-value">{first.value}</div>' +
            '    {firstAdditionalInfo}                                             ' +
            '    <div class="streamzine-preview-placeholder sz-tile-title-subtitle {titleClass}" title="{first.title}">{first.title}</div>      ' +
            '    <div class="streamzine-preview-placeholder sz-tile-title-subtitle {subTitleClass}" title="{first.subTitle}">{first.subTitle}</div>' +
            '    {firstBadge}' +
            '    </div>                                                            ' +
            '</div>' +
            '<div class="streamzine_block_common streamzine_block_NARROW_second {inSecondEdit}" id="{second.id}_value_holder">' +
            '    <div>' +
            '    <input type="checkbox" onclick="Streamzine.Presenter.includeBlockUI(this.checked, \'{first.id}\', \'{second.id}\')" title="Included" id="{second.id}_include" {includedSecond} />' +
            '    <a href="javascript:;" onclick="Events.fire(\'BLOCK_EDITED\', \'{second.id}\')" class="streamzineEditPropertiesBlock">Edit</a> |' +
            '    <a href="javascript:;" onclick="Events.fire(\'BLOCK_REMOVED\', [\'{first.id}\', \'{second.id}\']);" class="streamzineRemoveBlock" href=""">Remove</a> |' +
            '    <a href="javascript:;" onclick="Events.fire(\'BLOCK_PREVIWED\', \'{second.id}\')" class="streamzineRemoveBlock" href=""">Preview</a>' +
            '    <span class="sz-vip">{secondVip}</span>                          ' +
            '    </div>                                                           ' +
            '    <div class="streamzine-block-preview-icon">{secondImage}</div>   ' +
            '    <div class="streamzine-block-preview-info">                      ' +
            '    <div class="streamzine-preview-placeholder">{secondKey}</div>    ' +
            '    <div class="streamzine-preview-placeholder sz-tile-value">{second.value}</div>   ' +
            '    {secondAdditionalInfo}                                             ' +
            '    <div class="streamzine-preview-placeholder sz-tile-title-subtitle {titleClass}" title="{second.title}">{second.title}</div>      ' +
            '    <div class="streamzine-preview-placeholder sz-tile-title-subtitle {subTitleClass}" title="{second.subTitle}">{second.subTitle}</div>' +
            '    {secondBadge}' +
            '    </div>                                                             ' +
            '</div>' +
            '</div>' +
            '</div>' +
            '</div>';

        var _template =
            '<div class="streamzine_block_common streamzine_block_{block.shapeType.$name} {inEdit}" style="{expandStyle}">' +
            '<div class="sz-tile-toggle"><a href="javascript:;" onclick="Streamzine.Presenter.Tile.toggle(\'{block.id}\', this)">{toggle}</a></div>' +
            '<div>' +
            '<div id="{block.id}_header">' +
            '<input type="checkbox" onClick="Streamzine.Presenter.includeBlockUI(this.checked, \'{block.id}\')" title="Included" id="{block.id}_include" {included} />' +
            '<a href="javascript:;" onClick="Events.fire(\'BLOCK_EDITED\', \'{block.id}\')" class="streamzineEditPropertiesBlock">Edit</a> | ' +
            '<a href="javascript:;" onClick="Events.fire(\'BLOCK_REMOVED\', [\'{block.id}\']);" class="streamzineRemoveBlock">Remove</a> |' +
            '<a href="javascript:;" onClick="Events.fire(\'BLOCK_PREVIWED\', \'{block.id}\')" class="streamzineRemoveBlock">Preview</a>' +
            '<span class="sz-vip">{vip}</span>' +
            '<span>{shortDescription}</span>' +
            '</div>' +
            '<div id="{block.id}_body">' +
            '<div class="streamzine-block-preview-icon">{image}</div>          ' +
            '<div class="streamzine-block-preview-info">                       ' +
            '<div class="streamzine-preview-placeholder">{key}</div>           ' +
            '<div class="streamzine-preview-placeholder sz-tile-value">{block.value}</div>   ' +
            '{additionalInfo}                                                  ' +
            '<div class="streamzine-preview-placeholder sz-tile-title-subtitle {titleClass}" title="{block.title}">{block.title}</div>   ' +
            '<div class="streamzine-preview-placeholder sz-tile-title-subtitle {subTitleClass}" title="{block.subTitle}">{block.subTitle}</div>' +
            '{badge}' +
            '</div></div></div></div>                                          ';

        var ref = this;
        function doUpdateDraggableBlock(block) {
            if(block.shapeType.$name == 'NARROW') {
                var f, s;
                if(Streamzine.Model.isSecondBlockInNarrowTile(block.id)) {
                    f = Streamzine.Model.findPrevBlockById(block.id);
                    s = block;
                } else {
                    f = block;
                    s = Streamzine.Model.findNextBlockById(block.id);
                }

                var t = ref.createNarrowTile(f, s);
                // and now calculate from original blocks copy (before the rendering) which was the first (need it to know the id of the UI block)
                if(Streamzine.Model.isSecondBlockInNarrowTile(block.id)) {
                    var id = Streamzine.Model.findPrevBlockById(block.id).id;
                    $('div#' + id).empty().html(t);
                } else {
                    $('div#' + block.id).empty().html(t);
                }


            } else {
                var t = ref.createTile(block);
                $('div#' + block.id).empty().html(t);
            }
        }

        this.updateDraggableBlock = function() {
            var block = Streamzine.Model.getCurrentBlock();
            doUpdateDraggableBlock(block);
        }

        this.updateDraggableBlockById = function(blockId) {
            var block = Streamzine.Model.findBlockById(blockId);
            doUpdateDraggableBlock(block);
        }

        this.toggle = function(blockId, link) {
            try {
                if (inProgressToToggle[blockId]) {
                    return;
                }

                inProgressToToggle[blockId] = true;

                // 1) update the model
                var block = Streamzine.Model.findBlockById(blockId);
                block.expanded = !block.expanded;

                if (block.shapeType.$name == 'NARROW') {
                    if (Streamzine.Model.isSecondBlockInNarrowTile(block.id)) {
                        var prev = Streamzine.Model.findPrevBlockById(block.id);
                        prev.expanded = !prev.expanded;
                    } else {
                        var next = Streamzine.Model.findNextBlockById(block.id);
                        next.expanded = !next.expanded;
                    }
                }

                // 2) update the UI:
                //
                doUpdateDraggableBlock(block);
                if(!block.expanded) {
                    $('div#' + blockId + ' div.streamzine_block_common').animate({
                            height: 14
                        },
                        800,
                        null,
                        function () {
                            $(link).text('Expand');
                        }
                    );
                }
            } finally {
                delete inProgressToToggle[blockId];
            }
        }

        this.editBlockPropertiesUI = function(blockId) {
            // remove prev selected:
            if(Streamzine.Model.currentInEditId) {
                var block = Streamzine.Model.getCurrentBlock();
                Streamzine.Model.currentInEditId = null;
                doUpdateDraggableBlock(block);
            }

            // update new one:
            Streamzine.Model.currentInEditId = blockId;
            this.updateDraggableBlock();
        }

        this.createNarrowTile = function(first, second) {
            var f = _transformers.detect(first)(first);
            var s = _transformers.detect(second)(second);

            return Template.render(_narrowTemplate,
                {
                    first: f,
                    second: s,
                    firstKey: Streamzine.Presenter.Mapping.getTitle(f.shapeType.$name, f.contentType.$name, f.key),
                    secondKey: Streamzine.Presenter.Mapping.getTitle(s.shapeType.$name, s.contentType.$name, s.key),
                    firstImage: (f.coverUrl) ? Template.render('<img src="{coverUrl}"/>', {coverUrl: f.coverUrl}) : '',
                    secondImage: (s.coverUrl) ? Template.render('<img src="{coverUrl}"/>', {coverUrl: s.coverUrl}) : '',
                    inFirstEdit:  (Streamzine.Model.isInEdit(f) ) ? 'streamzine-in-edit' : '',
                    inSecondEdit: (Streamzine.Model.isInEdit(s)) ? 'streamzine-in-edit' : '',
                    includedFirst: (f.included) ? ' checked="checked" ' : '',
                    includedSecond: (s.included) ? ' checked="checked" ' : '',
                    firstAdditionalInfo: getAdditionalInfo(f),
                    secondAdditionalInfo: getAdditionalInfo(s),
                    firstVip: (f.vip) ? 'VIP' : '',
                    secondVip: (s.vip) ? 'VIP' : '',
                    toggle: (f.expanded) ? 'Collapse' : 'Expand',
                    expandStyle: (f.expanded) ? '' : 'height:14px',
                    shortDescription: (f.expanded) ? '' : Streamzine.Model.getShortInfo(f),
                    firstBadge: (f.badgeUrl) ? Template.render('<div class="streamzine-block-preview-badge-wrapper"><img src="{badgeUrl}" class="streamzine-block-preview-badge-icon"/></div>', {badgeUrl: f.badgeUrl}) : '',
                    secondBadge: (s.badgeUrl) ? Template.render('<div class="streamzine-block-preview-badge-wrapper"><img src="{badgeUrl}" class="streamzine-block-preview-badge-icon"/></div>', {badgeUrl: s.badgeUrl}): '',
                    titleClass: (Streamzine.Presenter.Editor.titlesMappingRules[f.shapeType.$name].title) ? '' : 'sz-not-visible',
                    subTitleClass: (Streamzine.Presenter.Editor.titlesMappingRules[f.shapeType.$name].subTitle) ? '' : 'sz-not-visible'
                });
        }

        function getAdditionalInfo(incoming) {
            if(incoming.key == 'PLAYLIST') {
                var amount;
                if(incoming.data) {
                    amount = ((incoming.data.tracksCount) ? incoming.data.tracksCount : 0);
                } else {
                    amount = 0;
                }
                var message = (amount == 1) ? '1 Track' : (amount + ' Tracks');
                return Template.render('<div class="streamzine-preview-placeholder sz-tile-value">{message}</div>', {message: message});
            }
            return '';
        }

        this.createTile = function(block) {
            var transformed = _transformers.detect(block)(block);

            return Template.render(_template,
                {
                    block: transformed,
                    key: Streamzine.Presenter.Mapping.getTitle(transformed.shapeType.$name, transformed.contentType.$name, transformed.key),
                    image: (transformed.coverUrl) ? Template.render('<img src="{coverUrl}"/>', {coverUrl: transformed.coverUrl}) : '',
                    inEdit: (Streamzine.Model.isInEdit(transformed)) ? 'streamzine-in-edit' : '',
                    included: (transformed.included) ? ' checked="checked" ' : '',
                    additionalInfo: getAdditionalInfo(transformed),
                    vip: (transformed.vip) ? 'VIP' : '',
                    toggle: (transformed.expanded) ? 'Collapse' : 'Expand',
                    expandStyle: (transformed.expanded) ? '' : 'height:14px',
                    shortDescription: (transformed.expanded) ? '' : Streamzine.Model.getShortInfo(transformed),
                    badge: (transformed.shapeType.$name=='SLIM_BANNER') ? '' : (
                        (transformed.badgeUrl)?(Template.render('<div class="streamzine-block-preview-badge-wrapper"><img src="{badgeUrl}" class="streamzine-block-preview-badge-icon"/></div>', {badgeUrl: transformed.badgeUrl})):''
                    ),
                    titleClass: (Streamzine.Presenter.Editor.titlesMappingRules[transformed.shapeType.$name].title) ? '' : 'sz-not-visible',
                    subTitleClass: (Streamzine.Presenter.Editor.titlesMappingRules[transformed.shapeType.$name].subTitle) ? '' : 'sz-not-visible'
                }
            );
        }

        this.init = function() {
            var blocks = Streamzine.Model.getBlocks();
            var f;
            var s;
            var i=0;
            while(i<blocks.length) {
                if(blocks[i].shapeType.$name == 'NARROW') {
                    f = blocks[i];
                    s = blocks[i+1];
                    i++; 
                    $('div#' + f.id).empty().html(this.createNarrowTile(f, s));
                    $('div#' + s.id).remove();
                } else {
                    $('div#' + blocks[i].id).empty().html(this.createTile(blocks[i]));
                }
                i++;
            }
        }
    };
}

