if(Events == undefined) {

    var Events = new function() {
        var _events = {};

        var _f;

        this.stop = function(f) {
            _f = f;

            return this;
        }

        this.map = function(map) {
            for(var event in map) {
                if(map.hasOwnProperty(event)) {
                    this.subscribe(event, map[event]);
                }
            }
        }

        this.subscribe = function(event, callbacks) {
            if(!_events[event]) {
                _events[event] = [];
            }
            var evt = _events[event];

            if(callbacks instanceof Array) {
                for(var i=0; i < callbacks.length; i++) {
                    evt.push(callbacks[i]);
                }
            } else {
                evt.push(callbacks);
            }
        }

        this.fire = function(event, data) {
            if(_f && _f()) {
                return;
            }

            var evts = _events[event];

            if(evts) {
                for(var i=0; i < evts.length; i++) {
                    var e = evts[i];
                    var result = e.callback.call(e.ctx, data);
                    if(result && result.stopEvent) {
                        break;
                    }
                }
            }
        }
    };
}