$(document).ready(function() {
    var variables = {
        showPayAsYouGoPaymentOption: true,
        showOneTimePaymentOption: true,
        showRecurrentPaymentOption: true
    };
    var Check = {
        PAYG: 'showPayAsYouGoPaymentOption',
        ONETIME: 'showOneTimePaymentOption',
        RECURRENT: 'showRecurrentPaymentOption'
    };

    try{
        if (LEANPLUM_IS_DEVELOPMENT) {
            Leanplum.setAppIdForDevelopmentMode(LEANPLUM_APP_ID, LEANPLUM_DEV_APP_KEY);
        } else {
            Leanplum.setAppIdForProductionMode(LEANPLUM_APP_ID, LEANPLUM_PROD_APP_KEY);
        }

        Leanplum.setRequestBatching(false);
        Leanplum.addVariablesChangedHandler(changePaymentOptionsVisibility);
        Leanplum.setVariables(variables);
        Leanplum.start(USER_UUID, function(success) {
            setupClickHandlers(success);
            changePaymentOptionsVisibility();
        });
    }catch(e){
        if(LEANPLUM_IS_DEVELOPMENT){
            alert("Failed to initialize Leanplum: message: " + e.message + ", string: " + e);
        }

        setupClickHandlers(false);
        changePaymentOptionsVisibility();
    }

    //
    // internals
    //
    function setupClickHandlers(leanplumLoaded) {
        var blocked = false;
        var redirect = function (url) {
            blocked = false;
            goTo(url);
        };

        $('[data-type]').each(function() {
            var dataId = $(this).attr('data-id');
            var productId = $(this).attr('data-productId');

            $('[data-button="' + dataId + '"]').on('click', function(){
                if(!blocked){
                    blocked = true;
                    var url = $(this).attr('data-goToUrl');
                    if(leanplumLoaded){
                        Leanplum.track("Purchase", {product: productId}, function(){
                            redirect(url);
                        });
                        setTimeout(function(){redirect(url);}, 2000);
                    }else{
                        redirect(url);
                    }
                }
            });
        });
    }

    function changePaymentOptionsVisibility() {
        $('[data-type]').each(function() {
            var dataId = $(this).attr('data-id');
            var type = $(this).attr('data-type') || '';
            var productId = $(this).attr('data-productId');
            var button = $('[data-button="' + dataId + '"]');
            var infoButton = $('[data-info-button="' + dataId + '"]');

            if(!!Leanplum.getVariable(Check[type])) {
                button.show();
                infoButton.show();
            }else{
                button.hide();
                infoButton.hide();
            }
        });
    }
});
