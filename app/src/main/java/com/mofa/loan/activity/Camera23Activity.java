package com.mofa.loan.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.common.utils.BinaryUtil;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.ObjectMetadata;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.mofa.loan.R;
import com.mofa.loan.utils.AlertDialog;
import com.mofa.loan.utils.BaseActivity;
import com.mofa.loan.utils.Config;
import com.mofa.loan.utils.Formatdou;
import com.mofa.loan.utils.HttpUtils;
import com.mofa.loan.utils.MD5Util;
import com.mofa.loan.utils.ToastUtil;
import com.mofa.loan.view.MyProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@TargetApi(23)
public class Camera23Activity extends BaseActivity implements
		SurfaceHolder.Callback {

	private static final String TAG = "MainActivity";
	private SurfaceView mSurfaceview;
	private boolean mStartedFlg = false;// 是否正在录像
	private MediaRecorder mediarecorder;
	private SurfaceHolder mSurfaceHolder;
	private TextView time, content, cameratxt;
	private Camera camera;
	private String path;
	private int text = 10;
	private int cameraPosition = 0;// 0代表前置摄像头，1代表后置摄像头

	private MyHandler handler;

	private void stopRecoder() {
		handler.removeCallbacks(runnable);
		if (mediarecorder != null) {
			mediarecorder.stop();
			mediarecorder.release(); // Now the object cannot be reused
			mediarecorder = null;
			Log.d(TAG, "surfaceDestroyed release mRecorder");
		}
		if (camera != null) {
			camera.stopPreview();
			camera.release();
			camera = null;
		}
	}

	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			text--;
			time.setText("00:0" + text);
			handler.postDelayed(this, 1000);
			if (text == 0) {
				stopRecoder();
				handler.sendEmptyMessage(1);
				dialog = new MyProgressDialog(Camera23Activity.this, "");
				dialog.show();
			}
		}
	};
	private MyProgressDialog dialog;
	private SurfaceHolder holder;
	private OSSClient oss;
	private String name;
	private String userId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_camera23);
		Log.i("MOFA", "Camera2---onCreate");
		initview();
	}

	private long timeIn;
	private long timeOut;

	@Override
	protected void onStart() {
		super.onStart();
		timeIn = System.currentTimeMillis();
		Log.i("MOFA", "Camera2---onStart:" + timeIn);
	}
	@Override
	protected void onStop() {
		super.onStop();
		timeOut = System.currentTimeMillis();
		Log.i("MOFA", "Camera2---onStop:" + timeOut);
		Log.i("MOFA", "Camera2---Show:" + (timeOut - timeIn));
	}

	private void initview() {
		String endpoint = "http://oss-cn-hongkong.aliyuncs.com";
		// 明文设置secret的方式建议只在测试时使用，更多鉴权模式请参考后面的访问控制章节
		OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(
				"LTAIZmB1IQUSkHvU", "WZFGiWk1ferhNhdC2ZiFm76L0xYqoP");
		ClientConfiguration conf = new ClientConfiguration();
		conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
		conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
		conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
		conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
		oss = new OSSClient(getApplicationContext(), endpoint,
				credentialProvider, conf);

		SharedPreferences sp = getSharedPreferences("config", 0x0000);
		userId = sp.getString("userid", "");

		Intent get = getIntent();
		String names = get.getStringExtra("name");
		String money = get.getStringExtra("money");

		handler = new MyHandler(this);
		mSurfaceview = findViewById(R.id.surfaceview);
		time = findViewById(R.id.time);
		content = findViewById(R.id.content);
		content.setText(getResources().getString(R.string.camera_speak1)
				+ " " + names + " " + getResources().getString(R.string.camera_speak2)
				+ " " + Formatdou.formatdou(money + "") + " VND");
		cameratxt = findViewById(R.id.camera);
		holder = mSurfaceview.getHolder();
		holder.setFixedSize(800, 480);
		holder.addCallback(this);
		// setType必须设置，要不出错.
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		cameratxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				cameratxt.setVisibility(View.INVISIBLE);
				mediarecorder = new MediaRecorder();// 创建mediarecorder对象
				/**
				 * 设置竖着录制
				 */
				if (camera != null) {
					camera.release();
				}

				camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
				camera.setDisplayOrientation(90);
				// camera.setParameters(parameters);

				camera.unlock();

				mediarecorder.setCamera(camera);

				mediarecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
				mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
				// 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4
				mediarecorder
						.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
				// 设置录制的视频编码h263 h264
				mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
				mediarecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
				// 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
				mediarecorder.setVideoSize(176, 144);
				// 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
//				mediarecorder.setVideoFrameRate(20);
				mediarecorder.setOrientationHint(270);
				mediarecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
				path = getSDPath();
				if (path != null) {
					File dir = new File(path + "/MOFA/recordtest");
					if (!dir.exists()) {
						dir.mkdir();
					}
					name = userId + "=" + System.currentTimeMillis() + ".mp4";
					path = dir + "/" + name;
					System.out.println(name + path);
				}
				// 设置视频文件输出的路径
				mediarecorder.setOutputFile(path);
				try {
					// 准备录制
					mediarecorder.prepare();
					mediarecorder.start();
				} catch (IllegalStateException e) {
					Log.e("MOFA", e.getMessage());
				} catch (IOException e) {

					Log.e("MOFA", e.getMessage());
				}
				handler.postDelayed(runnable, 1000);

			}
		});
	}

	protected void initMedia() {
		mediarecorder = new MediaRecorder();// 创建mediarecorder对象
		/**
		 * 设置竖着录制
		 */
		if (camera != null) {
			camera.release();
		}

		camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
		camera.setDisplayOrientation(90);
		// camera.setParameters(parameters);

		Parameters params = camera.getParameters();
		List<Size> prviewSizeList = params.getSupportedPreviewSizes();
		List<Size> videoSizeList = params.getSupportedVideoSizes();
		// 查找出最接近的视频录制分辨率
		int index = -1;
		if (videoSizeList != null && prviewSizeList != null) {

			if (videoSizeList.size() != 0 && prviewSizeList.size() != 0) {
				index = bestVideoSize(videoSizeList, 200);
			}
		}

		camera.unlock();

		mediarecorder.setCamera(camera);


		mediarecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		// 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4
		mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		// 设置录制的视频编码h263 h264
		mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
		mediarecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
		// 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
		// Parameters params = camera.getParameters();
		// List<Camera.Size> prviewSizeList =
		// params.getSupportedPreviewSizes();
		// List<Camera.Size> videoSizeList =
		// params.getSupportedVideoSizes();
		// //查找出最接近的视频录制分辨率
		// int index=bestVideoSize(videoSizeList,
		// prviewSizeList.get(0).width);
		// mediarecorder.setVideoSize(videoSizeList
		// .get(index).width,videoSizeList .get(index).height);

		// if (index == -1) {
		// mediarecorder.setVideoSize(176, 144);
		// } else {
		// mediarecorder.setVideoSize(videoSizeList.get(index).width,
		// videoSizeList.get(index).height);
		// Toast.makeText(Camera2Activity.this, "宽：" +
		// videoSizeList.get(index).width + "高：" +
		// videoSizeList.get(index).height, Toast.LENGTH_SHORT).show();
		// }

		mediarecorder.setVideoSize(176, 144);

		// 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
		// mediarecorder.setVideoFrameRate(20);
		mediarecorder.setOrientationHint(270);
		mediarecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
		path = getSDPath();
		if (path != null) {
			File dir = new File(path + "/MOFA/recordtest");
			if (!dir.exists()) {
				dir.mkdir();
			}
			name = userId + "=" + System.currentTimeMillis() + ".mp4";
			path = dir + "/" + name;
		}
		// 设置视频文件输出的路径
		mediarecorder.setOutputFile(path);
		mediarecorder.setOnErrorListener(null);
	}

	public int bestVideoSize(List<Size> videoSizeList, int _w) {
		// 降序排列
		Collections.sort(videoSizeList, new Comparator<Size>() {
			@Override
			public int compare(Size lhs, Size rhs) {
				if (lhs.width > rhs.width) {
					return -1;
				} else if (lhs.width == rhs.width) {
					return 0;
				} else {
					return 1;
				}
			}
		});
		for (int i = 0; i < videoSizeList.size(); i++) {
			if (videoSizeList.get(i).width <= _w) {
				return i;
			}
		}
		return 0;
	}

	/**
	 * 获取系统时间
	 *
	 * @return
	 */
	public static String getDate() {
		Calendar ca = Calendar.getInstance();
		int year = ca.get(Calendar.YEAR); // 获取年份
		int month = ca.get(Calendar.MONTH); // 获取月份
		int day = ca.get(Calendar.DATE); // 获取日
		int minute = ca.get(Calendar.MINUTE); // 分
		int hour = ca.get(Calendar.HOUR); // 小时
		int second = ca.get(Calendar.SECOND); // 秒

		String date = "" + year + (month + 1) + day + hour + minute + second;
		Log.d(TAG, "date:" + date);

		return date;
	}

	/**
	 * 获取SD path
	 *
	 * @return
	 */
	public String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
			return sdDir.toString();
		}

		return null;
	}

	@Override
	public void surfaceCreated(SurfaceHolder surfaceHolder) {
		mSurfaceHolder = surfaceHolder;
		if (!mStartedFlg) {
			camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
			camera.setDisplayOrientation(90);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1,
                               int i2) {
		// 将holder，这个holder为开始在onCreate里面取得的holder，将它赋给mSurfaceHolder
		mSurfaceHolder = surfaceHolder;
		try {
			camera.setPreviewDisplay(surfaceHolder);
		} catch (IOException e) {
			Log.e("MOFA", e.getMessage());
		}
		camera.startPreview();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
		stopRecoder();
	}

	@Override
	public void processMessage(Message message) {

	}
	private class MyHandler extends Handler {
		private WeakReference<Activity> activityWeakReference;
		public MyHandler (Activity activity) {
			activityWeakReference = new WeakReference<>(activity);
		}

		@Override
		public void handleMessage(Message message) {
            if (message.what == 1) {
                PutObjectRequest put = new PutObjectRequest("salobkdw", name,
                        path);

                // 文件元信息的设置是可选的
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentType("application/octet-stream"); // 设置content-type
                try {
                    metadata.setContentMD5(BinaryUtil.calculateBase64Md5(path));
                } catch (IOException e) {
                    Log.e("MOFA", e.getMessage());
                } // 校验MD5
                put.setMetadata(metadata);

                // 异步上传时可以设置进度回调
                put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
                    @Override
                    public void onProgress(PutObjectRequest request,
                                           long currentSize, long totalSize) {
                        Log.i("MOFA", "currentSize: " + currentSize
                                + " totalSize: " + totalSize);
                    }
                });

                OSSAsyncTask task = oss
                        .asyncPutObject(
                                put,
                                new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                                    @Override
                                    public void onSuccess(
                                            PutObjectRequest request,
                                            PutObjectResult result) {
                                        Log.i("MOFA", "UploadSuccess");
                                        // handler.sendEmptyMessage(0);
                                        HttpUtils.doGetAsyn(Config.VIDEO_CORD
                                                        + "&userid=" + userId
                                                        + "&videoUrl=" + name + "&avg=" + MD5Util.md5(userId + "gh57"), handler,
                                                Config.CODE_VIDEO);
                                        // dialog.dismiss();
                                    }

                                    @Override
                                    public void onFailure(
                                            PutObjectRequest request,
                                            ClientException clientExcepion,
                                            ServiceException serviceException) {

                                        // 请求异常
                                        if (clientExcepion != null) {
                                            // 本地异常如网络异常等
                                            clientExcepion.printStackTrace();
                                        }
                                        if (serviceException != null) {
                                            // 服务异常
                                            Log.e("ErrorCode", serviceException
                                                    .getErrorCode());
                                            Log.e("RequestId", serviceException
                                                    .getRequestId());
                                            Log.e("HostId", serviceException
                                                    .getHostId());
                                            Log.e("RawMessage",
                                                    serviceException
                                                            .getRawMessage());
                                        }
                                    }
                                });
            } else {
                if (message.what == Config.CODE_VIDEO) {
                    // case Config.CODE_VIDEO:
                    String result2 = message.obj.toString();
                    try {
                        JSONObject json = new JSONObject(result2);
                        if (json.getInt("err") == 0) {
                            // tv.setText(getResources().getString(R.string.upload_successed));
                            // pb.setVisibility(ProgressBar.GONE);
                            startActivity(new Intent(activityWeakReference.get(),
                                    IndexActivity.class));
                            finish();
                            dialog.dismiss();
                        } else {
                            // setDialog(json.getString("msg"));
                            new AlertDialog(activityWeakReference.get())
                                    .builder()
                                    .setMsg(json.getString("msg"))
                                    .setNegativeButton(
                                            getResources().getString(
                                                    R.string.OK),
                                            new OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    finish();
                                                }
                                            }).show();
                        }

                    } catch (JSONException e) {
                        ToastUtil.showShort(activityWeakReference.get(), getResources().getString(R.string.data_parsing_error));

                        Log.e("MOFA", e.getMessage());
                    }
                    Log.i("MOFA", "Camera2Activity---上传成功");
                }
            }
        }
	}

}
