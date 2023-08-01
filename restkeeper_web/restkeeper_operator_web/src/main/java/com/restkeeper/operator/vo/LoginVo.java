package com.restkeeper.operator.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 *  这里我们采用了Dubbo
 *   前端传递的所有数据都是二进制的数据
 *   @ApiModel : 在Swagger上显示当前的实体类信息
 *
 */
@Data
@ApiModel
public class LoginVo  {


    @ApiModelProperty(name = "loginname",value = "登录帐号")
    private String loginname;

    @ApiModelProperty(name = "loginpass",value = "登录密码")
    private String loginpass;

}
