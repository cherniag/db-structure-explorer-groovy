<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>

<!DOCTYPE html>
<html>
<head>
	<s:message code='page.playlists.menu.apply' var="page_playlists_menu_apply" />
	
    <title>Playlists</title>
    <meta http-equiv="content-type" content="text/html; charset=utf-8" />
    <meta http-equiv="Cache-Control" content="no-store, no-cache, must-revalidate" />
    <meta http-equiv="Pragma" content="no-cache" />
    <meta name="viewport" content="width=device-width, target-densitydpi=160, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <meta name="MobileOptimized" content="640"/>

    <link rel="stylesheet" type="text/css" href="${requestScope.assetsPathAccordingToCommunity}css/mobile.css" />
    <script src="${requestScope.assetsPathWithoutCommunity}scripts/jquery-1.7.2.min.js"></script>
    <script src="${requestScope.assetsPathWithoutCommunity}scripts/underscore.js"></script>
    <script src="${requestScope.assetsPathWithoutCommunity}scripts/json2.js"></script>
    <script src="${requestScope.assetsPathWithoutCommunity}scripts/backbone.js"></script>
    <script src="${requestScope.assetsPathWithoutCommunity}scripts/utils.js"></script>
    <script src="${requestScope.assetsPathWithoutCommunity}scripts/playlist.js?ver=8"></script>
    <script>
	    var Messages = {
	    		'page.playlists.header.text' : '<s:message code="page.playlists.header.text"/>',
	    		'page.playlists.tracks.header.text' : '<s:message code="page.playlists.tracks.header.text"/>',
	    		'page.playlists.alert.swap' : '<s:message code="page.playlists.alert.swap"/>',
	    		'page.playlists.menu.apply' : '<s:message code="page.playlists.menu.apply"/>',
	    		'page.playlists.item.current.label' : '<s:message code="page.playlists.item.current.label"/>',
	    		'page.playlists.item.tracks' : '<s:message code="page.playlists.item.tracks"/>',
	    		'page.playlists.tracks.item.tracks' : '<s:message code="page.playlists.tracks.item.tracks"/>',
	    		'page.playlists.tracks.menu.back' : '<s:message code="page.playlists.tracks.menu.back"/>',
	    		'page.playlists.tracks.item.select' : '<s:message code="page.playlists.tracks.item.select"/>',
                'page.playlists.button.swap.text': '<s:message code="page.playlists.button.swap.text"/> ',

                'page.playlists.home.header.text' : '<s:message code="page.playlists.home.header.text"/>',
                'page.playlists.home.title.text': '<s:message code="page.playlists.home.title.text"/>',
                'page.playlists.home.message.text' : '<s:message code="page.playlists.home.message.text"/>',
                'page.playlists.home.button.go.text' : '<s:message code="page.playlists.home.button.go.text"/>',
                'page.playlists.home.button.back.text': '<s:message code="page.playlists.home.button.back.text"/> ',
                'page.playlists.home.info.middle.text': '<s:message code="page.playlists.home.info.middle.text"/>',
                'page.playlists.home.checkbox.text':'<s:message code="page.playlists.home.checkbox.text"/>',

                'page.swap.header.text' : '<s:message code="page.swap.header.text"/>',
                'page.swap.button.back.text' : '<s:message code="page.swap.button.back.text"/>',
                'page.swap.title.text' : '<s:message code="page.swap.title.text"/>',
                'page.swap.message.text' : '<s:message code="page.swap.message.text"/>',
                'page.swap.button.ok.text': '<s:message code="page.swap.button.ok.text"/>',
                
                'assetPath': '${requestScope.assetsPathAccordingToCommunity}'
		};
    
        $(document).ready(function () {      	
            Backbone.chartType = '${playlistType}';
            
            Templates.templatesPath = '/web/${requestScope.assetsPathAccordingToCommunityWithoutHost}templates/';
            Templates.load(['home', 'playlists', 'tracks', 'swap'], 'home', function(){
            	var router = new PlaylistRouter();
            	Backbone.history.start();
            });
        });
        
        /* $(window).bind("beforeunload", 
			function() {
			}); */
    </script>
</head>
<body>
</body>
</html>