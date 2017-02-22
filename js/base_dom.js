(function () {
    var style = document.createStyleSheet();
    var select = function (selector, maxCount) {
        var resultSet = [];
        style.addRule(selector, "miniquery:t");
        for (var i = 0, l = document.all.length; i < l; i += 1) {
            if (document.all[i].currentStyle.miniquery === "t") {
                resultSet.push(document.all[i]);
                if (resultSet.length > maxCount) {
                    break;
                }
            }
        }
        style.removeRule(0);
        return resultSet;
    };

    if (!document.querySelectorAll) {
        document.querySelectorAll = document.body.querySelectorAll = function (selector) {
            return select(selector, Infinity);
        };
    }
    if (!document.querySelector) {
        document.querySelector = document.body.querySelector = function (selector) {
        return select(selector, 1)[0] || null;
    }
}());
