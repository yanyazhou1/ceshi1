package com.restkeeper.operator.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.restkeeper.constants.SystemCode;
import com.restkeeper.operator.config.RabbitMQConfig;
import com.restkeeper.operator.entity.EnterpriseAccount;
import com.restkeeper.operator.mapper.EnterpriseAccountMapper;
import com.restkeeper.sms.SmsObject;
import com.restkeeper.utils.*;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;

/**
 * 企业的业务层实现类
 */
@Service(version = "1.0.0",protocol = "dubbo")
@RefreshScope
@SuppressWarnings("all")
public class EnterpriseAccountServiceImpl extends ServiceImpl<EnterpriseAccountMapper,EnterpriseAccount> implements IEnterpriseAccountService {

    /**
     * 用于操作MQ的工具类
     */
    @Autowired
    private RabbitTemplate rabbitTemplate;

    //短信签名 --我们目前还没写
    @Value("${sms.operator.signName}")
    private String signName;

    //短信模板
    @Value("${sms.operator.templateCode}")
    private String templateCode;

    //注入密钥
    @Value("${gateway.secret}")
    private String secret;

    /**
     * 由于新增和修改都需要发送短信
     *  所以我们可以在实现类定义一个发短信的方法 避免代码写两份
     */
      public void sendMessage(String phone,String shopId,String pwd){
         //把短信推送到消息队列当中(消息队列里面接收的是JSON格式的数据)
         //{签名:xxx,模板:xxx,手机号:xxx....} fastJSON
         SmsObject smsObject = new SmsObject();
         smsObject.setPhoneNumber(phone);
         smsObject.setSignName(signName);
         smsObject.setTemplateCode(templateCode);
         JSONObject jsonObject = new JSONObject();
         jsonObject.put("shopId",shopId);
         jsonObject.put("pwd",pwd);
         smsObject.setTemplateJsonParam(jsonObject.toJSONString());
         rabbitTemplate.convertAndSend(RabbitMQConfig.ACCOUNT_QUEUE,JSON.toJSONString(smsObject));
      }


    /**
     * 分页查询企业帐号信息
     */
    public IPage<EnterpriseAccount> queryPageByName(int pageNum, int pageSize, String enterpriseName) {

        IPage<EnterpriseAccount> page = new Page<>(pageNum,pageSize);
        //构造条件
        QueryWrapper<EnterpriseAccount> wrapper = new QueryWrapper<>();
        //如果前端传递的搜索条件 不为空
        if (!StringUtils.isEmpty(enterpriseName)){
            wrapper.like("enterprise_name",enterpriseName);
        }
        return this.page(page,wrapper);
    }

    /**
     * 新增企业帐号
     * @param account
     * @return
     */
    public Boolean add(EnterpriseAccount account) {
        boolean flag = true;
        try{
          //生成商户号和密码以短信的形式发送给商户
          String shopId = getShopId();
          account.setShopId(shopId);
          //生成密码 （商户要租借我们的SAAS系统 密码）
          String pwd = RandomStringUtils.randomNumeric(6);
          account.setPassword(Md5Crypt.md5Crypt(pwd.getBytes()));
          //执行保存
          this.save(account);
          //保存成功后把消息推入MQ
          sendMessage(account.getPhone(),shopId,pwd);
        }catch (Exception e){
            e.printStackTrace();
            flag = false;
            throw e;
        }
        return flag;
    }

    //帐号还原
    public boolean recovery(String id) {
        return this.getBaseMapper().recovery(id);
    }

    /**
     * 重置密码
     */
    @Transactional //事务注解
    public boolean restPwd(String id, String password) {
        boolean falg = true;
        try{
          //1.根据id查询该用户是否存在
          EnterpriseAccount account = this.getById(id);
          if (account == null){
              return false;
          }
          //2.密码的修改方式有两种 一种还是我们重新生成 另外一种用户自己输入新密码
          String pwd;
          if (StringUtils.isNotEmpty(password)){
              //如果用户是自己输入的新密码 那就直接给密码赋值就可以了
              pwd = password;
          } else {
             //帮用户生成新密码
              pwd = RandomStringUtils.randomNumeric(6);
          }
          //对新密码进行加密
          account.setPassword(Md5Crypt.md5Crypt(pwd.getBytes()));
          //执行更新
          this.updateById(account);
          //更新密码完成后消息推入MQ
          sendMessage(account.getPhone(),account.getShopId(),pwd);
        }catch (Exception e){
            e.printStackTrace();
            falg = false;
            throw e;
        }
        return falg;
    }


    public Result login(String shopId, String phoneNumber, String loginPass) {
        Result result = new Result();
        if (StringUtils.isEmpty(shopId)){
            result.setStatus(ResultCode.error);
            result.setDesc("商户号未输入!");
            return result;
        }
        if (StringUtils.isEmpty(phoneNumber)){
            result.setStatus(ResultCode.error);
            result.setDesc("手机号未输入!");
            return result;
        }
        if (StringUtils.isEmpty(loginPass)){
            result.setStatus(ResultCode.error);
            result.setDesc("密码未输入!");
            return result;
        }
       //根据手机号和商户号查询用户是否存在
       QueryWrapper<EnterpriseAccount> wrapper = new QueryWrapper<>();
       wrapper.lambda().eq(EnterpriseAccount::getPhone,phoneNumber)
                       .eq(EnterpriseAccount::getShopId,shopId);
       //排除掉禁用的商户
       wrapper.lambda().notIn(EnterpriseAccount::getStatus,AccountStatus.Forbidden.getStatus());
       EnterpriseAccount account = this.getOne(wrapper);
       if (account == null){
        result.setStatus(ResultCode.error);
        result.setDesc("用户未找到!");
        return result;
       }
       //比对用户输入的密码(获取盐)
       String salt = MD5CryptUtil.getSalts(account.getPassword());
       if (!Md5Crypt.md5Crypt(loginPass.getBytes(),salt).equals(account.getPassword())){
           result.setStatus(ResultCode.error);
           result.setDesc("密码错误!");
           return result;
       }

        HashMap<String, Object> tokenMap = Maps.newHashMap();
        tokenMap.put("shopId",account.getShopId());
        tokenMap.put("loginName",account.getEnterpriseName());
        tokenMap.put("loginType", SystemCode.USER_TYPE_SHOP);
        String token = null;
        try {
         token = JWTUtil.createJWTByObj(tokenMap,secret);
        } catch (IOException e){
          e.printStackTrace();
          result.setStatus(ResultCode.error);
          result.setDesc("令牌签发失败!");
          return result;
        }
        //签发令牌返回给前端
        result.setStatus(ResultCode.success);
        result.setDesc("OK!");
        result.setToken(token);
        return result;
    }

    //公共方法用于生成商户号
     private String getShopId() {
     //1.使用随机数生成 8位的商户号
     String shopId = RandomStringUtils.randomNumeric(8);
     //2.判断看用户之前是否注册过
     QueryWrapper<EnterpriseAccount> wrapper = new QueryWrapper<>();
     wrapper.eq("shop_id",shopId);
     EnterpriseAccount enterpriseAccount = this.getOne(wrapper);
     if (enterpriseAccount!=null){
      //重新生成ShopId
      this.getShopId();
     }
     return shopId;
    }
}
