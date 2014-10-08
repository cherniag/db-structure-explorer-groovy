//
// Handles Model
//
var UpdateModel = function(model) {
    var _model = model;

    init(_model.blocks);

    //
    // internals
    //
    calcSubTitle = function(block) {
        var shapeType = block.shapeType.$name;
        var subTitle = block.subTitle;

        return (Streamzine.Presenter.Editor.titlesMappingRules[shapeType].subTitle) ? subTitle : null;
    }

    function init(blocks) {
        // generate ids for every block
        for(var i=0; i < blocks.length; i++) {
            blocks[i].id = 'block_item_' + blocks[i].position;
        }
    }

    function findIndex(blockId) {
        var blocks = _model.blocks;
        for(var i=0; i < blocks.length; i++) {
            if(blocks[i] && blocks[i].id == blockId) {
                return i;
            }
        }

        return -1;
    }

    function copyProps(from, to) {
        for(p in from) {
            if(from.hasOwnProperty(p) && to.hasOwnProperty(p)) {
                to[p] = from[p];
            }
        }
    }

    function getFullTitle(m) {
        var fullTitle = '';
        if(m.title) {
            fullTitle += m.title;
        }
        if(m.subTitle) {
            fullTitle += ', '  + m.subTitle;
        }
        return fullTitle;
    }

    function doGetShortDescription() {
        var description = '';
        for(var j=0; j < arguments.length; j++) {
            var m = arguments[j];
            if(j > 0) {
                description += ' | ';
            }
            var fullTitle = getFullTitle(m)
            description += Streamzine.Presenter.Mapping.getShapeTypeTitle(m.key) + (  (fullTitle) ? ', ' + fullTitle : ''  )
        }
        return description;
    }

    //
    // API
    //
    this.getBlocks = function() {
        return _model.blocks;
    }

    this.addUser = function(userName) {
        var i = $.inArray(userName, _model.userNames);
        if(i == -1){
            _model.userNames.push(userName);
        }
    }

    this.removeUser = function(userName) {
        var i = $.inArray(userName, _model.userNames);
        if(i >= 0) {
            _model.userNames.splice(i, 1);
        }
    }

    this.getUsers = function(){
        return _model.userNames;
    }

    this.getShortInfo = function(m) {
        var description = '';
        if(m.shapeType.$name == 'NARROW') {
            if(this.isSecondBlockInNarrowTile(m.id)) {
                description = doGetShortDescription(this.findPrevBlockById(m.id), m);
            } else {
                description = doGetShortDescription(m, this.findNextBlockById(m.id));
            }
        } else {
            description = doGetShortDescription(m);
        }

        return '(' + description + ')';
    }

    this.deleteBlockById = function(id) {
        var i = findIndex(id);

        if(i >= 0) {
            delete _model.blocks[i];
        }
    }

    this.createEmptyBlock = function(generatedId, shapeType) {
        var emptyBlock = {
            id: generatedId,
            coverUrl: '',
            included: true,
            expanded: true,
            badgeFileNameAlias: null,
            position: _model.blocks.length,
            shapeType: {
                '$type': 'ShapeType',
                '$name': shapeType
            },
            contentType: {
                $type: 'ContentType',
                $name: 'PROMOTIONAL'
            },
            key: 'INTERNAL_AD',
            subTitle: '',
            title: '',
            value: undefined,
            data: null,
            vip: false,
            player:defaultPlayer
        };
        return emptyBlock;
    }

    this.cloneBlock = function(block) {
        var newOne = this.createEmptyBlock();

        copyProps(block, newOne);

        newOne.shapeType = {
            '$type': 'ShapeType',
            '$name': block.shapeType.$name
        };
        newOne.contentType = {
            '$type': 'ContentType',
            '$name': block.contentType.$name
        };
        return newOne;
    }

    this.isInEdit = function(block) {
        return this.currentInEditId && this.currentInEditId == block.id;
    }

    this.getCurrentBlock = function() {
        if(this.currentInEditId) {
            return this.findBlockById(this.currentInEditId);
        }
    }

    this.updateCurrentBlock = function(prop, value) {
        var id = this.currentInEditId;
        var block = Streamzine.Model.findBlockById(id);
        block[prop] = value;
        return block;
    }

    this.findBlockById = function(blockId) {
        var i = findIndex(blockId);

        if(i >= 0) {
            return _model.blocks[i];
        }
    }

    this.isSecondBlockInNarrowTile = function(blockId) {
        var first = false;

        for(var i=0; i < _model.blocks.length; i++) {
            var b = _model.blocks[i];
            if(!b) {
                continue;
            }
            if(b.shapeType.$name == 'NARROW') {
                first = !first;
            }
            if(b.id == blockId) {
                return !first;
            }
        }

        throw new Error('Not found');
    }

    this.findPrevBlockById = function(blockId) {
        var i = findIndex(blockId);

        if(i >= 0) {
            return _model.blocks[i - 1];
        }
    }

    this.findNextBlockById = function(blockId) {
        var i = findIndex(blockId);

        if(i >= 0) {
            return _model.blocks[i + 1];
        }
    }

    this.addBlock = function(block) {
        _model.blocks.push(block);
    }

    this.swap = function(firstId, secondId) {
        var firstIndex = findIndex(firstId);
        var secondIndex = findIndex(secondId);

        var b = _model.blocks[firstIndex];
        _model.blocks[firstIndex] = _model.blocks[secondIndex];
        _model.blocks[secondIndex] = b;
    }

    this.normalize = function(updatePositions, ids) {
        // prepare model to be properly demarshaled:
        var normalized = {};

        // 1) set the header of
        normalized.id = _model.id;
        normalized.userNames = _model.userNames;
        normalized.timestamp = _model.date.time;
        normalized.blocks = [];

        // 2) and the blocks
        // insert proper positions according to the current sortable state passed in 'ids' parameter
        var currentPointer = 0;

        if(updatePositions) {
            for(var i = 0; i < ids.length; i++) {
                var id = ids[i];

                var foundBlock = this.findBlockById(id);

                // avoid situation when this is first block (no model for him): so first time 'foundBlock' could be not defined
                if(foundBlock) {
                    // position pointer starts with 1 at the begging
                    currentPointer++;

                    // do not forget that NARROW block contains two internal blocks
                    if(foundBlock.shapeType.$name == 'NARROW') {
                        // first
                        foundBlock.position = currentPointer;

                        // and the second
                        var nextNarrow = this.findNextBlockById(id);
                        currentPointer++;
                        nextNarrow.position = currentPointer;
                    } else {
                        foundBlock.position = currentPointer;
                    }
                }
            }
        }

        // clear up from empty placeholders (we can get them after removal - some elements in blocks array could be not defined), normalize model
        var j = 0;
        for(var i = 0; i < _model.blocks.length; i++) {
            var b = _model.blocks[i];

            // skip that case when not defined due to
            if(b) {
                normalized.blocks[j++] = {
                    shapeType: (b.shapeType) ? b.shapeType.$name : null,
                    included: b.included,
                    //
                    title: b.title,
                    subTitle: calcSubTitle(b),
                    coverUrl: b.coverUrl,
                    badgeId: (b.badgeFileNameAlias) ? b.badgeFileNameAlias.id : null,
                    position: b.position,
                    contentType: (b.contentType) ? b.contentType.$name : null,
                    //
                    key: b.key,
                    value: b.value,
                    vip: b.vip,
                    expanded: b.expanded,
                    //
                    id: b.id,
                    player: b.player
                };
            }
        }

        // sort according to positions
        normalized.blocks.sort(function(f, s) {
            return parseInt(s.position) - parseInt(f.position);
        });

        // add method to compare
        normalized.equalTo = function(another) {
            // actually compare them:
            //      userNames:
            if(this.userNames.length != another.userNames.length){
                return false;
            }else{
                for(var i = 0; i < this.userNames.length; i++){
                    if(this.userNames[i] != another.userNames[i]){
                        return false;
                    }
                }
            }

            //      blocks:
            if(this.blocks.length != another.blocks.length) {
                return false;
            }

            for(var i = 0; i < this.blocks.length; i++) {
                var currentBlock = this.blocks[i];
                var copyBlock = another.blocks[i];

                if(
                    currentBlock.contentType != copyBlock.contentType ||
                    currentBlock.shapeType != copyBlock.shapeType ||
                    currentBlock.key != copyBlock.key ||
                    currentBlock.value != copyBlock.value ||
                    currentBlock.included != copyBlock.included ||
                    currentBlock.title != copyBlock.title ||
                    currentBlock.subTitle != copyBlock.subTitle ||
                    currentBlock.vip != copyBlock.vip ||
                    currentBlock.badgeId != copyBlock.badgeId ||
                    currentBlock.coverUrl != copyBlock.coverUrl||
                    currentBlock.player != copyBlock.player) {
                    return false;
                }
            }

            return true;
        }

        normalized.hasIncluded = function() {
            for(var i=0; i < this.blocks.length; i++) {
                if(this.blocks[i].included) {
                    return true;
                }
            }
            return false;
        }

        return normalized;
    }
};

