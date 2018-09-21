[官网GitHub地址](https://github.com/apache/rocketmq-externals/blob/master/rocketmq-spring-boot-starter/README_zh_CN.md)
,这个文档超级坑，特别是里面的辣鸡Demo,全是错

[本文Demo源码](https://github.com/naah69/spring-boot-starter-rocketmq-demo)
# 1 Maven依赖
文档中的`Maven`依赖,引用后无法下载jar包,因为Apach没有将这个jar包发布到中央仓库，所以需要我们自己手动编译。

现在你可以在[releases页面](https://github.com/naah69/spring-boot-starter-rocketmq-demo/releases)下载Jar包，并把这个目录复制到你的maven本地仓库中，然后在`pom.xml`中加入下列依赖
```xml
<!--在pom.xml中添加依赖-->
<dependency>
    <groupId>org.apache.rocketmq</groupId>
    <artifactId>spring-boot-starter-rocketmq</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

# 2 PO类
由于该starter默认使用`jackson`进行序列化

`jackson`原生无法序列化(序列化后编程一个json对象)`JDK1.8`提供的`LocalDate`、`LocalDateTime`等

要想序列化`LocalDate`、`LocalDateTime`等，有两方案
1. 自定义序列化和反序列化工具类
2. 使用阿里的fastjson进行序列化，发送字符串

## 2.1 LogVO
```java
@Data
@AllArgsConstructor
//一定要提供无参数构造，否则报错
@NoArgsConstructor
public class LogVO implements Serializable {
     private Long id;

    //jackson原生无法序列化jdk1.8提供的日期时间类

    //1.自定义序列化和反序列化工具类

    //2.使用阿里的fastjson进行序列化，发送字符串

    //Product中加入这个@JsonSerialize注解
    @JsonSerialize(using = LocalDateTimeTimestampSerializer.class)
    //consumer中加入这个@JsonDeserialize注解
    @JsonDeserialize(using = LocalDateTimeTimestampDeserialize.class)
    private LocalDateTime time;
    private LogKind kind;
    private String context;

    //Product中加入这个@JsonSerialize注解
    @JsonSerialize(using = LocalDateTimeTimestampSerializer.class)
    //consumer中加入这个@JsonDeserialize注解
    @JsonDeserialize(using = LocalDateTimeTimestampDeserialize.class)
    private LocalDateTime createTime;

    //Product中加入这个@JsonSerialize注解
    @JsonSerialize(using = LocalDateTimeTimestampSerializer.class)
    //consumer中加入这个@JsonDeserialize注解
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

```

## 2.2 LocalDateTimeTimestampSerializer
在Product中加入这个序列化类
```java
public class LocalDateTimeTimestampSerializer extends JsonSerializer<LocalDateTime> {
    @Override
    public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {

        jsonGenerator.writeString(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli()+"");
    }
}
```
## 2.3 LocalDateTimeTimestampDeserialize
在Consumer中加入这个反序列化类
```java
public class LocalDateTimeTimestampDeserialize extends JsonDeserializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String text = p.getText();
        long timestamp = Long.parseLong(text);
        return LocalDateTime.ofEpochSecond(timestamp/1000,0, ZoneOffset.ofHours(8));
    }
}
```
# 3 发送消息
```properties
## application.properties
# 不是spring.rocketmq.name-server
spring.rocketmq.nameServer=127.0.0.1:9876
spring.rocketmq.producer.group=my-group
```
```yml
## application.yml
spring:
  rocketmq:
    nameServer: 127.0.0.1:9876
    producer:
      group: my-group

```
>注意：
>
>请将上述示例配置中的127.0.0.1:9876替换成真实RocketMQ的NameServer地址与端口

```java
@SpringBootApplication
public class RocketmqProducterApplication implements CommandLineRunner {
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

```
>更多发送相关配置
```
##application.properties
spring.rocketmq.producer.retry-times-when-send-async-failed=0
spring.rocketmq.producer.send-msg-timeout=300000
spring.rocketmq.producer.compress-msg-body-over-howmuch=4096
spring.rocketmq.producer.max-message-size=4194304
spring.rocketmq.producer.retry-another-broker-when-not-store-ok=false
spring.rocketmq.producer.retry-times-when-send-failed=2
```
```yml
#application.yml
spring:
  rocketmq:
    producer:
      retry-another-broker-when-not-store-ok: false
      max-message-size: 4194304
      compress-msg-body-over-howmuch: 4096
      send-msg-timeout: 300000
      retry-times-when-send-async-failed: 0
      retry-times-when-send-failed: 2

```


# 4 接收消息
```properties
## application.properties
# 不是spring.rocketmq.name-server
spring.rocketmq.nameServer=127.0.0.1:9876
```
```yml
## application.yml
spring:
  rocketmq:
    nameServer: 127.0.0.1:9876
```
>注意：
>
>请将上述示例配置中的127.0.0.1:9876替换成真实RocketMQ的NameServer地址与端口
```java
@Slf4j
@Service
@RocketMQMessageListener(topic = "log_obj_demo", consumerGroup = "log_obj_demo")
public class LogObjConsumer implements RocketMQListener<LogVO> {
    @Override
    public void onMessage(LogVO message) {
        log.info(message.toString());
    }
}

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
```
