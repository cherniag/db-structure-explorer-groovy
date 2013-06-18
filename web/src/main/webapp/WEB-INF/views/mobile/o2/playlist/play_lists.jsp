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

    <link rel="stylesheet" type="text/css" href="/web/${requestScope.assetsPathAccordingToCommunity}css/mobile.css" />
    <script type="text/javascript" src="/web/assets/scripts/jquery-1.7.2.min.js"></script>
    <script type="text/javascript" src="/web/assets/scripts/underscore.js"></script>
    <script type="text/javascript" src="/web/assets/scripts/json2.js"></script>
    <script type="text/javascript" src="/web/assets/scripts/backbone.js"></script>
    <script type="text/javascript" src="/web/assets/scripts/utils.js"></script>
    <script type="text/javascript" src="/web/assets/scripts/playlist.js"></script>
    <script type="text/javascript">
	    Messages = {
	    		'page.playlists.header.text' : '<s:message code="page.playlists.header.text"/>',
	    		'page.playlists.tracks.header.text' : '<s:message code="page.playlists.tracks.header.text"/>',
	    		'page.playlists.alert.swap' : '<s:message code="page.playlists.alert.swap"/>',
	    		'page.playlists.menu.apply' : '<s:message code="page.playlists.menu.apply"/>',
	    		'page.playlists.item.current.label' : '<s:message code="page.playlists.item.current.label"/>',
	    		'page.playlists.item.tracks' : '<s:message code="page.playlists.item.tracks"/>',
	    		'page.playlists.tracks.item.tracks' : '<s:message code="page.playlists.tracks.item.tracks"/>',
	    		'page.playlists.tracks.menu.back' : '<s:message code="page.playlists.tracks.menu.back"/>',
	    		'page.playlists.tracks.item.select' : '<s:message code="page.playlists.tracks.item.select"/>',
		};
    
        $(document).ready(function () {       	
            Backbone.chartType = '${playlistType}';
            
            Templates.templatesPath = '/web/${requestScope.assetsPathAccordingToCommunity}/templates/';
            Templates.load(['home', 'playlists', 'tracks', 'swap'], 'swap', function(){
            	var router = new PlaylistRouter();
            	Backbone.history.start();
            });
        });
    </script>
</head>
<body>
</body>
</html>
