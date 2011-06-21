package com.futurice.android.reservator.model.fum3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

import com.futurice.android.reservator.common.Helpers;
import com.futurice.android.reservator.model.AddressBook;
import com.futurice.android.reservator.model.AddressBookEntry;
import com.futurice.android.reservator.model.ReservatorException;
import com.futurice.android.reservator.model.soap.UnsafeSSLSocketFactory;

/*
 * https://fum3.futurice.com/api/group/Futurice/?username=ogre
 * https://fum3.futurice.com/api/user/hnev/?username=ogre&include=cn
 * roomreservator:ako7Thar
 *
 */

public class FumAddressBook extends AddressBook {
	@Override
	protected List<AddressBookEntry> fetchEntries() throws ReservatorException {
		List<AddressBookEntry> entries = new ArrayList<AddressBookEntry>();

		String result = "";

		// TODO: make configurable
		String user = "roomreservator";
		String password = "ako7Thar";

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("https", UnsafeSSLSocketFactory.getUnsafeSocketFactory(), 443)); // XXX, Unsafe, only for debugging!
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

		HttpParams params = new BasicHttpParams();

		SingleClientConnManager mgr = new SingleClientConnManager(params, schemeRegistry);

		DefaultHttpClient httpclient = new DefaultHttpClient(mgr, params);

		// http://msdn.microsoft.com/en-us/library/bb856547(v=exchg.80).aspx
		// Authentication scheme ntlm not supported

		Log.v("httpGet","credentials "+user+":"+password);
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(user, password);
		httpclient.getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);

		HttpGet httpGet = new HttpGet("https://fum3.futurice.com/api/group/Futurice/?username=ogre");

		//httpGet.setHeader("Content-Type","text/xml; charset=utf-8");

		try {
			HttpResponse response = httpclient.execute(httpGet);

			if (response.getStatusLine().getStatusCode() != 200) {
				throw new ReservatorException("Http error -- " + response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase());
			}

			result = Helpers.readFromInputStream(response.getEntity().getContent(), (int) response.getEntity().getContentLength());
		} catch (ClientProtocolException e) {
			Log.w("FUM3", "Exception", e);
			throw new ReservatorException("Error fetching FUM addressbook -- " + e.getMessage(), e);
		} catch (IOException e) {
			Log.w("FUM3", "Exception", e);
			throw new ReservatorException("Error fetching FUM addressbook -- " + e.getMessage(), e);
		}

		Log.v("FUM3", result);

		try {
			JSONObject object = (JSONObject) new JSONTokener(result).nextValue();
			JSONArray locations = object.getJSONArray("uniqueMember");
			for (int i = 0; i < locations.length(); ++i) {
				JSONObject member = locations.getJSONObject(i);
				entries.add(new AddressBookEntry(
						member.getString("display"),
						member.getString("rdn_value")+"@futurice.com"));
			}
			return entries;
		} catch (JSONException e) {
			Log.e("FUM3", "Json error", e);
			throw new ReservatorException(e);
		}

	}
}
