package cn.itcase.safe.db;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CommonQueryDao {

	/**
	 * 获取 Group 的总数
	 * 
	 * @param context
	 * @return
	 */
	public static int getGroupCount(Context context) {

		String path = new File(context.getFilesDir(), "commonnum.db")
				.getAbsolutePath();
		SQLiteDatabase sd = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		String sql = "select count(*)  from classlist";
		Cursor cursor = sd.rawQuery(sql, null);
		int count = -1;
		if (cursor != null) {
			while (cursor.moveToNext()) {
				count = cursor.getInt(0);
			}
			cursor.close();
		}
		sd.close();
		return count;
	}

	/**
	 * 查询 GroupText 的文本内容
	 * 
	 * @param context
	 * @return
	 */
	public static String getGroupText(Context context, int groupPosition) {

		String path = new File(context.getFilesDir(), "commonnum.db")
				.getAbsolutePath();
		SQLiteDatabase sd = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		String sql = "select name  from classlist where idx = ?";
		Cursor cursor = sd.rawQuery(sql, new String[] { (groupPosition + 1)
				+ "" });
		String result = null;
		if (cursor != null) {
			while (cursor.moveToNext()) {
				result = cursor.getString(0);
			}
			cursor.close();
		}
		sd.close();
		return result;
	}

	/**
	 * 查询子类 的总数
	 * 
	 * @param context
	 * @param groupPosition
	 * @return
	 */
	public static int getChildrenCount(Context context, int groupPosition) {

		String path = new File(context.getFilesDir(), "commonnum.db")
				.getAbsolutePath();
		SQLiteDatabase sd = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		String sql = "select count(*)  from  table" + (groupPosition + 1);
		Cursor cursor = sd.rawQuery(sql, null);
		int count = -1;
		if (cursor != null) {
			while (cursor.moveToNext()) {
				count = cursor.getInt(0);
			}
			cursor.close();
		}
		sd.close();
		return count;
	}

	/**
	 * 查询子类 的文本内容
	 * 
	 * @param context
	 * @param groupPosition
	 * @return
	 */
	public static String[] getChildrenText(Context context, int groupPosition,
			int childPosition) {

		String path = new File(context.getFilesDir(), "commonnum.db")
				.getAbsolutePath();
		SQLiteDatabase sd = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		String sql = "select number,name  from table" + (groupPosition + 1)
				+ " where _id = ?";
		Cursor cursor = sd.rawQuery(sql, new String[] { (childPosition + 1)
				+ "" });
		String number = null;
		String name = null;
		if (cursor != null) {
			while (cursor.moveToNext()) {
				number = cursor.getString(0);
				name = cursor.getString(1);
			}
			cursor.close();
		}
		sd.close();
		return new String[] { number, name };
	}

}
