package esposito.fall_detection;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;

public class FallHandler {

	private FallDetection activity;

	public FallHandler(FallDetection activity) {
		this.activity = activity;
	}

	// Post fall details to a REST web service
	protected void postDetectedFall() {

		double latitude = -1;
		double longitude = -1;
		synchronized (this) {
			latitude = activity.latitude;
			longitude = activity.longintude;
		}

		// Making an HTTP post request and reading out the response
		HttpClient httpclient = new DefaultHttpClient();
		httpclient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		HttpPost httppost = new HttpPost("http://195.240.74.93:3000/falls");
		// set post data
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("datetime",
				(activity.VveTime != 0 ? Long.toString(activity.VveTime)
						: (activity.RssTime != 0 ? Long
								.toString(activity.RssTime) : ""))));
		nameValuePairs.add(new BasicNameValuePair("rss",
				(activity.RssVal == 0 ? "" : Float.toString(activity.RssVal))));
		nameValuePairs.add(new BasicNameValuePair("vve",
				(activity.VveVal == 0 ? "" : Float.toString(activity.VveVal))));
		nameValuePairs
				.add(new BasicNameValuePair("lat", Double.toString(latitude)));
		nameValuePairs
				.add(new BasicNameValuePair("lon", Double.toString(longitude)));
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e) {
			// notify failure
		}
		HttpResponse response;
		String response_content = "";
		try {
			response = httpclient.execute(httppost);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					InputStream instream = entity.getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(instream));
					StringBuilder builder = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						builder.append(line + "\n");
					}
					response_content = builder.toString();
				}
			}
		} catch (Exception e) {
			// notify failure
		}
		if (response_content == "fall_created") {
			// notify success
		} else {
			// notify failure
		}
		// reset recorded values
		activity.reset_fall_values();
	}
}
