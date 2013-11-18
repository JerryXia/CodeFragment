var GLOBAL = {};
GLOBAL.namespace = function(str){
    var arr = str.split("."),o = GLOBAL;
    for (i=(arr[0] == "GLOBAL") ? 1 : 0; i<arr.length; i++) {
        o[arr[i]]=o[arr[i]] || {};
        o=o[arr[i]];
    }
}

/*!
* String prototype
* Date: 2013-11-18
*
* ltrim：去除string左边空白符
* rtrim：去除string右边空白符
* trim：去除string首尾空白符
* htmlEncode：把html字符串encode成普通字符串
* htmlDecode：把字符串decode为html字符串
* isStartsWith:判断字符串是否以参数开始
* isEndWith:判断字符串是否以参数结束
*/
if(typeof String.prototype.ltrim=='undefined'){
    String.prototype.ltrim = function(){
        var s = this;
        s = s.replace(/^\s*/g, '');
        return s;
    }
}

if(typeof String.prototype.rtrim=='undefined'){
    String.prototype.rtrim = function(){
        var s = this;
        s = s.replace(/\s*$/g, '');
        return s;
    }
}

if(typeof String.prototype.trim=='undefined'){
    String.prototype.trim = function(){
        return this.ltrim().rtrim();
    }
}

if(typeof String.prototype.htmlEncode=='undefined'){
    String.prototype.htmlEncode = function(encodeNewLine){//encodeNewLine:是否encode换行符
        var s = this;
        s = s.replace(/&/g, '&amp;');
        s = s.replace(/</g, '&lt;');
        s = s.replace(/>/g, '&gt;');
        s = s.replace(/'/g, '&quot;');
        if(encodeNewLine){
            s = s.replace(/\r\n/g, '<br />');
            s = s.replace(/\r/g, '<br />');
            s = s.replace(/\n/g, '<br />');
        }
        return s;
    }
}

if(typeof String.prototype.htmlDecode=='undefined'){
    String.prototype.htmlDecode = function(decodeNewLine){//decodeNewLine：是否decode换行符
        var s = this;
        if(decodeNewLine){
            s = s.replace(/<br\s*\/?>/gi, '\r\n');
        }
        s = s.replace(/&quot;/g, '\'');
        s = s.replace(/&gt;/g, '>');
        s = s.replace(/&lt;/g, '<');
        s = s.replace(/&amp;/g, '&');
        return s;
    }
}

if(typeof String.prototype.startsWith=='undefined'){
    String.prototype.startsWith = function(start, ignoreCase){//start：欲判断字符， ignoreCase：是否忽略大小写
        var s = this;
        if(ignoreCase)
        {
            s = s.toLowerCase();
            end = end.toLowerCase();
        }
        if(s.substr(0, start.length) == start)
            return true;
        return false;
    }
}

if(typeof String.prototype.endsWith=='undefined'){
    String.prototype.endsWith = function(end, ignoreCase){//end：欲判断字符， ignoreCase：是否忽略大小写
        var s = this;
        if(ignoreCase){
            s = s.toLowerCase();
            end = end.toLowerCase();
        }
        if(s.substr(s.length - end.length) == end)
            return true;
        return false;
    }
}

/*!
* Array prototype
* Date: 2013-11-18
*
* indexOf：返回参数在数组中index，不存在返回-1
*/
if(typeof Array.prototype.indexOf=='undefined'){
    Array.prototype.indexOf=function(item,strict){//strict：是否严格相等（===）
        var index=-1;
        var strict=strict=='undefined'? true || strict;
        var length=this.length;
        if(strict){
            for(var i=0;i<length;i++){
                if(this[i]===item){
                    index=i;
                    break;
                }
            }
        }else{
            for(var i=0;i<length;i++){
                if(this[i]==item){
                    index=i;
                    break;
                }
            }
        }
    }
}

/*!
* Date prototype
* Date: 2013-11-18
*
* format：格式化日期(网上找的，据说是个老外写的，但是找不到作者)
* getDaysInMonth：获取某月有多少天
*/
Date.getDaysInMonth = function (year, month) {
    var days = 0;
    switch (month) {
        case 1:
        case 3:
        case 5:
        case 7:
        case 8:
        case 10:
        case 12:
            days = 31
            break;
        case 4:
        case 6:
        case 9:
        case 11:
            days = 30;
            break;
        case 2:
            if (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0))
                days = 29;
            else
                days = 28;
            break;
    }
    return days;
}

if (typeof Date.prototype.format == 'undefined') {
    Date.prototype.format = function (mask) {
        var d = this;
        var zeroize = function (value, length) {
            if (!length) length = 2;
            value = String(value);
            for (var i = 0, zeros = ''; i < (length - value.length); i++) {
                zeros += '0';
            }
            return zeros + value;
        };

        return mask.replace(/"[^"]*"|'[^']*'|\b(?:d{1,4}|m{1,4}|yy(?:yy)?|([hHMstT])\1?|[lLZ])\b/g, function ($0) {
            switch ($0) {
                case 'd': return d.getDate();
                case 'dd': return zeroize(d.getDate());
                case 'ddd': return ['Sun', 'Mon', 'Tue', 'Wed', 'Thr', 'Fri', 'Sat'][d.getDay()];
                case 'dddd': return ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'][d.getDay()];
                case 'M': return d.getMonth() + 1;
                case 'MM': return zeroize(d.getMonth() + 1);
                case 'MMM': return ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'][d.getMonth()];
                case 'MMMM': return ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'][d.getMonth()];
                case 'yy': return String(d.getFullYear()).substr(2);
                case 'yyyy': return d.getFullYear();
                case 'h': return d.getHours() % 12 || 12;
                case 'hh': return zeroize(d.getHours() % 12 || 12);
                case 'H': return d.getHours();
                case 'HH': return zeroize(d.getHours());
                case 'm': return d.getMinutes();
                case 'mm': return zeroize(d.getMinutes());
                case 's': return d.getSeconds();
                case 'ss': return zeroize(d.getSeconds());
                case 'l': return zeroize(d.getMilliseconds(), 3);
                case 'L': var m = d.getMilliseconds();
                    if (m > 99) m = Math.round(m / 10);
                    return zeroize(m);
                case 'tt': return d.getHours() < 12 ? 'am' : 'pm';
                case 'TT': return d.getHours() < 12 ? 'AM' : 'PM';
                case 'Z': return d.toUTCString().match(/[A-Z]+$/);
                    // Return quoted strings with the surrounding quotes removed
                default: return $0.substr(1, $0.length - 2);
            }
        });
    };
}

