<div id="interstitial_page" class="interstitial_page_ios" style="display:none;">

    <div class="header_section_ios">
        <div class="interstitial_page_title_ios">
            <span><s:message code='interstitial.page.ios.title'/></span>
        </div>
    </div>

    <div class="description_section_ios">
        <s:message code='interstitial.page.ios.description.1'/>
    </div>

    <div class="poster_section_ios"></div>

    <div class="description_section_ios">
        <s:message code='interstitial.page.ios.description.2'/>
    </div>

    <div class="button button_back" onclick="hideIntersitialPage();">
        <s:message code='interstitial.page.ios.button.back'/>
    </div>

    <div class="button button_exit" onclick="returnToApp();">
        <s:message code='interstitial.page.ios.button.exit'/>
    </div>

</div>

<script type="text/javascript">
    function showIntersitialPage(){
        $("#subscribe_page").hide();
        $("#interstitial_page").show();
    }
    function hideIntersitialPage(){
        $("#interstitial_page").hide();
        $("#subscribe_page").show();
    }
</script>
