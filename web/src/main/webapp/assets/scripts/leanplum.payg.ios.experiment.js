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

    if (LEANPLUM_IS_DEVELOPMENT) {
        Leanplum.setAppIdForDevelopmentMode(LEANPLUM_APP_ID, LEANPLUM_DEV_APP_KEY);
    } else {
        Leanplum.setAppIdForProductionMode(LEANPLUM_APP_ID, LEANPLUM_PROD_APP_KEY);
    }
    Leanplum.addVariablesChangedHandler(changePaymentOptionsVisibility);
    Leanplum.setVariables(variables);
    Leanplum.start(USER_UUID, function(success) {
        if(success) {
            setupClickHandlers();
        }
        changePaymentOptionsVisibility();
    });

    //
    // internals
    //
    function setupClickHandlers() {
        $('[data-type]').each(function() {
            var dataId = $(this).attr('data-id');
            var productId = $(this).attr('data-productId');

            $('[data-button="' + dataId + '"]').on('click', function(){
                Leanplum.track("Purchase", {product: productId});
            });
        });
    }

    function changePaymentOptionsVisibility() {
        $('[data-type]').each(function() {
            var dataId = $(this).attr('data-id');
            var type = $(this).attr('data-type') || '';
            var productId = $(this).attr('data-productId');
            var button = $('[data-button="' + dataId + '"]');

            if(!!Leanplum.getVariable(Check[type])) {
                button.show();
            }else{
                button.hide();
            }
        });
    }
});
