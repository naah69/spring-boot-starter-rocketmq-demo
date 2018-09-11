package com.naah69.rocketmqconsumerdemo;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.starter.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.starter.core.RocketMQListener;
import org.springframework.stereotype.Service;

/**
 * LogConsumer
 *
 * @author naah
 * @date 2018-09-10 上午11:30
 * @desc
 */
@Slf4j
@Service
@RocketMQMessageListener(topic = "log_str_demo", consumerGroup = "log_str_demo")
public class LogStringConsumer implements RocketMQListener<String> {
    @Override
    public void onMessage(String message) {
        LogVO object = JSON.parseObject(message, LogVO.class);
        log.info(object.toString());
    }
}
