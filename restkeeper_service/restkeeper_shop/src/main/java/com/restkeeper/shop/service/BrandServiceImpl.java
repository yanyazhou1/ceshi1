package com.restkeeper.shop.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restkeeper.shop.entity.Brand;
import com.restkeeper.shop.mapper.BrandMapper;
import org.apache.dubbo.config.annotation.Service;

import java.util.List;
import java.util.Map;

@Service(version = "1.0.0",protocol = "dubbo")
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements IBrandService {


    /**
     * 分页查询品牌信息
     * @param pageNo
     * @param pageSize
     * @return
     */
    public IPage<Brand> queryPage(int pageNo, int pageSize) {
         IPage<Brand> iPage = new Page<>(pageNo,pageSize);
         QueryWrapper<Brand> wrapper = new QueryWrapper<>();
         wrapper.lambda().orderByDesc(Brand::getLastUpdateTime);
         return this.page(iPage,wrapper);
    }

    /**
     * TODO 获取品牌列表
     * @return
     */
    public List<Map<String, Object>> getBrandList() {
        QueryWrapper<Brand> queryWrapper = new QueryWrapper<>();
        //查询结果只需要brand_id,brand_name
        queryWrapper.lambda().select(Brand::getBrandId,Brand::getBrandName);
        return this.listMaps(queryWrapper);
    }
}
