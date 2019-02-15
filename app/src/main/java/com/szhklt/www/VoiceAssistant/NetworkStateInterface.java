package com.szhklt.www.VoiceAssistant;

public interface NetworkStateInterface {
	
	/**
	 * WIFI断开
	 */
	public void WifiDisconnect();

	/**
	 * WIFI链接成功
	 */
	public void WifiConnect();
}
