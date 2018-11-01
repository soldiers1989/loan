package com.mofa.loan.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.mofa.loan.utils.BitmapUtils;
import com.mofa.loan.utils.Config;
import com.mofa.loan.utils.HttpUtils;
import com.mofa.loan.utils.MD5Util;
import com.mofa.loan.utils.PermissionUtils;
import com.mofa.loan.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

@TargetApi(23)
public class BankPayActivity extends BaseActivity implements OnClickListener {

	private String userid;
	private Bitmap bitmap;
	private String FilePath;
	private OSSClient oss;
	private boolean OK = false;
	private boolean uploading = false;
	private String ossPath = "";

	private RelativeLayout rlCredentials;
	private Button btnUpload;
	private ImageView ivCamera;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			String result = msg.obj.toString();
			switch (msg.what) {
			case Config.CODE_ERROR:
				ToastUtil.showShort(BankPayActivity.this, "system error");
				break;
			case Config.UPLOAD_FAILED:
				ToastUtil.showShort(BankPayActivity.this, BankPayActivity.this.getResources().getString(R.string.upload_failture));
				OK = false;
				rlCredentials.setBackgroundResource(R.drawable.proof);
				break;
			case Config.CODE_URL_ERROR:
				ToastUtil.showShort(BankPayActivity.this, BankPayActivity.this.getResources().getString(R.string.url_error));
				break;
			case Config.CODE_NET_ERROR:
				ToastUtil.showShort(BankPayActivity.this, BankPayActivity.this.getResources().getString(R.string.network_error));
				break;
			case Config.CODE_CONFIRMMONEYBACK:
				try {
					JSONObject jsonResult = new JSONObject(result);
					int err = jsonResult.getInt("error");
					if (err == 0) {
						// pay();
						Log.i("MOFA", "BankPayActivity---已提交还款");
						ToastUtil.showShort(BankPayActivity.this, BankPayActivity.this.getResources().getString(R.string.repayment_submitted));

						Intent intent1 = new Intent(BankPayActivity.this,
								IndexActivity.class);
						intent1.putExtra("id", 2);
						startActivity(intent1);
//						Map<String, Object> eventValues = new HashMap<>();
//						eventValues.put("BackMoney", "ture");
//						AppsFlyerLib.getInstance().trackEvent(BankPayActivity.this, "BackMoney",
//								eventValues);
						finish();
					} else {
						showDialog(jsonResult.optString("msg"));
					}
				} catch (JSONException e2) {
					e2.printStackTrace();
				}

				break;
			default:
				break;
			}
		}
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bank_pay);
		Log.i("MOFA", "BankPayActivity---onCreate");
		initview();
		initOSS();
		initdate();
	}
	private long timeIn;
	private long timeOut;
	
	@Override
	protected void onStart() {
		super.onStart();
		timeIn = System.currentTimeMillis();
		Log.i("MOFA", "BankPayActivity---onStart:" + timeIn);
	}
	@Override
	protected void onStop() {
		super.onStop();
		timeOut = System.currentTimeMillis();
		Log.i("MOFA", "BankPayActivity---onStop:" + timeOut);
		Log.i("MOFA", "BankPayActivity---Show:" + (timeOut - timeIn));
	}

	private void initOSS() {
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
	}

	private void initdate() {
	}

	private void initview() {
		rlCredentials = findViewById(R.id.rl_credentials);
		btnUpload = findViewById(R.id.btn_upload);
		ivCamera = findViewById(R.id.iv_camera);
		TextView title = findViewById(R.id.title_txt_center);
		title.setText("Thanh toán qua ngân hàng");
		TextView word = findViewById(R.id.tv_word);
		SpannableString msp = new SpannableString("Quý khách có thể thông qua 2 kênh Viettel hoặc Bưu điện để chuyển tiền tới tài khoản Vietcombank của công ty Xương Thịnh.");
		msp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 34, 55, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		word.setText(msp);
//		TextView attention = (TextView) findViewById(R.id.tv_attention);
//		attention.setText(Html.fromHtml("<font color=\"#DA3636\"> Lưu ý:  </font> Phiền quý khách ghi Nội dung chuyển tiền như sau: số CMND xxx trả vay."));
		
		findViewById(R.id.back).setOnClickListener(this);

		SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
		userid = sp.getString("userid", "");
		int isBackMoney = sp.getInt("isBackMoney", 0);
		if (isBackMoney == 1) {
			ivCamera.setOnClickListener(this);
			btnUpload.setOnClickListener(this);
		} else {
			rlCredentials.setVisibility(View.GONE);
			btnUpload.setVisibility(View.GONE);
			findViewById(R.id.tv).setVisibility(View.GONE);
			findViewById(R.id.view).setVisibility(View.GONE);
		}
	}

	@Override
	public void processMessage(Message message) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;

		case R.id.iv_camera:
			if (Build.VERSION.SDK_INT >= 23) {
				if (this.checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
						|| this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
					// 申请权限 第二个参数是一个 数组 说明可以同时申请多个权限
					this.requestPermissions(
							new String[] {
									android.Manifest.permission.CAMERA,
									android.Manifest.permission.WRITE_EXTERNAL_STORAGE },
							PermissionUtils.LOCATION_PERMISSION);
				} else {// 已授权
					takePhoto();
				}
			} else {
				takePhoto();
			}
			break;

		case R.id.rl_credentials:

			break;

		case R.id.btn_upload:
			if (uploading) {
				ToastUtil.showShort(BankPayActivity.this, BankPayActivity.this.getResources().getString(R.string.uploading));

				return;
			}
			if (OK) {
				Upload(ossPath, FilePath, mHandler);
			} else {
				ToastUtil.showShort(BankPayActivity.this, "no photo!");

			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
		if (grantResults.length > 0
				&& grantResults[0] == PackageManager.PERMISSION_GRANTED
				&& grantResults[1] == PackageManager.PERMISSION_GRANTED) {
			// 权限申请成功
			takePhoto();
		} else {
			Intent intent1 = new Intent(BankPayActivity.this,
					IndexActivity.class);
			intent1.putExtra("id", 1);
			startActivity(intent1);
			finish();
			Log.i("MOFA", "权限拒绝：银行还款时拍摄照片权限");
		}
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	private void takePhoto() {
		Intent intent = new Intent(BankPayActivity.this,
				TakePhoto2Activity.class);
		String path = Environment.getExternalStorageDirectory().toString()
				+ "/MOFA/Credentials";
		File path1 = new File(path);
		if (!path1.exists()) {
			path1.mkdirs();
		}
		ossPath = userid + "=" + System.currentTimeMillis() + ".jpg";
		File file = new File(path1, ossPath);
		FilePath = file.getPath();
		intent.putExtra("FilePath", FilePath);
		intent.putExtra("from", "BankPayActivity");
		intent.putExtra("proof", "proof");
		startActivityForResult(intent, 1);

	}

	private void Upload(final String name, final String path,
                        final Handler handler) {
		
		Bitmap drawable2 = BitmapFactory.decodeFile(path);
		if (drawable2 == null) {
			ToastUtil.showShort(BankPayActivity.this, "not a photo! please retake!");
			btnUpload.setClickable(true);
			return;
		}
		drawable2.recycle();
		drawable2 = null;

		PutObjectRequest put = new PutObjectRequest("salobkdw", name, path);

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
			public void onProgress(PutObjectRequest request, long currentSize,
                                   long totalSize) {
				Log.i("MOFA", "   currentSize: " + currentSize
						+ " totalSize: " + totalSize);
				uploading = true;
			}
		});

		OSSAsyncTask task = oss.asyncPutObject(put,
				new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
					@Override
					public void onSuccess(PutObjectRequest request,
							PutObjectResult result) {
						Log.i("MOFA", "UploadSuccess:" + path + "/"
								+ name);
						// handler.sendEmptyMessage(0);
						// HttpUtils.doGetAsyn(Config.VIDEO_CORD + "&userid="
						// + userId + "&videoUrl=" + name, handler,
						// Config.CODE_VIDEO);
						// dialog.dismiss();
						File file = new File(path);
						if (file.exists()) {
							file.delete();
							// Log.i("Olava---BankPayActivity",
							// "file --> exist ---> delete");
						}
						uploading = false;
						HttpUtils.doGetAsyn(Config.CONFIRMBM_MONEY_BACK
								+ "&hkqd=" + 1 + "&userid=" + userid + "&p1="
								+ ossPath + "&cgv=" + MD5Util.md5("1" + userid), mHandler,
								Config.CODE_CONFIRMMONEYBACK);
						Log.i("MOFA", "BankPayActivity---执行 --> 还款");
					}

					@Override
					public void onFailure(PutObjectRequest request,
							ClientException clientExcepion,
							ServiceException serviceException) {
						Message msg = Message.obtain();
						msg.what = Config.UPLOAD_FAILED;
						handler.sendMessage(msg);
						uploading = false;
						// 请求异常
						if (clientExcepion != null) {
							// 本地异常如网络异常等
							clientExcepion.printStackTrace();
							ToastUtil.showShort(BankPayActivity.this, BankPayActivity.this.getResources().getString(R.string.host_exception));

						}
						if (serviceException != null) {
							// 服务异常
							Log.e("ErrorCode", serviceException.getErrorCode());
							Log.e("RequestId", serviceException.getRequestId());
							Log.e("HostId", serviceException.getHostId());
							Log.e("RawMessage",
									serviceException.getRawMessage());
							ToastUtil.showShort(BankPayActivity.this, BankPayActivity.this.getResources().getString(R.string.host_exception) + serviceException.getRawMessage());

						}

					}
				});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			bitmap = BitmapFactory.decodeFile(FilePath, null);

			// Bitmap bitmapFront = equalRatioScale(frontPhotoPath, 800,
			// 480);
			if (bitmap == null) {
				ToastUtil.showShort(BankPayActivity.this, BankPayActivity.this.getResources().getString(R.string.retake));

				Log.i("MOFA", "BankPayActivity---凭证照返回为空！");
			} else {
				// ivFront.setImageBitmap(BitmapUtils.compressImage(BitmapUtils.compressImage2(
				// bitmapFront, frontPhotoPath)));
				BitmapTask task = new BitmapTask(
						(RelativeLayout) findViewById(R.id.rl_credentials),
						bitmap, FilePath);
				task.execute();
				startFrameDrawable((ImageView) findViewById(R.id.iv_frame));
				Log.i("MOFA", "BankPayActivity---凭证照获取成功！");
			}
		} else if (resultCode == RESULT_CANCELED) {
			rlCredentials.setBackgroundResource(R.drawable.proof);
			OK = false;
		}
	}

	@Override
	protected void onDestroy() {
		if (bitmap != null) {
			bitmap.recycle();
		}
		super.onDestroy();
	}

	private void startFrameDrawable(ImageView imageview) {
		imageview.setVisibility(View.VISIBLE);
		imageview.setBackgroundResource(R.drawable.lading_lived);
		AnimationDrawable frame = (AnimationDrawable) imageview.getBackground();
		frame.start();
	}

	private class BitmapTask extends AsyncTask<Bitmap, Void, Bitmap> {
		private Bitmap bitmap;
		private String filepath;
		// private final WeakReference imageViewReference;
		private RelativeLayout imageView;

		public BitmapTask(RelativeLayout imageview, Bitmap bitmap,
                          String filepath) {
			this.bitmap = bitmap;
			this.filepath = filepath;
			this.imageView = imageview;
			// imageViewReference = new WeakReference(imageview);
		}

		@Override
		protected Bitmap doInBackground(Bitmap... params) {
			return BitmapUtils.compressImage2(bitmap, filepath);
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			// if (imageViewReference != null && result != null) {
			if (result != null) {
				// final ImageView imageView = (ImageView)
				// imageViewReference.get();
				if (imageView != null) {
					imageView.setBackground(new BitmapDrawable(result));
					findViewById(R.id.iv_frame).setVisibility(View.GONE);
					OK = true;
				} else {
					ToastUtil.showShort(BankPayActivity.this, BankPayActivity.this.getResources().getString(R.string.memory_need_more));

				}
			}
		}
	}

	private void showDialog(String Message) {
		new AlertDialog(BankPayActivity.this)
				.builder()
				.setMsg(Message)
				.setNegativeButton(getResources().getString(R.string.OK),
						new OnClickListener() {
							@Override
							public void onClick(View v) {

							}
						}).show();
	}
}
