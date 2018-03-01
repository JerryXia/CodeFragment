function loadScript(src, errorCallback, obj) {
	var tag = document.createElement("script");
	tag.type = 'text/javascript';
    tag.charset="utf-8";
    tag.onload = tag.onerror = tag.onreadystatechange = function() {
    	if (window[obj]) { // º”‘ÿ≥…π¶
    		loadJs.onloadTime = +new Date();
    		return;
    	}
        if ( !this.readyState ||((this.readyState === "loaded" || this.readyState === "complete")&&!window[obj]) ) {
            errorCallback&&errorCallback(); 
            tag.onerror = tag.onreadystatechange = null;
        }
    };
	tag.src = src;
	document.getElementsByTagName("head")[0].appendChild(tag);
};