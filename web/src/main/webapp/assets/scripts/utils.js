var i18n = {

};
var Strings = {
    cut: function (str, length) {
        if (str.length > length)
            return str.substring(0, length - 3) + '...';
        return str;
    },
    cutLn: function(str, rows, columns){
        var result = '';
        var cuted = Strings.cut(str, rows*columns);
        for (var c in cuted){
            if(c % columns == 0)
                result += '\n';
            result += cuted[c];
        }
        return result;
    }
};

Templates = {
    names:[],
    mainName: null,
    loaded: 0,
    notStarted: true,

    pick: function (name, callback) {
        var T = Templates;
            $.ajax({
            url: T.templatesPath + name + ".htm",
                success: function (data) {
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
    }
}