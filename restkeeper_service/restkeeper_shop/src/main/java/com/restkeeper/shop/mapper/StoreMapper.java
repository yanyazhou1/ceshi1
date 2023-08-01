package com.restkeeper.shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.restkeeper.redis.MybatisRedisCache;
import com.restkeeper.shop.entity.Store;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 门店的Mapper
 */
@Mapper
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface StoreMapper extends BaseMapper<Store> {

  //根据门店管理员id查询门店列表
  @Select("select * from t_store where store_manager_id=#{managerId} order by last_update_time desc")
  List<Store> selectStoreInfoByManagerId(@Param("managerId") String managerId);

  @Select("select count(1) from t_store where brand_id = #{brandId} and status = 1 and is_deleted = 0 ")
  Integer getStoreCount(@Param("brandId") String brandId);

  @Select("select count(distinct(city)) from t_store where brand_id = #{brandId} and status = 1 and is_deleted = 0 ")
  Integer getCityCount(@Param("brandId") String brandId);

  //TODO 获取所有有效的省份信息
  @Select(value="select DISTINCT(province) from t_store where status=1 and is_deleted=0")
  public List<String> getProvince();
}
