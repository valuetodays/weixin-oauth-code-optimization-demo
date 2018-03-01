package com.billy.weixinoauthcodeoptimization.controller.wx;

import com.billy.weixinoauthcodeoptimization.util.ServerPropertiesUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 跳转到h5页面时需要openid的数据均需继承本类，具体业务方法，不再接收code参数
 *
 * @author liulei@bshf360.com
 * @since 2017-10-20 13:29
 */
@Controller
public class WxOAuth2Controller {
    private static Logger LOG = Logger.getLogger(WxOAuth2Controller.class);

    private static final String APPID = ServerPropertiesUtil.getAppId();
    private static final String PROJECT_BASE_URL = ServerPropertiesUtil.getProjectBaseUrl();

    @ModelAttribute
    public void beforeH5Page(HttpServletRequest request)  {
        try {
            // 看Session中是否已有openid，没有的话就去授权认证
            Object openidObj = request.getSession().getAttribute(WxRedirectController.WX_OPENID_TOKEN);
            if (openidObj == null) {
                String requestURI = request.getRequestURL().toString();
                String queryString = request.getQueryString();
                if (StringUtils.isNotEmpty(queryString)) {
                    requestURI += "?" + queryString;
                }
                request.getSession().setAttribute(WxRedirectController.WX_BUSINESS_URL, requestURI);
                String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="
                        + APPID
                        + "&redirect_uri="
                        + URLEncoder.encode(PROJECT_BASE_URL + "/wxRedirect/callback", "utf-8")
                        + "&response_type=code&scope=snsapi_base&state=234545#wechat_redirect";
                LOG.debug("access wx with url: " + url);
                request.getSession().setAttribute(WxRedirectController.WX_OAUTH2_URL, url);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
