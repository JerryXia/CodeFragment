package 
{
	import flash.display.LoaderInfo;
	import flash.display.Sprite;
	import flash.events.Event;
	import flash.external.ExternalInterface;
	import flash.system.Security;
	
	/**
	 * ...FlashCookie 接口
	 * @author tianyu
	 */
	public class Main extends Sprite 
	{
		public var flashCookie:FlashCookie;
		public function Main():void 
		{
			if (stage) init();
			else addEventListener(Event.ADDED_TO_STAGE, init);
		}
		
		private function init(e:Event = null):void 
		{
			removeEventListener(Event.ADDED_TO_STAGE, init);
			Security.allowDomain("*");
			var paramObj:Object = LoaderInfo(this.stage.loaderInfo).parameters;
			var cookieName:String = "cookieName";
			var timeOut:uint = 3600;
			if (paramObj["cookieName"] != null && paramObj["cookieName"] != "") {
				cookieName = paramObj["cookieName"];
			}
			if (paramObj["timeOut"] != null && paramObj["timeOut"] != "") {
				timeOut = paramObj["timeOut"];
			}
			flashCookie = new FlashCookie(cookieName, timeOut);
			ExternalInterface.addCallback("clearTimeOut",flashCookie.clearCookies);
			ExternalInterface.addCallback("saveCookies",flashCookie.saveCookies);
			ExternalInterface.addCallback("removeCookies",flashCookie.removeCookies);
			ExternalInterface.addCallback("getCookies",flashCookie.getCookies);
			ExternalInterface.addCallback("clearCookies", flashCookie.clearCookies);
		}
	}	
}