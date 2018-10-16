package com.acoreful.xboot.admin.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

import com.acoreful.xboot.admin.BaseTests;

/**
 * Redis的String数据结构 （推荐使用StringRedisTemplate）
 * @author acoresoft
 *
 */
public class RedisStringTests extends BaseTests{
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	@Test
	public void testName() throws Exception {
		stringRedisTemplate.opsForValue().set("name", "JACK");
		System.out.println(stringRedisTemplate.opsForValue().get("name"));
		//由于设置的是10秒失效，十秒之内查询有结果，十秒之后返回为null
		stringRedisTemplate.opsForValue().set("name","tom",1, TimeUnit.SECONDS);
		Thread.sleep(2*1000+200);
		System.out.println(stringRedisTemplate.opsForValue().get("name"));
		
		//该方法是用 value 参数覆写(overwrite)给定 key 所储存的字符串值，从偏移量 offset 开始
		stringRedisTemplate.opsForValue().set("key","hello world");
		stringRedisTemplate.opsForValue().set("key","redis", 6);
        System.out.println("***************"+stringRedisTemplate.opsForValue().get("key"));
        
        //
        System.out.println(stringRedisTemplate.opsForValue().setIfAbsent("multi1","multi1"));//false  multi1之前已经存在
        System.out.println(stringRedisTemplate.opsForValue().setIfAbsent("multi111","multi111"));//true  multi111之前不存在
	}
	
