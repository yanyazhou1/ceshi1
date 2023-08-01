package com.restkeeper.sms;

import lombok.Data;

import java.io.Serializable;

@Data
public class SmsObject implements Serializable {
    //网络传输对象必须序列化
    private static final long serialVersionUID = -6986749569115643762L;

    //手机号码
    private String phoneNumber;

    //签名
    private String signName;

    //模板码
    private String templateCode;

    //模板参数 shopId  password
    private String templateJsonParam;
}
