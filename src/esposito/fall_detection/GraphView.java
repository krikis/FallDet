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
		mXOffset = 20;
		mMaxX = w;
		activity.mFallDetector.mLastX = mMaxX;
		// vve scale
		mScale[0] = (float) -((mYOffset - 5) * (1.0f / (SensorManager.STANDARD_GRAVITY * 3)));
		// rss scale
		mScale[1] = (float) -((mYOffset - 5) * (1.0f / (SensorManager.STANDARD_GRAVITY * 5)));
		// posture scale
		mScale[2] = -((mYOffset - 5) * (1.0f / 120));
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
					cavas.drawColor(0xFFFFFFFF);
					// Vertical Velocity graph
					paint.setColor(0xFFAAAAAA);
					cavas.drawText("Vertical Velocity", mXOffset + 4, 20, paint);
					cavas.drawLine(mXOffset, yoffset, mXOffset, 5, paint);
					cavas.drawLine(mXOffset, yoffset / 2.0f, maxx,
							yoffset / 2.0f, paint);
					float minusone = yoffset / 2.0f
							- SensorManager.STANDARD_GRAVITY * mScale[0];
					cavas.drawText("-1", 7, minusone + 3, paint);
					cavas.drawLine(mXOffset, minusone, mXOffset - 2, minusone,
							paint);
					float zero = yoffset / 2.0f;
					cavas.drawText("0", 10, zero + 3, paint);
					cavas.drawLine(mXOffset, zero, mXOffset - 2, zero, paint);
					float one = yoffset / 2.0f + SensorManager.STANDARD_GRAVITY
							* mScale[0];
					cavas.drawText("1", 10, one + 3, paint);
					cavas.drawLine(mXOffset, one, mXOffset - 2, one, paint);
					paint.setColor(0xFFFF0000);
					float ytresholdVve = yoffset / 2.0f
							+ activity.mFallDetector.VveTreshold
							* SensorManager.STANDARD_GRAVITY * mScale[0];
					cavas.drawLine(mXOffset, ytresholdVve, maxx, ytresholdVve,
							paint);
					// Fal Impact graph
					paint.setColor(0xFFAAAAAA);
					cavas.drawText("Fall Impact", mXOffset + 4, yoffset + 20,
							paint);
					cavas.drawLine(mXOffset, yoffset * 2, mXOffset,
							yoffset + 5, paint);
					cavas.drawLine(mXOffset, yoffset * 2, maxx, yoffset * 2,
							paint);
					zero = yoffset * 2;
					cavas.drawText("0", 10, zero + 3, paint);
					cavas.drawLine(mXOffset, zero, mXOffset - 2, zero, paint);
					float two = yoffset * 2 + 2
							* SensorManager.STANDARD_GRAVITY * mScale[1];
					cavas.drawText("2", 10, two + 3, paint);
					cavas.drawLine(mXOffset, two, mXOffset - 2, two, paint);
					float four = yoffset * 2 + 4
							* SensorManager.STANDARD_GRAVITY * mScale[1];
					cavas.drawText("4", 10, four + 3, paint);
					cavas.drawLine(mXOffset, four, mXOffset - 2, four, paint);
					paint.setColor(0xFFFF0000);
					float ytresholdRss = yoffset * 2
							+ activity.mFallDetector.RssTreshold
							* SensorManager.STANDARD_GRAVITY * mScale[1];
					cavas.drawLine(mXOffset, ytresholdRss, maxx, ytresholdRss,
							paint);
					// Posture graph
					paint.setColor(0xFFAAAAAA);
					cavas.drawText("Posture", mXOffset + 4, yoffset * 2 + 20,
							paint);
					cavas.drawLine(mXOffset, yoffset * 3, mXOffset,
							yoffset * 2 + 5, paint);
					cavas.drawLine(mXOffset, yoffset * 3, maxx, yoffset * 3,
							paint);
					zero = yoffset * 3;
					cavas.drawText("0", 10, zero - 1, paint);
					cavas.drawLine(mXOffset, zero, mXOffset - 2, zero,
							paint);
					float fortfiv = yoffset * 3 + 45 * mScale[2];
					cavas.drawText("45", 3, fortfiv - 1, paint);
					cavas.drawLine(mXOffset, fortfiv, mXOffset - 2, fortfiv,
							paint);
					float ninet = yoffset * 3 + 90 * mScale[2];
					cavas.drawText("90", 3, ninet - 1, paint);
					cavas.drawLine(mXOffset, ninet, mXOffset - 2, ninet, paint);
					paint.setColor(0xFFFF0000);
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
