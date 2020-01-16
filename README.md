## 《使用说明》 --ZhangRongBin/HePengYuan
#### 一、数据源
##### 根据namespace和mapperPackages来区分不同数据源
######  1、启动注解，namespace默认default
```
@EnableDataSource(namespace = "mall", mapperPackages = "com.mwb.app.sample.db.mall.mapper")
@EnableDataSource(mapperPackages = "com.mwb.app.sample.db.mapper")
```
###### 2、配置文件，固定前缀app.db.#namespace#.*
```
#mall数据源
app.db.mall.data-source.url=jdbc:mysql://127.0.0.1:3306/mwb?useUnicode=true&amp;characterEncoding=UTF-8&amp;autoReconnect=true
app.db.mall.data-source.username=admin
app.db.mall.data-source.password=admin
app.db.mall.type-aliases-package=com.mwb.app.sample.db
app.db.mall.data-source.initial-size=2
app.db.mall.data-source.max-active=10
app.db.mall.data-source.min-idle=2
#默认数据源
app.db.default.data-source.url=jdbc:mysql://127.0.0.1:3306/mwb?useUnicode=true&amp;characterEncoding=UTF-8&amp;autoReconnect=true
app.db.default.data-source.username=admin
app.db.default.data-source.password=admin
app.db.default.type-aliases-package=com.mwb.app.sample.db
app.db.default.data-source.initial-size=2
app.db.default.data-source.max-active=10
app.db.default.data-source.min-idle=2
```
#### 二、redis
##### 多个redis根据namespace区分
###### 1、启动注解，namespace默认default
```
@EnableJedisClient
@EnableJedisClient(namespace = "mall"）
```
###### 2、配置文件，固定前缀app.jedis.#namespace#.*
```
app.jedis.default.address=127.0.0.1
app.jedis.default.port=6379
app.jedis.default.pool.max-total=300
app.jedis.default.pool.max-idle=15
app.jedis.default.pool.min-idle=15
app.jedis.default.pool.max-wait-millis=6000
app.jedis.default.pool.test-on-borrow=false
app.jedis.default.pool.test-on-create=false
app.jedis.default.pool.test-on-return=true
app.jedis.default.pool.test-while-idle=true
app.jedis.mall.address=127.0.0.1
...
```
###### 3、使用，beanName= #namespace# + JedisClient
```
 @Autowired
    private JedisClient jedisClient;
    
 @Autowired
 @Qualifier("mallJedisClient")
    private JedisClient mallJedisClient;

```
#### 三、redisCluster
##### 多个redisCluster根据namespace区分
###### 1、启动注解，namespace默认default
```
@EnableJedisClusterClient(namespace = "mwb")
@EnableJedisClusterClient()
```
###### 2、配置文件，集群固定前缀app.jedis-cluster.#namespace#.*
```
app.jedis-cluster.mwb.address=127.0.0.1:7001,127.0.0.1:7002,127.0.0.1:7003
app.jedis-cluster.mwb.pool.max-total=300
app.jedis-cluster.mwb.pool.max-idle=12
app.jedis-cluster.mwb.pool.min-idle=10
app.jedis-cluster.mwb.pool.max-wait-millis=6000
app.jedis-cluster.default.address=127.0.0.1:7001,127.0.0.1:7002,127.0.0.1:7003
...
```
###### 3、使用，beanName= #namespace# + JedisClusterClient
```
 @Autowired
    private JedisClusterClient jedisClusterClient;
    
 @Autowired
 @Qualifier("mwbJedisClusterClient")
    private JedisClusterClient mwbJedisClusterClient;

```
#### 四、Rpc-motan
##### 多个prc调用根据namespace区分
###### 1、启动注解，namespace默认default
```
#服务方调用方都需使用@EnableMotan
@EnableMotan(namespace = "user")
@EnableMotan(namespace = "mall")
@EnableMotan(namespace = "own")
```
###### 2、配置文件，集群固定前缀app.motan.#namespace#.*(motan配置属性)
```
#服务方
app.motan.registry.address=127.0.0.1:2181
app.motan.user.basic-service.actives=0
app.motan.user.basic-service.application=userRpc
app.motan.user.basic-service.group=user_rpc
app.motan.user.basic-service.check=false
app.motan.user.basic-service.module=server
app.motan.user.basic-service.request-timeout=3003
app.motan.user.basic-service.retries=3
app.motan.user.basic-service.version=1.0.0
app.motan.user.port=9010
#调用方
#只需配一个zk
app.motan.registry.address=127.0.0.1:2181:2181
app.motan.user.basic-referer.group=user_rpc
app.motan.user.basic-referer.version=1.0.0
app.motan.mall.basic-referer.group=mall_rpc
```
###### 3、使用，beanName= #namespace# + BasicRefererConfigBean
```
#服务方
@MotanService(basicService = "userBasicServiceConfigBean")
public class UserApiImpl implements UserApi{}

#调用方
  @MotanReferer(basicReferer = "userBasicRefererConfigBean")
    private UserApi userApi;
    
 @MotanReferer(basicReferer = "mallBasicRefererConfigBean")
    private MallApi mallApi;

、、、