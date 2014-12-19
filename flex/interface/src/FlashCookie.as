package  {
	import flash.display.Sprite;	
    import flash.net.SharedObject;
    import flash.external.ExternalInterface;
	/**@author tianyu*/
    public class FlashCookie{
        private var cookieTimeOut:uint;
        private var cookieName:String;
        private var cookieSharedObj:SharedObject;
		private var currentCookie:Object;
        private var cookieValue:String;
        public function FlashCookie(cName:String = "cName", timeOut:uint=3600) {
            cookieName = cName;
            cookieTimeOut = timeOut;
            cookieSharedObj = SharedObject.getLocal(cName, "/" );
		    if(isCookieExist(cName)){
		        currentCookie = getCookies(cName);
		    }
        }
        /**到期删除Cookies*/
        public function clearTimeOut():void {
            var obj:* = cookieSharedObj.data.cookie;
            if(obj == undefined){
                return;
            }
            for(var key:String in obj){
                if(obj[key] == undefined || obj[key].time == undefined || isTimeOut(obj[key].time)){
                    delete obj[key];
                }
            }
            cookieSharedObj.data.cookie = obj;
            cookieSharedObj.flush();
        }
        /**添加Cookies( key-value )*/
        public function saveCookies(key:String, value:*):void {
            var today:Date = new Date();
            key = "key_" + key;
            value.time = today.getTime();
            if(cookieSharedObj.data.cookie == undefined){
                var obj:Object = {};
                obj[key] = value;
                cookieSharedObj.data.cookie = obj;
            }else{
                cookieSharedObj.data.cookie[key] = value;
            }
            cookieSharedObj.flush();
        }
        /**删除当前Cookies*/
        public function removeCookies(key:String):void {
            if (isCookieExist(key)) {
                delete cookieSharedObj.data.cookie["key_" + key];
                cookieSharedObj.flush();
            }
        }
        /**通过Key来获取Cookies值。*/
        public function getCookies(key:String):Object {
            return isCookieExist(key)?cookieSharedObj.data.cookie["key_" + key]:null;
        }
        /**检查Cookies是否存在。*/
        public function isCookieExist(key:String):Boolean{
            key = "key_" + key;
            return cookieSharedObj.data.cookie != undefined && cookieSharedObj.data.cookie[key] != undefined;
        }
        /**检查Cookies的到期时间*/
        public function isTimeOut(time:uint):Boolean {
            var today:Date = new Date();
            return time + cookieTimeOut * 1000 < today.getTime();
        }
        /**取得Cookies的到期时间*/
        public function getTimeOut():uint {
            return cookieTimeOut;
        }
        /**取得Cookies名称。*/
        public function getName():String {
            return cookieName;
        }
        /**清除所有的Cookies值。*/
        public function clearCookies():void {
            cookieSharedObj.clear();
        }
    }
}