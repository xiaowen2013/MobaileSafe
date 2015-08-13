package cn.itcase.safe.db;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AntiVirusDao {

	/**
	 * 查询特征码是不是病毒 返回真说明是病毒
	 * 
	 * @param context
	 * @param md5
	 * @return
	 */
	public static boolean isVirus(Context context, String md5) {
		String path = new File(context.getFilesDir(), "antivirus.db")
				.getAbsolutePath();
		// 获取数据库对象
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);

		String sql = "select count(1)  from  datable where md5 = ?";
		Cursor cursor = db.rawQuery(sql, new String[] { md5 });
		int count = 0;
		if (cursor != null) {
			while (cursor.moveToNext()) {
				//
				count = cursor.getInt(0);
			}
		}
		return count > 0;
	}
}
