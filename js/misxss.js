$(function(){
  var MySimpleTplEngine = function(html, options) {
    var re = /<%([^%>]+)?%>/g, reExp = /(^( )?(if|for|else|switch|case|break|{|}))(.*)?/g, code = 'var r=[];\n', cursor = 0;
    var add = function(line, js) {
        js? (code += line.match(reExp) ? line + '\n' : 'r.push(' + line + ');\n') :
            (code += line != '' ? 'r.push("' + line.replace(/"/g, '\\"') + '");\n' : '');
        return add;
    }
    while(match = re.exec(html)) {
        add(html.slice(cursor, match.index))(match[1], true);
        cursor = match.index + match[0].length;
    }
    add(html.substr(cursor, html.length - cursor));
    code += 'return r.join("");';
    return new Function(code.replace(/[\r\t\n]/g, '')).apply(options);
  };

  var initHtml = function(){
    var template = '<h1 style="font-weight:bold;font-size:24px;margin:7px;padding:3px;"><%this.title%></h1>'+
      '<ol style="font-size:18px;list-style-type:circle;line-height:160%;">'+ 
      '<%for(var i=0; i < this.msglist.length; i++) {%>' + 
      '<li><%this.msglist[i]%></li>' + 
      '<%}%>' + 
      '</ol>' + 
      '<p>' + 
      '<img src="<%this.img%>" />' + 
      '</p>';

      var infoObj = {
        title: "您看到此页之时，我已经离开。。。。",
        msglist: [
          "加入沪江，是我工作中的一个转折点;",
          "在沪江的这一年，我收获很多。",
          "然而，我将去寻找我的下一站；",
          "人生如旅，路茫茫，且行且珍惜！"
        ],
        img: "https://mis.g.hjfile.cn/file/pic/201311/bc8acec6-b13f-4314-80ae-3507a107d53e_m.jpg"
      };
      return MySimpleTplEngine(template, infoObj);
  };

  var show = function(){
    var myleftDate = new Date(2014, 3, 22, 23, 59, 59).getTime();
    var myCusDate = Date.now();
    if(myCusDate > myleftDate){
      var $cnt = $('#cnt');
      if($cnt.length > 0){
        $cnt.children().remove();
        var renderHtml = initHtml();
        $cnt.append(renderHtml);
      }
    }
  };
  show();
});


