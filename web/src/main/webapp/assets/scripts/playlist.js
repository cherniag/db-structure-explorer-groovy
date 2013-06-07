Backbone.player = {};
Backbone.playTrack = function (id) {
    if (!Backbone.player[id]) {
        var audio = document.getElementById(id);
        Backbone.player[id] = audio;
    }
    if (!Backbone.player.current) {
        Backbone.player[id].play();
        Backbone.player.current = id;
    } else if (Backbone.player.current == id) {
        Backbone.player[id].pause();
        Backbone.player.current = null;
    } else {
        var current = Backbone.player.current;
        Backbone.player[current].pause();
        Backbone.player[id].play();
        Backbone.player.current = id;
    }
}

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
        cover: '#'
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
    },
    parse: function (response) {
        return response.playlists;
    },
    select: function(id){
        this.models.forEach(function(list){
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
            this.collection.playlistId = ID;
            this.collection.fetch({async: false, reset: true});
            this.chache[ID] = new Tracks(this.collection.toJSON());
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
    apply: function(){
        var list = Backbone.playlists.findWhere({selected: true});
        list.save({selected: true});
    },
    hideAll: function () {
        _.each(this.views, function (view) {
            $(view.el).hide();
        });
    }
});