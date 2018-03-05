package com.billy.weixinoauthcodeoptimization;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.billy.weixinoauthcodeoptimization.util.HttpClientUtil;
import com.billy.weixinoauthcodeoptimization.util.ServerPropertiesUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * 微信认证拦截器
 *
 * @author liulei@bshf360.com
 * @since 2018-03-05 13:23
 */
public class WxOAuth2Interceptor extends HandlerInterceptorAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(WxOAuth2Interceptor.class);
    /** 微信token */
    public static final String WX_OPENID_TOKEN = "wx.openidtoken";
    private static final String APPID = ServerPropertiesUtil.getAppId();
    private static String SECRET = ServerPropertiesUtil.getSecret();
    private static final String PROJECT_BASE_URL = ServerPropertiesUtil.getProjectBaseUrl();


    /**
     * session中若没有openid，就判断参数中是否有code，若有code就根据code获取openid，
     * 获取到openid就把openid放到session中并进行业务处理，否则就进行微信授权认证
     *
     * @param request request
     * @param response response
     * @param handler handler
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        Object openidTokenObj = request.getSession().getAttribute(WX_OPENID_TOKEN);
        if (openidTokenObj == null) {
            String code = request.getParameter("code");
            String openid = getOpenidByCode(code);
            if (openid != null) {
                request.getSession().setAttribute(WX_OPENID_TOKEN, openid);
                return true;
            }
            String requestURI = request.getRequestURI();
            requestURI = requestURI.substring(request.getContextPath().length());
            String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="
                    + APPID
                    + "&redirect_uri="
                    + URLEncoder.encode(PROJECT_BASE_URL + requestURI, "utf-8")
                    + "&response_type=code&scope=snsapi_base&state=234545#wechat_redirect";
            LOG.debug("access wx with url: " + url);
            try {
                response.sendRedirect(url);
            } catch (IOException e) {
                e.printStackTrace();
                LOG.error("error to redirect to " + url);
            }
            return false;
        }
        return true;
    }

    /**
     * 根据code获取用户id
     * @param code code
     */
    private String getOpenidByCode(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
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