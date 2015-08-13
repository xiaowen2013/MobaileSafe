package cn.itcase.safe.text;

import android.test.AndroidTestCase;
import cn.itcase.safe.db.AppLockDao;

public class TextLock extends AndroidTestCase {

	public void add(){
		AppLockDao da = new AppLockDao(getContext());
		assertEquals(true, da.add("xiaomishangx"));
	}
	public void delect(){
		AppLockDao da = new AppLockDao(getContext());
		assertEquals(true, da.delect("xiaomishangx"));
	}
	public void ray(){
		AppLockDao da = new AppLockDao(getContext());
		assertEquals(false, da.select("xiaomishangx"));
	}
}
