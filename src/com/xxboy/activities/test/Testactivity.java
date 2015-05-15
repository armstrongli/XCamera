package com.xxboy.activities.test;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.xxboy.activities.mainview.XCamera.XCameraConst;
import com.xxboy.photo.R;

public class Testactivity extends Activity implements OnTouchListener/* , OnClickListener */{
	// private GestureDetector mGestureDetector;
	Matrix matrix = new Matrix();
	// Matrix savedMatrix = new Matrix();
	ImageView imageView;
	PointF first = new PointF();
	PointF start = new PointF();
	PointF mid = new PointF();;
	private float oldDist;
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;
	private long beginTime, endTime;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/* display.xml Layout */
		setContentView(12/* R.layout.main */);

		int screenWidth = XCameraConst.SCREEN_WIDTH;
		int screenHeight = XCameraConst.SCREEN_HEIGHT;

		// 获取图片本身的宽 和高
		Bitmap mybitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_media_embed_play);
		System.out.println("old===" + mybitmap.getWidth());

		int widOrg = mybitmap.getWidth();
		int heightOrg = mybitmap.getHeight();

		// 宽 高 比列
		float scaleWid = (float) screenWidth / widOrg;
		float scaleHeight = (float) screenHeight / heightOrg;
		float scale;

		imageView = (ImageView) this.findViewById(123/* R.id.bmp */);

		// 如果宽的 比列大于搞的比列 则用高的比列 否则用宽的

		if (scaleWid > scaleHeight) {
			scale = scaleHeight;
		} else {
			scale = scaleWid;
		}

		// matrix=new Matrix();
		imageView.setImageBitmap(mybitmap);

		matrix.postScale(scale, scale);

		imageView.setImageMatrix(matrix);

		imageView.setOnTouchListener(this);

		imageView.setLongClickable(true);

		// savedMatrix.set(matrix);
	}

	@SuppressLint({ "ClickableViewAccessibility", "FloatMath" })
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// mGestureDetector.onTouchEvent(event);
		System.out.println("action===" + event.getAction());
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:

			beginTime = System.currentTimeMillis();

			mode = DRAG;
			System.out.println("down");
			first.set(event.getX(), event.getY());
			start.set(event.getX(), event.getY());
			break;
		case MotionEvent.ACTION_UP:

			endTime = System.currentTimeMillis();

			System.out.println("endTime==" + (endTime - beginTime));
			float x = event.getX(0) - first.x;
			float y = event.getY(0) - first.y;
			// 多长的距离
			float move = FloatMath.sqrt(x * x + y * y);

			System.out.println("move==" + (move));

			// 计算时间和移动的距离 来判断你想要的操作，经过测试90%情况能满足
			if (endTime - beginTime < 500 && move > 20) {
				// 这里就是做你上一页下一页的事情了。
				Toast.makeText(this, "----do something-----", Toast.LENGTH_SHORT).show();
			}
			break;
		case MotionEvent.ACTION_MOVE:

			System.out.println("move");
			if (mode == DRAG) {
				matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
				start.set(event.getX(), event.getY());
			} else {
				float newDist = spacing(event);
				if (newDist > 10f) {
					// matrix.set(savedMatrix);
					float scale = newDist / oldDist;
					System.out.println("scale==" + scale);
					matrix.postScale(scale, scale, mid.x, mid.y);
				}
				oldDist = newDist;
			}
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = spacing(event);
			if (oldDist > 10f) {
				midPoint(mid, event);
				mode = ZOOM;
			}
			System.out.println("ACTION_POINTER_DOWN");
			break;
		case MotionEvent.ACTION_POINTER_UP:
			System.out.println("ACTION_POINTER_UP");
			break;
		}
		imageView.setImageMatrix(matrix);
		return false;
	}

	// @Override
	// public void onClick(View v) {
	// if (v == small) {
	// matrix.postScale(0.5f, 0.5f, 0, 0);
	// // matrix.setScale(0.5f, 0.5f);
	// bmp.setImageMatrix(matrix);
	// } else {
	// matrix.postScale(2f, 2f);
	// // matrix.setScale(2f,2f);
	// bmp.setImageMatrix(matrix);
	// }
	// }

	/**
	 * 计算拖动的距离
	 * 
	 * @param event
	 * @return
	 */
	@SuppressLint("FloatMath")
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/**
	 * 计算两点的之间的中间点
	 * 
	 * @param point
	 * @param event
	 */

	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}
}