package com.it.mobilesafe.test;

import com.it.mobilesafe.db.AntiVirusDao;

import android.test.AndroidTestCase;

public class AntiVirusDaoTest extends AndroidTestCase {
	
	public void testAdd() {
		String md5 = "feb25ebdc88ac85a7ff20e07c2b65c0a";
		boolean add = AntiVirusDao.add(getContext(), md5);
		
		assertEquals(true, add);
	}
	
}
