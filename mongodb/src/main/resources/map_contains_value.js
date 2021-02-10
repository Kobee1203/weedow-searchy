function() {
    var deepIterate = function (obj, path, value) {
        var pathSplit = path.split(".");
        var part = pathSplit[0];

        if (pathSplit.length > 1) { // path with multiple parts
            var newPath = pathSplit.slice(1).join(".");
            if (part != "value") { // *.*
                if (obj.hasOwnProperty(part) && deepIterate(obj[part], newPath, value)) {
                    return true;
                }
            } else { // *.value.*
                for (var field in obj) {
                    if (obj.hasOwnProperty(field) && deepIterate(obj[field], newPath, value)) {
                        return true;
                    }
                }
            }
        } else { // path with a single part
            if (part != "value") { // *.any
                return obj[part] == value;
            } else {
                for (var field in obj) { // *.value
                    if (obj[field] == value) {
                        return true;
                    }
                }
            }
        }
        return false;
    };
    return deepIterate(this, "{PATH}", {VALUE});
}