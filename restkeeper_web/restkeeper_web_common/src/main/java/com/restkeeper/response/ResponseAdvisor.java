package com.restkeeper.response;


import com.restkeeper.response.vo.PageVO;
import com.restkeeper.utils.Result;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 处理Controller返回值的处理类
 */
@RestControllerAdvice(basePackages = "com.restkeeper")
public class ResponseAdvisor implements ResponseBodyAdvice<Object> {

    //指定需要处理哪些接口 处理完成之后就会放行当前请求
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    //可以处理Controller所有的返回值
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        if (body instanceof Result){
            return body;
        }
        //返回值是boolean类型
        if (body instanceof Boolean){
            boolean result = (boolean)body;
            return new BaseResponse<Boolean>(result);
        }
        //返回值是pageVo 同样需要转换成 BaseResponse格式
        if (body instanceof PageVO){
            return new BaseResponse<>(body);
        }

        return new BaseResponse<>(body);
    }
}
