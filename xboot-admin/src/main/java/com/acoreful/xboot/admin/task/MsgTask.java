package com.acoreful.xboot.admin.task;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.acoreful.xboot.admin.redismq.RedisMQ;


@Component
public class MsgTask {

    @Resource private RedisMQ redisMQ;
    // @Value("${mq.list.first}") private String MQ_LIST_FIRST;

    @Scheduled(cron="*/5 * * * * *")
    public void sendMsg() {
        // 消费
        List<String> msgs = redisMQ.consume(redisMQ.getRoutes().get(0).getList());
        int len;
        if (null != msgs && 0 < (len = msgs.size())) {
            // 将每一条消息转为JSONObject
            for (int i = 0; i < len; i++) {
                if (!StringUtils.isEmpty(msgs.get(i))) {
                    // 取出消息
                    System.out.println(msgs.get(i));
                }
            }
        }
    }
}