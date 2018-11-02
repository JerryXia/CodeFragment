<script src="https://qidian.gtimg.com/lulu/theme/peak/js/plugin/sea.js"></script>
<script src="https://qidian.gtimg.com/lulu/theme/peak/js/plugin/jquery.js"></script>
<script>
var config = {
    'base': 'https://qidian.gtimg.com/lulu/theme/peak/js'
};

$('.aside').on('click', '.jsBar', function () {
    var attrOpen = $(this).attr('open');
    if (typeof attrOpen == 'string') {
        $(this).removeAttr('open');
    } else {
        $(this).attr('open', 'open');
    }
});

function loadScript(src, successCallback, errorCallback, obj) {
    var tag = document.createElement("script");
    tag.type = 'text/javascript';
    tag.charset = 'utf-8';
    tag.src = src;
    tag.onload = tag.onerror = tag.onreadystatechange = function() {
        if (window[obj]) {
            console.debug((src||'') + ': 加载成功');
            if(typeof successCallback === 'function') {
                successCallback();
            }
        }
        if ( !this.readyState || ( (this.readyState === "loaded" || this.readyState === "complete") && !window[obj]) ) {
            console.debug((src||'') + ': 加载失败');
            if(typeof errorCallback === 'function') {
                errorCallback();
            }
            tag.onerror = tag.onreadystatechange = null;
        }
    };
    document.getElementsByTagName("body")[0].appendChild(tag);
};
loadScript(location.pathname + '/page.js', function() {}, function() {}, location.pathname.split('/').pop());
</script>