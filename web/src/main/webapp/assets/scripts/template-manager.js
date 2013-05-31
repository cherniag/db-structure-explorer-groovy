Templates = {
    templates: {},

    load: function (names) {
        var that = this;
        for (var id in names) {
            $.ajax({
                url: Templates.templatesPath + names[id] + ".htm",
                success: function (data) {
                    that.templates[names[id]] = data;
                },
                async: false});
        }
    },
    get: function (name) {
        return this.templates[name];
    }

}