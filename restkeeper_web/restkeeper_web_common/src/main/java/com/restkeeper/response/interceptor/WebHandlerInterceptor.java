package com.restkeeper.response.interceptor;


import com.restkeeper.tenant.TenantContext;
import com.restkeeper.utils.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 自定义的SpringMVC的拦截器
 *   1.获取前端传递的token
 *   2.解析token获取shopId
 *   3.把shopId存入到RpcContext对象当中
 *    并不是所有的接口都需要携带shopId
 */
@Component
@SuppressWarnings("all")
@Slf4j
public class WebHandlerInterceptor implements HandlerInterceptor {
    //拦截器的执行顺序应该在controller之前
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
      //从请求头当中获取Token
      String tokenInfo = request.getHeader("Authorization");
      if (StringUtils.isNotEmpty(tokenInfo)){
          //解析Token获取shopId
          try {
          Map<String, Object> tokenMap = JWTUtil.decode(tokenInfo);
          String shopId = (String)tokenMap.get("shopId");
          //把ShopId存入到RpcContext当中实现Dubbo的隐式传参
           TenantContext.addAttchments(tokenMap);
          }catch (Exception e){
            e.printStackTrace();
            log.info("解析Token出现异常");
          }
      }
      //放行
      return true;
    }
}
