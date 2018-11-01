package com.mofa.loan.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("NewApi")
public class IDCardActivity extends BaseActivity implements OnClickListener {

	private TextView tvTitle;
	private TextView tvGuide1;
	private TextView tvGuide2;
	private TextView tvGuide3;
	private ImageView ivTakePhoto;
	private RelativeLayout rlCard;
	private Button btnNext;

	private SharedPreferences sp;

	private String from;

	/**
	 * 来自学生认证
	 */
	private String studentschoolname;
	private String studentaddress;
	private String studentclassname;
	private String studentstudentid;
	private String studenttime;

	/**
	 * 来自身份认证
	 */
	private String baseusername;
	private String baseIDnumber;
	private String baseaddress;
	private String baseaddressnow;
	private String basegender;
	private String baseemail;
	private String baseboth;
	private String basephonetype;

	/**
	 * 标志当前步骤： 1001：身份证正面 1002：身份证反面 1003：身份证手持 1004：学生证反面 1005：学生证反面
	 */
	private int nowStatus = 0;

	private String userid;
	private String ossStudentFrontPhotoPath;
	private String ossStudentHandholdPhotoPath;
	private String ossIDFrontPhotoPath;
	private String ossIDBackPhotoPath;
	private String ossIDHoldheldPhotoPath;
	private String photoPath;
	private String p1;
	private String p2;
	private String p3;
	private String s1;
	private String s3;

	private Bitmap bitmap;

	private OSSClient oss;

	private boolean isFinished = false;
	private boolean isExit = false;
	private boolean uploading = false;

	private TimerTask timeTask;
	private Timer timer = new Timer();

