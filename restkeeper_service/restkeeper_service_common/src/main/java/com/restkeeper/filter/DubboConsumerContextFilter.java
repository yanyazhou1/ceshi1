package com.restkeeper.filter;


import com.restkeeper.tenant.TenantContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;


@Slf4j
@Activate
public class DubboConsumerContextFilter implements Filter {

    //处理隐式传参的问题
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        RpcContext.getContext().setAttachment("shopId", TenantContext.getShopId());

        RpcContext.getContext().setAttachment("loginUserId", TenantContext.getLoginUserId());

        RpcContext.getContext().setAttachment("loginUserName", TenantContext.getLoginUserName());

        RpcContext.getContext().setAttachment("storeId", TenantContext.getStoreId());

        return invoker.invoke(invocation);
    }
}
