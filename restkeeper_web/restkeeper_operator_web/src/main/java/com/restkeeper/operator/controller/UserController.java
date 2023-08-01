package com.restkeeper.operator.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.restkeeper.operator.entity.OperatorUser;
import com.restkeeper.operator.service.IOperatorUserService;
import com.restkeeper.operator.vo.LoginVo;
import com.restkeeper.response.BaseResponse;
import com.restkeeper.response.vo.PageVO;
import com.restkeeper.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员的登录接口
 */
@RestController
@RefreshScope //配置中心的自动刷新
@Slf4j
@Api(tags = {"管理员相关接口"})
public class UserController{


    @Value("${server.port}")
    private String port;

    @Reference(version = "1.0.0",check = false)
    private IOperatorUserService operatorUserService;



    /**
     *  分页展示运营商(按照前端的要求)
     */
     @GetMapping("/pageList/{page}/{size}")
     @ApiOperation(value = "分页展示运营商")
     public PageVO<OperatorUser> findPageList(@PathVariable("page") int page,
                                                    @PathVariable("size") int size,
                                                    @RequestParam(value = "loginname",required = false) String loginname){
      //采用MybatisPlus内置的分页插件对查询结果分页
      IPage<OperatorUser> iPage = this.operatorUserService.queryByPage(page,size,loginname);
      final PageVO<OperatorUser> pageVO = new PageVO<>(iPage);
      return pageVO;
     }

     @PostMapping("/login")
     @ApiOperation(value = "登录")
     @ApiImplicitParam(name = "Authorization",value = "jwt token",required = false,dataType = "String",paramType = "header")
     public Result login(@RequestBody LoginVo loginVo){
      return operatorUserService.login(loginVo.getLoginname(),loginVo.getLoginpass());
     }


   /* @GetMapping("/pageList/{page}/{pageSize}")
    @ApiOperation(value = "运营商分页接口")
    //对接口参数的描述
    @ApiImplicitParams({
      @ApiImplicitParam(paramType = "path",name = "page",value = "当前页码",dataType = "int",required = true),
      @ApiImplicitParam(paramType = "path",name = "pageSize",value = "展示的条数",dataType = "int",required = true)
    })
    public IPage<OperatorUser> findListByPage(@PathVariable("page") int pageNum,
                                              @PathVariable("pageSize") int pageSize){

        IPage<OperatorUser> page = new Page<OperatorUser>(pageNum,pageSize);
        log.info("管理员数据分页查询："+ JSON.toJSONString(page));
        return operatorUserService.page(page);
    }*/


}
