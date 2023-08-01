package com.restkeeper.operator.config;


import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ的配置类
 */
@Component
public class RabbitMQConfig {
  //我们一开始生成的商户号 shopId 密码 需要以短信的形式下发给租户
  //为了保证 下发给租户的信息不会丢失  可以把信息存入MQ队列 等用户缴费后再把消息推给他
  public static final String ACCOUNT_QUEUE = "account_queue"; //队列名称

  //租借我们系统的人可能成百上千
  //为了确保短信能准确的下发给指定的用户 因此我们要定义一个路由规则
  public static final String ACCOUNT_QUEUE_KEY = "account_queue_key";

  //为了确保消息不会丢失能准确的推入到队列当中
  //需要绑定交换机
  public static final String SMS_EXCHANGE = "sms_exchange";

  //在Spring当中使用该队列推送消息
  @Bean(ACCOUNT_QUEUE)
  public Queue accountQueue(){
     //在MQ上创建一个消息队列
     Queue queue = new Queue(ACCOUNT_QUEUE);
     return queue;
  }

  @Bean(SMS_EXCHANGE)
  public Exchange exchange(){
     //直接模式 一个交换机对应一个队列
     return ExchangeBuilder.directExchange(SMS_EXCHANGE).build();
  }

   //将队列和交换机进行绑定（确保消息不丢失）
   @Bean
   public Binding bindingExchange(@Qualifier(ACCOUNT_QUEUE) Queue queue , @Qualifier(SMS_EXCHANGE) Exchange exchange){
    //绑定 第一个参数 队列 第二个参数 交换机 第三个 路由模式 最后一个  noargs 剩下的什么也没有了
    return BindingBuilder.bind(queue).to(exchange).with(ACCOUNT_QUEUE_KEY).noargs();
   }
}
