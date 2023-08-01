package com.restkeeper.shop.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.restkeeper.constants.SystemCode;
import com.restkeeper.shop.entity.Store;
import com.restkeeper.shop.mapper.StoreMapper;
import com.restkeeper.shop.vo.StoreDTO;
import com.restkeeper.utils.BeanListUtils;
import com.restkeeper.utils.JWTUtil;
import com.restkeeper.utils.Result;
import com.restkeeper.utils.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Service("storeService")
@Service(version = "1.0.0",protocol = "dubbo")
@Slf4j
public class StoreServiceImpl extends ServiceImpl<StoreMapper, Store> implements IStoreService {

    //分页查询门店列表
    public IPage<Store> queryPageByName(int pageNo, int pageSize, String name) {
      IPage<Store> storeIPage = new Page<>(pageNo,pageSize);
      QueryWrapper<Store> wrapper = new QueryWrapper<>();
      if (StringUtils.isNotEmpty(name)){
          wrapper.lambda().eq(Store::getStoreName,name);
      }
        return this.page(storeIPage,wrapper);
    }

    /**
     * TODO 获取所有的省份信息
     * @return
     */
    public List<String> getAllProvince() {
        return getBaseMapper().getProvince();
    }

    /**
     * TODO 根据省份查询门店信息
     * @param province
     * @return
     */
    public List<StoreDTO> getStoreByProvince(String province) {
        //1.构造条件 条件传递的省份信息
        QueryWrapper<Store> wrapper = new QueryWrapper<>();
        //2.门店必须是可用状态才会查到
        wrapper.lambda().eq(Store::getStatus,1);
        //3.传递的省份不为空 则按照省份查询
        if (!StringUtils.isEmpty(province) && "all".equalsIgnoreCase(province)){
           wrapper.lambda().eq(Store::getProvince,province);
        }
        //4.根据条件查询所有门店列表
        List<Store> stores = this.list(wrapper);
        //5.把List集合赋值给StoreDTO对象
        List<StoreDTO> storeDTOS = null;
        try {
          return storeDTOS = BeanListUtils.copy(stores,StoreDTO.class);
        }catch (Exception e){
            e.printStackTrace();
            log.info("集合转换失败");
        }
         return new ArrayList<StoreDTO>();
    }

    /**
     * 默认展示的门店信息
     * @return
     */
    public List<StoreDTO> getStoresByManagerId() {
        QueryWrapper<Store> queryWrapper = new QueryWrapper<Store>();
        queryWrapper.lambda().eq(Store::getStatus,1).
                eq(Store::getStoreManagerId, RpcContext.getContext().getAttachment("loginUserId"));
        List<Store> list = this.list(queryWrapper);
        try {
            return BeanListUtils.copy(list,StoreDTO.class);
        } catch (Exception e) {
            log.info("转换出错");
        }
        return new ArrayList<StoreDTO>();
    }

    @Value("${gateway.secret}")
    private String secret;

    /**
     * 门店切换 修改JWT信息
     * @param storeId
     * @return
     */
    public Result switchStore(String storeId) {
        Result result = new Result();

        Map<String,Object> tokenMap = Maps.newHashMap();
        tokenMap.put("shopId",RpcContext.getContext().getAttachment("shopId"));
        tokenMap.put("storeId",storeId);
        tokenMap.put("loginUserId",RpcContext.getContext().getAttachment("loginUserId"));
        tokenMap.put("loginUserName",RpcContext.getContext().getAttachment("loginUserName"));
        tokenMap.put("userType", SystemCode.USER_TYPE_STORE_MANAGER);

        String tokenInfo = "";

        try {
            tokenInfo = JWTUtil.createJWTByObj(tokenMap,secret);
        } catch (IOException e) {
            e.printStackTrace();

            result.setStatus(ResultCode.error);
            result.setDesc("生成令牌失败");
            return result;
        }

        result.setStatus(ResultCode.success);
        result.setDesc("ok");
        result.setData(storeId);
        result.setToken(tokenInfo);

        return result;
    }
}
