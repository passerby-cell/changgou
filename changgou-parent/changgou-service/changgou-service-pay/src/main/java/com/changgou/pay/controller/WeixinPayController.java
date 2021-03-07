package com.changgou.pay.controller;

import com.changgou.pay.service.WeixinService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/weixin/pay")
public class WeixinPayController {

    @Autowired
    private WeixinService weixinService;

    /***
     * 创建二维码
     * @return
     */
    @RequestMapping(value = "/create/native")
    public Result createNative(String outtradeno, String money) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("outtradeno", outtradeno);
        map.put("totalfee", money);
        Map<String, String> resultMap = weixinService.createNative(map);
        return new Result(true, StatusCode.OK, "创建二维码预付订单成功！", resultMap);
    }
}
