function loadScript(src, errorCallback, obj) {
	var tag = document.createElement("script");
	tag.type = 'text/javascript';
    tag.charset = 'utf-8';
    tag.src = src;
    tag.onload = tag.onerror = tag.onreadystatechange = function() {
    	if (window[obj]) {
            // 加载成功
    		return;
    	}
        if ( !this.readyState || ( (this.readyState === "loaded" || this.readyState === "complete") && !window[obj]) ) {
            errorCallback&&errorCallback(); 
            tag.onerror = tag.onreadystatechange = null;
        }
    };
	document.getElementsByTagName("head")[0].appendChild(tag);
};
