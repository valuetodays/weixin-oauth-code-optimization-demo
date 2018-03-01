package com.billy.weixinoauthcodeoptimization.controller;

import com.billy.weixinoauthcodeoptimization.util.ServerPropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * @author liulei@bshf360.com
 * @since 2018-03-01 16:37
 */
@Controller
@RequestMapping("/goto")
public class GotoController {
    private static final Logger LOG = LoggerFactory.getLogger(GotoController.class);

    private static final String APPID = ServerPropertiesUtil.getAppId();
    private static final String PROJECT_BASE_URL = ServerPropertiesUtil.getProjectBaseUrl();


    @GetMapping("/to/{action}/{method}")
    public void to(@PathVariable String action, @PathVariable String method, HttpServletResponse response) {
        try {
            String encodedUri = URLEncoder.encode(PROJECT_BASE_URL + "/" + action + "/" + method, "utf-8");
            String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="
                    + APPID
                    + "&redirect_uri="
                    + encodedUri
                    + "&response_type=code&scope=snsapi_base&state=1232123#wechat_redirect";
            LOG.debug("access wx with url: " + url);
            response.sendRedirect(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
