package cn.itcase.safe.activity;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import cn.itcase.safe.utils.GZipUtils;
import cn.itcase.safe.utils.IConstants;
import cn.itcase.safe.utils.PackageUtils;
import cn.itcase.safe.utils.PreferenceUtils;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

public class SplashActivity extends Activity {
	private static final String TAG = "SplashActivity";

	public static final int SHOW_ERROR = 110;

	public static final int SHOW_UPDATE_DIALOG = 120;

	protected static final int REQUEST_CODE_INSTALL = 100;

	private TextView mTvVersion;

	private String mDesc;// 版本更新的描述信息

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			int what = msg.what;

			switch (what) {
			case SHOW_ERROR:
				// 提示错误：
				Toast.makeText(getApplicationContext(), msg.obj.toString(),
						Toast.LENGTH_SHORT).show();
				// 进入主页
				load2Home();
				break;
			case SHOW_UPDATE_DIALOG:
				showUpdateDialog();
				break;
			default:
				break;
			}

		}
	};

	public String mUrl;// 最新版本的url

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		// 初始化view
		mTvVersion = (TextView) findViewById(R.id.splash_tv_version);

		// 显示版本号
		mTvVersion.setText(PackageUtils.getVersionName(this));

		// 检测是否更新

		boolean update = PreferenceUtils.getBoolean(this,
				IConstants.AUTO_UPDATE, true);
		if (update) {
			Log.d(TAG, "需要检测更新");
			checkVersionUpdate();
		} else {
			Log.d(TAG, "不需要检测更新");
			load2Home();
		}
		// 拷贝数据库
		copyAddressDB();
		// 拷贝常用号码数据库
		copyCommonNumberAddressDB();
		// 拷贝常用病毒数据库
		copyCommonAntiVirusDB();
	}

	private void copyCommonAntiVirusDB() {
		new Thread() {
			public void run() {
				// 判断数据库文件是否存在
				File descFile = new File(getFilesDir(), "antivirus.db");
				if (descFile.exists()) {
					// 存在
					Log.d(TAG, "病毒数据库存在,不需要拷贝...");
				} else {
					// 不存在
					Log.d(TAG, "病毒数据库不存在,需要拷贝...");
					AssetManager assets = getAssets();
					InputStream in = null;
					FileOutputStream out = null;
					try {
						in = assets.open("antivirus.db");
						out = new FileOutputStream(descFile);
						int len = 0;
						byte[] bys = new byte[1024];
						while ((len = in.read(bys)) != -1) {
							out.write(bys, 0, len);
						}
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						close(in);
						close(out);
					}
				}

			};
		}.start();
	}

	private void copyCommonNumberAddressDB() {
		new Thread() {
			public void run() {
				// 判断数据库文件是否存在
				File descFile = new File(getFilesDir(), "commonnum.db");
				if (descFile.exists()) {
					// 存在
					Log.d(TAG, "数据库存在,不需要拷贝...");
				} else {
					// 不存在
					Log.d(TAG, "数据库不存在,需要拷贝...");
					AssetManager assets = getAssets();
					InputStream in = null;
					FileOutputStream out = null;
					try {
						in = assets.open("commonnum.db");
						out = new FileOutputStream(descFile);
						int len = 0;
						byte[] bys = new byte[1024];
						while ((len = in.read(bys)) != -1) {
							out.write(bys, 0, len);
						}
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						close(in);
						close(out);
					}
				}

			};
		}.start();
	}

	private void copyAddressDB() {
		new Thread() {
			public void run() {
				// 判断数据库文件是否存在
				File descFile = new File(getFilesDir(), "address.db");
				if (descFile.exists()) {
					Log.d(TAG, "数据库存在,不需要拷贝...");

				} else {
					Log.d(TAG, "数据库不存在,需要拷贝..");
					AssetManager assets = getAssets();
					InputStream in = null;
					FileOutputStream out = null;
					try {
						in = assets.open("address.zip");
						out = new FileOutputStream(new File(getFilesDir(),
								"address.db"));
						GZipUtils.unZip(in, out);
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						close(in);
						close(out);
					}
				}

			};

		}.start();

	}

	private void close(Closeable is) {
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void load2Home() {

		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				String name = Thread.currentThread().getName();
				Log.d(TAG, "name : " + name);

				Intent intent = new Intent(SplashActivity.this,
						HomeActivity.class);
				startActivity(intent);

				finish();
			}
		}, 1200);
	}

	private void showUpdateDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		// 设置点击取消不可用
		builder.setCancelable(false);

		// 设置title
		builder.setTitle("版本更新提醒");
		// 设置文本
		builder.setMessage(mDesc);

		// button
		builder.setPositiveButton("立刻升级", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

				// 去下载最新版本
				showProgressDialog();
			}
		});

		builder.setNegativeButton("稍后再说", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

				load2Home();
			}
		});

		// 显示
		builder.show();
	}

	private void showProgressDialog() {
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setCancelable(false);
		dialog.show();

		// 去下载新版本
		HttpUtils utils = new HttpUtils();
		String url = mUrl;// 下载的地址
		final String target = new File(
				Environment.getExternalStorageDirectory(),
				System.currentTimeMillis() + ".apk").getAbsolutePath();

		utils.download(url, target, new RequestCallBack<File>() {

			@Override
			public void onSuccess(ResponseInfo<File> arg0) {
				// 成功时的回调
				Log.d(TAG, "下载成功");

				dialog.dismiss();
				// 去安装:
				// 安装是系统行为

				// <intent-filter>
				// <action android:name="android.intent.action.VIEW" />
				// <category android:name="android.intent.category.DEFAULT" />
				// <data android:scheme="content" />
				// <data android:scheme="file" />
				// <data
				// android:mimeType="application/vnd.android.package-archive" />
				// </intent-filter>

				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.setDataAndType(Uri.parse("file:" + target),
						"application/vnd.android.package-archive");

				// Uri.fromFile(file)//-->file:路径
				startActivityForResult(intent, REQUEST_CODE_INSTALL);
			}

			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				// total：下载的文件的总大小
				// current：当前下载到了什么位置
				dialog.setMax((int) total);
				dialog.setProgress((int) current);
			}

			@Override
			public void onFailure(HttpException e, String arg1) {

				e.printStackTrace();
				// 失败的回调
				Log.d(TAG, "下载失败");

				dialog.dismiss();

				// 进入主页
				load2Home();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// requestCode:自己发送的请求代码
		// resultCode ：结果代码,你打开的Activity做的标记

		if (requestCode == REQUEST_CODE_INSTALL) {
			// 安装的程序返回的数据

			switch (resultCode) {
			case Activity.RESULT_OK:
				// 用户成功操作
				Log.d(TAG, "用户成功安装");
				break;
			case Activity.RESULT_CANCELED:
				// 用户取消操作
				Log.d(TAG, "用户取消安装");
				load2Home();
				break;
			default:
				break;
			}
		}
	}

	private void checkVersionUpdate() {
		// 1.去网络获取最新的版本信息
		new Thread(new CheckVersionTask()).start();
	}

	private class CheckVersionTask implements Runnable {

		@Override
		public void run() {
			// 服务器必须提供网络接口

			String uri = "http://188.188.2.84:8080/update.txt";

			// 获取网络访问的客户端
			AndroidHttpClient client = AndroidHttpClient.newInstance("heima",
					getApplicationContext());
			HttpParams params = client.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 5000);// 设置访问网络的超时时间
			HttpConnectionParams.setSoTimeout(params, 5000);// 设置读取的超时时间

			// 创造get请求
			HttpGet get = new HttpGet(uri);

			try {
				// 获得response
				HttpResponse response = client.execute(get);

				// 获得状态码
				int statusCode = response.getStatusLine().getStatusCode();
				if (200 == statusCode) {
					// 访问成功
					String result = EntityUtils.toString(response.getEntity(),
							"utf-8");

					Log.d(TAG, "访问结果:" + result);
					// {versionCode:2}

					// 获得本地的版本号
					int localCode = PackageUtils
							.getVersionCode(getApplicationContext());

					// 解析json
					JSONObject jsonObject = new JSONObject(result);

					int netCode = jsonObject.getInt("versionCode");

					// 比对
					if (netCode > localCode) {
						// 需要更新 ,显示更新的对话框
						Log.d(TAG, "需要更新");

						mDesc = jsonObject.getString("desc");
						mUrl = jsonObject.getString("url");

						Message msg = Message.obtain();
						msg.what = SHOW_UPDATE_DIALOG;
						mHandler.sendMessage(msg);

					} else {
						// 不需要更新,进入主页
						Log.d(TAG, "不需要更新");
						load2Home();
					}
				} else {
					// 访问失败
					Message msg = Message.obtain();
					msg.what = SHOW_ERROR;
					msg.obj = "code:130";
					mHandler.sendMessage(msg);
				}
			} catch (ClientProtocolException e) {

				// Message msg = mHandler.obtainMessage();
				// msg.sendToTarget();

				Message msg = Message.obtain();
				msg.what = SHOW_ERROR;
				msg.obj = "code:110";
				mHandler.sendMessage(msg);

			} catch (IOException e) {

				Message msg = Message.obtain();
				msg.what = SHOW_ERROR;
				msg.obj = "code:问客服吧";
				mHandler.sendMessage(msg);
			} catch (JSONException e) {

				Message msg = Message.obtain();
				msg.what = SHOW_ERROR;
				msg.obj = "code:119";
				mHandler.sendMessage(msg);
			} finally {
				if (client != null) {
					client.close();
					client = null;
				}
			}

		}
	}

}
