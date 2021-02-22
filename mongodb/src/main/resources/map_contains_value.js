function() {
    var log = function (msg) {
        printjson(msg);
    }
    var deepIterate = function (obj, path, value) {
        var pathSplit = path.split(".");
        var part = pathSplit[0];

        if (Array.isArray(obj)) { // Object is an array
            log({message: "* Object is an array", path: path, current_part: part, current_obj: obj});
            for (var i = 0; i < obj.length; i++) {
                log({message: "Array's item", current_obj: obj[i]});
                if (deepIterate(obj[i], path, value)) {
                    return true;
                }
            }
        } else if (pathSplit.length > 1) { // path with multiple parts
            log({message: "* Path with multiple parts", path: path, current_part: part, current_obj: obj});
            var newPath = pathSplit.slice(1).join(".");
            if (part != "value") { // *.*
                if (obj.hasOwnProperty(part) && deepIterate(obj[part], newPath, value)) {
                    log({message: "The object matches the condition", current_obj: obj});
                    return true;
                }
            } else { // *.value.*
                for (var field in obj) {
                    if (obj.hasOwnProperty(field) && deepIterate(obj[field], newPath, value)) {
                        log({message: "The inner object matches the condition", current_obj: obj[field]});
                        return true;
                    }
                }
            }
        } else { // path with a single part
            log({message: "* Path with single part", path: path, current_part: part, current_obj: obj});
            if (part != "value") { // *.any
                log({message: "Part '" + part + "' (" + obj[part] + ") is equal to " + value + "? " + (obj[part] == value)});
                return obj[part] == value;
            } else {
                log({message: "Part is equal to 'value'"});
                for (var field in obj) { // *.value
                    log({message: "Field '" + field + "' (" + obj[field] + ") is equal to " + value + "? " + (obj[field] == value)});
                    if (obj.hasOwnProperty(field) && obj[field] == value) {
                        return true;
                    }
                }
            }
        }
        return false;
    };

    log({message: "Map value searching started...", path: "{PATH}", value: {VALUE}});
    return deepIterate(this, "{PATH}", {VALUE});
}