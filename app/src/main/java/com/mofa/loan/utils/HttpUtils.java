package com.mofa.loan.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtils {
	private static final int TIMEOUT_IN_MILLIONS = 10000;
	private static String retrunStr = "";
	private static String retrunStr2 = "";

	public interface CallBack {
		void onRequestComplete(String result);
	}

	/**
	 * 异步的Get请求
	 * 
	 * @param urlStr
	 * @param callBack
	 */
	static String result = "";

	public static String doGetAsyn(final String urlStr, final Handler handler,
                                   final int type) {

		new Thread() {
			public void run() {
				try {
					result = doGet(urlStr, handler, type);

				} catch (Exception e) {
					Log.e("MOFA", e.getMessage());
				}
			}
        }.start();
		return result;
	}

	public static String doGet(final String urlStr, Handler handler, int type) {
		Log.i("MOFA", urlStr.substring(60).replace("=", "-").replace("&", "-"));
		Message msg = Message.obtain();
		URL url = null;
		HttpURLConnection conn = null;
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		try {
			Log.i("MOFA", "HttpUtil---url--" + type);
			url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIMEOUT_IN_MILLIONS);
			conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.connect();
			if (conn.getResponseCode() == 200) {
				is = conn.getInputStream();
				baos = new ByteArrayOutputStream();
				int len = -1;
				byte[] buf = new byte[128];
				Log.i("MOFA", "--------");
				while ((len = is.read(buf)) != -1) {
					baos.write(buf, 0, len);
				}
				baos.flush();
				Log.i("MOFA", "HttpUtil---doGet:" + baos.toString());
				retrunStr = baos.toString();
				msg.what = type;
			} else {
				msg.what = Config.CODE_ERROR;
				Log.e("MOFA", "网络状态码为" + conn.getResponseCode());
				Log.e("MOFA", "网络信息为" + conn.getResponseMessage());
			}

		} catch (MalformedURLException e) {
			// url错误的异常
			msg.what = Config.CODE_URL_ERROR;
			Log.e("MOFA", e.getMessage());
		} catch (SocketTimeoutException e) {
			msg.what = Config.CODE_TIMEOUT_ERROR;
			Log.e("MOFA", "SocketTimeoutException -> 网络请求超时");
			Log.e("MOFA", e.getMessage());
		} catch (IOException e) {
			// 网络错误异常
			msg.what = Config.CODE_NET_ERROR;
			Log.e("MOFA", "IOException -> 提示网络传输错误");
			Log.e("MOFA", e.getMessage());
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
				Log.e("MOFA", e.getMessage());
			}
			try {
				if (baos != null)
					baos.close();
			} catch (IOException e) {
				Log.e("MOFA", e.getMessage());
			}
			conn.disconnect();
		}
		msg.obj = retrunStr;
		handler.sendMessage(msg);
		return retrunStr;
	}

	public static String doPostAsyn(final String urlStr, final String params,
                                    final Handler handler, final int type) {
		new Thread() {
			public void run() {
				try {
					result = doPost(urlStr, params, handler, type);

				} catch (Exception e) {
					Log.e("MOFA", e.getMessage());
				}
			}
        }.start();
		return result;
	}

	// /**
	// * 另一种Post请求，获得返回数据
	// *
	// * @param urlStr
	// * @return
	// * @throws Exception
	// */
	// public static String MyPost(String url, final Handler handler,
	// Map<String, String> param, int type) throws Exception {
	// DefaultHttpClient httpClient = new DefaultHttpClient();
	// HttpPost post = new HttpPost(url);
	// MultipartEntity entity = new MultipartEntity();
	// if (param != null && !param.isEmpty()) {
	// for (Map.Entry<String, String> entry : param.entrySet()) {
	// if (entry.getValue() != null
	// && entry.getValue().trim().length() > 0) {
	// entity.addPart(entry.getKey(),
	// new StringBody(entry.getValue()));
	// }
	// }
	// }
	// post.setEntity(entity);
	// HttpResponse response = httpClient.execute(post);
	// int stateCode = response.getStatusLine().getStatusCode();
	// if (stateCode == HttpStatus.SC_OK) {
	// HttpEntity result = response.getEntity();
	// if (result != null) {
	// InputStream is = result.getContent();
	// BufferedReader br = new BufferedReader(new InputStreamReader(
	// is, "utf-8"));
	// String data = br.readLine();
	// return data;
	// }
	// } else {
	// Log.i("MOFA", "获取失败");
	// }
	// post.abort();
	// return null;
	// }

	/**
	 * 向指定 URL 发送POST方法的请求
	 * 
	 * @param url
	 *            发送请求的 URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 * @throws Exception
	 */
	public static String doPost(String url, String param, Handler handler,
                                int type) {
		Log.i("MOFA", "HttpUtil---" + url + "参数" + param);
		Message msg = Message.obtain();
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			HttpURLConnection conn = (HttpURLConnection) realUrl
					.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.setRequestProperty("charset", "utf-8");
			conn.setUseCaches(false);
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setReadTimeout(TIMEOUT_IN_MILLIONS);
			conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);

			if (param != null && !param.trim().equals("")) {
				// 获取URLConnection对象对应的输出流
				out = new PrintWriter(conn.getOutputStream());
				// 发送请求参数
				out.print(param);
				// flush输出流的缓冲
				out.flush();
			}
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
			msg.what = type;
			Log.i("MOFA", "HttpUtil---" + result);
			// } else {
			// msg.what = Config.CODE_URL_ERROR;
			// }
		} catch (Exception e) {
			// 网络错误异常
			msg.what = Config.CODE_NET_ERROR;
			Log.e("MOFA", e.getMessage());
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				// 网络错误异常
				msg.what = Config.CODE_NET_ERROR;
				Log.e("MOFA", ex.getMessage());
			}
		}
		msg.obj = result;
		handler.sendMessage(msg);
		return retrunStr2;
	}

	/**
	 * post请求 提交数据到服务器
	 * 
	 */
	public static void httpPostJSON(String url, String json,
                                    final Handler handler) {
		MediaType JSON = MediaType.parse("application/json; charset=utf-8");
		final Message msg = Message.obtain();
		msg.obj = "上传通讯录返回数据";
		OkHttpClient client = new OkHttpClient();// 创建okhttp实例
		RequestBody body = RequestBody.create(JSON, json);
		Request request = new Request.Builder().url(url).post(body).build();
		Log.i("MOFA", "HttpUtil---post1---" + request.toString());
		Call call = client.newCall(request);
		call.enqueue(new Callback() {
			// 请求失败时调用
			@Override
			public void onFailure(Call call, IOException e) {
				Log.e("MOFA", "onFailure: " + e.getMessage());
				msg.obj = e.getMessage();
				msg.what = Config.CODE_CONTACT_FAILED;
				handler.sendMessage(msg);
			}

			// 请求成功时调用

			@Override
			public void onResponse(Call arg0, Response arg1) {
				try {
					Log.i("MOFA", "arg1: " + arg1.body().string());
					msg.obj = arg1.body().string();
				} catch (IOException e) {
					e.printStackTrace();
				}
				// try {
				// JSONObject responseobj = new JSONObject(arg1.body()
				// .toString());
				// } catch (JSONException e) {
				// Log.e("MOFA", e.getMessage());
				// }
				if (arg1.code() == 200) {
					msg.what = Config.CODE_CONTACT_SUCCESS;
				} else {
					msg.what = Config.CODE_CONTACT_FAIL;
				}
				handler.sendMessage(msg);
			}

		});

	}

	public static void httpPostJSONAsync(final String url, final String json,
                                         final Handler handler) {
		new Thread() {
			public void run() {
				httpPostJSON(url, json, handler);
			}
        }.start();
	}

}
