package com.restkeeper.operator.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.restkeeper.operator.entity.OperatorUser;
import com.restkeeper.operator.mapper.OperatorUserMapper;
import com.restkeeper.utils.JWTUtil;
import com.restkeeper.utils.MD5CryptUtil;
import com.restkeeper.utils.Result;
import com.restkeeper.utils.ResultCode;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;

//@Service("operatorUserService")

/**
 * @Service注解不是Spring的@Service注解
 */
@Service(version = "1.0.0",protocol = "dubbo")
/**
 * dubbo中支持的协议
 * dubbo 默认
 * rmi
 * hessian
 * http
 * webservice
 * thrift
 * memcached
 * redis
 */
public class OperatorUserServiceImpl extends ServiceImpl<OperatorUserMapper, OperatorUser> implements IOperatorUserService {
    /**
     * 从配置中心获取到密码用于生成token
     */
    @Value("${gateway.secret}")
    private String secret;

    /**
     *分页查询运营商 !
     */
    public IPage<OperatorUser> queryByPage(int page, int size, String loginname) {
        IPage<OperatorUser> pageNum = new Page<>(page, size);
        //创建MybatisPlus的条件构造器
        QueryWrapper<OperatorUser> wrapper = null;
        //如果用户传递的查询条件不为null 再实例化构造器
        if (!StringUtils.isEmpty(loginname)) {
            wrapper = new QueryWrapper<>();
            wrapper.like("loginname", loginname);
        }
        return this.page(pageNum, wrapper);
    }

    @Override
    public Result login(String loginName, String loginPass) {
        Result result = new Result();
        //参数校验
        if (StringUtils.isEmpty(loginName)) {
            result.setStatus(ResultCode.error);
            result.setDesc("用户名为空");
            return result;
        }
        if (StringUtils.isEmpty(loginPass)) {
            result.setStatus(ResultCode.error);
            result.setDesc("密码为空");
            return result;
        }
        //查询用户是否存在
        QueryWrapper<OperatorUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("loginname", loginName);
        OperatorUser user = this.getOne(queryWrapper);
        if (user == null) {
            result.setStatus(ResultCode.error);
            result.setDesc("用户不存在");
            return result;
        }
        //比对密码
        String salts = MD5CryptUtil.getSalts(user.getLoginpass());
        if (!Md5Crypt.md5Crypt(loginPass.getBytes(), salts).equals(user.getLoginpass())) {
            result.setStatus(ResultCode.error);
            result.setDesc("密码不正确");
            return result;
        }

        // 生成Token令牌带给前端
        HashMap<String, Object> hashMap = Maps.newHashMap();
        hashMap.put("loginName",user.getLoginname());
        String token = null;
        try {
           token = JWTUtil.createJWTByObj(hashMap,secret);
        }catch (Exception e){
            e.printStackTrace();
            result.setStatus(ResultCode.error);
            result.setDesc("生成令牌失败");
            return result;
        }
        //返回结果
        result.setStatus(ResultCode.success);
        result.setDesc("ok");
        result.setData(user);
        result.setToken(token);
        return result;
        }
    }