package cn.itcase.safe.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cn.itcase.safe.domain.CallSmsSafeInfo;

public class BlackDao {

	private BlacklistDbHelp mHelp;

	public BlackDao(Context context) {
		mHelp = new BlacklistDbHelp(context);
	}

	/**
	 * 增加数据
	 * 
	 * @param number
	 * @param type
	 * @return
	 */
	public boolean add(String number, int type) {
		SQLiteDatabase sd = mHelp.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(BlackListDb.BlackList.COLUMN_NUMBER, number);
		values.put(BlackListDb.BlackList.COLUMN_TYPE, type);
		long insert = sd.insert(BlackListDb.BlackList.TABLE_NAME, null, values);
		sd.close();
		return insert != -1;
	}

	/**
	 * 删除数据
	 * 
	 * @param number
	 * @return
	 */
	public boolean delect(String number) {
		SQLiteDatabase db = mHelp.getWritableDatabase();

		String whereClause = BlackListDb.BlackList.COLUMN_NUMBER + "=?";

		int delete = db.delete(BlackListDb.BlackList.TABLE_NAME, whereClause,
				new String[] { number });
		db.close();
		return delete != 0;
	}

	/**
	 * 修改数据
	 * 
	 * @param number
	 * @param type
	 * @return
	 */
	public boolean updata(String number, int type) {
		SQLiteDatabase db = mHelp.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(BlackListDb.BlackList.COLUMN_TYPE, type);
		String whereClause = BlackListDb.BlackList.COLUMN_NUMBER + "=?";

		int update = db.update(BlackListDb.BlackList.TABLE_NAME, values,
				whereClause, new String[] { number });

		db.close();
		return update != 0;
	}

	/**
	 * 查找类型
	 * 
	 * @param number
	 * @return
	 */
	public int findType(String number) {
		SQLiteDatabase rd = mHelp.getReadableDatabase();
		String sql = "select  " + BlackListDb.BlackList.COLUMN_TYPE + " from "
				+ BlackListDb.BlackList.TABLE_NAME + "  where "
				+ BlackListDb.BlackList.COLUMN_NUMBER + "=?";
		Cursor cursor = rd.rawQuery(sql, new String[] { number });
		int type = -1;
		if (cursor != null) {
			if (cursor.moveToNext()) {
				type = cursor.getInt(0);
				cursor.close();
			}
		}
		return type;
	}

	/**
	 * 查找所有的数据
	 * 
	 * @return
	 */
	public List<CallSmsSafeInfo> getAllInfo() {
		SQLiteDatabase rd = mHelp.getReadableDatabase();
		String sql = "select  *" + " from " + BlackListDb.BlackList.TABLE_NAME;
		Cursor cursor = rd.rawQuery(sql, null);
		List<CallSmsSafeInfo> list = new ArrayList<CallSmsSafeInfo>();
		CallSmsSafeInfo info = null;
		while (cursor.moveToNext()) {
			String num = cursor.getString(1);
			int type = cursor.getInt(2);
			info = new CallSmsSafeInfo();
			info.phoneNumber = num;
			info.type = type;
			list.add(info);
		}

		return list;
	}

	/**
	 * 查找部分的数据
	 * 
	 * @param perPageSize
	 *            每次需要查找的数目
	 * @param index
	 *            从index 的角标开始查找 , 数据库的角标是从零开始的
	 * @return
	 */
	public List<CallSmsSafeInfo> findPart(int perPageSize, int index) {

		SQLiteDatabase rd = mHelp.getReadableDatabase();
		String sql = "select  " + BlackListDb.BlackList.COLUMN_NUMBER + ","
				+ BlackListDb.BlackList.COLUMN_TYPE + " from "
				+ BlackListDb.BlackList.TABLE_NAME + " limit " + perPageSize
				+ " offset " + index;
		Cursor cursor = rd.rawQuery(sql, null);
		List<CallSmsSafeInfo> list = new ArrayList<CallSmsSafeInfo>();
		CallSmsSafeInfo info = null;
		if (cursor != null) {
			while (cursor.moveToNext()) {
				String num = cursor.getString(0);
				int type = cursor.getInt(1);
				info = new CallSmsSafeInfo();
				info.phoneNumber = num;
				info.type = type;
				list.add(info);
			}
			cursor.close();
		}
		rd.close();
		return list;
	}

}
