package cn.itcase.safe.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class AppLockDao {

	private static final String TAG = "AppLockDao";
	private AppLockDbHelp mHelp;
	private Context mContext;

	public AppLockDao(Context context) {
		mContext = context;
		mHelp = new AppLockDbHelp(context);
	}

	/**
	 * 增加数据
	 * 
	 * @param appName
	 * @return
	 */
	public boolean add(String appName) {
		Log.d(TAG, appName + "数据库这边传过来的值 ,添加这里");
		
		Uri uri = Uri.parse("content://cn.itcase.lock");
		// 注册内容观察者
		mContext.getContentResolver().notifyChange(uri, null);
		
		// 获取写数据库的对象
		SQLiteDatabase db = mHelp.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(AppLockDb.AppLockList.COLUMN_NAME, appName);
		long result = db.insert(AppLockDb.AppLockList.TABLE_NAME, null, values);

		db.close();
		Log.d(TAG, (result != -1) + "数据库这边传过来的返回值 ,添加这里");
		return result != -1;
	}

	/**
	 * 删除数据
	 * 
	 * @param appName
	 * @return
	 */
	public boolean delect(String appName) {
		Log.d(TAG, appName + "数据库这边传过来的值 ,删除这里");
		// 获取读数据库的对象
		SQLiteDatabase db = mHelp.getReadableDatabase();
		String whereClause = AppLockDb.AppLockList.COLUMN_NAME + "= ?";
		int result = db.delete(AppLockDb.AppLockList.TABLE_NAME, whereClause,
				new String[] { appName });
		db.close();
		Log.d(TAG, (result != 0) + "数据库这边传过来的返回值 ,删除这里");
		// 删除成功后返回来的是一个具体的行号
		return result > 0;
	}

	/**
	 * 查询是否存在该元素 , 返回真,说明上锁了
	 * 
	 * @param appName
	 * @return
	 */
	public boolean select(String appName) {
		// 获取读数据库的对象
		SQLiteDatabase db = mHelp.getReadableDatabase();

		String sql = "select  count(1)" + " from "
				+ AppLockDb.AppLockList.TABLE_NAME + "  where  "
				+ AppLockDb.AppLockList.COLUMN_NAME + " = ?";
		Cursor cursor = db.rawQuery(sql, new String[] { appName });
		int count = 0;
		if (cursor != null) {
			if (cursor.moveToNext()) {
				count = cursor.getInt(0);
			}
			cursor.close();
		}

		db.close();
		return count > 0;
	}

	/**
	 * 获取所有的加锁程序
	 * 
	 * @return
	 */
	public List<String> getAllLockApp() {
		// 获取读数据库的对象
		SQLiteDatabase db = mHelp.getReadableDatabase();

		String sql = "select " + AppLockDb.AppLockList.COLUMN_NAME + " from "
				+ AppLockDb.AppLockList.TABLE_NAME;
		Cursor cursor = db.rawQuery(sql, null);
		List<String> list = new ArrayList<String>();
		
		if(cursor != null){
			while(cursor.moveToNext()){
				String appname = cursor.getString(0);
				list.add(appname);
			}
			cursor.close();
		}
		db.close();
		return list;
	}
}
