package cn.itcase.safe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class AppLockDbHelp extends SQLiteOpenHelper {

	public AppLockDbHelp(Context context) {
		super(context, AppLockDb.DB_NAME, null, AppLockDb.VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
		// 创建数据库表
		db.execSQL(AppLockDb.AppLockList.SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
