package com.restkeeper.controller.store;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.restkeeper.constants.SystemCode;
import com.restkeeper.response.vo.PageVO;
import com.restkeeper.shop.entity.Store;
import com.restkeeper.shop.service.IStoreService;
import com.restkeeper.shop.vo.StoreDTO;
import com.restkeeper.utils.Result;
import com.restkeeper.vo.shop.AddStoreVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Api(tags = {"门店信息"})
@RestController
@RequestMapping("/store")
public class StoreController {


    @Reference(version = "1.0.0",check = false)
    private IStoreService storeService;

    @ApiOperation(value = "门店切换")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "storeId", value = "门店Id", dataType = "String")})
    @GetMapping(value = "/switchStore/{storeId}")
    public Result switchStore(@PathVariable("storeId") String storeId){
        return storeService.switchStore(storeId);
    }

    @ApiOperation(value = "获取当前商户管理的门店信息")
    @GetMapping(value = "/listManagerStores")
    public List<StoreDTO> getStoreListByManagerId(){
        return storeService.getStoresByManagerId();
    }

    @ApiOperation(value = "获取门店省份信息")
    @GetMapping("/listProvince")
    @ResponseBody
    public List<String> listProvince() {
        return storeService.getAllProvince();
    }


    @ApiOperation(value = "根据省份获取门店列表")
    @GetMapping("/getStoreByProvince/{province}")
    @ResponseBody
    public List<StoreDTO> getStoreByProvince(@PathVariable String province) {
        return storeService.getStoreByProvince(province);
    }


    /**
     * 门店新增
     */
     @ApiOperation(value = "门店新增")
     @PostMapping("/add")
     public boolean add(@RequestBody AddStoreVO addStoreVO){
         Store store = new Store();
         BeanUtils.copyProperties(addStoreVO,store);
         return storeService.save(store);
     }

    /**
     * 分页查询门店列表
     */
    @ApiOperation(value = "分页展示门店列表")
    @GetMapping("/pageList/{pageNo}/{pageSize}")
    public PageVO<Store> findPageList(@PathVariable("pageNo") int pageNo,
                                      @PathVariable("pageSize") int pageSize,
                                      @RequestParam("storeName") String storeName){
    IPage<Store> iPage = this.storeService.queryPageByName(pageNo, pageSize, storeName);
    PageVO<Store> pageVO = new PageVO<>(iPage);
    return pageVO;
    }


    /**
     * 门店停用
     */
     @ApiOperation(value = "门店停用")
     @ApiImplicitParam(paramType = "path",name = "id",value = "主键",required = true,dataType = "String")
     @PutMapping(value = "/disabled/{id}")
     public boolean disabled(@PathVariable("id") String id){
         Store store = storeService.getById(id);
         store.setStatus(SystemCode.FORBIDDEN);
         return storeService.updateById(store);
     }
}
