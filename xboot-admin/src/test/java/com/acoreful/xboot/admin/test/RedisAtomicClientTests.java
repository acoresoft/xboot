package com.acoreful.xboot.admin.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.acoreful.xboot.admin.BaseTests;
import com.acoreful.xboot.admin.common.redis.lock.RedisAtomicClient;
import com.acoreful.xboot.admin.common.redis.lock.RedisLock;

public class RedisAtomicClientTests extends BaseTests {
	@Autowired
	private RedisAtomicClient redisAtomicClient;

	@Test
	public void testName() throws Exception {
		RedisLock lock = redisAtomicClient.getLock("lock-001", 10);
		System.out.println();
		;
		if(lock!=null) {
			
		}
		lock.unlock();
	}

	@Test
	public void testName1() throws Exception {
		try (RedisLock lock = redisAtomicClient.getLock("lock-001", 2)) {
			if (lock != null) {
				// lock succeed, do something
			}
		}
	}
}
