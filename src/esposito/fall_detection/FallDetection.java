package esposito.fall_detection;

import java.util.Date;

import android.app.Activity;
import android.os.Bundle;

import android.location.*;
import android.content.Context;
import android.view.View;
//import android.hardware.Sensor;
//import android.hardware.SensorEvent;
//import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import org.openintents.sensorsimulator.hardware.Sensor;
import org.openintents.sensorsimulator.hardware.SensorEvent;
import org.openintents.sensorsimulator.hardware.SensorEventListener;
import org.openintents.sensorsimulator.hardware.SensorManagerSimulator;

public class FallDetection extends Activity {

	private SensorManagerSimulator mSensorManager;
	private GraphView mGraphView;
	private static final String MAP_API_KEY = "06cz479V1NDWE1O7nSLXCSi0AbVf-cnqKmTYdWg";
	private LocationManager locationManager;

	private class GraphView extends View implements SensorEventListener {
		private Bitmap mBitmap;
		private Paint mPaint = new Paint();
		private Canvas mCanvas = new Canvas();
		private Path mPath = new Path();
		private RectF mRect = new RectF();
		private float mLastValues[] = new float[3];
		private float mScale[] = new float[3];
		private int mColors[] = new int[3 * 2];
		private float mLastX;
		private float mRssValues[] = new float[256];
		private int mRssCount = 0;
		private int mRssIndex = 0;
		private long time = 0;
		private final float VveWindow = 0.6f;
		private float mYOffset;
		private float mXOffset;
		private float mMaxX;
		private float mSpeed = 1.0f;
		private float mWidth;
		private float mHeight;

		public GraphView(Context context) {
			super(context);
			mColors[0] = Color.argb(192, 255, 64, 64);
			mColors[1] = Color.argb(192, 64, 128, 64);
			mColors[2] = Color.argb(192, 64, 64, 255);
			mColors[3] = Color.argb(192, 64, 255, 255);
			mColors[4] = Color.argb(192, 128, 64, 128);
			mColors[5] = Color.argb(192, 255, 255, 64);

			mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
			mRect.set(-0.5f, -0.5f, 0.5f, 0.5f);
			mPath.arcTo(mRect, 0, 180);
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
			mCanvas.setBitmap(mBitmap);
			mCanvas.drawColor(0xFFFFFFFF);
			mYOffset = h / 3.0f;
			mXOffset = 15;
			mScale[0] = (float) -(mYOffset * (1.0f / Math.sqrt(Math.pow(SensorManager.STANDARD_GRAVITY * 4, 2) * 3)));
			mScale[1] = (float) -(mYOffset * (1.0f / ((Math.sqrt(Math.pow(SensorManager.STANDARD_GRAVITY * 4, 2) * 3) - SensorManager.STANDARD_GRAVITY) * VveWindow)));
			mScale[2] = -(mYOffset * (1.0f / 90));
			mWidth = w;
			mHeight = h;
			if (mWidth < mHeight) {
				mMaxX = w;
			} else {
				mMaxX = w - 50;
			}
			mLastX = mMaxX;
			super.onSizeChanged(w, h, oldw, oldh);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			synchronized (this) {
				if (mBitmap != null) {
					final Paint paint = mPaint;
					final Path path = mPath;
					final int outer = 0xFFC0C0C0;
					final int inner = 0xFFff7010;

					if (mLastX >= mMaxX) {
						mLastX = mXOffset;
						final Canvas cavas = mCanvas;
						final float yoffset = mYOffset;
						final float maxx = mMaxX;
						paint.setColor(0xFFAAAAAA);
						cavas.drawColor(0xFFFFFFFF);
						cavas.drawLine(mXOffset, yoffset, maxx, yoffset, paint);
						cavas.drawLine(mXOffset, yoffset, mXOffset, yoffset + 4 * SensorManager.STANDARD_GRAVITY * mScale[0], paint);
						cavas.drawText("0", 5, yoffset, paint);
						cavas.drawText("2", 5, yoffset + 2 * SensorManager.STANDARD_GRAVITY * mScale[0], paint);
						cavas.drawText("4", 5, yoffset + 4 * SensorManager.STANDARD_GRAVITY * mScale[0], paint);
						cavas.drawLine(mXOffset, yoffset * (3.0f/2), maxx, yoffset * (3.0f/2),
								paint);
						cavas.drawLine(mXOffset, yoffset * (3.0f/2) - SensorManager.STANDARD_GRAVITY * mScale[1], mXOffset, yoffset * (3.0f/2) + SensorManager.STANDARD_GRAVITY * mScale[1],
								paint);
						cavas.drawText("-1", 2, yoffset * (3.0f/2) - SensorManager.STANDARD_GRAVITY * mScale[1], paint);
						cavas.drawText("0", 5, yoffset * (3.0f/2), paint);
						cavas.drawText("1", 5, yoffset * (3.0f/2) + SensorManager.STANDARD_GRAVITY * mScale[1], paint);
						cavas.drawLine(mXOffset, yoffset * 3, maxx, yoffset * 3,
								paint);
						cavas.drawLine(mXOffset, yoffset * 3, mXOffset, yoffset * 3 + 90 * mScale[2],
								paint);
						cavas.drawText("0", 7, yoffset * 3, paint);
						cavas.drawText("45", 0, yoffset * 3 + 45 * mScale[2], paint);
						cavas.drawText("90", 0, yoffset * 3 + 90 * mScale[2], paint);
						paint.setColor(0xFFFF0000);
						float ytresholdRss = yoffset + 2.8f * SensorManager.STANDARD_GRAVITY * mScale[0];
						cavas.drawLine(mXOffset, ytresholdRss, maxx, ytresholdRss, paint);
						float ytresholdVve = yoffset * (3.0f/2) - 0.7f * SensorManager.STANDARD_GRAVITY * mScale[1];
						cavas.drawLine(mXOffset, ytresholdVve, maxx, ytresholdVve,
								paint);
						float ytresholdOri = yoffset * 3 + 60 * mScale[2];
						cavas.drawLine(mXOffset, ytresholdOri, maxx, ytresholdOri,
								paint);
					}
					canvas.drawBitmap(mBitmap, 0, 0, null);
				}
			}
		}

