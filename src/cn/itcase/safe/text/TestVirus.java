package cn.itcase.safe.text;

import cn.itcase.safe.db.AntiVirusDao;
import android.test.AndroidTestCase;

public class TestVirus extends AndroidTestCase {

	public void select() {
		boolean virus = AntiVirusDao.isVirus(getContext(),
				"540e8b5fdff054be1831cfbb4cdef7f0");
		assertEquals(true, virus);
	}
}
