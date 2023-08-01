package com.restkeeper.shop.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.shop.entity.StoreManager;
import com.restkeeper.utils.Result;

import java.util.List;

public interface IStoreManagerService extends IService<StoreManager> {

    /**
     * 门店管理员登录接口
     * @param shopId
     * @param phone
     * @param loginPass
     * @return
     */
    Result login(String shopId, String phone, String loginPass);

    /**
     * 店长列表分页查询
     * @param pageNo
     * @param pageSize
     * @param criteria
     * @return
     */
    IPage<StoreManager> queryPageByCriteria(int pageNo, int pageSize, String criteria);

     /**
     * 店长新增
     */
     public boolean addManagerStore(String name, String phone,List<String> storeIds);

    /**
     * 店长信息修改
     */
     boolean updateStoreManager(String storeManagerId, String name, String phone, List<String> storeIds);


    /**
     * 删除门店管理员
     * @param storeManagerId
     * @return
     */
    boolean deleteStoreManager(String storeManagerId);

    /**
     * 暂停门店管理员
     * @param storeManagerId
     * @return
     */
    boolean pauseStoreManager(String storeManagerId);
}
