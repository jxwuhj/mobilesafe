package com.it.mobilesafe.test;

import com.it.mobilesafe.db.AppLockDao;

import android.test.AndroidTestCase;

public class AppLockTest extends AndroidTestCase {

	public void testAdd() {
		AppLockDao dao = new AppLockDao(getContext());
		
		boolean add = dao.add("xxx.wushi.org");
		assertEquals(true, add);
	}
	
	public void testFind() {
		AppLockDao dao = new AppLockDao(getContext());
		boolean findLock = dao.findLock("xxx.wushi.org");
		assertEquals(true, findLock);
	}
	
	public void testDelete() {
		
		AppLockDao dao = new AppLockDao(getContext());
		boolean delete = dao.delete("xxx.wushi.org");
		assertEquals(true, delete);
	}
}
