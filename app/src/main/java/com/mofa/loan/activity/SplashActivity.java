package com.mofa.loan.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.mofa.loan.R;
import com.mofa.loan.utils.BaseActivity;
import com.mofa.loan.utils.Config;
import com.mofa.loan.utils.ConnectionDetector;
import com.mofa.loan.utils.PermissionUtils;
import com.mofa.loan.utils.StreamUtils;
import com.mofa.loan.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@TargetApi(23)
public class SplashActivity extends BaseActivity {

	protected static final int CODE_UPDATE_DIALOG = 0;
	protected static final int CODE_URL_ERROR = 1;
	protected static final int CODE_NET_ERROR = 2;
	protected static final int CODE_JSON_ERROR = 3;
	protected static final int CODE_ENTER_HOME = 4;// 进入主页面

	private TextView tvVersion;
	private TextView tvProgress;// 下载进度展示

	// 服务器返回的信息
	private String mVersionName;// 版本名
	private int mVersionCode;// 版本号
	private String mDesc;// 版本描述
	private String mDownloadUrl;// 下载地址
	private String mForce;// 是否强制更新 1为不强制
	private int mWitch;// 更新渠道 1为谷歌市场；2为APP内下载

	RelativeLayout rlRoot;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case CODE_UPDATE_DIALOG:
//				showUpdateDailog();
				jumpNextPage();
				break;
			case CODE_URL_ERROR:
				ToastUtil.showShort(SplashActivity.this, getResources()
						.getString(R.string.url_error));

				jumpNextPage();
				break;
			case CODE_NET_ERROR:
				ToastUtil.showShort(SplashActivity.this, getResources()
						.getString(R.string.network_error));

				jumpNextPage();
				break;
			case Config.CODE_ERROR:
				ToastUtil.showShort(SplashActivity.this, "system error");

				break;
			case CODE_JSON_ERROR:
				ToastUtil.showShort(SplashActivity.this, getResources()
						.getString(R.string.data_parsing_error));

				jumpNextPage();
				break;
			case CODE_ENTER_HOME:
				jumpNextPage();
				break;

