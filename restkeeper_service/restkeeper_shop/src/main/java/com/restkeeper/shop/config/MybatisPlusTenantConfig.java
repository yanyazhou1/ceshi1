package com.restkeeper.shop.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantHandler;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantSqlParser;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.awt.*;
import java.util.ArrayList;

/**
 * MybatisPlus的SQL解析配置类
 */
@Configuration
@SuppressWarnings("all")
@Slf4j
public class MybatisPlusTenantConfig {

    //由于这个处理器的作用是需要在每个SQL上添加 shop_id 参数指定在这个位置
    private static final String SYSTEM_TENANT_ID = "shop_id";

    //添加 store_id 用于门店身份登录和数据隔离
    private static final String SYSTEM_STORE_ID="store_id";

    //指定哪些表不需要携带 shop_id
    private static final ArrayList<String> IGNORE_TABLES = Lists.newArrayList("");

    /**
     * 注入MP提供的SQL解析器
     */
     @Bean
     public PaginationInterceptor paginationInterceptor(){
        log.info("-------SQL解析器开始执行--------");
        PaginationInterceptor interceptor = new PaginationInterceptor();

        TenantSqlParser sqlParser = new TenantSqlParser().setTenantHandler(new TenantHandler() {
            public Expression getTenantId(boolean where) {
                String shopId = RpcContext.getContext().getAttachment("shopId");
                if (shopId == null){
                    throw  new RuntimeException("shopID没有传递");
                }
                return new StringValue(shopId);
            }
            public String getTenantIdColumn() {
                return SYSTEM_TENANT_ID;
            }
            public boolean doTableFilter(String tableName) {
                return IGNORE_TABLES.stream().anyMatch((t)->t.equalsIgnoreCase(tableName));
            }
        });


        //创建第二个SQL解析器 这个解析器主要处理storeId字段
         TenantSqlParser sqlParser2 = new TenantSqlParser().setTenantHandler(new TenantHandler() {
             public Expression getTenantId(boolean where) {
                 String storeId = RpcContext.getContext().getAttachment("storeId");
                 if (storeId == null){
                     throw  new RuntimeException("storeId没有传递");
                 }
                 return new StringValue(storeId);
             }
             public String getTenantIdColumn() {
                 return SYSTEM_STORE_ID;
             }
             public boolean doTableFilter(String tableName) {
                 return IGNORE_TABLES.stream().anyMatch((t)->t.equalsIgnoreCase(tableName));
             }
         });

        interceptor.setSqlParserList(Lists.newArrayList(sqlParser,sqlParser2));
        return interceptor;
     }

}