	@Test
	public void testExample2() throws Exception {
		//为多个键分别设置它们的值
		Map<String,String> maps = new HashMap<String, String>();
        maps.put("multi1","multi1");
        maps.put("multi2","multi2");
        maps.put("multi3","multi3");
        stringRedisTemplate.opsForValue().multiSet(maps);
        List<String> keys = new ArrayList<String>();
        keys.add("multi1");
        keys.add("multi2");
        keys.add("multi3");
        System.out.println(stringRedisTemplate.opsForValue().multiGet(keys));
	}
	@Test
	public void testExample3() throws Exception {
		//为多个键分别设置它们的值，如果存在则返回false，不存在返回true
		Map<String,String> maps = new HashMap<String, String>();
        maps.put("multi11","multi11");
        maps.put("multi22","multi22");
        maps.put("multi33","multi33");
        Map<String,String> maps2 = new HashMap<String, String>();
        maps2.put("multi1","multi1");
        maps2.put("multi2","multi2");
        maps2.put("multi3","multi3");
        System.out.println(stringRedisTemplate.opsForValue().multiSetIfAbsent(maps));
        System.out.println(stringRedisTemplate.opsForValue().multiSetIfAbsent(maps2));
	}
	@Test
	public void testExample4() throws Exception {
		stringRedisTemplate.opsForValue().set("key","hello world");
        System.out.println("***************"+stringRedisTemplate.opsForValue().get("key"));
		//设置键的字符串值并返回其旧值
        stringRedisTemplate.opsForValue().set("getSetTest","test");
        System.out.println(stringRedisTemplate.opsForValue().getAndSet("getSetTest","test2"));
        //为多个键分别取出它们的值
        Map<String,String> maps = new HashMap<String, String>();
        maps.put("multi1","multi1");
        maps.put("multi2","multi2");
        maps.put("multi3","multi3");
        stringRedisTemplate.opsForValue().multiSet(maps);
        List<String> keys = new ArrayList<String>();
        keys.add("multi1");
        keys.add("multi2");
        keys.add("multi3");
        System.out.println(stringRedisTemplate.opsForValue().multiGet(keys));

	}
	@Test
	public void testExample5() throws Exception {
		//支持整数
		stringRedisTemplate.delete("increlong");
		stringRedisTemplate.opsForValue().increment("increlong",1);
		System.out.println("***************"+stringRedisTemplate.opsForValue().get("increlong"));
		
	}
	@Test
	public void testExample6() throws Exception {
		//支持整数
		stringRedisTemplate.delete("increlong");
		stringRedisTemplate.opsForValue().increment("increlong",1);
        System.out.println("***************"+stringRedisTemplate.opsForValue().get("increlong"));
        //支持浮点数
        stringRedisTemplate.opsForValue().increment("increlong",1.2);
        System.out.println("***************"+stringRedisTemplate.opsForValue().get("increlong"));
        stringRedisTemplate.opsForValue().increment("increlong",1.0);
        System.out.println("***************"+stringRedisTemplate.opsForValue().get("increlong"));
	}
	/**
	 * 如果key已经存在并且是一个字符串，则该命令将该值追加到字符串的末尾。如果键不存在，则它被创建并设置为空字符串，因此APPEND在这种特殊情况下将类似于SET
	 * @throws Exception
	 */
	@Test
	public void testExample7() throws Exception {
		stringRedisTemplate.delete("appendTest");
		stringRedisTemplate.opsForValue().append("appendTest","Hello");
        System.out.println(stringRedisTemplate.opsForValue().get("appendTest"));
        stringRedisTemplate.opsForValue().append("appendTest","world");
        System.out.println(stringRedisTemplate.opsForValue().get("appendTest"));
        //截取key所对应的value字符串
        System.out.println("*********"+stringRedisTemplate.opsForValue().get("appendTest",0,5));
		System.out.println("*********"+stringRedisTemplate.opsForValue().get("appendTest",0,-1));
		System.out.println("*********"+stringRedisTemplate.opsForValue().get("appendTest",-3,-1));
//		//返回key所对应的value值得长度
		stringRedisTemplate.opsForValue().set("key","hello world");
		System.out.println("***************"+stringRedisTemplate.opsForValue().size("key"));

	}
	@Test
	public void testExample8() throws Exception {
		//对 key 所储存的字符串值，设置或清除指定偏移量上的位(bit)
		//key键对应的值value对应的ascii码,在offset的位置(从左向右数)变为value
		stringRedisTemplate.opsForValue().set("bitTest","a");
        // 'a' 的ASCII码是 97。转换为二进制是：01100001
        // 'b' 的ASCII码是 98  转换为二进制是：01100010
        // 'c' 的ASCII码是 99  转换为二进制是：01100011
        //因为二进制只有0和1，在setbit中true为1，false为0，因此我要变为'b'的话第六位设置为1，第七位设置为0
		stringRedisTemplate.opsForValue().setBit("bitTest",6, true);
		stringRedisTemplate.opsForValue().setBit("bitTest",7, false);
        System.out.println(stringRedisTemplate.opsForValue().get("bitTest"));
//        /获取键对应值的ascii码的在offset处位值
        System.out.println(stringRedisTemplate.opsForValue().getBit("bitTest",7));

	}
	//TODO Redis的List数据结构
	@Test
	public void testExample11() throws Exception {
		stringRedisTemplate.delete("list");
		stringRedisTemplate.opsForList().leftPushAll("list", "c#","c++", "python", "java", "c#", "c#");
		System.out.println(stringRedisTemplate.opsForList().size("list"));
		
		System.out.println(stringRedisTemplate.opsForList().range("list",0,-1));
		stringRedisTemplate.opsForList().trim("list",1,-1);//裁剪第一个元素
		System.out.println(stringRedisTemplate.opsForList().range("list",0,-1));
		System.out.println(stringRedisTemplate.opsForList().size("list"));
		
		stringRedisTemplate.opsForList().leftPush("list","java");
		stringRedisTemplate.opsForList().leftPush("list","python");
		stringRedisTemplate.opsForList().leftPush("list","c++");
		stringRedisTemplate.opsForList().set("list", 2, "323232323232323232323232323232");
		System.out.println(stringRedisTemplate.opsForList().range("list",0,-1));
	}
	@Test
	public void testExample12() throws Exception {
		stringRedisTemplate.delete("listRight");
		stringRedisTemplate.opsForList().rightPush("listRight","java");
		stringRedisTemplate.opsForList().rightPush("listRight","setValue");
		stringRedisTemplate.opsForList().rightPush("listRight","python");
		stringRedisTemplate.opsForList().rightPush("listRight","c++");
		//从存储在键中的列表中删除等于值的元素的第一个计数事件。
		//计数参数以下列方式影响操作：
		//count> 0：删除等于从头到尾移动的值的元素。
		//count <0：删除等于从尾到头移动的值的元素。
		//count = 0：删除等于value的所有元素。
		System.out.println(stringRedisTemplate.opsForList().range("listRight",0,-1));
		stringRedisTemplate.opsForList().remove("listRight",1,"setValue");//将删除列表中存储的列表中第一次次出现的“setValue”。
        System.out.println(stringRedisTemplate.opsForList().range("listRight",0,-1));
		//index 根据下表获取列表中的值，下标是从0开始的
        System.out.println(stringRedisTemplate.opsForList().index("listRight",2));
	}
	@Test
	public void testExample13() throws Exception {
		stringRedisTemplate.delete("listRight");
		stringRedisTemplate.opsForList().rightPush("listRight","java");
		stringRedisTemplate.opsForList().rightPush("listRight","setValue");
		stringRedisTemplate.opsForList().rightPush("listRight","python");
		stringRedisTemplate.opsForList().rightPush("listRight","c++");
		//从存储在键中的列表中删除等于值的元素的第一个计数事件。
		//计数参数以下列方式影响操作：
		//count> 0：删除等于从头到尾移动的值的元素。
		//count <0：删除等于从尾到头移动的值的元素。
		//count = 0：删除等于value的所有元素。
		System.out.println(stringRedisTemplate.opsForList().range("listRight",0,-1));
		stringRedisTemplate.opsForList().remove("listRight",1,"setValue");//将删除列表中存储的列表中第一次次出现的“setValue”。
		System.out.println(stringRedisTemplate.opsForList().range("listRight",0,-1));
		//index 根据下表获取列表中的值，下标是从0开始的
		System.out.println(stringRedisTemplate.opsForList().index("listRight",2));
		//弹出最左边的元素，弹出之后该值在列表中将不复存在
		System.out.println(stringRedisTemplate.opsForList().leftPop("listRight"));
		System.out.println(stringRedisTemplate.opsForList().leftPop("listRight",1,TimeUnit.MICROSECONDS));
		
		//用于移除列表的最后一个元素，并将该元素添加到另一个列表并返回。
		System.out.println("list="+stringRedisTemplate.opsForList().range("list",0,-1));
		stringRedisTemplate.opsForList().rightPopAndLeftPush("list","rightPopAndLeftPush");
		System.out.println("list="+stringRedisTemplate.opsForList().range("list",0,-1));
		System.out.println("rightPopAndLeftPush="+stringRedisTemplate.opsForList().range("rightPopAndLeftPush",0,-1));
	}
	@Test
	public void testExample14() throws Exception {
		stringRedisTemplate.opsForHash().put("redisHash","name","tom");
		stringRedisTemplate.opsForHash().put("redisHash","age","26");
		stringRedisTemplate.opsForHash().put("redisHash","class","6");
		stringRedisTemplate.opsForHash().increment("redisHash", "age", 1);
		System.out.println(stringRedisTemplate.opsForHash().get("redisHash", "age"));
		System.out.println(stringRedisTemplate.opsForHash().keys("redisHash"));
		System.out.println(stringRedisTemplate.opsForHash().values("redisHash"));
		System.out.println(stringRedisTemplate.opsForHash().entries("redisHash"));
		
		
		Cursor<Map.Entry<Object, Object>> curosr = stringRedisTemplate.opsForHash().scan("redisHash", ScanOptions.NONE);
        while(curosr.hasNext()){
            Map.Entry<Object, Object> entry = curosr.next();
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
	}
	//Redis的Set数据结构
	@Test
	public void testExample15() throws Exception {
		String[] strarrays = new String[]{"strarr1","sgtarr2"};
        System.out.println(stringRedisTemplate.opsForSet().add("setTest", strarrays));
        stringRedisTemplate.opsForSet().remove("setTest", "strarr1");
	}
	@Test
	public void testExample27() throws Exception {
		System.out.println(stringRedisTemplate.opsForZSet().add("zzset1","zset-1",1.0));
        System.out.println(stringRedisTemplate.opsForZSet().add("zzset1","zset-2",2.0));
        System.out.println(stringRedisTemplate.opsForZSet().add("zzset1","zset-3",3.0));
        System.out.println(stringRedisTemplate.opsForZSet().add("zzset1","zset-4",6.0));
 
        System.out.println(stringRedisTemplate.opsForZSet().add("zzset2","zset-1",1.0));
        System.out.println(stringRedisTemplate.opsForZSet().add("zzset2","zset-2",2.0));
        System.out.println(stringRedisTemplate.opsForZSet().add("zzset2","zset-3",3.0));
        System.out.println(stringRedisTemplate.opsForZSet().add("zzset2","zset-4",6.0));
        System.out.println(stringRedisTemplate.opsForZSet().add("zzset2","zset-5",7.0));
        System.out.println(stringRedisTemplate.opsForZSet().unionAndStore("zzset1","zzset2","destZset11"));
 
        Set<ZSetOperations.TypedTuple<String>> tuples = stringRedisTemplate.opsForZSet().rangeWithScores("destZset11",0,-1);
        Iterator<ZSetOperations.TypedTuple<String>> iterator = tuples.iterator();
        while (iterator.hasNext())
        {
            ZSetOperations.TypedTuple<String> typedTuple = iterator.next();
            System.out.println("value:" + typedTuple.getValue() + "score:" + typedTuple.getScore());
        }
	}
	@Test
	public void testExample28() throws Exception {
		//unionAndStore 计算给定的多个有序集的并集，并存储在新的 destKey中
		//System.out.println(template.opsForZSet().add("zzset1","zset-1",1.0));
        //System.out.println(template.opsForZSet().add("zzset1","zset-2",2.0));
        //System.out.println(template.opsForZSet().add("zzset1","zset-3",3.0));
        //System.out.println(template.opsForZSet().add("zzset1","zset-4",6.0));
        //
        //System.out.println(template.opsForZSet().add("zzset2","zset-1",1.0));
        //System.out.println(template.opsForZSet().add("zzset2","zset-2",2.0));
        //System.out.println(template.opsForZSet().add("zzset2","zset-3",3.0));
        //System.out.println(template.opsForZSet().add("zzset2","zset-4",6.0));
        //System.out.println(template.opsForZSet().add("zzset2","zset-5",7.0));
 
        System.out.println(stringRedisTemplate.opsForZSet().add("zzset3","zset-1",1.0));
        System.out.println(stringRedisTemplate.opsForZSet().add("zzset3","zset-2",2.0));
        System.out.println(stringRedisTemplate.opsForZSet().add("zzset3","zset-3",3.0));
        System.out.println(stringRedisTemplate.opsForZSet().add("zzset3","zset-4",6.0));
        System.out.println(stringRedisTemplate.opsForZSet().add("zzset3","zset-5",7.0));
 
        List<String> stringList = new ArrayList<String>();
        stringList.add("zzset2");
        stringList.add("zzset3");
        System.out.println(stringRedisTemplate.opsForZSet().unionAndStore("zzset1",stringList,"destZset22"));
 
        Set<ZSetOperations.TypedTuple<String>> tuples = stringRedisTemplate.opsForZSet().rangeWithScores("destZset22",0,-1);
        Iterator<ZSetOperations.TypedTuple<String>> iterator = tuples.iterator();
        while (iterator.hasNext())
        {
            ZSetOperations.TypedTuple<String> typedTuple = iterator.next();
            System.out.println("value:" + typedTuple.getValue() + "score:" + typedTuple.getScore());
        }

	}
	@Test
	public void testExample29() throws Exception {
		//intersectAndStore 计算给定的一个或多个有序集的交集并将结果集存储在新的有序集合 key 中
		System.out.println(stringRedisTemplate.opsForZSet().intersectAndStore("zzset1","zzset2","destZset33"));
		 
        Set<ZSetOperations.TypedTuple<String>> tuples = stringRedisTemplate.opsForZSet().rangeWithScores("destZset33",0,-1);
        Iterator<ZSetOperations.TypedTuple<String>> iterator = tuples.iterator();
        while (iterator.hasNext())
        {
            ZSetOperations.TypedTuple<String> typedTuple = iterator.next();
            System.out.println("value:" + typedTuple.getValue() + "score:" + typedTuple.getScore());
        }
	}
	@Test
	public void testExample30() throws Exception {
		
		//intersectAndStore 计算给定的一个或多个有序集的交集并将结果集存储在新的有序集合 key 中
		List<String> stringList = new ArrayList<String>();
        stringList.add("zzset2");
        stringList.add("zzset3");
        System.out.println(stringRedisTemplate.opsForZSet().intersectAndStore("zzset1",stringList,"destZset44"));
        Set<ZSetOperations.TypedTuple<String>> tuples = stringRedisTemplate.opsForZSet().rangeWithScores("destZset44",0,-1);
        Iterator<ZSetOperations.TypedTuple<String>> iterator = tuples.iterator();
        while (iterator.hasNext())
        {
            ZSetOperations.TypedTuple<String> typedTuple = iterator.next();
            System.out.println("value:" + typedTuple.getValue() + "score:" + typedTuple.getScore());
        }
		//遍历zset
		Cursor<TypedTuple<String>> cursor = stringRedisTemplate.opsForZSet().scan("zzset1", ScanOptions.NONE);
        while (cursor.hasNext()){
            ZSetOperations.TypedTuple<String> item = cursor.next();
            System.out.println(item.getValue() + ":" + item.getScore());
        }
	}
}
