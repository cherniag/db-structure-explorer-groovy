Backbone.player = {
    cssPlaying: function(id){
        $('div#track' + id).removeClass('color-main');
        $('div#track' + id).addClass('color-player');
    },
    cssStop: function(id){
        $('div#track' + id).removeClass('color-player');
        $('div#track' + id).addClass('color-main');
    },
    playTrack: function (id) {
        if (!this[id]) {
            var audio = document.getElementById(id);
            this[id] = audio;
        }
        if (!this.current) {
            this[id].play();
            this.current = id;
            this.cssPlaying(id);
        } else if (this.current == id) {
            this[id].pause();
            this.current = null;
            this.cssStop(id);
        } else {
            var current = this.current;
            this[current].pause()
            this[current].currentTime = 0;
            this.cssStop(current);
            this[id].play();
            this.cssPlaying(id);
            this.current = id;
        }
    }

};

var Playlist = Backbone.Model.extend({
    defaults: {
        id: null,
        title: '',
        length: 0,
        selected: false
    }
});

var Track = Backbone.Model.extend({
    defaults: {
        id: '',
        title: '',
        artist: '',
        cover: '#',
        channel: ''
    }
});

var Tracks = Backbone.Collection.extend({
    model: Track,
    url: function () {
        return "/web/playlists/" + this.playlistId + "/tracks"
    },
    parse: function (response) {
        return response.tracks;
    }
});

var Playlists = Backbone.Collection.extend({
    model: Playlist,
    url: function () {
        return "/web/playlists/" + Backbone.chartType;
    },
    initialize: function () {
        this.fetch({async: false});
        var selected = this.findWhere({selected: true});
        this.preSelected = selected.get('id');
    },
    parse: function (response) {
        return response.playlists;
    },
    select: function (id) {
        this.models.forEach(function (list) {
            list.set('selected', list.get('id') == id);
        });
    }
});

var PlaylistView = Backbone.View.extend({
    el: 'body',
    render: function () {
        var data = this.collection.toJSON();
        var html = _.template(Templates.get('playlists'), {data: data});
        $(this.el).empty();
        $(this.el).html(html);
    }
});

var TracksView = Backbone.View.extend({
    el: 'body',
    chache: {},
    takeList: function (ID) {
        if (this.chache.hasOwnProperty(ID))
            this.collection = this.chache[ID];
        else {
            var c = this.collection;
            c.playlistId = ID;
            c.fetch({async: false, reset: true});
            this.chache[ID] = new Tracks(c.toJSON());
        }
        this.currentPlaylist = Backbone.playlists.get(ID);
    },
    render: function () {
        var data = this.collection.toJSON();
        var html = _.template(Templates.get('tracks'), {data: data, playlist: this.currentPlaylist.toJSON()});
        $(this.el).empty();
        $(this.el).html(html);
    }
});


var PlaylistRouter = Backbone.Router.extend({
    initialize: function () {
        //collections
        Backbone.playlists = new Playlists();
        Backbone.tracks = new Tracks();

        //views
        this.playlistView = new PlaylistView({collection: Backbone.playlists});
        this.tracksView = new TracksView({collection: Backbone.tracks});

        this.views = [this.playlistView, this.tracksView];
    },
    routes: {
        "tracks/:listID": "goTracks",
        "allPlaylists": "allPlaylists",
        "": "allPlaylists",
        "select/:listID": "select",
        "apply": "apply"
    },
    allPlaylists: function () {
        this.hideAll();
        $(this.playlistView.el).show();
        this.playlistView.render();
    },
    goTracks: function (listID) {
        this.hideAll();
        this.tracksView.takeList(listID);
        $(this.tracksView.el).show();
        this.tracksView.render();
    },
    select: function (listID) {
        Backbone.playlists.select(listID);
    },
    apply: function () {
        var list = Backbone.playlists.findWhere({selected: true});
        if (Backbone.playlists.preSelected != list.get('id'))
            list.save({selected: true});
        window.location.href = '/web/playlist/swap.html';
    },
    hideAll: function () {
        _.each(this.views, function (view) {
            $(view.el).hide();
        });
    }
});