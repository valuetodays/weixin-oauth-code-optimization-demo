package com.billy.weixinoauthcodeoptimization.controller;

import com.billy.weixinoauthcodeoptimization.WxOAuth2Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author liulei@bshf360.com
 * @since 2018-03-01 16:41
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {
    private static final Logger LOG = LoggerFactory.getLogger(GoodsController.class);


    @GetMapping("list")
    public String to(HttpServletRequest request, Model model) {
        Object openidTokenObj = request.getSession().getAttribute(WxOAuth2Interceptor.WX_OPENID_TOKEN);
        LOG.debug("openid: " + openidTokenObj);
        model.addAttribute("openid", openidTokenObj);
        return "goods/list";

    }


}
