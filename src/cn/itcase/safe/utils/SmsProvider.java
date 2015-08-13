package cn.itcase.safe.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

public class SmsProvider {

	private static final String TAG = "SmsProvider";
	private static final String SMSSVAEPATHNAME = "SmsBackups.xml";
	private static final int TYPE_SIZE = 0;
	private static final int TYPE_PROGRESS = 1;

	public static void saveAllSms(final Context context,
			final OnSmsListener listenter) {

		// 由于保存数据是一个耗时的操作 , 因此的用AsyncTack
		new AsyncTask<Void, Integer, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {

				// 在这里做耗时的操作
				// 获取内容提供者
				ContentResolver resolver = context.getContentResolver();

				Uri uri = Uri.parse("content://sms/");

				// 需要查询的内容
				String[] projection = new String[] { "address", "date", "type",
						"body" };
				// 查询的条件 selection --> 查询所有,为空
				// 查询的条件值 selectionArgs ---> 查询所有,为空
				// 去查询数据
				Cursor cursor = resolver.query(uri, projection, null, null,
						null);
				// 保存到xml文件 , 创建一个xml文件
				XmlSerializer xml = null;
				try {
					// 获取到内容啦
					if (cursor != null) {

						xml = Xml.newSerializer();
						File filesDir = new File(context.getFilesDir(),
								SMSSVAEPATHNAME);
						Log.d(TAG, filesDir.getAbsolutePath() + "  保存的路径");
						xml.setOutput(new FileOutputStream(filesDir), "utf-8");
						// 开始文档
						xml.startDocument("utf-8", true);
						// 开始的根节点
						xml.startTag(null, "list");

						// 获取cursor 的数量
						int count = cursor.getCount();
						// 将数据传送出去 , 总的大小
						publishProgress(TYPE_SIZE, count);

						// 设置当前的进度
						int progress = 1;
						while (cursor.moveToNext()) {

							// 开始的根节点
							xml.startTag(null, "sms");
							// adress
							xml.startTag(null, "address");
							xml.text(cursor.getString(0));
							xml.endTag(null, "address");

							// date
							xml.startTag(null, "date");
							xml.text(cursor.getString(1));
							xml.endTag(null, "date");

							// type
							xml.startTag(null, "type");
							xml.text(cursor.getString(2));
							xml.endTag(null, "type");

							// body
							xml.startTag(null, "body");
							xml.text(cursor.getString(3));
							xml.endTag(null, "body");

							// 结束的根节点
							xml.endTag(null, "sms");

							// 睡一会
							Thread.sleep(50);
							progress++;
							// 将数据推送出去
							publishProgress(TYPE_PROGRESS, progress);
						}

						// 结束根节点
						xml.endTag(null, "list");
						// 结束文本
						xml.endDocument();
					}
					cursor.close();
				} catch (Exception e) {
					e.printStackTrace();
					Log.d(TAG, "出异常啦...");
					return false;
				}
				// 成功
				return true;
			}

			// 当数据推送出来的时候执行的方法
			protected void onProgressUpdate(Integer[] values) {
				Log.d(TAG, "更新当前进度");
				if (values[0] == TYPE_SIZE) {
					// 总数 最大值在 values[1]中
					if (listenter != null) {
						listenter.onMax(values[1]);
						Log.d(TAG, "更新当前进度:总数" + values[1]);
					}

				} else if (values[0] == TYPE_PROGRESS) {
					// 当前的进度
					if (listenter != null) {
						listenter.onProgress(values[1]);
						Log.d(TAG, "更新当前进度" + values[1]);
					}
				}

			};

			// 当数据执行完毕时执行的方法
			protected void onPostExecute(Boolean result) {
				if (result) {
					// 执行成功
					if (listenter != null) {
						listenter.onSucess();
						Log.d(TAG, " 执行成功");
					}
				} else {
					// 执行失败
					if (listenter != null) {
						listenter.onFailed();
						Log.d(TAG, " 执行失败");
					}
				}
			};
		}.execute();
	}

	/**
	 * 
	 * 定义的接口 : 接口,规范 ---> 设置总量 --> 设置进度 -->结果是:成功(失败)
	 */
	public interface OnSmsListener {
		// 数据的最大值
		void onMax(int max);

		// 当前的进度
		void onProgress(int progress);

		// 成功
		void onSucess();

		// 失败
		void onFailed();
	}

	/**
	 * 短信恢复的功能逻辑
	 * 
	 * @param context
	 * @param onSmsListener
	 */
	public static void recoveryAllSms(final Context context,
			final OnSmsListener listenter) {

		new AsyncTask<Void, Integer, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {

				// 将xml的数据写入到短信的数据库中
				XmlPullParser parser = Xml.newPullParser();
				// 获取xml的路径
				File filesDir = new File(context.getFilesDir(), SMSSVAEPATHNAME);
				Log.d(TAG, "文件的路径: " + filesDir.getAbsolutePath());
				FileInputStream in = null;

				// 创建集合对象
				List<SmsInfo> list = new ArrayList<SmsInfo>();
				try {
					in = new FileInputStream(filesDir);
					parser.setInput(in, "utf-8");

					// 获得xml的类型
					int type = parser.getEventType();
					// 创建对象封装数据
					SmsInfo info = null;
					// 不等于文档的结束
					while (type != XmlPullParser.END_DOCUMENT) {
						// 标签的开始
						if (type == XmlPullParser.START_TAG) {
							//
							if ("sms".equals(parser.getName())) {
								// list 的开始
								// 创建对象
								info = new SmsInfo();
							} else if ("address".equals(parser.getName())) {
								info.address = parser.nextText();
							} else if ("type".equals(parser.getName())) {
								info.type = parser.nextText();
							} else if ("date".equals(parser.getName())) {
								info.date = parser.nextText();
							} else if ("body".equals(parser.getName())) {
								info.body = parser.nextText();
							}

						} else {
							// 标签的结束
							if ("sms".equals(parser.getName())) {
								// 一个数据的封装完成
								list.add(info);
								Log.d(TAG, " 一个数据的封装完成");
							}
						}
						// 指向下一行
						type = parser.next();
					}

				} catch (Exception e) {
					e.printStackTrace();
					return false;
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

				// 获取内容提供者
				ContentResolver resolver = context.getContentResolver();

				// 获取URI
				Uri uri = Uri.parse("content://sms/");

				// 发送总大小
				publishProgress(TYPE_SIZE, list.size());
				// 遍历list添加数据
				// 定义当前进度
				int count = 1;
				for (SmsInfo sms : list) {
					ContentValues values = new ContentValues();
					values.put("address", sms.address);
					values.put("type", sms.type);
					values.put("date", sms.date);
					values.put("body", sms.body);
					resolver.insert(uri, values);
					// 发送当前进度
					publishProgress(TYPE_PROGRESS, count++);
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				return true;
			}

			// 当数据推送出来的时候执行的方法
			protected void onProgressUpdate(Integer[] values) {
				Log.d(TAG, "更新当前进度");
				if (values[0] == TYPE_SIZE) {
					// 总数 最大值在 values[1]中
					if (listenter != null) {
						listenter.onMax(values[1]);
					}

				} else if (values[0] == TYPE_PROGRESS) {
					// 当前的进度
					if (listenter != null) {
						listenter.onProgress(values[1]);
					}
				}

			};

			// 当数据执行完毕时执行的方法
			protected void onPostExecute(Boolean result) {
				if (result) {
					// 执行成功
					if (listenter != null) {
						listenter.onSucess();
						Log.d(TAG, " 执行成功");
					}
				} else {
					// 执行失败
					if (listenter != null) {
						listenter.onFailed();
						Log.d(TAG, " 执行失败");
					}
				}
			};

		}.execute();
	}

	public static class SmsInfo {
		public String address;
		public String type;
		public String date;
		public String body;
	}

	public boolean mas(Context context) {
		// 获取内容提供者
		ContentResolver resolver = context.getContentResolver();

		// 获取URI
		Uri uri = Uri.parse("content://sms/");

		return resolver.delete(uri, null, null) != -1;
	}
}
