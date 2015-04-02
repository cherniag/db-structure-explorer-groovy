<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>

<div class="page-container">
    <img src="${requestScope.assetsPathAccordingToCommunity}imgs/icon_close_72.png" class="go-premium-button-target go-premium-button-close" onclick="returnToApp();" />

    <div class="center-container">
        <img src="${requestScope.assetsPathAccordingToCommunity}imgs/logo_holding_big.png" class="holding-logo-app" />
        <img src="${requestScope.assetsPathAccordingToCommunity}imgs/img_holding_big.png" class="holding-logo" />
    </div>

    <div class="center-container holding-header">
        <br/>
        Relax.
        <br/>
        You're on a free trial
        <br/>
        <br/>
    </div>

    <div class="message">
        Enjoy the music and we'll let you know when it's time to upgrade
    </div>

    <a class="go-premium-button go-premium-button-device go-premium-button-target go-premium-body-ok" onclick="returnToApp();">
        <span>
            Close
        </span>
    </a>
</div>


