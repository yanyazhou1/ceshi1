package com.restkeeper.controller.store;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.restkeeper.response.vo.PageVO;
import com.restkeeper.shop.entity.Brand;
import com.restkeeper.shop.service.IBrandService;
import com.restkeeper.vo.shop.AddTBrandVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "品牌管理接口")
@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference(version = "1.0.0",check = false)
    private IBrandService brandService;


    /**
     * TODO 分页查询品牌信息
     */
     @ApiOperation(value = "分页查询品牌")
     @GetMapping("/pageList/{pageNo}/{pageSize}")
     public PageVO<Brand> findListByPage(@PathVariable("pageNo") int pageNo,
                                         @PathVariable("pageSize") int pageSize){
         IPage<Brand> iPage = this.brandService.queryPage(pageNo, pageSize);
         PageVO<Brand> pageVO = new PageVO<>(iPage);
         return pageVO;
     }

      /**
       * TODO 品牌新增
       */
       @ApiOperation(value = "品牌新增")
       @PostMapping("/add")
       public boolean add(@RequestBody AddTBrandVO addTBrandVO){
           Brand brand = new Brand();
           BeanUtils.copyProperties(addTBrandVO,brand);
           return this.brandService.save(brand);
       }

       /**
        * TODO 查询品牌列表(用于下拉框展示)
        */
       @ApiOperation(value = "查询品牌列表(用于下拉框展示)")
       @GetMapping("/brandList")
       public List<Map<String,Object>> brandList(){
           return this.brandService.getBrandList();
       }
}
