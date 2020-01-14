package com.mwb.maf.core.rocketmq.core.consumer;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;

/**
 * @author hepengyuan
 * @version create time: 2018年10月25日 上午11:04:18
 */
public class RocketMQConsumerConfig extends DefaultMQPushConsumer {

    public static final String DEFAULT_CONSUMER_GROUP = "consumerDefaultGroup";

    private String subExpression = "*";

    public String getSubExpression() {
        return subExpression;
    }

    public void setSubExpression(String subExpression) {
        this.subExpression = subExpression;
    }

}
