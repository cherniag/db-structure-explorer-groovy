<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<div class="root-container-selection">
    <div class="root-container">
        <div class="container-option">
            <div class="go-premium-header">
                <div class="go-premium-header-logo">
                    <img src="${requestScope.assetsPathAccordingToCommunity}imgs/badge_mtv.png"
                         class="go-premium-header-logo-img"/>
                </div>
                <div class="go-premium-header-ad">
                    <div class="go-premium-header-title">
                        <s:message code='subscription.header.block.headline1'/>
                    </div>
                    <div class="go-premium-header-title-options">
                        <s:message code='manage.account.options'/>
                    </div>
                </div>
            </div>
        </div>
        <div class="container-option">
            <div>
                <img src="${requestScope.assetsPathAccordingToCommunity}imgs/icon_payment_vf.png" class="payment-logo" />
            </div>

            <div class="message">
                <s:message code='subscription.awaiting.title'/>
            </div>

            <a class="go-premium-button go-premium-button-device go-premium-button-target go-premium-body-cancel" onclick="returnToApp();">
                <span><s:message code='button.back.to.the.app.title' /></span>
            </a>
        </div>
    </div>
</div>
