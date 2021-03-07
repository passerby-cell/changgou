package com.changgou.pay.service;

import java.util.Map;

public interface WeixinService {
    /***
     * 创建二维码操作
     */
    Map<String, String> createNative(Map<String, String> parameterMap);
}
