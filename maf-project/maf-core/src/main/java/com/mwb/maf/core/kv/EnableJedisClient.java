package com.mwb.maf.core.kv;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 举个栗子：
 *
 * @EnableJedisClient(namespace = "redis1")
 * 创建一个JedisClient, 其bean的名字为redis1JedisClient, 在代码里这样使用即可
 * @Autoware private JedisClient redis1JedisClient;
 * ps:
 * @EnableJedisClient() 等同于 -> @EnableJedisClient(namespace = "default")
 * @Autoware private JedisClient defaultJedisClient;
 * <p>
 * 配置说明:
 * - redis ip: ${app.jedis.jedis1.address}
 * - redis port: ${app.jedis.jedis1.port}
 * - redis connection pool: prefix = app.jedis.jedis1.pool - 能配置的值请参考{@link redis.clients.jedis.JedisPoolConfig}
 * 默认配置值请参考{@link BaseJedisConfiguration}
 * <p>
 * 原理:
 * 其等价于
 * 创建一个JedisPoolConfig绑定配置prefix=app.jedis.jedis1.pool
 * 创建一个JedisClient绑定配置address=app.jedis.jedis1.address, port=app.jedis.jedis1.port
 * 注册该JedisClient到spring容器并指定beanName=jedisClient1, autowire=By_Name
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(EnableJedisClients.class)
@Import(JedisClientRegistrar.class)
public @interface EnableJedisClient {
    String namespace() default "default";
}