			default:
				break;
			}
		}
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		// StatConfig.setDebugEnable(true);
		if (!isTaskRoot()) {
			finish();
			return;
		}
		rlRoot = findViewById(R.id.rl_root);
		tvProgress = findViewById(R.id.tv_progress);// 默认隐藏
		// startAnim();
		checkVerson();
	}

	/**
	 * 获取版本名称
	 * 
	 * @return
	 */
	private String getVersionName() {
		PackageManager packageManager = getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(
					getPackageName(), 0);// 获取包的信息

			int versionCode = packageInfo.versionCode;
			String versionName = packageInfo.versionName;

			Log.i("MOFA", "---Splash---" + "versionName=" + versionName
					+ ";versionCode=" + versionCode);

			return versionName;
		} catch (NameNotFoundException e) {
			// 没有找到包名的时候会走此异常
			Log.e("MOFA", e.getMessage());
		}

		return "";
	}

	/**
	 * 获取本地app的版本号
	 * 
	 * @return
	 */
	private int getVersionCode() {
		PackageManager packageManager = getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(
					getPackageName(), 0);// 获取包的信息

			int versionCode = packageInfo.versionCode;
			return versionCode;
		} catch (NameNotFoundException e) {
			// 没有找到包名的时候会走此异常
			Log.e("MOFA", e.getMessage());
		}

		return -1;
	}

	/**
	 * 从服务器获取版本信息进行校验
	 */
	private void checkVerson() {
		final long startTime = System.currentTimeMillis();
		// 启动子线程异步加载数据
		new Thread() {

			@Override
			public void run() {
				Message msg = Message.obtain();
				HttpURLConnection conn = null;
				try {
					// 本机地址用localhost, 但是如果用模拟器加载本机的地址时,可以用ip(10.0.2.2)来替换
					URL url = new URL(Config.URL + Config.UPDATE_CODE);
					conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");// 设置请求方法
					conn.setConnectTimeout(5000);// 设置连接超时
					conn.setReadTimeout(5000);// 设置响应超时, 连接上了,但服务器迟迟不给响应
					conn.connect();// 连接服务器

					int responseCode = conn.getResponseCode();// 获取响应码
					if (responseCode == 200) {
						InputStream inputStream = conn.getInputStream();
						String result = StreamUtils.readFromStream(inputStream);
						Log.i("MOFA", "---Splash---" + "网络返回:" + result);

						// 解析json
						JSONObject jo = new JSONObject(result);
						mVersionName = jo.getString("versionName");
						mVersionCode = jo.getInt("versionCode");
						mWitch = jo.getInt("witch");
						mDesc = jo.getString("description");
						mForce = jo.optString("Force");// 强制更新标志位；1为不强制，其余为强制
						mDownloadUrl = jo.getString("downloadUrl");
						// Log.i("MOFA","---Splash---"+"版本描述:" + mDesc);

						if (mVersionCode > getVersionCode()) {// 判断是否有更新
							// 服务器的VersionCode大于本地的VersionCode
							// 说明有更新, 弹出升级对话框
							msg.what = CODE_UPDATE_DIALOG;
						} else {
							// 没有版本更新
							msg.what = CODE_ENTER_HOME;
						}
					}
				} catch (MalformedURLException e) {
					// url错误的异常
					msg.what = CODE_URL_ERROR;
					Log.e("MOFA", e.getMessage());
				} catch (IOException e) {
					// 网络错误异常
					msg.what = CODE_NET_ERROR;
					Log.e("MOFA", e.getMessage());
				} catch (JSONException e) {
					// json解析失败
					msg.what = CODE_JSON_ERROR;
					Log.e("MOFA", e.getMessage());
				} finally {
					long endTime = System.currentTimeMillis();
					long timeUsed = endTime - startTime;// 访问网络花费的时间
					if (timeUsed < 2000) {
						// 强制休眠一段时间,保证闪屏页展示2秒钟
						try {
							Thread.sleep(2000 - timeUsed);
						} catch (InterruptedException e) {
							Log.e("MOFA", e.getMessage());
						}
					}

					mHandler.sendMessage(msg);
					if (conn != null) {
						conn.disconnect();// 关闭网络连接
					}

				}
			}
		}.start();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
		if (grantResults.length > 0
				&& grantResults[0] == PackageManager.PERMISSION_GRANTED
				&& grantResults[1] == PackageManager.PERMISSION_GRANTED) {
			// 权限申请成功
			download();
		} else {
			ToastUtil.showShort(
					SplashActivity.this,
					getResources().getString(
							R.string.EXTERNAL_STORAGE_permission));
			Log.i("MOFA", "权限拒绝：初始页下载时读写SD卡权限");
		}
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	/**
	 * 升级对话框
	 */
	protected void showUpdateDailog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getResources().getString(R.string.new_version)
				+ mVersionName);
		builder.setMessage(mDesc);
		// builder.setCancelable(false);//不让用户取消对话框, 用户体验太差,尽量不要用
		builder.setPositiveButton(getResources().getString(R.string.update),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Log.i("MOFA", "---Splash---" + "立即更新");
						if (mWitch == 1) {
							if (isAvilible(SplashActivity.this, "com.android.vending")) {
								launchAppDetail("com.mofa.loan", "com.android.vending");
							} else {
								Uri uri = Uri.parse("http://play.google.com/store/apps/details?id=com.mofa.loan");
								Intent intent = new Intent(Intent.ACTION_VIEW, uri);
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(intent); 
							}
						} else {

							if (Build.VERSION.SDK_INT >= 23) {
								if (SplashActivity.this
										.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
									// 申请权限 第二个参数是一个 数组 说明可以同时申请多个权限
									SplashActivity.this
											.requestPermissions(
													new String[] {
															android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
															android.Manifest.permission.READ_EXTERNAL_STORAGE },
													PermissionUtils.STRONGE_PERMISSION);
								} else {// 已授权
									download();
								}
							} else {
								download();
							}

						}
					}
				});

		builder.setNegativeButton(getResources().getString(R.string.later),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if ("1".equals(mForce)) {
							jumpNextPage();
						}
						finish();
					}
				});

		// 设置取消的监听, 用户点击返回键时会触发
		builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				if ("1".equals(mForce)) {
					jumpNextPage();
				}
				finish();
			}
		});

		builder.show();
	}

	/*
	 * 开启动画
	 */
	private void startAnim() {

		// 动画集合
		AnimationSet set = new AnimationSet(false);
		// 旋转动画
		RotateAnimation rotate = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		rotate.setDuration(2000);// 动画时间
		rotate.setFillAfter(true);// 保持动画状态

		// 缩放动画
		ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		scale.setDuration(2000);// 动画时间
		scale.setFillAfter(true);// 保持动画状态

		// 渐变动画
		AlphaAnimation alpha = new AlphaAnimation(0, 1);
		scale.setDuration(2000);// 动画时间
		scale.setFillAfter(true);// 保持动画状态

		set.addAnimation(rotate);
		set.addAnimation(scale);
		set.addAnimation(alpha);

		// 设置动画监听
		set.setAnimationListener(new AnimationListener() {

			// 动画执行结束
			@Override
			public void onAnimationEnd(Animation animation) {

				jumpNextPage();
				checkVerson();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationStart(Animation animation) {

			}

		});

		rlRoot.startAnimation(set);

	}

	// 跳转下一个页面
	private void jumpNextPage() {
		// 判断之前有没有显示过新手引导页
		ConnectionDetector cd = new ConnectionDetector(SplashActivity.this);
		SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
		boolean userGuide = sp.getBoolean("is_user_guide_showed", false);
		String userLogin = sp.getString("phone", "");

		if (!userGuide) {
			startActivity(new Intent(SplashActivity.this, GuideAcytivity.class));
			finish();
			return;
		}
		if (cd.isConnectingToInternet2()) {
			if (userLogin == "") {
				startActivity(new Intent(SplashActivity.this,
						LoginActivity.class));
			} else {
				startActivity(new Intent(SplashActivity.this,
						IndexActivity.class));
			}

		} else {
			startActivity(new Intent(SplashActivity.this, LoginActivity.class));
			finish();
		}
		finish();
	}

	// 如果用户取消安装的话,回调此方法
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// jumpNextPage();
		finish();
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 下载apk文件
	 */
	protected void download() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {

			tvProgress.setVisibility(View.VISIBLE);// 显示进度

			String target = Environment.getExternalStorageDirectory()
					+ "/updateOlava.apk";
			// XUtils
			HttpUtils utils = new HttpUtils();
			utils.download(mDownloadUrl, target, new RequestCallBack<File>() {

				// 下载文件的进度
				@Override
				public void onLoading(long total, long current,
						boolean isUploading) {
					super.onLoading(total, current, isUploading);
					tvProgress.setText(getResources().getString(
							R.string.download_progress2)
							+ current * 100 / total + "%");
				}

				// 下载成功
				@Override
				public void onSuccess(ResponseInfo<File> arg0) {
					Log.i("MOFA", "---Splash---" + "下载成功!");
					// 跳转到系统下载页面
					// Intent intent = new Intent(Intent.ACTION_VIEW);
					// intent.addCategory(Intent.CATEGORY_DEFAULT);
					// intent.setDataAndType(PermissionUtils.getUriForFile(SplashActivity.this,arg0.result),
					// "application/vnd.android.package-archive");
					// // startActivity(intent);
					// startActivityForResult(intent, 0);// 如果用户取消安装的话,
					// // 会返回结果,回调方法onActivityResult
					SharedPreferences sp = getSharedPreferences("config",
							MODE_PRIVATE);
					SharedPreferences.Editor editor = sp.edit();// 获取Editor
					editor.clear();
					editor.commit();
					Log.i("MOFA", "清空config数据");
					if (Build.VERSION.SDK_INT >= 24) {// 判读版本是否在7.0以上
						Uri apkUri = FileProvider.getUriForFile(
								SplashActivity.this,
								"com.mofa.loan.fileprovider", arg0.result);// 在AndroidManifest中的android:authorities值
						Intent install = new Intent(Intent.ACTION_VIEW);
						install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);// 添加这一句表示对目标应用临时授权该Uri所代表的文件
						install.setDataAndType(apkUri,
								"application/vnd.android.package-archive");
						startActivity(install);
					} else {
						Intent install = new Intent(Intent.ACTION_VIEW);
						install.setDataAndType(Uri.fromFile(arg0.result),
								"application/vnd.android.package-archive");
						install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(install);
					}
				}

				// 下载失败
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					ToastUtil.showShort(SplashActivity.this, getResources()
							.getString(R.string.download_failed));

				}
			});
		} else {
			ToastUtil.showShort(SplashActivity.this,
					getResources().getString(R.string.no_sdcard));

		}
	}
	
	/**
	 * 启动到应用商店app详情界面
	 *
	 * @param appPkg    目标App的包名
	 * @param marketPkg 应用商店包名 ,如果为""则由系统弹出应用商店列表供用户选择,否则调转到目标市场的应用详情界面，某些应用商店可能会失败
	 */
	public void launchAppDetail(String appPkg, String marketPkg) {
	    try {
	        if (TextUtils.isEmpty(appPkg)) return;

	        Uri uri = Uri.parse("market://details?id=" + appPkg);
	        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
	        if (!TextUtils.isEmpty(marketPkg)) {
	            intent.setPackage(marketPkg);
	        }
	        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        startActivity(intent);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	/** 
	 * 判断相对应的APP是否存在 
	 *  
	 * @param context 
	 * @param packageName(包名)(若想判断QQ，则改为com.tencent.mobileqq，若想判断微信，则改为com.tencent.mm) 
	 * @return 
	 */  
	public static boolean isAvilible(Context context, String packageName) {
	    PackageManager packageManager = context.getPackageManager();
	  
	    //获取手机系统的所有APP包名，然后进行一一比较  
	    List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
	    for (int i = 0; i < pinfo.size(); i++) {  
	        if (pinfo.get(i).packageName
	                .equalsIgnoreCase(packageName))  
	            return true;  
	    }  
	    return false;  
	}  

	@Override
	public void processMessage(Message message) {

	}

}
