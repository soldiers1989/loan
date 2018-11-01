package com.mofa.loan.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.mofa.loan.R;
import com.mofa.loan.utils.Config;
import com.mofa.loan.utils.HttpUtils;
import com.mofa.loan.utils.MD5Util;
import com.mofa.loan.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

public class ShareActivity extends Activity implements OnClickListener {
	
	private int shareZalo = 0;
	private String userid = "";

	private ResolveInfo infoFacebook;
	private ResolveInfo infoZalo;

	private android.app.AlertDialog phoneDialog;

	private ShareDialog shareFacebook;
	private CallbackManager callBackManager;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			String reString = msg.obj.toString();
			switch (msg.what) {
			case Config.CODE_HONGBAO:
				try {
					JSONObject jsonObject = new JSONObject(reString);
					String error = jsonObject.getString("error");
					String message = jsonObject.getString("msg");
					ToastUtil.showShort(ShareActivity.this, message);
					if ("0".equalsIgnoreCase(error)) {
						
					} else {
						
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case Config.CODE_URL_ERROR:
				ToastUtil.showShort(ShareActivity.this, getResources()
						.getString(R.string.url_error));

				break;
			case Config.CODE_ERROR:
				ToastUtil.showShort(ShareActivity.this, "system error");
				
				break;
			case Config.CODE_NET_ERROR:
				ToastUtil.showShort(ShareActivity.this, getResources()
						.getString(R.string.network_error));

				break;
			default:
				break;
			}
		}
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_share);
		super.onCreate(savedInstanceState);
		SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
		userid = sp.getString("userid", "");
		Log.i("MOFA", "ShareActivity---onCreate");
		initview();
	}

	private void initview() {
		TextView tvTitle = findViewById(R.id.title_txt_center);
		tvTitle.setText("Hoạt động khuyến mãi");
		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.btn_share).setOnClickListener(this);
	}

	private long timeIn;
	private long timeOut;

	@Override
	protected void onStart() {
		super.onStart();
		timeIn = System.currentTimeMillis();
		Log.i("MOFA", "ShareActivity---onStart:" + timeIn);
	}

	@Override
	protected void onStop() {
		super.onStop();
		timeOut = System.currentTimeMillis();
		Log.i("MOFA", "ShareActivity---onStop:" + timeOut);
		Log.i("MOFA", "ShareActivity---Show:" + (timeOut - timeIn));
	}
	
	@Override
	protected void onResume() {
		if (shareZalo == 1) {
			Log.i("MOFA", "Zalo分享成功！");
			HttpUtils.doGetAsyn(Config.SHAREHONGBAO + "&userid=" + userid + "&fhjg=" + MD5Util.md5(userid), mHandler, Config.CODE_HONGBAO);
		}
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;

		case R.id.btn_share:
			getShareActivities2();
			phoneDialog = new android.app.AlertDialog.Builder(
					ShareActivity.this).create();
			LayoutInflater lay = LayoutInflater.from(ShareActivity.this);
			final View inflate = lay.inflate(R.layout.layout_share_dialog,
                    null);
			LinearLayout llShare = inflate
					.findViewById(R.id.ll_share);
			LinearLayout llFacebook = inflate
					.findViewById(R.id.ll_facebook);
			llFacebook.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// share(infoFacebook);
					phoneDialog.dismiss();
					shareFacebook = new ShareDialog(ShareActivity.this);
					callBackManager = CallbackManager.Factory.create();
					shareFacebook.registerCallback(callBackManager,
							new FacebookCallback<Sharer.Result>() {

								@Override
								public void onError(FacebookException error) {
									Log.e("MOFA", "Facebook分享失败：" + error.toString());
									ToastUtil.showShort(ShareActivity.this, "share failed：" + error.toString());
									error.printStackTrace();
								}

								@Override
								public void onCancel() {
									Log.i("MOFA", "Facebook取消分享！");
								}

								@Override
								public void onSuccess(Sharer.Result result) {
									Log.i("MOFA", "Facebook分享成功！");
									SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
									userid = sp.getString("userid", "");
									HttpUtils.doGetAsyn(Config.SHAREHONGBAO + "&userid=" + userid + "&fhjg=" + MD5Util.md5(userid), mHandler, Config.CODE_HONGBAO);
								}
							});
					if (ShareDialog.canShow(ShareLinkContent.class)) {
						ShareLinkContent linkContent = new ShareLinkContent.Builder()
								.setContentTitle("Olava Vay tiền online nhanh")
//								.setImageUrl(Uri.parse("http://192.168.0.113/page/images/lld.png"))
								.setContentDescription("Chia sẻ nhận ngay phiếu khuyến mãi")
								.setContentUrl(
										Uri.parse("http://olava.com.vn/?tgid=" + userid))
								.build();
						shareFacebook.show(linkContent);
						Log.i("MOFA", "调用facebook分享");
					} else {
						ToastUtil.showShort(ShareActivity.this, "未安装facebook");
					}

				}
			});
			LinearLayout llZalo = inflate
					.findViewById(R.id.ll_zalo);
			llZalo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					share(infoZalo);
