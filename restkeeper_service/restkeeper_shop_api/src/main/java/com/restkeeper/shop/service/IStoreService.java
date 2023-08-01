package com.restkeeper.shop.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.shop.entity.Store;
import com.restkeeper.shop.vo.StoreDTO;
import com.restkeeper.utils.Result;

import java.util.List;

public interface IStoreService extends IService<Store> {

  //分页查询门店列表
  IPage<Store> queryPageByName(int pageNo,int pageSize,String name);

    /**
     * 获取所有省份信息
     * @return
     */
    List<String> getAllProvince();
    /**
     * 根据省份获取门店信息
     * @return
     */
    List<StoreDTO> getStoreByProvince(String province);

    /**
     * 默认给一个门店展示
     */
     List<StoreDTO> getStoresByManagerId();

    /**
     * 门店切换逻辑
     * @param storeId
     * @param storeId
     * @return
     */
    Result switchStore(String storeId);


}
