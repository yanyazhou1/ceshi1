package com.restkeeper.tenant;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 为了防止Dubbo隐式传参的参数丢失
 * 将参数存入ThreadLocal
 */
public class TenantContext {

    static ThreadLocal<Map<String,Object>> threadLocal = new ThreadLocal<Map<String,Object>>(){
        @Override
        protected Map<String, Object> initialValue() {
            return new LinkedHashMap<String,Object>();
        }
    };

     public static void addAttchment(String key, Object value){
       threadLocal.get().put(key,value);
     }

    public static void addAttchments(Map<String,Object> map){
        threadLocal.get().putAll(map);
    }

    public static Map<String,Object> getAttchments(Map<String,Objects> map){
        return threadLocal.get();
    }

    public static String getStoreId() {
        return String.valueOf(threadLocal.get().get("storeId"));
    }

    public void clear(){
         threadLocal.remove();
    }

    public static String getLoginUserId(){
        return String.valueOf(threadLocal.get().get("loginUserId"));
    }

    public static String getShopId(){
        return String.valueOf(threadLocal.get().get("shopId"));
    }

    public static String getLoginUserName(){
        return String.valueOf(threadLocal.get().get("loginUserName"));
    }
}
