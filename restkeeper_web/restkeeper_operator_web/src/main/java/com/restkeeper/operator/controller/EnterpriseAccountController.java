package com.restkeeper.operator.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.restkeeper.operator.entity.EnterpriseAccount;
import com.restkeeper.operator.service.IEnterpriseAccountService;
import com.restkeeper.operator.vo.AddEnterpriseAccountVO;
import com.restkeeper.operator.vo.ResetPwdVO;
import com.restkeeper.operator.vo.UpdateEnterpriseAccountVO;
import com.restkeeper.response.vo.PageVO;
import com.restkeeper.utils.AccountStatus;
import com.restkeeper.utils.Result;
import com.restkeeper.utils.ResultCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Api(tags = "企业帐号管理")
@RestController
@RequestMapping("/enterprise")
@RefreshScope  //监控Nacos配置中文配置文件变化
@CrossOrigin  //跨域注解
public class EnterpriseAccountController {

    //注入Service用于调用
    @Reference(version = "1.0.0",check = false)
    private IEnterpriseAccountService enterpriseAccountService;

    /**
     * 分页查询企业帐号列表
     */
     @GetMapping("/pageList/{page}/{pageSize}")
     @ApiOperation(value = "分页查询企业帐号列表")
     public PageVO<EnterpriseAccount> page(@PathVariable("page") int page,
                                           @PathVariable("pageSize") int pageSize,
                                           @RequestParam("enterpriseName") String enterpriseName){
      IPage<EnterpriseAccount> pageName = this.enterpriseAccountService.queryPageByName(page, pageSize, enterpriseName);
      PageVO<EnterpriseAccount> pageVO = new PageVO<>(pageName);
      return pageVO;
     }


    /**
     * 新增企业帐号
     */
     @PostMapping("/add")
     @ApiOperation(value = "新增企业帐号")
     public Boolean add(@RequestBody AddEnterpriseAccountVO  enterpriseAccountVO){
       //把前端填写的数据 赋值给  EnterpriseAccount 对象
       EnterpriseAccount account = new EnterpriseAccount();
       BeanUtils.copyProperties(enterpriseAccountVO,account);
       //设置创建帐号的时间
       LocalDateTime now = LocalDateTime.now();
       account.setApplicationTime(now);
       //设置帐号过期时间
       LocalDateTime expireTime = null;
       //如果添加的帐号是试用帐号 过期时间 7天以后
       if (enterpriseAccountVO.getStatus() == 0 ){
           //当前时间向后偏移7天
           expireTime = now.plusDays(7);
       }
       if (enterpriseAccountVO.getStatus() == 1 ){
          //如果添加的是正式帐号 设置过期时间 按照前端填写为准
           expireTime = now.plusDays(enterpriseAccountVO.getValidityDay());
       }

       if (expireTime != null){
         account.setExpireTime(expireTime);
       }else{
          throw new RuntimeException("设置帐号类型有误");
       }
        return enterpriseAccountService.add(account);
     }

      /**
      * 根据id查询帐号信息
      */
     @ApiOperation(value="查询企业帐号信息")
     @GetMapping("/getById/{id}")
     public EnterpriseAccount getById(@PathVariable("id") String id){
         return this.enterpriseAccountService.getById(id);
     }

    /**
     * TODO 更新企业帐号信息
     * 1）试用期可以改正式 status =0 试用  status=1 正式  status = -1 禁用
     * 2）正式期不能修改成试用期
     * 3）正式期延期支持
     */
    @PutMapping("/update")
    @ApiOperation(value="更新企业帐号信息")
    public Result update(@RequestBody UpdateEnterpriseAccountVO accountVO){
        Result result = new Result();
        //1.根据id查询该企业帐号是否存在 存在才能修改
        EnterpriseAccount enterpriseAccount = this.enterpriseAccountService.getById(accountVO.getEnterpriseId());
        if (enterpriseAccount == null){
            result.setStatus(ResultCode.error); //500
            result.setDesc("查询不到该用户无法修改");
            return result;
        }
        //2.正式期不能改试用
        if (accountVO.getStatus() != null){
            if (accountVO.getStatus() == 0 && enterpriseAccount.getStatus() == 1){
                result.setStatus(ResultCode.error);
                result.setDesc("不能将正式期帐号修改为试用期");
                return result;
            }
            //3.试用期的改正式期
            if (accountVO.getStatus() == 1 && enterpriseAccount.getStatus() == 0){
                //1.获取当前时间
                LocalDateTime now = LocalDateTime.now();
                //2.获取前端传递的时间
                LocalDateTime expireTime = now.plusDays(accountVO.getValidityDay());
                //3.设置一下变成正式期的时间和过期时间
                enterpriseAccount.setApplicationTime(now);
                enterpriseAccount.setExpireTime(expireTime);
            }
            //4.正式期帐号延期处理
                //页面传递的值是 1 true          //获取数据库原本状态 1 true
            if (accountVO.getStatus() == 1 && enterpriseAccount.getStatus() == 1){ //true  正式期帐号
                LocalDateTime now = LocalDateTime.now(); //获取当前时间 7/24 9:37
                //获取要延长的时间
                LocalDateTime days = now.plusDays(accountVO.getValidityDay()); //300天 2024-05-19 09:35:04
                //重新给过期时间赋值
                enterpriseAccount.setExpireTime(days);
            }
            //PS: 如果不做全表的修改只是修改了两个字段 那么其他的值都会变成null
            //为了避免这种情况 所以我们还要给其他的字段进行赋值
            BeanUtils.copyProperties(accountVO,enterpriseAccount);
            //调用更新方法完成更新
            boolean res = this.enterpriseAccountService.updateById(enterpriseAccount);
            if (res){
             result.setStatus(ResultCode.success);//200
             result.setDesc("更新成功");
             return result;
            } else {
            result.setStatus(ResultCode.error);//200
            result.setDesc("更新失败");
            return result;
            }
        }
        return result;
    }

    /**
     * TODO 删除企业帐号信息 逻辑删除 修改 is_deleted 字段的状态
     * MybatisPlus提供了逻辑删除的注解 只需要加到逻辑删除字段上即可
     */
     @ApiOperation(value = "根据id删除企业帐号")
     @DeleteMapping("/delete/{id}")
     public boolean deleteById(@PathVariable("id") String id){
      return this.enterpriseAccountService.removeById(id);
     }

     //TODO 帐号还原接口
     @ApiOperation(value = "帐号还原")
     @PutMapping("/recovery/{id}")
     public boolean recovery(@PathVariable("id") String id){
         return this.enterpriseAccountService.recovery(id);
     }

     //TODO 帐号禁用
     @ApiOperation(value = "帐号禁用")
     @PutMapping("/foribben/{id}")
     public boolean foribben(@PathVariable("id") String id){
      EnterpriseAccount account = this.enterpriseAccountService.getById(id);
      account.setStatus(AccountStatus.Forbidden.getStatus()); // -1
      return this.enterpriseAccountService.updateById(account);
     }

     //TODO 修改密码
     @ApiOperation(value = "修改密码")
     @PutMapping("/updatePwd")
     public boolean updatePwd(@RequestBody ResetPwdVO resetPwdVO){
       return this.enterpriseAccountService.restPwd(resetPwdVO.getId(),resetPwdVO.getPwd());
     }
}
