
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
			audio.addEventListener('ended', this.onEnded);
			audio.addEventListener('pause', this.onPaused);
			audio.addEventListener('play', this.onPlayed);
			audio.addEventListener('error', this.onError);
			audio.addEventListener('abort', this.onError);
			Player[id] = audio;
		}
	},

	play : function(delay) {
		if (delay != 0) {
			setTimeout(function() {
				Player.play(0);
			}, delay);
		} else {
			var id = Player.current;
			if (id) {
				Player.cssPlaying();
				Player[id].play();
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
	onPlayed : function() {
		Player.cssPlaying();
	},
	onEnded : function() {
		Player.cssStop(Player.current);
		Player.current = null;
		
		if(Player.isiPhone3)
			Player.cssStop(Player.previous);
	},
	onPaused : function() {
		var prevId = Player.previous;
		Player.cssStop(prevId);
		
		var id = Player.current;
		if (Player[id] && Player[id].currentTime != Player[id].duration) {
			Player.play(200);
		} 
	},
	stop : function(nextId) {
		var id = Player.previous = Player.current;
		if (id && Player[id].canplay && !Player[id].paused) {
			Player.current = nextId;
			Player[id].currentTime = 0;
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
		var nowdate = new Date();
		var nowtime = nowdate.getTime();
		var delay =  Player.isiPhone3 ? 2000 : 500;
		
		Player.load(id);
		
		if (!Player.lastPlayed || ((nowtime - Player.lastPlayed) > delay)) {
			if (!Player.current) {
				Player.current = id;
				Player.play(0);
			} else if (Player.current == id) {
				Player.stop();
				Player.lastPlayed = null;
			} else {
				Player.stop(id);
			}
			Player.lastPlayed = nowtime;
		}
	},
	clearCache : function() {
		for ( var p in Player) {
			var constructor = Player[p] ? Player[p]['constructor'] : null;
			if (constructor == Audio) {
				Player[p].setAttribute("src", null);
				Player[p].load();
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

				data.each(function(playlist) {
					Backbone.tracksView.load(playlist.get('id'));
				});
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
	},
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
		var cache = me.cache;

		if (this.cache[ID]) {
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
		Backbone.router = this;
		Backbone.playlists = new Playlists();
		Backbone.tracks = new Tracks();

		Backbone.playlistView = this.playlistView = new PlaylistView({
			collection : Backbone.playlists
		});
		Backbone.tracksView = this.tracksView = new TracksView({
			collection : Backbone.tracks
		});

		this.views = [ this.playlistView, this.tracksView ];
		
		Player.isiPhone3 = Browser.isiPhone3();
	},
	routes : {
		"tracks/:listID" : "goTracks",
		"allPlaylists" : "allPlaylists",
		"" : "allPlaylists",
		"select/:listID" : "select",
		"apply" : "apply"
	},
	allPlaylists : function() {
		me = this;
		Player.stop();
		Player.clearCache();
		me.hideAll();
		me.playlistView.render();
		$(me.playlistView.el).show();
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
