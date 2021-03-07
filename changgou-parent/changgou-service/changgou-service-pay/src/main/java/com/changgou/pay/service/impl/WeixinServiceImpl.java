package com.changgou.pay.service.impl;

import com.changgou.pay.service.WeixinService;
import com.github.wxpay.sdk.WXPayUtil;
import entity.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeixinServiceImpl implements WeixinService {
    //应用id
    @Value("${weixin.appid}")
    private String appid;
    //商户号
    @Value("${weixin.partner}")
    private String partner;
    //密钥
    @Value("${weixin.partnerkey}")
    private String partnerkey;
    //支付回调地址
    @Value("${weixin.notifyurl}")
    private String notifyurl;

    /***
     * 创建二维码操作
     * @param parameterMap
     * @return
     */
    @Override
    public Map<String, String> createNative(Map<String, String> parameterMap) {
        try {
            //参数
            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("appid", appid);
            paramMap.put("mch_id", partner);
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            paramMap.put("body", "畅购商城商品不错");
            //订单号
            paramMap.put("out_trade_no", parameterMap.get("outtradeno"));
            //金额，本该传过来parameterMap.get("totalfee")，但是没钱支付，写死
            paramMap.put("total_fee", "1");
            paramMap.put("spbill_create_ip", "127.0.0.1");
            //交易结果回调通知地址
            paramMap.put("notify_url", notifyurl);
            paramMap.put("trade_type", "NATIVE");
            //将map转换为xml可以携带签名
            String signedXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);
            System.err.println(signedXml);
            //URL地址
            String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
            //提交方式
            HttpClient httpClient = new HttpClient(url);
            httpClient.setHttps(true);
            //提交参数
            httpClient.setXmlParam(signedXml);
            //获取返回的数据
            httpClient.post();
            //返回的数据转成Map
            String content = httpClient.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("微信支付异常!");
        }
        return null;
    }
}
