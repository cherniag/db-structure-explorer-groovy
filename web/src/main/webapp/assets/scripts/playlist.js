Player = {
	current : null,
	player : null,
	load : function() {
		if (!this.player) {
			this.player = new Audio();
			this.player.addEventListener('ended', this.onEnded);
		}
	},
	playPause : function() {
		this.load();
		
		if (this.player.paused && this.current) {
			this.player.play();
		} else {
			this.player.pause();
		}
	},
	play : function() {
		this.load();
		
		var track = Backbone.tracks.get(Player.current);
        this.player.src=track.get('audio');
        this.player.load();
        this.player.play();
	},
	stop : function() {
		this.current = null;
		this.playPause();
	},
	onEnded : function() {
		Player.cssStop(Player.current);
	},
	cssPlaying : function() {
		var id = Player.current;
		if (id) {
			$('div#track' + id).removeClass('color-main');
			$('div#track' + id).addClass('color-player');
			$('#icon-speakers' + id).removeClass('hidden');
		}
	},
	cssStop : function(id) {
		var id = Player.current;
		if (id) {
			$('div#track' + id).removeClass('color-player');
			$('div#track' + id).addClass('color-main');
			$('#icon-speakers' + id).addClass('hidden');
		}
	},
	playTrack : function(id) {
		this.cssStop();
		
		if(this.current != id){
			this.current = id;
			this.cssPlaying();
			this.play();
		}else{
			this.stop();
		}
	},

};

var Playlist = Backbone.Model.extend({
	defaults : {
		id : null,
		title : '',
		length : 0,
		selected : false
	}
});

var Track = Backbone.Model.extend({
	defaults : {
		id : '',
		title : '',
		artist : '',
		cover : '#',
		channel : ''
	},
	parse : function(track) {
		track.artist = Strings.cut(track.artist, 25);
		track.title = Strings.cut(track.title, 25);
		return track;
	}
});

var Tracks = Backbone.Collection.extend({
	model : Track,
	url : function() {
		return "/web/playlists/" + this.playlistId + "/tracks";
	},
	parse : function(response) {
		return response.tracks;
	}
});

var Playlists = Backbone.Collection.extend({
	model : Playlist,
	url : function() {
		return "/web/playlists/" + Backbone.chartType;
	},
	parse : function(response) {
		return response.playlists;
	},
    comparator:function(playlist){
        return playlist.get('selected') ? -1 : 1;
    },
	select : function(id) {
		this.models.forEach(function(list) {
			list.set('selected', list.get('id') == id);
		});
		var selected = this.findWhere({
			selected : true
		});
		this.swaped = selected.get('id') != this.preSelected;
	}
});

var PlaylistView = Backbone.View.extend({
    el: 'body',
    loaded: false,
    load: function () {
        var me = this;
        var list = me.collection;
        list.fetch({
            success: function (data) {
                var selected = list.findWhere({selected: true});
                list.preSelected = selected ? selected.get('id') : -1;
                me.draw(data.toJSON());
                Backbone.playlists = data;
                me.loaded = true;
                
                data.each(function(playlist) {
                    Backbone.tracksView.load(playlist.get('id'));
                });
            }
        });
    },
    render: function () {
        if(this.loaded)
            this.draw(this.collection.toJSON());
        else
            this.load();
    },
    draw: function (data) {
        this.collection.sort();
        var data = this.collection.toJSON();
        var html = Templates.playlists({data: data});
        $(this.el).empty();
        $(this.el).html(html);
    }
});

var HomeView = Backbone.View.extend({
    el: 'body',
    render: function(){
        var html = Templates.home();
        $(this.el).empty();
        $(this.el).html(html);
    }
});

var SwapView = Backbone.View.extend({
    el: 'body',
    render: function(){
        var html = Templates.swap();
        $(this.el).empty();
        $(this.el).html(html);
    }
});

var TracksView = Backbone.View.extend({
	el : 'body',
	cache : {},
	currentPlaylist : null,
	takeList : function(ID) {
		var currentPL = this.currentPlaylist;
		currentPL = Backbone.playlists.get(ID);

		if (currentPL) {
			var me = this;
			var list = me.collection;
			var draw = me.draw;

			this.load(ID, function(JSON) {
				list.reset(JSON);
				list.playlistId = ID;
				draw(JSON, currentPL.toJSON(), me);
			});
		}
	},
	load : function(playlistId, callback) {
		var ID = playlistId;
		var me = this;
        var list = me.collection;
		var cache = me.cache;

		if (cache[ID]) {
			var JSON = cache[ID];
			if (callback)
				callback(JSON);
		} else {
			list = new Tracks();
			list.playlistId = playlistId;
			list.fetch({
				success : function(data) {
					var JSON = data.toJSON();
					cache[ID] = JSON;
					if (callback)
						callback(JSON);
				}
			});
		}
	},
	render : function(ID) {
		this.takeList(ID);
	},
	draw : function(data, currentPL, me) {
		var html = Templates.tracks({ data : data, playlist : currentPL });
		$(me.el).empty();
		$(me.el).html(html);
	}
});

var PlaylistRouter = Backbone.Router.extend({
	initialize : function() {
		Backbone.router = this;
		Backbone.playlists = new Playlists();
		Backbone.tracks = new Tracks();

        Backbone.playlistView = new PlaylistView({collection: Backbone.playlists});
        Backbone.tracksView = new TracksView({collection: Backbone.tracks});
        Backbone.homeView = new HomeView();
        Backbone.swapView = new SwapView();

        this.views = [Backbone.playlistView, Backbone.tracksView, Backbone.homeView, Backbone.swapView];
    },
    routes: {
        "home": "home",
        "": "home",
        "swap/:listID":"swap",
        "tracks/:listID": "goTracks",
        "allPlaylists": "allPlaylists",
        "select/:listID": "select",
        "apply": "apply",
        "back": "back"
    },
    swap: function(listID){
        Backbone.playlists.select(listID);
        this.gotoView(Backbone.swapView);
    },
    home: function () {
        this.gotoView(Backbone.homeView);
    },
    allPlaylists: function () {
        Player.stop();
        this.gotoView(Backbone.playlistView);
    },
    goTracks: function (listID) {
        this.hideAll();
        Backbone.tracksView.render(listID);
        $(Backbone.tracksView.el).show();
    },
    select: function (listID) {
        Backbone.playlists.select(listID);
        this.goTracks(listID);
    },
    back: function(){
        window.location.href = '/web/playlist/swap.html';
    },
    apply: function () {
        var list = Backbone.playlists.findWhere({selected: true});
        var preSelected = Backbone.playlists.preSelected;
        Player.stop();
        if (!preSelected || preSelected != list.get('id'))
            list.save({selected: true});
        window.location.href = '/web/playlist/swap.html';
    },
    gotoView: function(view){
        this.hideAll();
        view.render();
        $(view.el).show();
    },
    hideAll: function () {
        _.each(this.views, function (view) {
            $(view.el).hide();
        });
    }
});

