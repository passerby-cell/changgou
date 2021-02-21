package com.changgou.search;

import com.changgou.search.feign.SkuFeign;
import com.changgou.search.pojo.SkuInfo;
import entity.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @author lhy
 * @version 1.0
 * @date 2021/2/15 19:59
 */
@Controller
@RequestMapping("/search")
public class controller {
    @Autowired
    private SkuFeign skuFeign;

    /**
     * 实现搜索调用
     *
     * @return
     */
    @GetMapping("/list")
    public String search(@RequestParam(required = false) Map<String, String> searchMap, Model model) {
        Map<String, Object> resultMap = skuFeign.search(searchMap);
        model.addAttribute("result", resultMap);
        model.addAttribute("searchMap", searchMap);
        String[] urls = url(searchMap);
        String url = urls[0];
        String sortUrl = urls[1];
        model.addAttribute("url", url);
        model.addAttribute("sortUrl", sortUrl);
        Page<SkuInfo> pageInfo = new Page<SkuInfo>(
                Long.parseLong(resultMap.get("total").toString()),
                Integer.parseInt(resultMap.get("pageNum").toString()),
                Integer.parseInt(resultMap.get("pageSize").toString())
        );
        model.addAttribute("pageInfo", pageInfo);
        return "search";
    }

    public String[] url(Map<String, String> searchMap) {
        String url = "/search/list";//初始化地址
        String sortUrl = "/search/list";//排序地址
        if (!searchMap.isEmpty()) {
            url += "?";
            sortUrl += "?";
            for (Map.Entry<String, String> entry : searchMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key.equalsIgnoreCase("pageNum")){
                    continue;
                }
                url += key + "=" + value + "&";
                //排序参数跳过
                if (key.equalsIgnoreCase("sortField") || key.equalsIgnoreCase("sortRule")) {
                    continue;
                }
                sortUrl += key + "=" + value + "&";
            }
            url = url.substring(0, url.length() - 1);
            sortUrl = sortUrl.substring(0, sortUrl.length() - 1);
        }
        return new String[]{url, sortUrl};
    }
}
