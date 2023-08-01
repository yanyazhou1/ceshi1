package com.restkeeper.operator.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 企业账号管理
 * </p>
 */
@Data
@Accessors(chain = true)
@TableName(value="t_enterprise_account")
@ApiModel(value="EnterpriseAccount对象", description="企业账号管理对象")
public class EnterpriseAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "企业id")
    @TableId(type= IdType.ASSIGN_ID)
    private String enterpriseId;

    @ApiModelProperty(value = "企业名称")
    private String enterpriseName;

    @JsonIgnore
    @ApiModelProperty(value = "密码（后台自动下发）")
    private String password;

    @JsonIgnore
    @ApiModelProperty(value = "商户号（下发生成）")
    private String shopId;

    @ApiModelProperty(value = "申请人")
    private String applicant;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "省")
    private String province;

    @ApiModelProperty(value = "市")
    private String city;

    @ApiModelProperty(value = "区")
    private String area;

    @ApiModelProperty(value = "详细地址")
    private String address;

    @ApiModelProperty(value = "申请时间（当前时间，精准到分）")
    private LocalDateTime applicationTime;

    @ApiModelProperty(value = "到期时间 (试用下是默认七天后到期，状态改成停用)")
    private LocalDateTime expireTime;

    @ApiModelProperty(value = "状态(试用中1，已停用0，正式2)")
    private Integer status;

    @ApiModelProperty(value = "最后更新时间")
    private LocalDateTime lastUpdateTime;

    @ApiModelProperty(value = "逻辑删除字段")
    @TableLogic(value = "0",delval = "1") //0 未删除 1 已删除
    private Integer isDeleted;
}
