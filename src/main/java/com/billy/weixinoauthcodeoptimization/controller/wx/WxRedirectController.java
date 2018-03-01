package com.billy.weixinoauthcodeoptimization.controller.wx;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.billy.weixinoauthcodeoptimization.util.HttpClientUtil;
import com.billy.weixinoauthcodeoptimization.util.ServerPropertiesUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 接收从微信过来的请求，并且重定向到业务代码里
 *
 * @author liulei@bshf360.com
 * @since 2017-10-30 13:40
 */
@Controller
@RequestMapping("/wxRedirect")
public class WxRedirectController {
    private static Logger LOG = Logger.getLogger(WxRedirectController.class);

    public static String SECRET = ServerPropertiesUtil.getSecret();
    public static String APPID = ServerPropertiesUtil.getAppId();
    /** 微信token */
    public static final String WX_OPENID_TOKEN = "wx.openidtoken";
    /** 微信验证url */
    public static final String WX_OAUTH2_URL = "wxoauth2.url";
    /** 业务url */
    public static final String WX_BUSINESS_URL = "business.url";

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    /**
     * 微信返回到此方法，获取用户的openid，再重定向到业务方法中
     * @param code 微信返回的code，用于获取用户的openid
     */
    @RequestMapping("/callback")
    public String callback(String code) throws IOException {
        LOG.debug("code:" + code);

        if (StringUtils.isNotEmpty(code)) {
            String openidByCode = getOpenidByCode(code);
            request.getSession().setAttribute(WX_OPENID_TOKEN, openidByCode);
        }
        Object urlObj = request.getSession().getAttribute(WX_BUSINESS_URL);
        if (urlObj == null) {
            return null;
        }
        return "redirect:" + urlObj.toString();
    }

    /**
     * 重定向到微信，再返回到业务方法里，该方法会导致请求到达{@link #callback(String)}
     */
    public void toOAuth2() {
        Object urlObj = request.getSession().getAttribute(WX_OAUTH2_URL);
        if (urlObj == null) {
            return;
        }
        String url = urlObj.toString();
        LOG.debug("url=" + url);
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("error to redirect to " + url);
        }
    }

    /**
     * 根据code获取用户id
     * @param code code
     */
    private String getOpenidByCode(String code) {
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" +
                APPID +
                "&secret=" + SECRET +
                "&code=" + code +
                "&grant_type=authorization_code";
        String doGet = HttpClientUtil.doGet(url, null, null);
        LOG.info("msg: " + doGet);
        JSONObject jsonObject = JSON.parseObject(doGet);
        return jsonObject.getString("openid");
    }

}
