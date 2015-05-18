//default values for variables
var variables = {
    showPayAsYouGoPaymentOption: true,
    showOneTimePaymentOption: true,
    showRecurrentPaymentOption: true
};


var onLeanplumStart = function(success) {
    Leanplum.addVariablesChangedHandler(initPaymentOptions);
    initPaymentOptions();
};


var initPaymentOptions = function(){
    $('.subscribe_option_holder .go-premium-button span').each(function(){
        var button = $(this).closest('.subscribe_option_holder'),
            type = $(this).attr('type'),
            productId = $(this).attr('productId');

        if(Check.showPayAsYouGo(type) || Check.showOneTime(type) || Check.showRecurrent(type) || !type){
            button.show().off('click').on('click', function(){
                Leanplum.track("Purchase", {product: productId});
            });
            button.next().show();
        }else{
            button.hide().off('click');
            button.next().hide();
        }
    });
};


var Check = {
    showPayAsYouGo: function (type) {
        return type == 'payg' && Leanplum.getVariable('showPayAsYouGoPaymentOption');
    },
    showOneTime: function (type) {
        return type == 'onetime' && Leanplum.getVariable('showOneTimePaymentOption');
    },
    showRecurrent: function (type) {
        return type == 'recurrent' && Leanplum.getVariable('showRecurrentPaymentOption');
    }
};


$(document).ready(function() {
    if (LEANPLUM_IS_DEVELOPMENT) {
        Leanplum.setAppIdForDevelopmentMode(LEANPLUM_APP_ID, LEANPLUM_DEV_APP_KEY);
    } else {
        Leanplum.setAppIdForProductionMode(LEANPLUM_APP_ID, LEANPLUM_PROD_APP_KEY);
    }

    Leanplum.setVariables(variables);
    Leanplum.start(USER_ID, onLeanplumStart);
});