	private Handler handler = new Handler() {
		@SuppressWarnings("deprecation")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Config.UPLOAD_FAILED:
				ToastUtil.showShort(IDCardActivity.this, getResources()
						.getString(R.string.upload_failture));
				isFinished = false;
				if (nowStatus == 1001) {
					rlCard.setBackgroundResource(R.drawable.idcardfront);
					Log.i("MOFA", "IDCard---身份正面上传失败！");
				} else if (nowStatus == 1002) {
					rlCard.setBackgroundResource(R.drawable.idcardback);
					Log.i("MOFA", "IDCard---身份反面上传失败！");
				} else if (nowStatus == 1003) {
					rlCard.setBackgroundResource(R.drawable.handholdid);
					Log.i("MOFA", "IDCard---身份手持上传失败！");
				} else if (nowStatus == 1004) {
					rlCard.setBackgroundResource(R.drawable.studentfront);
					Log.i("MOFA", "IDCard---学生正面上传失败！");
				} else if (nowStatus == 1005) {
					rlCard.setBackgroundResource(R.drawable.handholdstudent);
					Log.i("MOFA", "IDCard---学生手持上传失败！");
				}
				btnNext.setClickable(true);
				break;
			case Config.UPLOAD_SUCCESSED:
				if (nowStatus == 1001) {
					tvTitle.setText("Xác minh thông tin cá nhân");
					tvGuide1.setText("Xác minh thẻ CMND");
					tvGuide2.setText("Mặt sau CMND");
					tvGuide3.setText("Khi chụp, vui lòng đặt thẻ vào đúng khung chụp");
					rlCard.setBackgroundResource(R.drawable.idcardback);

					nowStatus = 1002;
					isFinished = false;
					Log.i("MOFA", "IDCard---身份正面上传成功！");
				} else if (nowStatus == 1002) {
					tvTitle.setText("Xác minh thông tin cá nhân");
					tvGuide1.setText("Xác minh thẻ CMND");
					tvGuide2.setText("Hình cầm CMND");
					tvGuide3.setText("Vui lòng cầm CMND như hình dưới");
					rlCard.setBackgroundResource(R.drawable.handholdid);

					nowStatus = 1003;
					isFinished = false;
					Log.i("MOFA", "IDCard---身份反面上传成功！");
				} else if (nowStatus == 1004) {
					tvTitle.setText("Xác minh trường học");
					tvGuide1.setText("Xác minh thẻ sinh viên");
					tvGuide2.setText("Hình tự cầm thẻ sinh viên");
					tvGuide3.setText("Vui lòng cầm thẻ sinh viên như hình dưới");
					rlCard.setBackgroundResource(R.drawable.handholdstudent);

					nowStatus = 1005;
					isFinished = false;
					Log.i("MOFA", "IDCard---学生正面上传成功！");
				}
				btnNext.setClickable(true);
				break;
			case Config.CODE_URL_ERROR:
				ToastUtil.showShort(IDCardActivity.this, getResources()
						.getString(R.string.url_error));
				Log.i("MOFA",
						"IDCard---"
								+ getResources().getString(R.string.url_error));
				break;
			case Config.CODE_NET_ERROR:
				ToastUtil.showShort(IDCardActivity.this, getResources()
						.getString(R.string.network_error));
				Log.i("MOFA",
						"IDCard---"
								+ getResources().getString(
										R.string.network_error));
				break;
			case Config.CODE_ERROR:
				ToastUtil.showShort(IDCardActivity.this, "system error");
				break;
			case 2000:
				ToastUtil.showShort(IDCardActivity.this, (String) msg.obj);
				Log.i("MOFA", "IDCard---" + msg.obj);
				break;

			case Config.CODE_GETSHENFEN:
				String reString = msg.obj.toString();
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(reString);
					int error = jsonObject.optInt("error");
					if (0 == error) {
						ToastUtil.showShort(IDCardActivity.this, getResources()
								.getString(R.string.upload_successed));
						sp = getSharedPreferences("config", MODE_PRIVATE);
						Editor editor = sp.edit();
						editor.putInt("isshenfen", 1);
						editor.commit();
						int isyhbd = sp.getInt("isyhbd", 0);
						int isjob = sp.getInt("isjob", 0);
						int islianxi = sp.getInt("islianxi", 0);
						int isschool = sp.getInt("isschool", 0);
//						int isfacebook = sp.getInt("isfacebook", 0);
						int profession = sp.getInt("profession", 0);
						if (isyhbd == 0) {
							Intent intent = new Intent(IDCardActivity.this,
									BindCard3Activity.class);
							startActivity(intent);
						} else if (islianxi == 0) {
							Intent intent = new Intent(IDCardActivity.this,
									RelationInfo2Activity.class);
							startActivity(intent);
						} else if ((profession == 2 && isjob == 0) || (profession == 1 && isschool == 0)) {
							Intent intent = new Intent(IDCardActivity.this,
									WorkInfo3Activity.class);
							startActivity(intent);
//						} else if (profession == 1 && isschool == 0) {
//							Intent intent = new Intent(IDCardActivity.this,
//									StudentInfo2Activity.class);
//							startActivity(intent);
//						}
//						else if (isfacebook == 0) {
//							startActivity(new Intent(IDCardActivity.this,
//									FacebookAcytivity.class));
						} else {
							Intent intent = new Intent(IDCardActivity.this,
									IndexActivity.class);
							intent.putExtra("id", 11);
							startActivity(intent);
						}
						finish();
					} else {
						ToastUtil.showShort(IDCardActivity.this,
								jsonObject.optString("msg"));

					}
				} catch (JSONException e) {
					Log.e("MOFA", e.getMessage());
				}
				break;
			case Config.CODE_GETSCHOOL:
				String reString2 = msg.obj.toString();
				Log.i("MOFA", "IDCard---" + reString2);
				JSONObject jsonObject2;
				try {
					jsonObject2 = new JSONObject(reString2);
					int error = jsonObject2.optInt("error");
					Log.i("MOFA", "IDCard---" + "error:" + error);
					if (0 == error) {
						ToastUtil.showShort(IDCardActivity.this, getResources()
								.getString(R.string.upload_successed));
						SharedPreferences sp = getSharedPreferences("config",
								MODE_PRIVATE);
						Editor editor = sp.edit();
						editor.putInt("isschool", 1);
						editor.commit();
//						int isfacebook = sp.getInt("isfacebook", 0);
//						if (isfacebook == 0) {
//							startActivity(new Intent(IDCardActivity.this,
//									FacebookAcytivity.class));
//						} else {
//							Intent intent = new Intent(IDCardActivity.this,
//									IndexActivity.class);
//							intent.putExtra("id", 1);
//							startActivity(intent);
//						}
						finish();
					} else {
						ToastUtil.showShort(IDCardActivity.this,
								jsonObject2.optString("msg"));
					}
				} catch (JSONException e) {
					Log.e("MOFA", e.getMessage());
				}
				break;
			default:
				break;
			}

		}
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_id_card);
		super.onCreate(savedInstanceState);
		Log.i("MOFA", "IDCard---" + "onCreate");
		initview();
		initdata();
		initOSS();
	}

	private long timeIn;
	private long timeOut;

	@Override
	public void onStart() {
		super.onStart();
		timeIn = System.currentTimeMillis();
		Log.i("MOFA", "IDCard---onStart:" + timeIn);
	}

	@Override
	public void onStop() {
		super.onStop();
		timeOut = System.currentTimeMillis();
		Log.i("MOFA", "IDCard---onStop:" + timeOut);
		Log.i("MOFA", "IDCard---Show:" + (timeOut - timeIn));
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

			Intent intent1 = new Intent(IDCardActivity.this,
					IndexActivity.class);
			intent1.putExtra("id", 1);
			startActivity(intent1);
			finish();
			Log.i("MOFA", "权限拒绝：上传照片时拍摄照片和存储权限");
		}
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	private void initdata() {
		from = getIntent().getStringExtra("from");
		if ("base".equalsIgnoreCase(from)) {
			baseusername = getIntent().getStringExtra("username");
			baseIDnumber = getIntent().getStringExtra("IDnumber");
			baseaddress = getIntent().getStringExtra("address");
			baseaddressnow = getIntent().getStringExtra("addressnow");
			basegender = getIntent().getStringExtra("gender");
			baseemail = getIntent().getStringExtra("email");
			baseboth = getIntent().getStringExtra("both");
			basephonetype = getIntent().getStringExtra("phonetype");

			tvTitle.setText("Xác minh thông tin cá nhân");
			tvGuide1.setText("Xác minh thẻ CMND");
			tvGuide2.setText("Mặt trước CMND");
			tvGuide3.setText("Khi chụp, vui lòng đặt thẻ vào đúng khung chụp");
			rlCard.setBackgroundResource(R.drawable.idcardfront);

			nowStatus = 1001;
			isFinished = false;
			sp = getSharedPreferences("config", 0x0000);
			p1 = sp.getString("p1", null);
			p2 = sp.getString("p2", null);
			p3 = sp.getString("p3", null);
		} else if ("student".equalsIgnoreCase(from)) {
			studentschoolname = getIntent().getStringExtra("schoolname");
			studentaddress = getIntent().getStringExtra("address");
			studentclassname = getIntent().getStringExtra("classname");
			studentstudentid = getIntent().getStringExtra("studentid");
			studenttime = getIntent().getStringExtra("time");

			tvTitle.setText("Xác minh trường học");
			tvGuide1.setText("Xác minh thẻ sinh viên");
			tvGuide2.setText("Mặt trước thẻ sinh viên");
			tvGuide3.setText("Khi chụp, vui lòng đặt thẻ vào đúng khung chụp");
			rlCard.setBackgroundResource(R.drawable.studentfront);

			nowStatus = 1004;
			isFinished = false;
			sp = getSharedPreferences("config", 0x0000);
			s1 = sp.getString("s1", null);
			s3 = sp.getString("s3", null);

			findViewById(R.id.ll_veifingid).setVisibility(View.GONE);
			findViewById(R.id.ll_veifingstudent).setVisibility(View.VISIBLE);
		}

	}

	private void initview() {
		sp = getSharedPreferences("config", 0x0000);
		int profession = sp.getInt("profession", 0);
		userid = sp.getString("userid", "");
		if (profession == 1) {
			ImageView ivProfession = findViewById(R.id.iv_profession);
			ivProfession.setImageResource(R.drawable.unveifiedstudent);
		}
		tvTitle = findViewById(R.id.tv_title);
		tvGuide1 = findViewById(R.id.tv_guide1);
		tvGuide2 = findViewById(R.id.tv_guide2);
		tvGuide3 = findViewById(R.id.tv_guide3);
		ivTakePhoto = findViewById(R.id.iv_takephoto);
		ivTakePhoto.setOnClickListener(this);
		rlCard = findViewById(R.id.rl_card);
		btnNext = findViewById(R.id.btn_next);
		btnNext.setOnClickListener(this);
		findViewById(R.id.back).setOnClickListener(this);
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

	private void takePhoto() {
		// Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		Intent intent = new Intent(IDCardActivity.this,
				TakePhoto2Activity.class);
		String path = "";
		File file = null;
		File path1 = null;
		if (nowStatus == 1001) {
			if (TextUtils.isEmpty(p1)) {
				ossIDFrontPhotoPath = userid + "=" + System.currentTimeMillis()
						+ ".jpg";
			} else {
				ossIDFrontPhotoPath = p1;
			}
			path = Environment.getExternalStorageDirectory().toString()
					+ "/MOFA/Identity";
			path1 = new File(path);
			if (!path1.exists()) {
				path1.mkdirs();
			}
			file = new File(path1, ossIDFrontPhotoPath);
			intent.putExtra("idfront", "idfront");
		} else if (nowStatus == 1002) {
			if (TextUtils.isEmpty(p2)) {
				ossIDBackPhotoPath = userid + "=" + System.currentTimeMillis()
						+ ".jpg";
			} else {
				ossIDBackPhotoPath = p2;
			}
			path = Environment.getExternalStorageDirectory().toString()
					+ "/MOFA/Identity";
			path1 = new File(path);
			if (!path1.exists()) {
				path1.mkdirs();
			}
			file = new File(path1, ossIDBackPhotoPath);
			intent.putExtra("idback", "idback");
		} else if (nowStatus == 1003) {
			if (TextUtils.isEmpty(p3)) {
				ossIDHoldheldPhotoPath = userid + "="
						+ System.currentTimeMillis() + ".jpg";
			} else {
				ossIDHoldheldPhotoPath = p3;
			}
			path = Environment.getExternalStorageDirectory().toString()
					+ "/MOFA/Identity";
			path1 = new File(path);
			if (!path1.exists()) {
				path1.mkdirs();
			}
			file = new File(path1, ossIDHoldheldPhotoPath);

			intent.putExtra("idhandhold", "idhandhold");
		} else if (nowStatus == 1004) {
			// if (TextUtils.isEmpty(s1)) {
			ossStudentFrontPhotoPath = userid + "="
					+ System.currentTimeMillis() + ".jpg";
			// } else {
			// ossStudentFrontPhotoPath = s1;
			// }
			path = Environment.getExternalStorageDirectory().toString()
					+ "/MOFA/Student";
			path1 = new File(path);
			if (!path1.exists()) {
				path1.mkdirs();
			}
			file = new File(path1, ossStudentFrontPhotoPath);
			intent.putExtra("studentfront", "studentfront");
		} else if (nowStatus == 1005) {
			// if (TextUtils.isEmpty(s3)) {
			ossStudentHandholdPhotoPath = userid + "="
					+ System.currentTimeMillis() + ".jpg";
			// } else {
			// ossStudentHandholdPhotoPath = s3;
			// }
			path = Environment.getExternalStorageDirectory().toString()
					+ "/MOFA/Student";
			path1 = new File(path);
			if (!path1.exists()) {
				path1.mkdirs();
			}
			file = new File(path1, ossStudentHandholdPhotoPath);

			intent.putExtra("studenthandhold", "studenthandhold");
		}
		if (file.exists()) {
			file.delete();
		}
		try {
			file.createNewFile();
		} catch (IOException e) {
			Log.e("MOFA", e.getMessage());
		} finally {
			if (!file.exists()) {
				ToastUtil.showShort(IDCardActivity.this, "create file failed");
			}
		}
		photoPath = file.getPath();
		if (TextUtils.isEmpty(photoPath) || "null".equalsIgnoreCase(photoPath)) {
			ToastUtil.showShort(IDCardActivity.this, "create file failed");
			return;
		}
		intent.putExtra("FilePath", photoPath);
		startActivityForResult(intent, nowStatus);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == 1001) {

			} else if (requestCode == 1002) {

			} else if (requestCode == 1003) {

			} else if (requestCode == 1004) {

			} else if (requestCode == 1005) {

			}
			bitmap = BitmapFactory.decodeFile(photoPath, null);
			if (bitmap == null) {
				ToastUtil.showShort(IDCardActivity.this, getResources()
						.getString(R.string.retake));

				Log.i("MOFA", "IDCard---" + "照片返回为空！");
			} else {
				BitmapTask task = new BitmapTask(rlCard, bitmap, photoPath);
				task.execute();
			}
		} else if (resultCode == RESULT_CANCELED) {
			if (requestCode == 1001) {
				rlCard.setBackgroundResource(R.drawable.idcardfront);
			} else if (requestCode == 1002) {
				rlCard.setBackgroundResource(R.drawable.idcardback);
			} else if (requestCode == 1003) {
				rlCard.setBackgroundResource(R.drawable.handholdstudent);
			} else if (requestCode == 1004) {
				rlCard.setBackgroundResource(R.drawable.studentfront);
			} else if (requestCode == 1005) {
				rlCard.setBackgroundResource(R.drawable.handholdid);
			}
			isFinished = false;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void processMessage(Message message) {

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
					isFinished = true;
				} else {
					ToastUtil.showShort(IDCardActivity.this, getResources()
							.getString(R.string.memory_need_more));

				}
			}
		}
	}

	private void Upload(final String name, final String path,
                        final Handler handler) {

		Bitmap drawable2 = BitmapFactory.decodeFile(path);
		if (drawable2 == null) {
			ToastUtil.showShort(IDCardActivity.this,
					"not a photo! please retake!");
			btnNext.setClickable(true);
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
				Log.i("MOFA", "currentSize: " + currentSize
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
						uploading = false;
						// File file = new File(path);
						// if (file.exists()) {
						// file.delete();
						// // Log.i("MOFA","IDCard---"+
						// // "file --> exist ---> delete");
						// }
						if (nowStatus == 1001 || nowStatus == 1002
								|| nowStatus == 1004) {
							Message msg = Message.obtain();
							msg.what = Config.UPLOAD_SUCCESSED;
							handler.sendMessage(msg);
						} else if (nowStatus == 1003) {
							// 全部上传完成！
							try {
								HttpUtils.doGetAsyn(
										Config.GETSHENFEN
												+ "&userid="
												+ userid
												+ "&numberId="
												+ baseIDnumber
												+ "&age="
												+ baseboth
												+ "&homeaddress="
												+ URLEncoder.encode(
														baseaddress, "UTF-8")
												+ "&address="
												+ URLEncoder
														.encode(baseaddressnow,
																"UTF-8")
												+ "&phonetype="
												+ URLEncoder.encode(
														basephonetype, "UTF-8")
												+ "&sex="
												+ basegender
												+ "&email="
												+ URLEncoder.encode(baseemail,
														"UTF-8")
												+ "&name="
												+ URLEncoder.encode(
														baseusername, "UTF-8")
												+ "&p1="
												+ ossIDFrontPhotoPath
												+ "&p2="
												+ ossIDBackPhotoPath
												+ "&p3="
												+ ossIDHoldheldPhotoPath
												+ "&kjhg="
												+ MD5Util.md5(userid
														+ basegender), handler,
										Config.CODE_GETSHENFEN);
							} catch (UnsupportedEncodingException e) {
								Log.e("MOFA", e.getMessage());
							}
							Log.i("MOFA", "IDCard---身份手持上传成功！");
							Log.i("MOFA", "IDCard---" + "执行 --> getshenfen");
						} else if (nowStatus == 1005) {
							try {
								HttpUtils.doGetAsyn(
										Config.GETSCHOOL
												+ "&userid="
												+ userid
												+ "&schoolName="
												+ URLEncoder.encode(
														studentschoolname,
														"UTF-8")
												+ "&className="
												+ URLEncoder.encode(
														studentclassname,
														"UTF-8")
												+ "&address="
												+ URLEncoder
														.encode(studentaddress,
																"UTF-8")
												+ "&time="
												+ studenttime
												+ "&stuId="
												+ studentstudentid
												+ "&p1="
												+ ossStudentFrontPhotoPath
												+ "&p3="
												+ ossStudentHandholdPhotoPath
												+ "&mu="
												+ MD5Util.md5(studentstudentid
														+ userid), handler,
										Config.CODE_GETSCHOOL);
							} catch (UnsupportedEncodingException e) {
								Log.e("MOFA", e.getMessage());
							}
							Log.i("MOFA", "IDCard---学生手持上传成功！");
							Log.i("MOFA", "IDCard---" + "执行 --> getschool");
						}
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
							ToastUtil.showShort(
									IDCardActivity.this,
									getResources().getString(
											R.string.host_exception));

						}
						if (serviceException != null) {
							// 服务异常
							Log.e("ErrorCode", serviceException.getErrorCode());
							Log.e("RequestId", serviceException.getRequestId());
							Log.e("HostId", serviceException.getHostId());
							Log.e("RawMessage",
									serviceException.getRawMessage());
							ToastUtil.showShort(
									IDCardActivity.this,
									getResources().getString(
											R.string.service_exception)
											+ serviceException.getRawMessage());

						}

					}
				});

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			Intent intent1 = new Intent(IDCardActivity.this,
					IndexActivity.class);
			intent1.putExtra("id", 1);
			startActivity(intent1);
			finish();
			break;

		case R.id.iv_takephoto:
			if (Build.VERSION.SDK_INT >= 23) {
				if (this.checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
						|| this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
					// 申请权限 第二个参数是一个 数组 说明可以同时申请多个权限
					this.requestPermissions(
							new String[] {
									android.Manifest.permission.CAMERA,
									android.Manifest.permission.WRITE_EXTERNAL_STORAGE },
							PermissionUtils.CAMERA_PERMISSION);
				} else {// 已授权
					takePhoto();
				}
			} else {
				takePhoto();
			}
			break;

		case R.id.btn_next:
			if (uploading) {
				ToastUtil.showShort(IDCardActivity.this, getResources()
						.getString(R.string.uploading));

				return;
			}
			if (!isFinished) {
				ToastUtil.showShort(IDCardActivity.this, "no photo!");

				return;
			}
			if (nowStatus == 1001) {
				Upload(ossIDFrontPhotoPath, photoPath, handler);
			} else if (nowStatus == 1002) {
				Upload(ossIDBackPhotoPath, photoPath, handler);
			} else if (nowStatus == 1003) {
				Upload(ossIDHoldheldPhotoPath, photoPath, handler);
			}
			btnNext.setClickable(false);
			break;

		default:
			break;
		}
	}

	// 监听返回键是否退出
	@Override
	public void onBackPressed() {
		if (isExit) {
			Intent intent1 = new Intent(IDCardActivity.this,
					IndexActivity.class);
			intent1.putExtra("id", 1);
			startActivity(intent1);
			finish();
		} else {
			isExit = true;
			ToastUtil.showShort(IDCardActivity.this,
					getResources().getString(R.string.warm_reclick_to_exit));

			timeTask = new TimerTask() {

				@Override
				public void run() {
					isExit = false;
				}
			};
			timer.schedule(timeTask, 3000);
		}
	}

}
