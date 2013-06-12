<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
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
    <script type="text/javascript" src="/web/assets/scripts/template-manager.js"></script>
    <script type="text/javascript" src="/web/assets/scripts/playlist.js"></script>
    <script type="text/javascript">
        $(document).ready(function () {
            Backbone.chartType = '${playlistType}';
            Templates.templatesPath = '/web/${requestScope.assetsPathAccordingToCommunity}/templates/';
            Templates.load(['home', 'playlists', 'tracks'], 'playlists', function(){
            var router = new PlaylistRouter();
            Backbone.history.start();
            });
        });
    </script>
</head>
<body class="color-bg">
</body>
</html>
