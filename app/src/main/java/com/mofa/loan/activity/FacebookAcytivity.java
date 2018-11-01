package com.mofa.loan.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.JsonObject;
import com.mofa.loan.R;
import com.mofa.loan.utils.BaseActivity;
import com.mofa.loan.utils.Config;
import com.mofa.loan.utils.HttpUtils;
import com.mofa.loan.utils.MD5Util;
import com.mofa.loan.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class FacebookAcytivity extends BaseActivity implements OnClickListener {

	private Button btnNext;
	private LoginButton mLoginButton;
	private CallbackManager callbackManager;

	private String userid;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Config.CODE_GETFACEBOOK:
				String reString = msg.obj.toString();
				try {
					JSONObject jsonObject = new JSONObject(reString);
					String message = jsonObject.getString("msg");
					ToastUtil.showShort(FacebookAcytivity.this, message);

					int error = jsonObject.getInt("error");
					if (error == 0) {
						SharedPreferences sp = getSharedPreferences("config",
								MODE_PRIVATE);
						Editor editor = sp.edit();
						editor.putInt("isfacebook", 1);
						editor.commit();
						btnNext.setClickable(false);
					}
					Intent intent1 = new Intent(FacebookAcytivity.this,
							IndexActivity.class);
					intent1.putExtra("id", 5);
					startActivity(intent1);
					finish();
				} catch (JSONException e) {
					Log.e("MOFA", e.getMessage());
				}
				break;
			case Config.CODE_URL_ERROR:
				ToastUtil.showShort(FacebookAcytivity.this, getResources()
						.getString(R.string.url_error));

				break;
			case Config.CODE_NET_ERROR:
				ToastUtil.showShort(FacebookAcytivity.this, getResources()
						.getString(R.string.network_error));

				break;

			case Config.CODE_CONTACT_SUCCESS:
				SharedPreferences sp = getSharedPreferences("config",
						MODE_PRIVATE);
				Editor editor = sp.edit();
				editor.putInt("isfacebook", 1);
				editor.commit();
				Intent intent1 = new Intent(FacebookAcytivity.this,
						IndexActivity.class);
				intent1.putExtra("id", 2);
				startActivity(intent1);
				finish();
				break;

			default:
				break;
			}
		}
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		callbackManager = CallbackManager.Factory.create();
		setContentView(R.layout.activity_facebook);
		Log.i("MOFA", "Facebook---onCreate");
		super.onCreate(savedInstanceState);
		initview();
	}

	private long timeIn;
	private long timeOut;

	@Override
	protected void onStart() {
		super.onStart();
		timeIn = System.currentTimeMillis();
		Log.i("MOFA", "Facebook---onStart:" + timeIn);
	}

	@Override
	protected void onStop() {
		super.onStop();
		timeOut = System.currentTimeMillis();
		Log.i("MOFA", "Facebook---onStop:" + timeOut);
		Log.i("MOFA", "Facebook---Show:" + (timeOut - timeIn));
	}

	private void initview() {
		btnNext = findViewById(R.id.btn_facebook);
		btnNext.setOnClickListener(this);
		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.btn_loan).setOnClickListener(this);

		SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
		userid = sp.getString("userid", "");

		// 3. 获取登陆按钮
		mLoginButton = new LoginButton(this);
		mLoginButton.setReadPermissions(Arrays.asList("public_profile",
				"email", "user_friends"));
		// 4. 设置回调接口
		mLoginButton.registerCallback(callbackManager,
				new FacebookCallback<LoginResult>() {
					@Override
					public void onSuccess(LoginResult loginResult) {
						Log.i("MOFA", "facebook---onSuccess");
						GraphRequest request = GraphRequest.newMeRequest(
								loginResult.getAccessToken(),
								new GraphRequest.GraphJSONObjectCallback() {

									@Override
									public void onCompleted(JSONObject object,
											GraphResponse response) {
										if (object == null) {
											Log.i("MOFA",
													"facebook---object	->	"
															+ null);
										} else {
											Log.i("MOFA",
													"facebook---object	->	"
															+ object.toString());
										}
										if (response == null) {
											Log.i("MOFA",
													"facebook---response == null");
											return;
										}
										JSONObject responseJsonObject = response
												.getJSONObject();
										if (responseJsonObject == null) {
											Log.i("MOFA",
													"facebook---无法获取用户基本信息2"
															+ response
																	.getError()
																	.getErrorType()
															+ "   "
															+ response
																	.getError()
																	.getErrorMessage());
											return;
										}
										try {
											String id = responseJsonObject
													.getString("id");
											String email = responseJsonObject
													.has("email") ? responseJsonObject
													.getString("email")
													: "null";
											// email = email == null ? "null"
											// : email;
											JSONObject picture = responseJsonObject
													.getJSONObject("picture");
											String pictureUrl = "";
											if (picture != null) {
												pictureUrl = picture
														.getJSONObject("data")
														.getString("url");
											}
											String link = responseJsonObject
													.getString("link");
											email = email == null ? "null"
													: email;
											String updated_time = responseJsonObject
													.getString("updated_time");
											updated_time = updated_time == null ? "null"
													: updated_time;
											String gender = responseJsonObject
													.has("gender") ? responseJsonObject
													.getString("gender")
													: "null";
											gender = gender == null ? "null"
													: gender;
											String name = responseJsonObject
													.getString("name");
											name = name == null ? "null" : name;
											String age_range = responseJsonObject
													.has("age_range") ? (responseJsonObject
													.getJSONObject("age_range")
													.getInt("min") + "")
													: "null";
											age_range = (age_range == "" || age_range == null) ? "null"
													: age_range;
											SharedPreferences sp = getSharedPreferences(
													"config", MODE_PRIVATE);
											Editor editor = sp.edit();
											editor.putString("facebookname",
													name);
											editor.putString(
													"facebookpictureUrl",
													pictureUrl);
											editor.commit();
											String json = "";
											JsonObject obj = new JsonObject();
											obj.addProperty("userid", userid);
											obj.addProperty("yang", MD5Util.md5(userid));
											obj.addProperty("picture",
													pictureUrl);
											obj.addProperty("link", link);
											obj.addProperty("updated_time",
													updated_time);
											obj.addProperty("age", age_range);
											obj.addProperty("email", email);
											obj.addProperty("name", name);
											obj.addProperty("gender", gender);
											obj.addProperty("facebookid", id);
											json = obj.toString();
											HttpUtils.httpPostJSONAsync(
													Config.GETFACEBOOK, json,
													handler);
										} catch (JSONException e) {
											Log.i("MOFA", "上传facebook的json错误:"
													+ e.getMessage());
											Log.e("MOFA", e.getMessage());
										}
										Log.i("MOFA",
												"facebook---responseJsonObject	->	"
														+ responseJsonObject
																.toString());

									}
								});
						Bundle parameters = new Bundle();
						parameters
								.putString(
										"fields",
										"id,name,email,gender,birthday,picture,link,verified,locale,updated_time,age_range,timezone");
						request.setParameters(parameters);
						request.executeAsync();
					}

					@Override
					public void onCancel() {
						Log.i("MOFA", "facebook---onCancel");
					}

					@Override
					public void onError(FacebookException e) {
						Log.i("MOFA", "facebook---onError" + e.getMessage());
					}
				});
	}

	@Override
	public void processMessage(Message message) {

	}

	// 5. 需要处理FacebookActivity的返回信息，才能触发登陆成功的回调
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		callbackManager.onActivityResult(requestCode, resultCode, data);
		boolean a = data == null;
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_facebook:
			mLoginButton.callOnClick();
			break;

		case R.id.back:
			Intent intent1 = new Intent(FacebookAcytivity.this,
					IndexActivity.class);
			intent1.putExtra("id", 1);
			startActivity(intent1);
			finish();
			break;
			
		case R.id.btn_loan:
			Intent intent2 = new Intent(FacebookAcytivity.this,
					IndexActivity.class);
			intent2.putExtra("id", 2);
			startActivity(intent2);
			finish();
			break;

		default:
			break;
		}
	}

}
