package com.restkeeper.shop.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 封装类用于筛选页面要展示的内容
 */
@Data
public class StoreDTO {

    @ApiModelProperty(value = "门店id")
    private String storeId;

    @ApiModelProperty(value = "门店名称")
    private String storeName;

    @ApiModelProperty(value = "门店管理员id")
    private String storeManagerId;

    @ApiModelProperty(value = "门店地址")
    private String address;

}
