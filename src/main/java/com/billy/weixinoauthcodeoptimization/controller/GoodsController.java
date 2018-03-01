package com.billy.weixinoauthcodeoptimization.controller;

import com.billy.weixinoauthcodeoptimization.controller.wx.WxOAuth2Controller;
import com.billy.weixinoauthcodeoptimization.controller.wx.WxRedirectController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class GoodsController extends WxOAuth2Controller {
    private static final Logger LOG = LoggerFactory.getLogger(GoodsController.class);

    @Autowired
    private WxRedirectController wxRedirectController;


    @GetMapping("list")
    public String to(HttpServletRequest request, Model model) {
        Object openidTokenObj = request.getSession().getAttribute(WxRedirectController.WX_OPENID_TOKEN);
        LOG.debug("openid: " + openidTokenObj);
        model.addAttribute("openid", openidTokenObj);
        return "goods/list";
    }


}
