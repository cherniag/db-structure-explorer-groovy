Player = {
	current : null,
	load : function(id) {
		if (!Player[id]) {
			var track = Backbone.tracks.get(id);
			var audio = new Audio();
			audio.setAttribute("src", track.get('audio'));
			audio.load();
			audio.addEventListener('canplay', function() {
				this.canplay = true;
			});
			audio.addEventListener('pause', this.onPaused(id));
			Player[id] = audio;
		}
	},
	play : function(delay) {
		if (delay != 0) {
			setTimeout(function() {
				Player.play(0);
			}, delay);
		} else {
			if (Player.current && Player[Player.current].currentTime == 0) {
				Player.cssPlaying();
				Player[Player.current].play();
			}
		}
	},
	pauseResume : function() {
		var id = Player.current;
		if (Player[id].currentTime > 0) {
			Player[id].pause();
			Player.cssStop(id);
		} else {
			Player.play(0);
		}
	},
	onPaused : function(id) {
		Player.play(200);
	},
	stop : function() {
		var id = Player.current;
		Player.current = null;
		Player.cssStop(id);
		if (id && Player[id].canplay && !Player[id].paused) {
			Player[id].pause();
			Player[id].currentTime = 0;
		}
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
		$('div#track' + id).removeClass('color-player');
		$('div#track' + id).addClass('color-main');
		$('#icon-speakers' + id).addClass('hidden');
	},
	playTrack : function(id) {
		Player.load(id);
		if (!Player.current) {
			Player.current = id;
			Player.play(0);
		} else if (Player.current == id) {
			Player.stop();
		} else {
			Player.stop();
			Player.current = id;
			Player.play(500);
		}
	},
	clearCache : function(){
		for(var p in Player){
			var constructor = Player[p] ? Player[p]['constructor'] : null;
			if(constructor == Audio){
				Player[p] = null;
			}
		}
	}
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
		return "/web/playlists/" + this.playlistId + "/tracks"
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
	el : 'body',
	initialize : function() {
		var me = this;
		var list = me.collection;
		list.fetch({
			success : function(data) {
				var selected = list.findWhere({
					selected : true
				});
				list.preSelected = selected ? selected.get('id') : -1;
				me.draw(data.toJSON());
				Backbone.playlists = data;
			}
		});
	},
	render : function() {
		this.draw(this.collection.toJSON());
	},
	draw : function(data) {
		var data = this.collection.toJSON();
		var html = Templates.playlists({
			data : data
		});
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
		
		if(currentPL){			
			var me = this;
			var list = me.collection;
			var cache = me.cache;
			var draw = me.draw;
			
			if (this.cache[ID]) {
				var JSON = cache[ID];
				me.collection.reset(JSON);
				draw(JSON, currentPL.toJSON(), me);
			} else {
				list.playlistId = ID;
				list.fetch({
					reset : true,
					success : function(data) {
						var JSON = data.toJSON();
						cache[ID] = JSON;
						draw(JSON, currentPL.toJSON(), me);
					}
				});
			}
		}
	},
	render : function(ID) {
		this.takeList(ID);
	},
	draw : function(data, currentPL, me) {
		var html = Templates.tracks({
			data : data,
			playlist : currentPL
		});
		$(me.el).empty();
		$(me.el).html(html);
	}
});

var PlaylistRouter = Backbone.Router.extend({
	initialize : function() {
		Backbone.playlists = new Playlists();
		Backbone.tracks = new Tracks();

		this.playlistView = new PlaylistView({
			collection : Backbone.playlists
		});
		this.tracksView = new TracksView({
			collection : Backbone.tracks
		});

		this.views = [ this.playlistView, this.tracksView ];
	},
	routes : {
		"tracks/:listID" : "goTracks",
		"allPlaylists" : "allPlaylists",
		"" : "allPlaylists",
		"select/:listID" : "select",
		"apply" : "apply"
	},
	allPlaylists : function() {
		Player.stop();
		Player.clearCache();
		this.hideAll();
		this.playlistView.render();
		$(this.playlistView.el).show();
	},
	goTracks : function(listID) {
		this.hideAll();
		this.tracksView.render(listID);
		$(this.tracksView.el).show();
	},
	select : function(listID) {
		Backbone.playlists.select(listID);
		this.goTracks(listID);
	},
	apply : function() {
		var list = Backbone.playlists.findWhere({
			selected : true
		});
		var preSelected = Backbone.playlists.preSelected;
		Player.stop();
		if (!preSelected || preSelected != list.get('id'))
			list.save({
				selected : true
			});
		window.location.href = '/web/playlist/swap.html';
	},
	hideAll : function() {
		_.each(this.views, function(view) {
			$(view.el).hide();
		});
	}
});
