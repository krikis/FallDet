package esposito.fall_detection;

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

	protected GraphView mGraphView;

	protected LocationUpdateHandler locationUpdateHandler;
	protected boolean hasAcquiredGps = false;
	protected double latitude;
	protected double longintude;

	protected FallDetector mFallDetector;
	protected long RssTime = 0;
	protected float RssVal = 0;
	protected long VveTime = 0;
	protected float VveVal = 0;
	protected boolean fall_detected = false;
	protected boolean handling_fall = false;

	static final int PROGRESS_DIALOG = 0;
	ProgressThread progressThread;
	ProgressDialog progressDialog;

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

	// Create a Progress Dialog that lets the user cancel reporting the fall
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case PROGRESS_DIALOG:
			progressDialog = new ProgressDialog(this);
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
				FallHandler handler = new FallHandler(FallDetection.this);
				handler.postDetectedFall(); // report the fall
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

	public void reset_fall_values() {
		// reset recorded values
		RssVal = VveVal = VveTime = RssTime = 0;
		fall_detected = false;
		handling_fall = false;
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