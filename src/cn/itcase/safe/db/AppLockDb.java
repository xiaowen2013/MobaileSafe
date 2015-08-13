package cn.itcase.safe.db;

public interface AppLockDb {

	String DB_NAME = "lock.db";
	int VERSION = 1;

	public interface AppLockList {
		String TABLE_NAME = "lock_list";
		String COLUMN_NAME = "name";
		String COLUMN_ID = "_id";

		// create tableblack_list(_idinteger primary key
		// autoincrement,number text unique,type integer)

		String SQL = "create table " + TABLE_NAME + "(" + COLUMN_ID
				+ " integer primary key autoincrement, " + COLUMN_NAME
				+ " text unique " + ")";
	}
}
