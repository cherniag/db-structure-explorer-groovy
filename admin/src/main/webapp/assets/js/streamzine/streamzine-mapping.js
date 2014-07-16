if(Streamzine.Presenter.Mapping == undefined) {
    Streamzine.Presenter.Mapping = new function() {
        var _m;

        function findDtos(shapeType) {
            for(var i=0; i<_m.length; i++) {
                if(_m[i].shapeType == shapeType) {
                    return _m[i].dtos;
                }
            }
        }

        this.init = function(mapping) {
            _m = mapping;
        }

        this.getShapeTypeTitle = function(contentType) {
            for(var i=0; i<_m.length; i++) {
                for(var j=0; j<_m[i].dtos.length; j++) {
                    var d = _m[i].dtos[j];
                    if(d.subTypes && d.subTypes[contentType]) {
                        return d.subTypes[contentType];
                    }
                }
            }
        }

        this.getTitle = function(shapeType, contentType, key) {
            var dtos = findDtos(shapeType);

            for(var j=0; j<dtos.length; j++) {
                var dto = dtos[j];
                if(dto.contentType == contentType) {
                    return dto.subTypes[key];
                }
            }
        }

        this.getContentTypes = function(shapeType) {
            var types = [];

            var dtos = findDtos(shapeType);
            for(var j=0; j<dtos.length; j++) {
                types.push(dtos[j].contentType);
            }

            return types;
        }

        this.getContentTypeTitle = function(shapeType, contentType) {
            var dtos = findDtos(shapeType);

            for(var j=0; j<dtos.length; j++) {
                var dto = dtos[j];
                if(dto.contentType == contentType) {
                    return dto.title;
                }
            }
        }

        this.getKeys = function(shapeType, contentType) {
            var keys = [];

            var dtos = findDtos(shapeType);
            for(var j=0; j<dtos.length; j++) {
                var dto = dtos[j];
                if(dto.contentType == contentType) {
                    return dto.subTypes;
                }
            }

            return keys;
        }


    };
}

