package com.acoreful.xboot.admin.test;

import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.acoreful.xboot.admin.BaseTests;
import com.acoreful.xboot.admin.common.redismq.RedisCloseOrderMq;

public class RedisCloseOrderMqTests extends BaseTests{
	@Autowired
	private RedisCloseOrderMq redisCloseOrderMq;
	
	@Test
	public void testInitData() throws Exception {
		
		for (int i = 0; i < 20; i++) {
			String orderNo=String.format("%09d", i);
			redisCloseOrderMq.add(orderNo, new Date());
			System.out.println(orderNo);
			Thread.sleep(1000);
		}
		redisCloseOrderMq.print();
	}
	@Test
	public void testPrintAll() throws Exception {
		
		redisCloseOrderMq.printAll();
	}
	@Test
	public void testGet() throws Exception {
		redisCloseOrderMq.get();;
	}
}
