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

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class FallDetection extends Activity {

	static final int PROGRESS_DIALOG = 0;
	ProgressThread progressThread;
	ProgressDialog progressDialog;
	protected GraphView mGraphView;
	protected FallDetector mFallDetector;
	protected LocationUpdateHandler locationUpdateHandler;
	protected boolean hasAcquiredGps = false;
	long RssTime = 0;
	float RssVal = 0;
	long VveTime = 0;
	float VveVal = 0;
	boolean fall_detected = false;
	boolean handling_fall = false;
	protected double lat;
	protected double lon;

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case PROGRESS_DIALOG:
			progressDialog = new ProgressDialog(FallDetection.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setMessage("Sending fall notification...");
			progressDialog.setTitle("A fall was Detected!");
			progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Cancel",
					buttonListener);
			progressDialog.setMax(10);
			return progressDialog;
		default:
			return null;
		}
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case PROGRESS_DIALOG:
			progressDialog.setProgress(0);
			progressThread = new ProgressThread(handler);
			progressThread.start();
		}
	}

	// Define the Handler that receives messages from the thread and update the
	// progress
	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			int total = msg.arg1;
			if (total < 11) {
				progressDialog.setProgress(total);
			} else if (total == 11) {
				progressDialog.setProgress(0);
				dismissDialog(PROGRESS_DIALOG);
				progressThread.setState(ProgressThread.STATE_DONE);
				postDetectedFall(); // report the fall
			}
		}
	};

	/** Nested class that performs progress calculations (counting) */
	class ProgressThread extends Thread {
		Handler mHandler;
		final static int STATE_DONE = 0;
		final static int STATE_RUNNING = 1;
		int mState;
		int total;

		ProgressThread(Handler h) {
			mHandler = h;
		}

		public void run() {
			mState = STATE_RUNNING;
			total = 0;
			while (mState == STATE_RUNNING) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					Log.e("ERROR", "Thread Interrupted");
				}
				Message msg = mHandler.obtainMessage();
				msg.arg1 = total;
				mHandler.sendMessage(msg);
				total++;
			}
		}

		/*
		 * sets the current state for the thread, used to stop the thread
		 */
		public void setState(int state) {
			mState = state;
		}
	}

	// Create an anonymous implementation of OnClickListener
	private OnClickListener buttonListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			progressThread.setState(ProgressThread.STATE_DONE);
			// reset recorded values
			reset_fall_values();
		}
	};

	// Displays a dialog that a fall has been detected.
	public void handle_fall() {

		showDialog(PROGRESS_DIALOG);
	}

	// Post fall details to a REST web service
	private void postDetectedFall() {

		double lat2 = -1;
		double lon2 = -1;

		synchronized (this) {
			lat2 = lat;
			lon2 = lon;
		}

		// Making an HTTP post request and reading out the response
		HttpClient httpclient = new DefaultHttpClient();
		httpclient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		HttpPost httppost = new HttpPost("http://195.240.74.93:3000/falls");
		// set post data
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("datetime",
				(VveTime != 0 ? Long.toString(VveTime) : (RssTime != 0 ? Long
						.toString(RssTime) : ""))));
		nameValuePairs.add(new BasicNameValuePair("rss", (RssVal == 0 ? ""
				: Float.toString(RssVal))));
		nameValuePairs.add(new BasicNameValuePair("vve", (VveVal == 0 ? ""
				: Float.toString(VveVal))));
		nameValuePairs
				.add(new BasicNameValuePair("lat", Double.toString(lat2)));
		nameValuePairs
				.add(new BasicNameValuePair("lon", Double.toString(lon2)));
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
		reset_fall_values();
	}

	public void reset_fall_values() {
		// reset recorded values
		RssVal = VveVal = VveTime = RssTime = 0;
		fall_detected = false;
		handling_fall = false;
	}

	/**
	 * Initialization of the Activity after it is first created. Must at least
	 * call {@link android.app.Activity#setContentView setContentView()} to
	 * describe what is to be displayed in the screen.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Be sure to call the super class.
		super.onCreate(savedInstanceState);
		// Create the view
		mGraphView = new GraphView(this);
		setContentView(mGraphView);
		// Create the fall detector
		mFallDetector = new FallDetector(this);
		// initialize location manager
		locationUpdateHandler = new LocationUpdateHandler(this);
		// check whether gps is turned on
		locationUpdateHandler.checkGPS();
		// set app orientation to landscape
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mFallDetector.registerListeners();
	}

	@Override
	protected void onStop() {
		mFallDetector.unregisterListeners();
		locationUpdateHandler.unregisterListeners();
		super.onStop();
	}
}