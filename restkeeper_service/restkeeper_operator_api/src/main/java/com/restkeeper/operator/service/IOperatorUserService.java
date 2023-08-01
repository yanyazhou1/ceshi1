package com.restkeeper.operator.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.operator.entity.OperatorUser;
import com.restkeeper.utils.Result;

public interface IOperatorUserService extends IService<OperatorUser> {

    //分页查询运营商
    IPage<OperatorUser> queryByPage(int page, int size, String loginname);

    //登录方法
    Result login(String loginName, String loginPass);
}
