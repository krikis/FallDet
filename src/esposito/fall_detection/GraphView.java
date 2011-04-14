package esposito.fall_detection;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.hardware.SensorManager;
import android.view.View;

public class GraphView extends View {

	protected Bitmap mBitmap;
	protected Paint mPaint = new Paint();
	protected Canvas mCanvas = new Canvas();
	private Path mPath = new Path();
	private RectF mRect = new RectF();
	protected float mScale[] = new float[3];
	protected int mColors[] = new int[3 * 2];
	protected float mYOffset;
	private float mXOffset;
	private float mMaxX;
	protected float mSpeed = 1.0f;

	private FallDetection activity;

	public GraphView(FallDetection activity) {
		super(activity);
		this.activity = activity;

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
		mXOffset = 17;
		mScale[0] = (float) -(mYOffset * (1.0f / Math.sqrt(Math.pow(
				SensorManager.STANDARD_GRAVITY * 4, 2) * 3)));
		mScale[1] = (float) -(mYOffset * (1.0f / ((Math.sqrt(Math.pow(
				SensorManager.STANDARD_GRAVITY * 4, 2) * 3) - SensorManager.STANDARD_GRAVITY) * activity.mFallDetector.VveWindow)));
		mScale[2] = -(mYOffset * (1.0f / 90));
		mMaxX = w;
		activity.mFallDetector.mLastX = mMaxX;
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		synchronized (this) {
			if (mBitmap != null) {
				final Paint paint = mPaint;

				if (activity.mFallDetector.mLastX >= mMaxX
						|| activity.mFallDetector.mLastXOri >= mMaxX) {
					activity.mFallDetector.mLastXOri = mXOffset;
					activity.mFallDetector.mLastX = mXOffset;
					activity.mFallDetector.newX = mSpeed;
					final Canvas cavas = mCanvas;
					final float yoffset = mYOffset;
					final float maxx = mMaxX;
					paint.setColor(0xFFAAAAAA);
					cavas.drawColor(0xFFFFFFFF);
					// Fal Impact graph
					cavas.drawText("Fall Impact", mXOffset + 4, 25, paint);
					cavas.drawLine(mXOffset, yoffset, maxx, yoffset, paint);
					cavas.drawLine(mXOffset, yoffset, mXOffset, 5, paint);
					cavas.drawText("0", 7, yoffset, paint);
					cavas.drawText("2", 7, yoffset + 2
							* SensorManager.STANDARD_GRAVITY * mScale[0], paint);
					cavas.drawText("4", 7, yoffset + 4
							* SensorManager.STANDARD_GRAVITY * mScale[0], paint);
					// Vertical Velocity graph
					cavas.drawText("Vertical Velocity", mXOffset + 4, yoffset
							* (3.0f / 2) + SensorManager.STANDARD_GRAVITY
							* mScale[1], paint);
					cavas.drawLine(mXOffset, yoffset * (3.0f / 2), maxx,
							yoffset * (3.0f / 2), paint);
					cavas.drawLine(mXOffset, yoffset * (3.0f / 2)
							- SensorManager.STANDARD_GRAVITY * mScale[1],
							mXOffset, yoffset * (3.0f / 2)
									+ SensorManager.STANDARD_GRAVITY
									* mScale[1] - 12, paint);
					cavas.drawText("-1", 4, yoffset * (3.0f / 2)
							- SensorManager.STANDARD_GRAVITY * mScale[1], paint);
					cavas.drawText("0", 7, yoffset * (3.0f / 2), paint);
					cavas.drawText("1", 7, yoffset * (3.0f / 2)
							+ SensorManager.STANDARD_GRAVITY * mScale[1], paint);
					// Posture graph
					cavas.drawText("Posture", mXOffset + 4, yoffset * 3 + 90
							* mScale[2], paint);
					cavas.drawLine(mXOffset, yoffset * 3, maxx, yoffset * 3,
							paint);
					cavas.drawLine(mXOffset, yoffset * 3, mXOffset, yoffset * 3
							+ 90 * mScale[2] - 15, paint);
					cavas.drawText("0", 7, yoffset * 3, paint);
					cavas.drawText("45", 2, yoffset * 3 + 45 * mScale[2], paint);
					cavas.drawText("90", 2, yoffset * 3 + 90 * mScale[2], paint);
					paint.setColor(0xFFFF0000);
					float ytresholdRss = yoffset
							+ activity.mFallDetector.RssTreshold
							* SensorManager.STANDARD_GRAVITY * mScale[0];
					cavas.drawLine(mXOffset, ytresholdRss, maxx, ytresholdRss,
							paint);
					float ytresholdVve = yoffset * (3.0f / 2)
							+ activity.mFallDetector.VveTreshold
							* SensorManager.STANDARD_GRAVITY * mScale[1];
					cavas.drawLine(mXOffset, ytresholdVve, maxx, ytresholdVve,
							paint);
					float ytresholdOri = yoffset * 3
							+ activity.mFallDetector.OriTreshold * mScale[2];
					cavas.drawLine(mXOffset, ytresholdOri, maxx, ytresholdOri,
							paint);
				}
				canvas.drawBitmap(mBitmap, 0, 0, null);
			}
		}
	}

}