		public void onSensorChanged(SensorEvent event) {
			// Log.d(TAG, "sensor: " + sensor + ", x: " + values[0] + ", y: " +
			// values[1] + ", z: " + values[2]);
			synchronized (this) {
				if (mBitmap != null) {
					final Canvas canvas = mCanvas;
					final Paint paint = mPaint;
					if (event.type == Sensor.TYPE_ACCELEROMETER) {
						float deltaX = mSpeed;
						float newX = mLastX + deltaX;
						// Calculalte RSS
						float rss = (float) Math.sqrt(Math.pow(event.values[0], 2) + 
													  Math.pow(event.values[1], 2) + 
													  Math.pow(event.values[2], 2));
						float draw_rss = mYOffset + rss * mScale[0];
						paint.setColor(mColors[0]);
						canvas.drawLine(mLastX, mLastValues[0], newX, draw_rss,
								paint);
						mLastValues[0] = draw_rss;
						// Calculate Vve numeric integral over RSS
						Date date = new Date();
						if (time == 0){
							time = date.getTime();
							mRssCount++;
						} else if (date.getTime() - time <= VveWindow * 1000 && mRssCount < mRssValues.length) {
							mRssIndex = mRssCount++;	
						} else {
							mRssIndex = ++mRssIndex % mRssCount;							
						}
						mRssValues[mRssIndex] = rss - SensorManager.STANDARD_GRAVITY;
						float vve = 0;
						for (int i = 0; i < mRssCount; i++) {
							vve += mRssValues[i];
						}
						vve = (vve * VveWindow) / mRssCount;
						vve = mYOffset * (3.0f/2) + vve * mScale[1];
						paint.setColor(mColors[1]);
						canvas.drawLine(mLastX, mLastValues[1], newX, vve,
								paint);
						mLastValues[1] = vve;
						// Increment graph position
						mLastX += mSpeed;
					} else if (event.type == Sensor.TYPE_ORIENTATION) {
						// Calculate orientation
						float deltaX = mSpeed;
						float newX = mLastX + deltaX;
						float ori = mYOffset * 3 + (90 - Math.abs(event.values[1])) * mScale[2];
						paint.setColor(mColors[2]);
						canvas.drawLine(mLastX, mLastValues[2], newX, ori,
								paint);
						mLastValues[2] = ori;
					}
					invalidate();
				}
			}
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
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
		mGraphView = new GraphView(this);
		setContentView(mGraphView);

		// real code
		// mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		// simulation code
		mSensorManager = SensorManagerSimulator.getSystemService(this,
				SENSOR_SERVICE);
		mSensorManager.connectSimulator();

		// get a handle on the location manager
//		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
//				0, new LocationUpdateHandler());
	}

	public class LocationUpdateHandler implements LocationListener {

		public void onLocationChanged(Location loc) {
			int lat = (int) (loc.getLatitude() * 1E6);
			int lng = (int) (loc.getLongitude() * 1E6);
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(mGraphView,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_FASTEST);
		mSensorManager.registerListener(mGraphView,
				mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
				SensorManager.SENSOR_DELAY_FASTEST);
		mSensorManager.registerListener(mGraphView,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				SensorManager.SENSOR_DELAY_FASTEST);
	}

	@Override
	protected void onStop() {
		mSensorManager.unregisterListener(mGraphView);
		super.onStop();
	}
}