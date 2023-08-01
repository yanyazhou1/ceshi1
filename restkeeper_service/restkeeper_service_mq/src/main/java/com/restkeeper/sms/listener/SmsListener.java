package com.restkeeper.sms.listener;


import com.alibaba.alicloud.sms.ISmsService;
import com.alibaba.fastjson.JSON;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.restkeeper.constants.SystemCode;
import com.restkeeper.sms.SmsObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * 监听MQ队列的监听器
 *  一旦队列中产生了消息 发送短信给用户
 */
@Component
@Slf4j
@SuppressWarnings("all")
@RefreshScope
public class SmsListener {

    @Autowired
    private ISmsService smsService;

    /**
     * 监听队列的方法
     */
    @RabbitListener(queues = SystemCode.SMS_ACCOUNT_QUEUE)  //"account_queue"
    public void listenMessage(String message){ // [phone:xxx,signName:xxx,code:xxxx,{shopId:xxx,pwd:xxx}]
       log.info("监听到了从队列获取的消息:"+message);
       //把接收到的JSON转换成对象 获取需要发送短信的参数
       SmsObject smsObject = JSON.parseObject(message, SmsObject.class);
       //定义发送短信的方法来发送短信 cloudAlibaba SMS 组件短信发送
        SendSmsResponse response = this.sendSms(
        smsObject.getPhoneNumber(),
        smsObject.getSignName(),
        smsObject.getTemplateCode(),
        smsObject.getTemplateJsonParam());
    }

    //发送短信
    private SendSmsResponse sendSms(String phoneNumber, String signName, String templateCode, String templateJsonParam) {
        // 组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        // 必填:待发送手机号
        request.setPhoneNumbers(phoneNumber);
        // 必填:短信签名-可在短信控制台中找到
        request.setSignName(signName);
        // 必填:短信模板-可在短信控制台中找到
        request.setTemplateCode(templateCode);
        // 可选:模板中的变量替换JSON串,如模板内容为"【企业级分布式应用服务】,您的验证码为${code}"时,此处的值为
        request.setTemplateParam(templateJsonParam);
        SendSmsResponse sendSmsResponse ;
        try {
            //把需要发送短信的参数进行封装 创建sendSmsRequest请求
            sendSmsResponse = smsService.sendSmsRequest(request);
        }
        catch (com.aliyuncs.exceptions.ClientException e) {
            e.printStackTrace();
            sendSmsResponse = new SendSmsResponse();
        }
        return sendSmsResponse;
    }
}
