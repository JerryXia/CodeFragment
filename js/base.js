/*!
* GLOBAL namespace
* Date: 2013-09-28
* 全局命名空间 生成公共变量
*
*/
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

/*!
* Event
* Date: 2013-11-26
*
*
*/
function addEvent(node, type, handler) {
    if (!node) return false;
    if (node.addEventListener) {
        node.addEventListener(type, handler, false);
        return true;
    }
    else if (node.attachEvent) {
        node['e' + type + handler] = handler;
        node[type + handler] = function() {
            node['e' + type + handler](window.event);
        };
        node.attachEvent('on' + type, node[type + handler]);
        return true;
    }
    return false;
}

function removeEvent(node, type, handler) {
    if (!node) return false;
    if (node.removeEventListener) {
        node.removeEventListener(type, handler, false);
        return true;
    }
    else if (node.detachEvent) {
        node.detachEvent('on' + type, node[type + handler]);
        node[type + handler] = null;
    }
    return false;
}




(function(){function c(){var e=document.createElement("link");e.setAttribute("type","text/css");e.setAttribute("rel","stylesheet");e.setAttribute("href",f);e.setAttribute("class",l);document.body.appendChild(e)}function h(){var e=document.getElementsByClassName(l);for(var t=0;t<e.length;t++){document.body.removeChild(e[t])}}function p(){var e=document.createElement("div");e.setAttribute("class",a);document.body.appendChild(e);setTimeout(function(){document.body.removeChild(e)},100)}function d(e){return{height:e.offsetHeight,width:e.offsetWidth}}function v(i){var s=d(i);return s.height>e&&s.height<n&&s.width>t&&s.width<r}function m(e){var t=e;var n=0;while(!!t){n+=t.offsetTop;t=t.offsetParent}return n}function g(){var e=document.documentElement;if(!!window.innerWidth){return window.innerHeight}else if(e&&!isNaN(e.clientHeight)){return e.clientHeight}return 0}function y(){if(window.pageYOffset){return window.pageYOffset}return Math.max(document.documentElement.scrollTop,document.body.scrollTop)}function E(e){var t=m(e);return t>=w&&t<=b+w}function S(){var e=document.createElement("audio");e.setAttribute("class",l);e.src=i;e.loop=false;e.addEventListener("canplay",function(){setTimeout(function(){x(k)},500);setTimeout(function(){N();p();for(var e=0;e<O.length;e++){T(O[e])}},15500)},true);e.addEventListener("ended",function(){N();h()},true);e.innerHTML=" <p>If you are reading this, it is because your browser does not support the audio element. We recommend that you get a new browser.</p> <p>";document.body.appendChild(e);e.play()}function x(e){e.className+=" "+s+" "+o}function T(e){e.className+=" "+s+" "+u[Math.floor(Math.random()*u.length)]}function N(){var e=document.getElementsByClassName(s);var t=new RegExp("\\b"+s+"\\b");for(var n=0;n<e.length;){e[n].className=e[n].className.replace(t,"")}}var e=30;var t=30;var n=350;var r=350;var i="//s3.amazonaws.com/moovweb-marketing/playground/harlem-shake.mp3";var s="mw-harlem_shake_me";var o="im_first";var u=["im_drunk","im_baked","im_trippin","im_blown"];var a="mw-strobe_light";var f="//s3.amazonaws.com/moovweb-marketing/playground/harlem-shake-style.css";var l="mw_added_css";var b=g();var w=y();var C=document.getElementsByTagName("*");var k=null;for(var L=0;L<C.length;L++){var A=C[L];if(v(A)){if(E(A)){k=A;break}}}if(A===null){console.warn("Could not find a node of the right size. Please try a different page.");return}c();S();var O=[];for(var L=0;L<C.length;L++){var A=C[L];if(v(A)){O.push(A)}}})()
