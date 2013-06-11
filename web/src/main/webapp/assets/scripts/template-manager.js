Templates = {
    templates: {},
    names:[],
    mainName: null,
    loaded: 0,
    notStarted: true,

    pick: function (name, callback) {
        var T = Templates;
            $.ajax({
            url: T.templatesPath + name + ".htm",
                success: function (data) {
                T.templates[name] = data;
                T[name] = _.template(data);
                T.loaded++;
                if(T.notStarted && (T.loaded == T.names.length || T.mainName == name)){
                    T.notStarted = false;
                    callback();
                }
            }
        });
                },
    load: function (names, mainName, callback) {
        var T = Templates;
        T.names = names;
        T.mainName = mainName;
        for(var i in T.names)
            T.pick(T.names[i], callback);
    },
    get: function (name) {
        return this.templates[name];
    }

}