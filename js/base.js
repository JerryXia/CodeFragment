var GLOBAL = {};
GLOBAL.namespace = function(str){
    var arr = str.split("."),o = GLOBAL;
    for (i=(arr[0] == "GLOBAL") ? 1 : 0; i<arr.length; i++) {
        o[arr[i]]=o[arr[i]] || {};
        o=o[arr[i]];
    }
}

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
                        if(decodeNewLine)
                        {
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
                        if(ignoreCase)
                        {
                            s = s.toLowerCase();
                            end = end.toLowerCase();
                        }
                        if(s.substr(s.length - end.length) == end)
                            return true;
                        return false;
                    }
                }