//					FeedData feed = new FeedData();
//					feed.setMsg("Prefill message");
//					feed.setLink("http://olava.com.vn/?tgid=007");
//					feed.setLinkTitle("Zing News");
//					feed.setLinkSource("http://olava.com.vn/?tgid=007");
//					feed.setLinkThumb(new String[] {"https://img.v3.news.zdn.vn/w660/Uploaded/xpcwvovb/2015_12_15/cua_kinh_2.jpg"});
//					OpenAPIService.getInstance().shareMessage(ShareActivity.this, feed, new ZaloPluginCallback() {
//						
//						@Override
//						public void onResult(boolean arg0, int arg1, String arg2, String arg3) {
//							if (arg0) {
//								ToastUtil.showShort(ShareActivity.this, "分享成功！");
//							} else {
//								ToastUtil.showShort(ShareActivity.this, "分享失败！");
//							}
//						}
//					});
					shareZalo = 1;
					phoneDialog.dismiss();
				}
			});
			inflate.findViewById(R.id.iv_cancle).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							phoneDialog.dismiss();
						}
					});
			TextView tvEmpty = inflate.findViewById(R.id.tv_empty);
			if (infoFacebook == null && infoZalo == null) {
				tvEmpty.setVisibility(View.VISIBLE);
				llShare.setVisibility(View.GONE);
			} else if (infoFacebook == null) {
				tvEmpty.setVisibility(View.GONE);
				llShare.setVisibility(View.VISIBLE);
				llFacebook.setVisibility(View.GONE);
				llZalo.setVisibility(View.VISIBLE);
			} else if (infoZalo == null) {
				tvEmpty.setVisibility(View.GONE);
				llShare.setVisibility(View.VISIBLE);
				llFacebook.setVisibility(View.VISIBLE);
				llZalo.setVisibility(View.GONE);
			}
			if (infoFacebook != null) {
				ImageView iv = inflate
						.findViewById(R.id.iv_facebook);
				PackageManager pm = ShareActivity.this.getApplicationContext()
						.getPackageManager();
				iv.setImageDrawable(infoFacebook.loadIcon(pm));
			}
			if (infoZalo != null) {
				ImageView iv = inflate.findViewById(R.id.iv_zalo);
				PackageManager pm = ShareActivity.this.getApplicationContext()
						.getPackageManager();
				iv.setImageDrawable(infoZalo.loadIcon(pm));
			}
			phoneDialog.show();
			phoneDialog.setCanceledOnTouchOutside(true);
			phoneDialog.setContentView(inflate);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {
		callBackManager.onActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void getShareActivities2() {
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		PackageManager pm = ShareActivity.this.getApplicationContext()
				.getPackageManager();
		List<ResolveInfo> activityList = pm.queryIntentActivities(
				sharingIntent, 0);

		for (Iterator<ResolveInfo> it = activityList.iterator(); it.hasNext();) {
			ResolveInfo info = it.next();
			// 过滤出facebook google+ whatapp twitter 分享app单独处理
//			Log.i("MOFA", info.activityInfo.packageName);
			if (info.activityInfo.packageName.equals("com.zing.zalo")) {
				infoZalo = info;
				Log.i("MOFA", "包含zalo");
			} else if (info.activityInfo.packageName
					.equals("com.facebook.katana")) {
				Log.i("MOFA", "包含facebook");
				infoFacebook = info;
			}
		}
	}

	private void share(ResolveInfo info) {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.setType("text/plain");
		sendIntent.putExtra(Intent.EXTRA_TEXT, "http://olava.com.vn/?tgid=" + userid);
		sendIntent.putExtra(Intent.EXTRA_SUBJECT, "MOFA");
		sendIntent.setClassName(info.activityInfo.packageName,
				info.activityInfo.name);
		Intent chooserIntent = Intent.createChooser(sendIntent, "MOFA");
		startActivity(chooserIntent);
	}

}
