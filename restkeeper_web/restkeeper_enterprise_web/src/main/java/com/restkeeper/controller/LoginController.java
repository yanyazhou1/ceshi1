package com.restkeeper.controller;
import com.restkeeper.constants.SystemCode;
import com.restkeeper.operator.service.IEnterpriseAccountService;
import com.restkeeper.shop.service.IStoreManagerService;
import com.restkeeper.utils.Result;
import com.restkeeper.utils.ResultCode;
import com.restkeeper.vo.LoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Api(tags = "企业端登录接口")
public class LoginController {

  @Reference(version = "1.0.0",check = false)
  private IEnterpriseAccountService enterpriseAccountService;

  @Reference(version = "1.0.0",check = false)
  private IStoreManagerService storeManagerService;

    /**
     * 企业端登录接口
     */
  @PostMapping("/login")
  @ApiImplicitParam(name = "Authorization",
          value = "jwt token", required = false, dataType = "String",paramType="header")
  public Result login(@RequestBody LoginVO loginVO){
      System.out.println(loginVO.getType());

      //登录类型是门店可以登录
     if (SystemCode.USER_TYPE_SHOP.equals(loginVO.getType())){
         return enterpriseAccountService.login(loginVO.getShopId(),loginVO.getPhone(),loginVO.getPassword());
     }

     //门店管理员可以登录
      if(SystemCode.USER_TYPE_STORE_MANAGER.equals(loginVO.getType())){
          return storeManagerService.login(loginVO.getShopId(),loginVO.getPhone(),loginVO.getPassword());
      }

      Result result =new Result();
      result.setStatus(ResultCode.error);
      result.setDesc("不支持该类型用户登录");
      return result;
  }
}
