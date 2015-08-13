package cn.itcase.safe.service;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import cn.itcase.safe.utils.IConstants;
import cn.itcase.safe.utils.PreferenceUtils;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class GPSService extends Service {

	private static final String TAG = "gpsservice";
	LocationManager mLm;

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public void onCreate() {

		// 获得gps 服务对象
		mLm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Log.d(TAG, "GPS服务开启啦...");
		List<String> allProviders = mLm.getAllProviders();
		for (String string : allProviders) {
			Log.d(TAG, string);
		}

		// provider : 提供者
		// minTime : 请求的最小时间
		// minDistance : 最短的移动距离

		long minTime = 5 * 1000;
		float minDistance = 10;
		mLm.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime,
				minDistance, listener);
	}

	private LocationListener listener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// 当GPS芯片连接状态变化时 的回调

		}

		@Override
		public void onProviderEnabled(String provider) {
			// 当GPS芯片可用时的回调

		}

		@Override
		public void onProviderDisabled(String provider) {
			// 当GPS芯片不可用时的回调

		}

		@Override
		public void onLocationChanged(Location location) {
			// 位置改变时的回调
			// 获取纬度信息
			double latitude = location.getLatitude();
			// 获取经度信息
			double longitude = location.getLongitude();
			// 将此信息进行百度联网校对, 获取中文的地址
			loadLocation(longitude, latitude);
		}

	};

	private void loadLocation(final double longitude, final double latitude) {

		// 接口地址：http://lbs.juhe.cn/api/getaddressbylngb
		// 支持格式：JSON/XML
		// 请求方式：GET
		// 请求示例：http://lbs.juhe.cn/api/getaddressbylngb?lngx=116.407431&lngy=39.914492
		// 请求参数：
		// 名称 类型 必填 说明
		// lngx String Y google地图经度 (如：119.9772857)
		// lngy String Y google地图纬度 (如：27.327578)
		// dtype String N 返回数据格式：json或xml,默认json

		// 连接网络
		HttpUtils hu = new HttpUtils();
		// 设置连接超时的时间
		hu.configTimeout(5 * 1000);
		// 设置超长的响应时间
		hu.configSoTimeout(5 * 1000);
		// 设置请求tou

		RequestParams params = new RequestParams();
		params.addQueryStringParameter("lngx", longitude + "");
		params.addQueryStringParameter("lngy", latitude + "");
		params.addQueryStringParameter("dtype", "json");
		String url = "http://lbs.juhe.cn/api/getaddressbylngb";
		// 发送连接上网
		hu.send(HttpMethod.GET, url, new RequestCallBack<String>() {

			/**
			 * 失败时调用的方法
			 */
			@Override
			public void onFailure(HttpException e, String msg) {
				// 失败,发送短信 : 内容是发送经纬度
				e.printStackTrace();
				Log.d(TAG, "联网失败...");
				String text = "longitude:" + longitude + "  ,latitude:"
						+ latitude;
				sendMessge(text);
				// 停止
				stopSelf();
			}

			/**
			 * 成功时调用的方法
			 */
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// 成功: 发送经纬度和详细地址
				// 解析json数据
				Log.d(TAG, "联网成功...");
				String result = responseInfo.result;
				try {
					JSONObject root = new JSONObject(result);
					JSONObject rowObject = root.getJSONObject("row");
					JSONObject resultObject = rowObject.getJSONObject("result");
					String data = resultObject.getString("formatted_address");

					Log.d(TAG, "联网返回解析的数据:" + data);
					sendMessge("success...");
					sendMessge(data + "  ,longitude:" + longitude
							+ "  ,latitude:" + latitude);

					// 停止
					stopSelf();
				} catch (JSONException e) {
					e.printStackTrace();
					// 出异常了 , 返回数据
					String text = "longitude:" + longitude + "  ,latitude:"
							+ latitude;
					sendMessge(text);
					// 停止
					stopSelf();
				}
			}
		});
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "gps服务关闭");

		// 将此服务关闭,否则会消耗大量的电量
		mLm.removeUpdates(listener);
	}

	/**
	 * 发送短信的方法
	 */
	private void sendMessge(String text) {
		SmsManager sm = SmsManager.getDefault();
		// 获取安全号码的地址
		String address = PreferenceUtils
				.getString(this, IConstants.SJFD_PHONUM);
		sm.sendTextMessage(address, null, text, null, null);
		Log.d(TAG, "发送短信:" + text);
	}
}
