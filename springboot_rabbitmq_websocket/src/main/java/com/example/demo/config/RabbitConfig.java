package com.example.demo.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;

import java.util.HashMap;
import java.util.Map;

/**
 * 死信队列的配置文件
 */
@Configuration
public class RabbitConfig {

    @Value("${sunspring.order.exchange}")
    private String orderExchange;

    @Value("${sunspring.order.queue}")
    private String orderQueue;

    @Value("${sunspring.order.routingKey}")
    private String orderRoutingKey;

    @Value("${sunspring.dlx.exchange}")
    private String dlxExchange;

    @Value("${sunspring.dlx.queue}")
    private String dlxQueue;

    @Value("${sunspring.dlx.routingKey}")
    private String dlxRoutingKey;

    /**
     * 声明死信队列
     * @return DirectExchange
     */
    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(dlxExchange);
    }

    /**
     * 声明死信队列
     * @return Queue
     */
    @Bean
    public Queue dlxQueue() {
        return new Queue(dlxQueue);
    }

    /**
     * 绑定死信队列到死信交换机
     * @return Binding
     */
    @Bean
    public Binding binding() {
        return BindingBuilder.bind(dlxQueue())
                .to(dlxExchange())
                .with(dlxRoutingKey);
    }

    /**
     * 声明订单业务交换机
     * @return DirectExchange
     */
    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(orderExchange);
    }

    /**
     * 声明订单业务队列
     * @return Queue
     */
    @Bean
    public Queue orderQueue() {
        Map<String,Object> arguments = new HashMap<>(2);
        // 绑定该队列到死信交换机
        arguments.put("x-dead-letter-exchange",dlxExchange);//x-dead-letter-exchange：死信交换机的名称
        arguments.put("x-dead-letter-routing-key",dlxRoutingKey);//x-dead-letter-routing-key：死信交换机的路由键，因为demo中两个交换机的类型都是direct的，因此路由键必须相同。
        return new Queue(orderQueue,true,false,false,arguments);
    }

    /**
     * 绑定订单队列到订单交换机
     * @return Binding
     */
    @Bean
    public Binding orderBinding() {
        return BindingBuilder.bind(orderQueue())
                .to(orderExchange())
                .with(orderRoutingKey);
    }
}

