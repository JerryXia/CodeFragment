<script src="https://cdn.staticfile.org/zui/1.9.2/lib/jquery/jquery.js"></script>
<script src="https://cdn.staticfile.org/zui/1.9.2/js/zui.min.js"></script>
<script src="https://cdn.staticfile.org/vue/2.5.16/vue.min.js"></script>
<script src="https://cdn.staticfile.org/underscore.js/1.8.3/underscore-min.js"></script>
<script src="https://cdn.jsdelivr.net/gh/teambition/then.js/then.js"></script>
<script src="https://cdn.jsdelivr.net/gh/zhangxinxu/html5Validate/src/jquery-html5Validate.js"></script>
<script>
var utils = { __version__: 'testVersion 0.0.1' };
utils.format = function () {
    if (arguments.length == 0) return null;
    var str = arguments[0];
    for (var i = 1; i < arguments.length; i++) {
        var regExp = new RegExp('\\{' + (i - 1) + '\\}', 'gm');
        str = str.replace(regExp, arguments[i])
    }
    return str;
};
// 遍历表单指定的元素
utils.formEach = function (selector) {
    var dataObj = {};
    $(selector).each(function (index, element) {
        // element == this
        if (element.name) {
            dataObj[element.name] = $(element).val();
        } else if (element.id) {
            dataObj[element.id] = $(element).val();
        } else {
            //
        }
    });
    return dataObj;
};
//解析location.search
utils.getQueryStringArgs = function () {
    var qs = (window.location.search.length > 0 ? window.location.search.substring(1) : ""); //去除？
    args = {},
    items = qs.length ? qs.split("&") : [];
    item = null,
    name = null,
    value = null,
    i = 0,
    len = items.length;
    for (i = 0; i < len; i++) {
        item = items[i].split("=");
        name = decodeURIComponent(item[0]);
        value = decodeURIComponent(item[1]);
        if (name.length) {
            args[name] = value;
        };
    }
    return args;
};
utils.jsonPost = function(url, postData, successCallback, errorCallback, completeCallback) {
    $.ajax({
        method: 'post',
        url: url,
        contentType: 'application/json; charset=UTF-8',
        data: JSON.stringify(postData),
        success: successCallback,
        error: function (jqXHR, textStatus, errorThrown) {
            console.info(jqXHR); 
            console.info(textStatus); 
            console.error(errorThrown);
            if(typeof errorCallback !== 'undefined') {
                errorCallback();
            }
        },
        complete: function (jqXHR, textStatus) {
            if(typeof completeCallback !== 'undefined') {
                completeCallback(jqXHR, textStatus);
            }
        }
    });
};
utils.formPost = function(url, postData, successCallback, errorCallback, completeCallback) {
    $.ajax({
        method: 'post',
        url: url,
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
        data: postData,
        success: successCallback,
        error: function (jqXHR, textStatus, errorThrown) {
            console.info(jqXHR); 
            console.info(textStatus); 
            console.error(errorThrown);
            if(typeof errorCallback !== 'undefined') {
                errorCallback();
            }
        },
        complete: function (jqXHR, textStatus) {
            if(typeof completeCallback !== 'undefined') {
                completeCallback(jqXHR, textStatus);
            }
        }
    });
};

Vue.filter('timeStamp', function (value, formater) {
    return value ? new Date(value).format(formater) : '';
});
Vue.filter('objectIdTimeStamp', function (value, formater) {
    return new Date(parseInt(value.substring(0, 8), 16) * 1000).format(formater);
});
Vue.filter('joinArray', function (input, separator) {
    if(!Array.isArray(input)){
        return input;
    } else {
        return input.join(separator);
    }
});



function loadScript(src, successCallback, errorCallback, obj) {
    var tag = document.createElement("script");
    tag.type = 'text/javascript';
    tag.charset = 'utf-8';
    tag.src = src;
    tag.onload = tag.onerror = tag.onreadystatechange = function() {
        if (window[obj]) {
            console.debug((src||'') + ': load success');
            if(typeof successCallback === 'function') {
                successCallback();
            }
        }
        if ( !this.readyState || ( (this.readyState === "loaded" || this.readyState === "complete") && !window[obj]) ) {
            console.debug((src||'') + ': load fail');
            if(typeof errorCallback === 'function') {
                errorCallback();
            }
            tag.onerror = tag.onreadystatechange = null;
        }
    };
    document.getElementsByTagName("body")[0].appendChild(tag);
};
setTimeout(function(){
    var pathname = window.location.pathname;
    var scriptSrc = pathname.endsWith('/') ? pathname + 'page.js?t=' : pathname + '/page.js?t=';
    loadScript(scriptSrc + Math.floor(Date.now() / 3600000), function() {}, function() {}, window.location.pathname.split('/').pop());
}, 200);
</script>