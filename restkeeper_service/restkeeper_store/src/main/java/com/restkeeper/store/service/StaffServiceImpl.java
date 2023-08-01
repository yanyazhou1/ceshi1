package com.restkeeper.store.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restkeeper.constants.SystemCode;
import com.restkeeper.sms.SmsObject;
import com.restkeeper.store.mapper.StaffMapper;
import com.restkeeper.store.entity.Staff;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Service(version = "1.0.0",protocol = "dubbo")
public class StaffServiceImpl extends ServiceImpl<StaffMapper, Staff> implements IStaffService {

    /**
     * TODO 新增员工
     * @param staff
     * @return
     */
    public boolean addStaff(Staff staff) {
       boolean flag = true;
       //1.获取用户输入的密码
       String password = staff.getPassword();
       //2.如果用户没有填写密码 后端自动生成密码发送短信
       if (StringUtils.isEmpty(password)){
           password = RandomStringUtils.randomNumeric(8);
       }
       //3.生成的密码保存到数据库 方便下次登录
       staff.setPassword(Md5Crypt.md5Crypt(password.getBytes()));
       try{
           //保存用户
           this.save(staff);
           //发送短信
           this.sendMessage(staff.getPhone(),staff.getShopId(),staff.getPassword());
       }catch (Exception e){
           e.printStackTrace();
           flag = false;
       }
       return flag;
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${sms.operator.signName}")
    private String signName;

    @Value("${sms.operator.templateCode}")
    private String templateCode;

    //发送短信的公用方法
    private void sendMessage(String phone, String shopId, String pwd) {
        SmsObject smsObject = new SmsObject();
        smsObject.setPhoneNumber(phone);
        smsObject.setSignName(signName);
        smsObject.setSignName(templateCode);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("shopId", shopId);
        jsonObject.put("password", pwd);
        smsObject.setTemplateJsonParam(jsonObject.toJSONString());
        rabbitTemplate.convertAndSend(SystemCode.SMS_ACCOUNT_QUEUE,JSON.toJSONString(smsObject));
    }
}
