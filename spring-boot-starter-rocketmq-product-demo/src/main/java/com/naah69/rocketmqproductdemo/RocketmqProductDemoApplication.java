package com.naah69.rocketmqproductdemo;

import com.alibaba.fastjson.JSON;
import org.apache.rocketmq.spring.starter.core.RocketMQTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Random;

@SpringBootApplication
public class RocketmqProductDemoApplication implements CommandLineRunner {
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public static void main(String[] args) {
        SpringApplication.run(RocketmqProductDemoApplication.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        this.objProducter();
        this.stringProducter();
    }

    public void objProducter() {
        Random random = new Random();
        for (int i=0;i<10;i++){
            LogVO log = generateLog(random, i);
            //默认使用jackson的ObjectMapper进行序列化
            //it use ObjectMapper of jackson to serializable
            rocketMQTemplate.convertAndSend("log_obj_demo", log);
        }
    }

    public void stringProducter() {
        Random random = new Random();
        for (int i=0;i<10;i++){
            LogVO log = generateLog(random, i);
            String json= JSON.toJSONString(log);
            rocketMQTemplate.convertAndSend("log_str_demo", json);
        }
    }

    private LogVO generateLog(Random random, int i) {
        return new LogVO(0L, LocalDateTime.now(), random.nextInt() % 2 == 0 ? LogVO.LogKind.add : LogVO.LogKind.delete, "", LocalDateTime.now(), null);
    }
}
