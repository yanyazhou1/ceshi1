package com.restkeeper.shop;


import com.restkeeper.shop.entity.Store;
import com.restkeeper.shop.service.IStoreService;
import com.restkeeper.shop.vo.StoreDTO;
import org.apache.dubbo.config.annotation.Reference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ShopTest extends BaseTest {

  @Reference(version = "1.0.0",check = false)
  private IStoreService storeService;

    @Test
    public void listAllProvince(){
        List<String> allProvince = storeService.getAllProvince();
        System.out.println(allProvince);
    }

    @Test
    public void getStoreByProvince(){
        List<StoreDTO> storeDTO = storeService.getStoreByProvince("all");
        System.out.println(storeDTO);
    }

  @Test
  public void testAdd(){
    Store store = new Store();
    store.setStoreId("10086");
    store.setStoreName("元神启动");
    store.setStatus(0);
    store.setStoreManagerId("1");
    store.setBrandId("1");
    store.setProvince("河北省");
    store.setCity("廊坊市");
    store.setArea("广阳区");
    store.setAddress("啦啦啦");
    storeService.save(store);
  }
}
