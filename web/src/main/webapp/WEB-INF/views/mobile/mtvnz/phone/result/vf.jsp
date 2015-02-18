<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div class="subscription_root_container">
    Please confirm for ${phone}.

    Enter
    <div class="pin-code" digitsCount="4" name="pin"></div>

    <script>
        var enterPin = function() {
            window.location = "pin/result?pin=" + $('#pin').val();
        }
    </script>

    <a class="go-premium-button subscribe-button-device go-premium-button-target go-premium-body-ok" onclick="enterPin()">
        <span>Go</span>
    </a>
</div>

<script type="text/javascript">
    PinCodeControl = {
        init: function(){
            $(".pin-code").each(function(){
                PinCodeControl.render(this);
                PinCodeControl.initControl(this);
            });
        },

        render: function(rootDom) {
            var digitsCount = $(rootDom).attr('digitsCount'),
                name = $(rootDom).attr('name'),
                value = $(rootDom).attr('value');

            if(!digitsCount) throw "Pin control should have valid 'digitsCount' attribute";
            if(!name) throw "Pin control should have valid 'name' attribute";

            $(rootDom).append('<input type="hidden" id="'+name+'" name="'+name+'" value="'+(value || "")+'" />');

            var width = 100/digitsCount;

            for(var i=0; i<digitsCount; i++){
                var left = width*i;
                $(rootDom).append('<input class="pin-code-digit" type="text" tabindex="'+(10+i)+'" maxlength="1" style="left:'+left+'%; width:'+width+'%;"/>');
            }
        },

        initControl: function(rootDom) {
            $(rootDom).find('.pin-code-digit').each(function(){
                $(this)
                .on('focus', function(){
                    var previous = $(this).prev();
                    if(previous && previous.length && !previous.val() && previous.hasClass('pin-code-digit')){
                        previous[0].focus();
                    }
                })
                .on('keyup', function(e){
                    var next = $(this).next();
                    if($(this).val() && next && next.length){
                        next[0].focus();
                    }
                });
            });
        }
    };


    $( document ).ready(function() {
        PinCodeControl.init();
    });
</script>



