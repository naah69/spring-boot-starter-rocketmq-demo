package com.naah69.rocketmqconsumerdemo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
//一定要提供无参数构造，否则报错
//must have the @NoArgsConstructor,otherwise error
@NoArgsConstructor
/**
 * LogVO
 *
 * @author naah
 * @date 2018-09-10 上午10:46
 * @desc
 */
public class LogVO implements Serializable {
    private Long id;

    //jackson原生无法序列化jdk1.8提供的日期时间类
    //jackson can't serializable the new Local Date or Time Class of JDK8,so we need do it yourself

    //1.自定义序列化和反序列化工具类
    //1.Custom Serialization and Deserialize Tools

    //2.使用阿里的fastjson进行序列化，发送字符串
    //2.use fastjson to Serialization,and send String
    @JsonDeserialize(using = LocalDateTimeTimestampDeserialize.class)
    private LocalDateTime time;
    private LogKind kind;
    private String context;

    @JsonDeserialize(using = LocalDateTimeTimestampDeserialize.class)
    private LocalDateTime createTime;

    @JsonDeserialize(using = LocalDateTimeTimestampDeserialize.class)
    private LocalDateTime updateTime;

    public enum LogKind {
        //添加类型
        add("add"),

        //修改类型
        update("update"),

        //删除类型
        delete("delete");

        private final String kind;

        private LogKind(String kind) {
            this.kind = kind;
        }

        public String getKind() {
            return kind;
        }
    }

}
