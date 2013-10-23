var Player = {
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
        Player.player.src=track.get('audio');
        Player.player.load();
        Player.player.addEventListener("loadeddata", function(){
            Player.player.play();
            GA.trackPlaying(track.get('id'));
        });
	},
	stop : function() {
		this.current = null;
		this.playPause();
	},
	onEnded : function() {
		Player.cssStop();
	},
	cssPlaying : function() {
		var id = Player.current;
		if (id) {
			$('div#track' + id).removeClass('color-main').addClass('color-player');
			$('#icon-speakers' + id).removeClass('hidden');
		}
	},
	cssStop : function() {
		var id = Player.current;
		if (id) {
			$('div#track' + id).removeClass('color-player').addClass('color-main');
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
    comparator: function(playlist){
        return playlist.get('selected') ? -1 : 1;
    }
});

var PlaylistView = Backbone.View.extend({
    el: 'body',
    loaded: false,
    willBeSelected: null,
    load: function () {
        var me = this;
        var list = me.collection;
        list.fetch({
            success: function (data) {
                var selected = list.findWhere({selected: true});
                list.preSelected = selected ? selected.get('id') : -1;
                me.draw(data);
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
            this.draw(this.collection);
        else
            this.load();
    },
    draw: function (data) {
        data.sort();
        var html = Templates.playlists({data: data.toJSON()});
        $(this.el).empty();
        $(this.el).html(html);
        $(this.el).addClass('color-bg');
        GA.trackClick('playlists_screen');
    }
});

var HomeView = Backbone.View.extend({
    el: 'body',
    render: function(){
        var html = Templates.home();
        $(this.el).empty();
        $(this.el).html(html);
        $(this.el).removeClass('color-bg');
        GA.trackClick('home_screen');
    }
});

var SwapView = Backbone.View.extend({
    el: 'body',
    render: function(){
        var html = Templates.swap();
        $(this.el).empty();
        $(this.el).html(html);
        $(this.el).removeClass('color-bg');
        GA.trackClick('swap_screen');
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

			me.load(ID, function(JSON) {
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
        $(me.el).addClass('color-bg');
        GA.trackClick('playlist_'+currentPL['title']);
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
        document.addEventListener("backbutton", function(){Player.stop()});

    },
    routes: {
        "": "home",
        "home": "home",
        "swap/:listID":"swap",
        "tracks/:listID": "goTracks",
        "allPlaylists": "allPlaylists",
        "apply": "apply",
        "back": "back",
        "backOrExit": "backOrExit"
    },
    swap: function(listID){
        Backbone.playlistView.willBeSelected = listID;
        this.gotoView(Backbone.swapView);
    },
    home: function () {
    	// if the user checked 'don't show me...', the view for 'home' will be playlistView
    	var cookieSaved = readCookie("playlist_NoHomepage");
    	if ( "1" === cookieSaved ) {
    		this.allPlaylists();
    	} else {
    		this.gotoView(Backbone.homeView);
    	}
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
    back: function(){
        GA.trackClick('back_to_app');
        window.location.href = '/web/playlist/swap.html';
    },
    backOrExit: function() {
    	/* this is called from the playlists template for the close button and we can be in two situations:
    	 	1. if user does not want to see the home page, we just exit
    	 	2. otherwise, the user will go to the home page, as before
    	 */
    	var cookieSaved = readCookie("playlist_NoHomepage");
    	if ( "1" === cookieSaved ) {
    		this.back();
    	} else {
    		this.gotoView(Backbone.homeView);
    	}
    },
    apply: function () {
        var listID = Backbone.playlistView.willBeSelected;
        if (listID){
            var list = Backbone.playlists.get(listID);
            list.save({selected: true});
        }
        Player.stop();
        GA.trackClick('apply_swapping_'+listID);
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

var ChckboxElement = (function() {
	switchState = function() {
		var imgOff = $("#doNotShowCheckboxOff");
		var imgOn  = $("#doNotShowCheckboxOn");
		
		if ( imgOff.length === 0 || imgOn.length === 0 ) {
			return;
		}
		
		imgOff.toggle();
		imgOn.toggle();
		
		if ( imgOn.is(":visible") ) {
			// set the cookie for 6 months
			createCookie("playlist_NoHomepage", "1", 365);
		} else {
			// remove the cookie
			createCookie("playlist_NoHomepage", "0", -1);
		}
	};
	
	return {
		// updateOptions: updateOptions,
		switchState: switchState
	};
})();
