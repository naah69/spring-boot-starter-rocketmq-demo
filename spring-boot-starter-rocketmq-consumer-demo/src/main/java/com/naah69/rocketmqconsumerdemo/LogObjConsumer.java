package com.naah69.rocketmqconsumerdemo;

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
@RocketMQMessageListener(topic = "log_obj_demo", consumerGroup = "log_obj_demo")
public class LogObjConsumer implements RocketMQListener<LogVO> {
    @Override
    public void onMessage(LogVO message) {
        log.info(message.toString());
    }
}
