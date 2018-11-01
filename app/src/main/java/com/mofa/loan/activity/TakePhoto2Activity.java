package com.mofa.loan.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.mofa.loan.R;
import com.mofa.loan.utils.ToastUtil;
import com.mofa.loan.view.CameraPreview;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class TakePhoto2Activity extends Activity implements OnClickListener {

	private String destination = "汽车证";
	private String FilePath;

	private int now = 1;

	private ImageView btnTakePhoto;
	private ImageView ivUse;
	private ImageView ivChange;
	private ImageView ivRetake;

	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;

	@SuppressWarnings("deprecation")
	private Camera mCamera;
	private CameraPreview mPreview;

	private File pictureDir;

	private boolean saved = false;
	private boolean click = false;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				if (click) {
					setResult(RESULT_OK, null);
					finish();
				}
			}
		}
    };

	@SuppressWarnings("deprecation")
	private PictureCallback mPicture = new PictureCallback() {

		@Override
		public void onPictureTaken(final byte[] data, Camera camera) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					File file = new File(FilePath);
					try {

						// 获取当前旋转角度, 并旋转图片
						Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
								data.length);
						if (mCameraId == CameraInfo.CAMERA_FACING_BACK) {
							bitmap = rotateBitmapByDegree(bitmap, 90);
						} else {
							bitmap = rotateBitmapByDegree(bitmap, -90);
						}
						Matrix matrix = new Matrix();
						if (now == 2) {
							matrix.postScale(-1, 1);
							bitmap = Bitmap.createBitmap(bitmap, 0, 0,
									bitmap.getWidth(), bitmap.getHeight(),
									matrix, true);
						} else if (now == 3) {
//							matrix.postRotate(270);
//							bitmap = Bitmap.createBitmap(bitmap, 0, 0,
//									bitmap.getWidth(), bitmap.getHeight(),
//									matrix, true);
						} else {
							matrix.postRotate(270);
							bitmap = Bitmap.createBitmap(bitmap, 0, 0,
									bitmap.getWidth(), bitmap.getHeight(),
									matrix, true);
						}

						BufferedOutputStream bos = new BufferedOutputStream(
								new FileOutputStream(file));
						bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
						bos.flush();
						bos.close();
						bitmap.recycle();
						Log.i("MOFA", "---takepicture---保存照片");
						saved = true;
						Message msg = Message.obtain();
						msg.what = 1;
						handler.sendMessage(msg);
					} catch (FileNotFoundException e) {
						Log.e("MOFA", e.getMessage());
					} catch (IOException e) {
						Log.e("MOFA", e.getMessage());
					}
				}
			}).start();

			// mCamera.startPreview();
			mCamera.stopPreview();
		}
	};

	private static int mCameraId = 0;

	private FrameLayout preview;

	// 切换前置和后置摄像头
	@SuppressWarnings("deprecation")
	public void switchCamera() {
		CameraInfo cameraInfo = new CameraInfo();
		Camera.getCameraInfo(mCameraId, cameraInfo);
		if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
			mCameraId = CameraInfo.CAMERA_FACING_FRONT;
		} else {
			mCameraId = CameraInfo.CAMERA_FACING_BACK;
		}
		preview.removeView(mPreview);
		releaseCamera();
		// new Thread(new Runnable() {
		// public void run() {
		// try {
		// Thread.sleep(50);
		// } catch (InterruptedException e) {
		// Log.e("MOFA", e.getMessage());
		// }
		openCamera();
		setCameraDisplayOrientation(TakePhoto2Activity.this, mCameraId, mCamera);
		// }
		// }).start();
	}

	private long timeIn;
	private long timeOut;

	@Override
	public void onStart() {
		super.onStart();
		timeIn = System.currentTimeMillis();
		Log.i("MOFA", "TakePhoto---onStart:" + timeIn);
	}

	@Override
	public void onStop() {
		super.onStop();
		timeOut = System.currentTimeMillis();
		Log.i("MOFA", "TakePhoto---onStop:" + timeOut);
		Log.i("MOFA", "TakePhoto---Show:" + (timeOut - timeIn));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("MOFA", "-TakePhoto--onCreate");
		// 设置无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		FilePath = getIntent().getStringExtra("FilePath");
		if (getIntent().getStringExtra("idhandhold") != null
				|| getIntent().getStringExtra("studenthandhold") != null) {
			setContentView(R.layout.activity_takephoto_handhold);
			now = 2;
		} else if (getIntent().getStringExtra("idback") != null) {
			setContentView(R.layout.activity_takephoto_back);
		} else if (getIntent().getStringExtra("proof") != null) {
			setContentView(R.layout.activity_takephoto_proof);
		} else {
			setContentView(R.layout.activity_takephoto_front);
		}
		if (!checkCameraHardware(this)) {
			ToastUtil.showShort(TakePhoto2Activity.this, "camera not support!");

			finish();
		} else {
			initview();
			// new Thread(new Runnable() {
			// public void run() {
			// try {
			// Thread.sleep(50);
			// } catch (InterruptedException e) {
			// Log.e("MOFA", e.getMessage());
			// }
			openCamera();
			setCameraDisplayOrientation(TakePhoto2Activity.this, mCameraId,
					mCamera);
			// }
			// }).start();

		}
	}

	private void initview() {
		btnTakePhoto = findViewById(R.id.iv_takephoto);
		btnTakePhoto.setOnClickListener(this);
		ivUse = findViewById(R.id.iv_OK);
		ivUse.setOnClickListener(this);
		ivChange = findViewById(R.id.iv_change);
		ivChange.setOnClickListener(this);
		ivRetake = findViewById(R.id.iv_retake);
		ivRetake.setOnClickListener(this);
		findViewById(R.id.back).setOnClickListener(this);
	}

	/** A safe way to get an instance of the Camera object. */
	@SuppressWarnings("deprecation")
	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open(mCameraId); // attempt to get a Camera instance
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
		}
		return c; // returns null if camera is unavailable
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onPause() {
		super.onPause();
		releaseCamera();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// new Thread(new Runnable() {
		// public void run() {
		// try {
		// Thread.sleep(50);
		// } catch (InterruptedException e) {
		// Log.e("MOFA", e.getMessage());
		// }
		openCamera();
		setCameraDisplayOrientation(TakePhoto2Activity.this, mCameraId,
				mCamera);
		// }
		// }).start();
	}

	// 释放相机
	public void releaseCamera() {
		if (mCamera != null) {
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}

	// 开始预览相机
	public void openCamera() {
		if (mCamera == null) {
			// Create an instance of Camera
			mCamera = getCameraInstance();
			// get Camera parameters
			Camera.Parameters params = mCamera.getParameters();
			// set the focus mode
			if (mCameraId == CameraInfo.CAMERA_FACING_BACK) {// 因为三星和华为手机前置摄像头不能设置自动聚焦，否则报错
				params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
			}
			// 获取可预览的尺寸集合
			List<Size> supportedPreviewSizes = params
					.getSupportedPreviewSizes();
			Size previewSize = getSupportSize(supportedPreviewSizes);
			// 获取可设置图片的大小集合
			List<Size> supportedPictureSizes = params
					.getSupportedPictureSizes();
			// 设置生成最大的图片
			// Size pictureSize =
			// PreviewSizeUtil.getSupportSize(supportedPictureSizes);
			Size pictureSize = supportedPictureSizes.get((supportedPictureSizes
					.size() - 1) / 2);
			// 获取可设置的帧数
			List<Integer> supportedPreviewFrameRates = params
					.getSupportedPreviewFrameRates();
			Integer frameRates = supportedPreviewFrameRates
					.get((supportedPreviewFrameRates.size() - 1) / 2);
			// 设置Camera的参数
			params.setPreviewSize(previewSize.width, previewSize.height);
			params.setPictureSize(pictureSize.width, pictureSize.height);
			// 设置帧数(每秒从摄像头里面获得几个画面)
			params.setPreviewFrameRate(frameRates);

			// 设置图片格式
			params.setPictureFormat(ImageFormat.JPEG);
			// 设置照片质量
			params.setJpegQuality(100);
			// set Camera parameters
			mCamera.setParameters(params);

			// Create our Preview view and set it as the content of our
			// activity.
			mPreview = new CameraPreview(this, mCamera);
			mPreview.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					mCamera.autoFocus(null);
					return false;
				}
			});
			preview = findViewById(R.id.camera_preview);
			preview.addView(mPreview);
			mCamera.startPreview();
		}
	}

	/** Create a file Uri for saving an image or video */
	@SuppressWarnings("unused")
	private static Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	@SuppressLint("SimpleDateFormat")
	private static File getOutputMediaFile(int type) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"MyCameraApp");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.i("MOFA", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else if (type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "VID_" + timeStamp + ".mp4");
		} else {
			return null;
		}

		return mediaFile;
	}

	// 设置相机横竖屏
	public static void setCameraDisplayOrientation(Activity activity,
			int cameraId, Camera camera) {
		android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}

		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360;
		} else {
			result = (info.orientation - degrees + 360) % 360;
		}
		camera.setDisplayOrientation(result);
	}

	// 旋转图片
	public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
		Bitmap returnBm = null;
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		try {
			returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
					bm.getHeight(), matrix, true);
		} catch (OutOfMemoryError e) {
		}
		if (returnBm == null) {
			returnBm = bm;
		}
		if (bm != returnBm) {
			bm.recycle();
		}
		return returnBm;
	}

	// 判断相机是否支持
	private boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_takephoto:
			mCamera.takePicture(null, null, mPicture);
			Change(1);
			Log.i("MOFA", "take photo --> 拍照");
			break;

		case R.id.iv_OK:
			Log.i("MOFA", "take photo --> 使用");
			click = true;
			if (saved) {
				setResult(RESULT_OK, null);
				finish();
			}
			break;

		case R.id.iv_retake:
			if (mCamera != null) {
				mCamera.startPreview();
			}
			Change(2);
			Log.i("MOFA", "take photo --> 重拍");
			break;

		case R.id.iv_change:
			switchCamera();
			Log.i("MOFA", "take photo --> 切换");
			break;

		case R.id.back:
			setResult(RESULT_CANCELED);
			finish();
			Log.i("MOFA", "take photo --> 取消");
			break;

		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		finish();
		Log.i("MOFA", "take photo --> onBackPressed");
		super.onBackPressed();
	}

	/**
	 * 1:toked 2:taking
	 *
	 * @param position
	 */
	private void Change(int position) {
		if (position == 1) {
			btnTakePhoto.setVisibility(View.GONE);
			ivChange.setVisibility(View.GONE);
			ivUse.setVisibility(View.VISIBLE);
			ivRetake.setVisibility(View.VISIBLE);
		} else {
			btnTakePhoto.setVisibility(View.VISIBLE);
			ivChange.setVisibility(View.VISIBLE);
			ivUse.setVisibility(View.GONE);
			ivRetake.setVisibility(View.GONE);
		}
	}

	/**
	 * 获取最大的预览/图片尺寸
	 *
	 * @return 预览尺寸集合
	 */
	public static Size getSupportSize(List<Size> listSize) {

		if (listSize == null || listSize.size() <= 0) {

			return null;
		}
		Size largestSize = listSize.get(0);
		if (listSize.size() > 1) {
			Iterator<Size> iterator = listSize.iterator();
			while (iterator.hasNext()) {
				Camera.Size size = iterator.next();
				if (size.width > largestSize.width
						&& size.height > largestSize.height) {
					largestSize = size;
				}
			}
		}
		return largestSize;

	}

}
