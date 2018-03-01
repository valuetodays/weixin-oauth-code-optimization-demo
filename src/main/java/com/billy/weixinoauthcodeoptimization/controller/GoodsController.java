package com.billy.weixinoauthcodeoptimization.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.billy.weixinoauthcodeoptimization.util.HttpClientUtil;
import com.billy.weixinoauthcodeoptimization.util.ServerPropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author liulei@bshf360.com
 * @since 2018-03-01 16:41
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {
    private static final Logger LOG = LoggerFactory.getLogger(GoodsController.class);

    private static final String APPID = ServerPropertiesUtil.getAppId();
    private static final String SECRET = ServerPropertiesUtil.getSecret();
    private static final String PROJECT_BASE_URL = ServerPropertiesUtil.getProjectBaseUrl();

    @GetMapping("list")
    public String to(String code, Model model) {
        String openid = getOpenidByCode(code);

        model.addAttribute("openid", openid);
        return "goods/list";
    }

    /**
     * 根据code获取用户id
     * @param code code
     */
    private String getOpenidByCode(String code) {
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + APPID +
                "&secret=" + SECRET +
                "&code=" + code +
                "&grant_type=authorization_code";
        String doGet = HttpClientUtil.doGet(url, null, null);
        LOG.debug("msg: " + doGet);
        JSONObject jsonObject = JSON.parseObject(doGet);
        return jsonObject.getString("openid");
    }

}
