package com.restkeeper.shop.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.shop.entity.Brand;

import java.util.List;
import java.util.Map;

public interface IBrandService extends IService<Brand> {

    /**
     * TODO 分页展示品牌列表
     */
     IPage<Brand> queryPage(int pageNo,int pageSize);

    /**
     * TODO 获取品牌列表
     */
     List<Map<String,Object>> getBrandList();




}
