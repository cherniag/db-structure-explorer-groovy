if(Template == undefined) {
    var Template = {};

    Template.render = function(template, model) {
        var extractValue = function(model, key) {
            var keys = key.split('.');
            var value = model;
            for(var i=0; i<keys.length; i++) {
                value = value[keys[i]];
            }
            return value;
        };

        var rendered = [];
        var size = template.length;

        var started = -1;

        for(var i=0; i < size; i++) {
            var at = template.charAt(i);

            if(at == '{') {
                started = i;
            } else if(at == '}') {
                var key = template.substring(started+1, i);
                rendered.push(extractValue(model, key));
                started = -1;
            } else {
                if(started < 0) {
                    rendered.push(at);
                }
            }
        }

        return rendered.join('');
    };

    Template.renderDom = function(templateId, model) {
        var templateString = $('#'+templateId).html();
        return Template.render(templateString, model);
    }
}