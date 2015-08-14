package com.it.mobilesafe.test;

import android.test.AndroidTestCase;

import com.it.mobilesafe.db.BlackDao;

public class BlackDaoTest extends AndroidTestCase {
	
	
	BlackDao dao ;
	
	
	
	public void testAdd() {
		dao = new BlackDao(getContext());
		boolean add = false;
		for (int i = 10; i < 200; i++) {
			 add = dao.add("1314"+i, 0);
		}
		
		assertEquals(true, add);
	}
	
	public void testDelete() {
		dao = new BlackDao(getContext());
		boolean delete = dao.delete("13143");
		assertEquals(true, delete);
	}
	
	public void testUpdate(){
		dao = new BlackDao(getContext());
		boolean update = dao.update("1314", 2);
		assertEquals(true, update);
	}
	
	public void testQuery() {
		dao = new BlackDao(getContext());
		int type = dao.findType("1314");
		assertEquals(2, type);
	}
}
