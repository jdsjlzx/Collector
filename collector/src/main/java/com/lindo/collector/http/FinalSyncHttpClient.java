package com.lindo.collector.http;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;

import com.lindo.collector.utils.Utils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.SyncHttpClient;

public class FinalSyncHttpClient {

	SyncHttpClient client;
	
	public FinalSyncHttpClient() {
		client = new SyncHttpClient();
		if (Utils.getCookies() != null) {
			BasicCookieStore bcs = new BasicCookieStore();
			bcs.addCookies(Utils.getCookies().toArray(
					new Cookie[Utils.getCookies().size()]));
			client.setCookieStore(bcs);
		}
	}
	
	public SyncHttpClient getSyncHttpClient(){
		return this.client;
	}
}
