package com.restkeeper.operator.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.operator.entity.EnterpriseAccount;
import com.restkeeper.utils.Result;
import org.apache.ibatis.annotations.Param;

public interface IEnterpriseAccountService extends IService<EnterpriseAccount> {

    //TODO 分页查询企业帐号列表
    IPage<EnterpriseAccount> queryPageByName(int pageNum,int pageSize,String enterpriseName);

    //TODO 新增企业帐号
    Boolean add(EnterpriseAccount account);

    //TODO 帐号还原
    boolean recovery(String id);

    //TODO 重置密码
    boolean restPwd(String id,String password);

    //TODO 管理端登录
    Result login(String shopId,String phoneNumber,String loginPass);


}
