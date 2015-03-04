<%@taglib uri="http://www.springframework.org/tags" prefix="s" %>

<div class="page-container">
    <img src="${requestScope.assetsPathAccordingToCommunity}imgs/icon_close_72.png" class="go-premium-button-target go-premium-button-close" onclick="returnToApp();" />

    <div class="message">
        <s:message code='error.not.vf.reassigned.body'/>
    </div>

    <a class="go-premium-button go-premium-button-device go-premium-button-target go-premium-body-cancel"
       onclick="returnToApp();">
        <span><s:message code='button.cancel.title'/></span>
    </a>
</div>

