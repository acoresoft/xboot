package com.acoreful.xboot.admin.common.redismq;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

@Component
public class RedisCloseOrderMq {
	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	private final String TASK_ORDER_CLOSE = "task:order:close";

	public void add(String orderNo, Date date) {
		stringRedisTemplate.opsForZSet().add(TASK_ORDER_CLOSE, orderNo, date.getTime());
	}

	public void print() {
		System.out.println(stringRedisTemplate.opsForZSet().range(TASK_ORDER_CLOSE, 0, -1));
	}

	public void printAll() {
		Set<ZSetOperations.TypedTuple<String>> tuples = stringRedisTemplate.opsForZSet()
				.rangeWithScores(TASK_ORDER_CLOSE, 0, -1);
		Iterator<ZSetOperations.TypedTuple<String>> iterator = tuples.iterator();
		while (iterator.hasNext()) {
			ZSetOperations.TypedTuple<String> typedTuple = iterator.next();
			System.out.println("value:" + typedTuple.getValue() + ",score:" + typedTuple.getScore());
		}
	}

	public void get() {
		this.printAll();
		System.out.println("================================");
		Set<ZSetOperations.TypedTuple<String>> tuples = stringRedisTemplate.opsForZSet()
				.rangeByScoreWithScores(TASK_ORDER_CLOSE, 0, new Date().getTime(), 0, 1);
		Iterator<ZSetOperations.TypedTuple<String>> iterator = tuples.iterator();
		while (iterator.hasNext()) {
			ZSetOperations.TypedTuple<String> typedTuple = iterator.next();
			System.out.println("value:" + typedTuple.getValue() + ",score:" + typedTuple.getScore());
		}
	}

	public void remove(String orderNo) {
		stringRedisTemplate.opsForZSet().remove(TASK_ORDER_CLOSE, orderNo);
	}

}
